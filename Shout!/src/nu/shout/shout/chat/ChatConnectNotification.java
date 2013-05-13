package nu.shout.shout.chat;

import nu.shout.shout.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class ChatConnectNotification {
	@SuppressWarnings("unused")
	private static final String TAG = "ChatConnectNotification";
	
	private int id;
	private boolean visible = false;
	
	private Context ctx;
	
	private NotificationManager nm;
	private SharedPreferences prefs;
	
	private NotificationCompat.Builder builder;
	
	public ChatConnectNotification(Context ctx, int id) {
		this.ctx = ctx;
		this.id = id;
		
		this.nm = (NotificationManager) this.ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this.ctx);
		
        Intent notiIntent = new Intent(this.ctx, ChatActivity.class);
        PendingIntent i = PendingIntent.getActivity(this.ctx, 0, notiIntent, 0);
        
        this.builder = new NotificationCompat.Builder(this.ctx)
	     	.setSmallIcon(android.R.drawable.ic_media_ff)
	     	.setContentText(this.ctx.getString(R.string.noti_not_in_channel))
	     	.setOngoing(true)
	     	.setContentIntent(i);
	}
	
	public void setChannel(String channel) {
		this.builder.setContentText(this.ctx.getString(R.string.noti_in_channel) + " " + channel);
		if(this.visible) {
	    	Notification n = this.builder.getNotification();
	    	this.nm.notify(this.id, n);
		}
	}
	
	public void setConncted() {
		if(this.prefs.getBoolean("noti_connected", true)) {
			this.builder.setContentTitle(this.ctx.getString(R.string.app_name) + " " + this.ctx.getString(R.string.noti_connected));
			Notification n = this.builder.getNotification();
			this.nm.notify(this.id, n);
			this.visible = true;
		}
	}
	
	public void cancel() {
		this.nm.cancel(this.id);
		this.visible = false;
	}
}
