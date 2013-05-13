package nu.shout.shout.chat;

import nu.shout.shout.R;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class ChatConnectNotification {
	private Context ctx;
	
	private NotificationCompat.Builder builder;
	
	public ChatConnectNotification(Context ctx) {
		this.ctx = ctx;
		
        Intent notiIntent = new Intent(this.ctx, ChatActivity.class);
        PendingIntent i = PendingIntent.getActivity(this.ctx, 0, notiIntent, 0);
        
        this.builder = new NotificationCompat.Builder(this.ctx)
	     	.setSmallIcon(android.R.drawable.ic_media_ff)
	     	.setContentText(this.ctx.getString(R.string.noti_not_in_channel))
	     	.setContentIntent(i);
	}
	
	public Notification changeText(String text) {
		this.builder.setContentText(text);
    	Notification n = this.builder.getNotification();
    	n.flags |= Notification.FLAG_ONGOING_EVENT;
    	return n;
	}
	
	public Notification changeTitle(String text) {
		this.builder.setContentTitle(text);
		Notification n = this.builder.getNotification();
		n.flags |= Notification.FLAG_ONGOING_EVENT;
		return n;
	}
}
