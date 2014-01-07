package nu.shout.shout.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;

import nu.shout.shout.Notifications;
import nu.shout.shout.R;
import nu.shout.shout.irc.IRCConnection;
import nu.shout.shout.irc.IRCListener;
import nu.shout.shout.irc.IRCListenerAdapter;
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
	
	// Min. time between location updates (in milliseconds)
	private static final int TIME_BETWEEN_LOC = 5000;
	// Min. distance between location updates (in meters)
	private static final int DIST_BETWEEN_LOC = 1;
	
	private final IBinder binder = new LocalBinder();
	
	private IRCConnection irc;
	
	private LocationManager lm;
	private BuildingFetcher bf;
	
	private ChatMentionNotifier mentionNoti;
	
	private Building currentBuilding;
	
	private List<ChatServiceListener> listeners = new ArrayList<ChatServiceListener>();
	
	private SharedPreferences settings;
	
	@Override
	public void onCreate() {
		Log.v(TAG, "Creating ChatService");
        
        this.mentionNoti = new ChatMentionNotifier(this, Notifications.MENTIONED.ordinal());
        
		startForeground(Notifications.CONNECTED.ordinal(), getNotification(getString(R.string.noti_not_in_channel)));
        
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
	
	private Notification getNotification(String text) {
		Intent notiIntent = new Intent(this, ChatActivity.class);
        PendingIntent i = PendingIntent.getActivity(this, 0, notiIntent, 0);
        
        Notification noti = new NotificationCompat.Builder(this)
	     	.setSmallIcon(android.R.drawable.ic_media_ff)
	     	.setContentText(text)
	     	.setOngoing(true)
	     	.setContentIntent(i)
	     	.getNotification();
        
        return noti;
	}
	
	public void connect() {
		this.connect(settings.getString("nickname", null));
	}
	
	public void connect(String nickname) {
		this.irc.setName(nickname);
		
		if(!this.irc.isConnected())
		{
			new AsyncTask<Void, Void, Exception>() {
				@Override
				protected Exception doInBackground(Void... arg0) {
					try {
						Log.v(TAG, "Connecting");
						// TODO: ergens hardcoden
						// TODO: geeft nullpointerexception als niet kan connecten
						ChatService.this.irc.connect("server.shout.nu");
						return null;
					} catch (NickAlreadyInUseException e) {
						for(ChatServiceListener l : ChatService.this.listeners)
							l.onNicknameInUse();
						return e;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return e;
					} catch (IrcException e) {
						// You have not registered
						// TODO Auto-generated catch block
						e.printStackTrace();
						return e;
					} catch (NullPointerException e) {
						// TODO: kon niet connecten
						return e;
					}
				}
				
				@Override
				protected void onPostExecute(Exception e) {
					
				}
			}.execute();
			
			for(ChatServiceListener l : this.listeners)
				l.onStartConnecting();
		}
	}
	
	public void disconnect() {
		if(this.irc.isConnected())
		{	this.irc.disconnect();
			for(ChatServiceListener l : this.listeners)
				l.onStartDisconnecting();
		}
	}
	
	public void sendMessage(String message) {
		this.irc.sendMessage(message);
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
		if(event.getMessage().contains(this.irc.getNick())) {
			this.mentionNoti.notify(event.getUser().getNick(), event.getMessage());
		}
		
		for(ChatServiceListener l : this.listeners) {
			l.onMessage(event.getUser().getNick(), event.getMessage());
		}
	}

	@Override
	public void onConnect(ConnectEvent<IRCConnection> event) {
		// Identify
		if(this.settings.getString("password", null) != null)
			this.irc.identify(this.settings.getString("password", null));
		
		startForeground(Notifications.CONNECTED.ordinal(), getNotification(getString(R.string.app_name) + " " + getString(R.string.noti_connected)));
		
		for(ChatServiceListener l : this.listeners) {
			l.onConnect();
		}
	}

	@Override
	public void onDisconnect(DisconnectEvent<IRCConnection> event) {
		startForeground(Notifications.CONNECTED.ordinal(), getNotification(getString(R.string.app_name) + " " + getString(R.string.noti_not_in_channel)));
		this.currentBuilding = null;
		
		for(ChatServiceListener l : this.listeners) {
			l.onDisconnect();
		}
	}
	
	/**
     * Leave all channels
     */
    private void leave() {
    	this.currentBuilding = null;
    	this.irc.partAllChannels();
    	
    	for(ChatServiceListener l : this.listeners) {
    		l.onLeave();
    	}
    }
    
    /**
     * Join a channel (and leave all others)
     * @param building building to join
     */
    private void join(Building building) {
    	this.irc.joinChannel(building.ircroom);
    	this.currentBuilding = building;
    	
    	for(ChatServiceListener l : this.listeners) {
    		l.onJoin(building);
    	}
    }
	
	@Override
	public void onLocationChanged(final Location loc) {
		//this.chatBox.addVerboseNotice("New location lat " + loc.getLatitude() + " lon " + loc.getLongitude());
		
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
						l.onError(getString(R.string.error_ioexception));
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNotice(NoticeEvent<IRCConnection> event) {
		for(ChatServiceListener l : this.listeners) {
			l.onNotice(event.getUser().getNick(), event.getNotice());
		}
	}
}
