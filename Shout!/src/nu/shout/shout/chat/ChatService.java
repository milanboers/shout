package nu.shout.shout.chat;

import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import nu.shout.shout.Notifications;
import nu.shout.shout.R;
import nu.shout.shout.irc.IRCConnection;
import nu.shout.shout.irc.IRCListener;
import nu.shout.shout.irc.IRCListenerAdapter;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ChatService extends Service implements IRCListener {
	public class LocalBinder extends Binder {
		ChatService getService() {
			return ChatService.this;
		}
	}
	@SuppressWarnings("unused")
	private static final String TAG = "ChatService";
	
	private final IBinder binder = new LocalBinder();
	
	public IRCConnection irc;
	
	private ChatMentionNotifier mentionNoti;
	
	@Override
	public void onCreate() {
		Log.v(TAG, "Creating ChatService");
        
        this.mentionNoti = new ChatMentionNotifier(this, Notifications.MENTIONED.ordinal());
        
		startForeground(Notifications.CONNECTED.ordinal(), getNotification(getString(R.string.noti_not_in_channel)));
        
        this.irc = new IRCConnection("temporary_name");
        
        IRCListenerAdapter adapter = new IRCListenerAdapter(this);
        this.irc.getListenerManager().addListener(adapter);
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
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return START_STICKY;
	}
	
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
	}

	@Override
	public void onConnect(ConnectEvent<IRCConnection> event) {
		startForeground(Notifications.CONNECTED.ordinal(), getNotification(getString(R.string.app_name) + " " + getString(R.string.noti_connected)));
	}

	@Override
	public void onDisconnect(DisconnectEvent<IRCConnection> event) {
		startForeground(Notifications.CONNECTED.ordinal(), getNotification(getString(R.string.app_name) + " " + getString(R.string.noti_not_in_channel)));
	}
}
