package nu.shout.shout.chat;

import java.util.Locale;

import nu.shout.shout.R;
import nu.shout.shout.chat.items.Chat;
import nu.shout.shout.chat.items.Notice;
import nu.shout.shout.location.Building;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class ChatNotifier implements ChatServiceListener {
	private int id;
	
	private ChatService chatService;

	private NotificationManager nm;
	private SharedPreferences prefs;
	
	private NotificationCompat.Builder builder;
	
	public ChatNotifier(ChatService chatService, int id) {
		this.chatService = chatService;
		this.id = id;
		
		this.nm = (NotificationManager) this.chatService.getSystemService(Context.NOTIFICATION_SERVICE);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this.chatService);
		
		Intent notiIntent = new Intent(this.chatService, ChatActivity.class);
        PendingIntent i = PendingIntent.getActivity(this.chatService, 0, notiIntent, 0);
        
		this.builder = new NotificationCompat.Builder(this.chatService)
     		.setSmallIcon(android.R.drawable.ic_media_ff)
     		.setContentText(this.chatService.getString(R.string.noti_not_in_channel))
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

	@Override
	public void onLeave() {
	}

	@Override
	public void onJoining(Building building) {
	}

	@Override
	public void onMessage(Chat c) {
		// Mention notification
		Locale locale = this.chatService.getResources().getConfiguration().locale;
		if(c.message.toLowerCase(locale).contains(this.chatService.getNick().toLowerCase(locale))) {
			if(this.prefs.getBoolean("noti_mentioned", true)) {
				this.applySettings();
				this.builder.setContentTitle(c.nickname + " " + this.chatService.getString(R.string.noti_mentioned));
				this.builder.setContentText(c.message);
				Notification n = this.builder.getNotification();
				this.nm.notify(this.id, n);
			}
		}
	}

	@Override
	public void onNotice(Notice notice) {
	}

	@Override
	public void onStartConnecting() {
	}

	@Override
	public void onStartDisconnecting() {
	}

	@Override
	public void onConnect() {
	}

	@Override
	public void onDisconnect() {
	}

	@Override
	public void onIssueProviderDisabled() {
	}

	@Override
	public void onErrorBuildingFetch() {
	}

	@Override
	public void onErrorNicknameInUse() {
	}

	@Override
	public void onErrorCouldNotConnect() {
	}

	@Override
	public void onErrorUnknown(Exception e) {
	}

	@Override
	public void onUserJoined(String nickname) {
	}

	@Override
	public void onUserParted(String nickname) {
	}
}
