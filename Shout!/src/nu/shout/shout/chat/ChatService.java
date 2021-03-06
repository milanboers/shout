/* This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *   * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package nu.shout.shout.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PartEvent;

import nu.shout.shout.Notifications;
import nu.shout.shout.chat.items.Chat;
import nu.shout.shout.chat.items.Notice;
import nu.shout.shout.irc.IRCConnection;
import nu.shout.shout.irc.IRCListener;
import nu.shout.shout.irc.IRCListenerAdapter;
import nu.shout.shout.irc.NotInChannelException;
import nu.shout.shout.location.Building;
import nu.shout.shout.location.BuildingFetcher;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ChatService extends Service implements IRCListener, LocationListener {
	public class LocalBinder extends Binder {
		public ChatService getService() {
			return ChatService.this;
		}
	}
	@SuppressWarnings("unused")
	private static final String TAG = "ChatService";

	private static final String SERVER_ADDR = "server.shout.nu";

	// Min. time between location updates (in milliseconds)
	private static final int TIME_BETWEEN_LOC = 5000;
	// Min. distance between location updates (in meters)
	private static final int DIST_BETWEEN_LOC = 1;

	// Counter of how many processes (i.e. connecting or disconnecting) are busy
	protected AtomicInteger busy = new AtomicInteger();

	protected final IBinder binder = new LocalBinder();

	protected IRCConnection irc;

	protected ChatNotifier mentionNoti;

	protected List<ChatServiceListener> listeners = new ArrayList<ChatServiceListener>();

	private SharedPreferences settings;

	private Building currentBuilding;

	private List<Chat> memory = new ArrayList<Chat>();

	private LocationManager lm;
	private BuildingFetcher bf;

	@Override
	public void onCreate() {
		Log.v(TAG, "Creating ChatService");

        this.mentionNoti = new ChatNotifier(this, Notifications.MENTIONED.ordinal());
        this.listeners.add(this.mentionNoti);

		this.irc = new IRCConnection();
        IRCListenerAdapter adapter = new IRCListenerAdapter(this);
        this.irc.getListenerManager().addListener(adapter);

        this.bf = new BuildingFetcher();
        this.settings = PreferenceManager.getDefaultSharedPreferences(this);

        this.lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        this.lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_BETWEEN_LOC, DIST_BETWEEN_LOC, this);
        // Pick last known location
        this.onLocationChanged(this.lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
	}

	private void invalidateForeground() {
        if(this.isConnected()) {
        	Intent notiIntent = new Intent(this, ChatActivity.class);
            PendingIntent i = PendingIntent.getActivity(this, 0, notiIntent, 0);

            String title = "Not in any channel";
            if(this.getCurrentBuilding() != null)
            	title = this.getCurrentBuilding().name;

            Notification notification = new NotificationCompat.Builder(this)
    	     	.setSmallIcon(android.R.drawable.ic_media_ff)
    	     	.setContentTitle(title)
    	     	.setContentTitle("Shout is running")
    	     	.setOngoing(true)
    	     	.setContentIntent(i)
    	     	.getNotification();
        	Log.v(TAG, "starting foreground");
        	startForeground(Notifications.CONNECTED.ordinal(), notification);
        } else {
        	Log.v(TAG, "stopping foreground");
        	stopForeground(true);
        }
	}

	public List<Chat> getMemory() {
		return this.memory;
	}

	public void connect() {
		this.connect(settings.getString("nickname", null));
	}

	public void connect(String nickname) {
		this.irc.setName(nickname);

		if(!this.irc.isConnected() && !this.isBusy())
		{
			this.busy.incrementAndGet();
			new AsyncTask<Void, Void, Exception>() {
				@Override
				protected Exception doInBackground(Void... arg0) {
					try {
						Log.v(TAG, "Connecting");
						ChatService.this.irc.connect(SERVER_ADDR);
						return null;
					} catch (Exception e) {
						return e;
					}
				}

				@Override
				protected void onPostExecute(Exception e) {
					if(e != null) {
						for(ChatServiceListener l : ChatService.this.listeners) {
							try {
								throw e;
							} catch (NickAlreadyInUseException e1) {
								l.onErrorNicknameInUse();
							} catch (IOException e1) {
								l.onErrorCouldNotConnect();
							} catch (IrcException e1) {
								l.onErrorCouldNotConnect();
							} catch (NullPointerException e1) {
								l.onErrorCouldNotConnect();
							} catch (Exception e1) {
								// Unknown error
								e1.printStackTrace();
								l.onErrorUnknown(e1);
							}
						}
					}
				}
			}.execute();

			for(ChatServiceListener l : this.listeners)
				l.onStartConnecting();
		}
	}

	public void disconnect() {
		if(this.irc.isConnected())
		{
			this.busy.incrementAndGet();
			this.irc.disconnect();
			for(ChatServiceListener l : this.listeners)
				l.onStartDisconnecting();
		}
	}

	public String getNick() {
		return this.irc.getNick();
	}

	public void sendMessage(String message) throws NotInChannelException {
		Chat chat = new Chat(System.currentTimeMillis(), "me", message);
		this.memory.add(chat);
		this.irc.sendMessage(message);

		// Tell to the listeners that there is a new message
		for(ChatServiceListener l : this.listeners) {
			l.onMessage(chat);
		}
	}

	public void sendMessage(String target, String message) {
		this.irc.sendMessage(target, message);
	}

	public boolean changeNick(String nickname) {
		this.irc.changeNick(nickname);
		Log.v(TAG, "nickname now " + this.irc.getNick());
		Log.v(TAG, "should be " + nickname);
		if(this.irc.getNick().equals(nickname))
			return true;
		return false;
	}

	public boolean isConnected() {
		return this.irc.isConnected();
	}

	public boolean isBusy() {
		return this.busy.get() != 0;
	}

	public Set<User> getUsers() {
		return this.irc.getChannel().getUsers();
	}

	public void addListener(ChatServiceListener l) {
		this.listeners.add(l);
	}

	public Building getCurrentBuilding() {
		return currentBuilding;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "ChatService destroyed");
		stopForeground(false);
	}

	@Override
	public IBinder onBind(Intent i) {
		return binder;
	}

	@Override
	public void onMessage(MessageEvent<IRCConnection> event) {
		Log.v(TAG, "Message from " + event.getUser().getNick() + " " + event.getMessage());

		Chat c = new Chat(event.getTimestamp(), event.getUser().getNick(), event.getMessage());
		this.memory.add(c);

		for(ChatServiceListener l : this.listeners) {
			l.onMessage(c);
		}
	}

	@Override
	public void onNotice(NoticeEvent<IRCConnection> event) {
		for(ChatServiceListener l : this.listeners) {
			l.onNotice(new Notice(event.getUser().getNick(), event.getMessage()));
		}
	}

	@Override
	public void onConnect(ConnectEvent<IRCConnection> event) {
		// Identify
		if(this.settings.getString("password", null) != null)
			this.irc.identify(this.settings.getString("password", null));
		invalidateForeground();

		this.busy.decrementAndGet();
		for(ChatServiceListener l : this.listeners) {
			l.onConnect();
		}
	}

	@Override
	public void onDisconnect(DisconnectEvent<IRCConnection> event) {
		invalidateForeground();

		this.currentBuilding = null;

		this.busy.decrementAndGet();
		for(ChatServiceListener l : this.listeners) {
			l.onDisconnect();
		}
	}

	/**
     * Leave all channels
     */
    protected void leave() {
    	this.irc.partAllChannels();
    	this.currentBuilding = null;

    	for(ChatServiceListener l : this.listeners) {
    		l.onLeave();
    	}
    }

    /**
     * Join a channel (and leave all others)
     * @param building building to join
     */
    protected void join(Building building) {
    	this.currentBuilding = building;
    	this.irc.joinChannel(building.ircroom);

    	invalidateForeground();

    	for(ChatServiceListener l : this.listeners) {
    		l.onJoining(building);
    	}
    }

	@Override
	public void onLocationChanged(final Location loc) {
		new AsyncTask<Void, Void, List<Building>>() {
			@Override
			protected List<Building> doInBackground(Void... arg0) {
				try {
					return ChatService.this.bf.getBuildings(loc);
				} catch (IOException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(List<Building> buildings) {
				if(buildings == null) {
					for(ChatServiceListener l : ChatService.this.listeners) {
						l.onErrorBuildingFetch();
					}
				} else if(buildings.size() == 0) {
					ChatService.this.leave();
				} else if(!buildings.get(0).equals(ChatService.this.currentBuilding)) {
					ChatService.this.join(buildings.get(0));
				}
			}
		}.execute();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		for(ChatServiceListener l : this.listeners) {
			l.onIssueProviderDisabled();
		}
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// An enabled provider is not really interesting.. Is it?
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

	@Override
	public void onJoin(JoinEvent<IRCConnection> event) {
		for(ChatServiceListener l : this.listeners) {
			l.onUserJoined(event.getUser().getNick());
		}
	}

	@Override
	public void onPart(PartEvent<IRCConnection> event) {
		for(ChatServiceListener l : this.listeners) {
			l.onUserParted(event.getUser().getNick());
		}
	}
}
