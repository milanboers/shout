/* This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *   * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

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
		// No notifications if ChatActivity is running
		if(ChatActivity.running)
			return;

		// All notifications
		if(this.prefs.getString("noti_mentioned", "mentioned").equals("all")) {
			notification(String.format(this.chatService.getString(R.string.noti_mentioned), c.nickname), c.message);
		} else if(this.prefs.getString("noti_mentioned", "mentioned").equals("mentioned")) {
			Locale locale = this.chatService.getResources().getConfiguration().locale;
			if(c.message.toLowerCase(locale).contains(this.chatService.getNick().toLowerCase(locale))) {
				notification(String.format(this.chatService.getString(R.string.noti_message), c.nickname), c.message);
			}
		}
	}

	private void notification(String title, String message) {
		this.applySettings();
		this.builder.setContentTitle(title);
		this.builder.setContentText(message);
		Notification n = this.builder.getNotification();
		this.nm.notify(this.id, n);
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
