package nu.shout.shout.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.pircbotx.User;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ChatService extends Service implements IRCListener, LocationListener {
	public class LocalBinder extends Binder {
		ChatService getService() {
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
	
	@Override
	public void onCreate() {
		Log.v(TAG, "Creating ChatService");
        
        this.mentionNoti = new ChatMentionNotifier(this, Notifications.MENTIONED.ordinal());
        
		startForeground(Notifications.CONNECTED.ordinal(), getNotification(getString(R.string.noti_not_in_channel)));
        
        this.irc = new IRCConnection("temporary_name");
        
        IRCListenerAdapter adapter = new IRCListenerAdapter(this);
        this.irc.getListenerManager().addListener(adapter);
        
        this.bf = new BuildingFetcher();
        
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
		this.irc.connect();
		
		for(ChatServiceListener l : this.listeners) {
			l.onStartConnecting();
		}
	}
	
	public void disconnect() {
		this.irc.disconnect();
		
		for(ChatServiceListener l : this.listeners) {
			l.onStartDisconnecting();
		}
	}
	
	public void sendMessage(String message) {
		this.irc.sendMessage(message);
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
		if(event.getMessage().contains(this.irc.getNick())) {
			this.mentionNoti.notify(event.getUser().getNick(), event.getMessage());
		}
		
		for(ChatServiceListener l : this.listeners) {
			l.onMessage(event.getUser().getNick(), event.getMessage());
		}
	}

	@Override
	public void onConnect(ConnectEvent<IRCConnection> event) {
		startForeground(Notifications.CONNECTED.ordinal(), getNotification(getString(R.string.app_name) + " " + getString(R.string.noti_connected)));
		
		for(ChatServiceListener l : this.listeners) {
			l.onConnect();
		}
	}

	@Override
	public void onDisconnect(DisconnectEvent<IRCConnection> event) {
		startForeground(Notifications.CONNECTED.ordinal(), getNotification(getString(R.string.app_name) + " " + getString(R.string.noti_not_in_channel)));
		
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
}
