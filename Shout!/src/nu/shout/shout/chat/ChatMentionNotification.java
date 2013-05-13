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

public class ChatMentionNotification {
	private int id;
	
	private Context ctx;

	private NotificationManager nm;
	private SharedPreferences prefs;
	
	private NotificationCompat.Builder builder;
	
	public ChatMentionNotification(Context ctx, int id) {
		this.ctx = ctx;
		this.id = id;
		
		this.nm = (NotificationManager) this.ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this.ctx);
		
		Intent notiIntent = new Intent(this.ctx, ChatActivity.class);
        PendingIntent i = PendingIntent.getActivity(this.ctx, 0, notiIntent, 0);
        
		this.builder = new NotificationCompat.Builder(this.ctx)
     		.setSmallIcon(android.R.drawable.ic_media_ff)
     		.setContentText(this.ctx.getString(R.string.noti_not_in_channel))
     		.setAutoCancel(true)
     		.setContentIntent(i);
		
		this.applySettings();
	}
	
	private void applySettings() {
		int defaults = 0;
		if(this.prefs.getBoolean("noti_sound", true))
			defaults |= Notification.DEFAULT_SOUND;
		if(this.prefs.getBoolean("noti_vibrate", true))
			defaults |= Notification.DEFAULT_VIBRATE;
		if(this.prefs.getBoolean("noti_light", true))
			defaults |= Notification.DEFAULT_LIGHTS;
		this.builder.setDefaults(defaults);
	}
	
	public void notify(String name, String message) {
		if(this.prefs.getBoolean("noti_mentioned", true)) {
			this.applySettings();
			this.builder.setContentTitle(name + " " + this.ctx.getString(R.string.noti_mentioned));
			this.builder.setContentText(message);
			Notification n = this.builder.getNotification();
			this.nm.notify(this.id, n);
		}
	}
}
