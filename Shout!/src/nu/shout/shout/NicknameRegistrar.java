/* This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *   * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package nu.shout.shout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.util.Log;
import nu.shout.shout.chat.ChatService;
import nu.shout.shout.chat.ChatServiceListener;
import nu.shout.shout.chat.items.Chat;
import nu.shout.shout.chat.items.Notice;
import nu.shout.shout.location.Building;

public class NicknameRegistrar implements ChatServiceListener {
	@SuppressWarnings("unused")
	private static final String TAG = "NicknameRegistrar";

	private String password = "p" + UUID.randomUUID().toString().replaceAll("\\-", "").substring(0, 25);
	private String nickname;

	protected ChatService chatService;

	protected List<NicknameRegistrarListener> listeners = new ArrayList<NicknameRegistrarListener>();

	public NicknameRegistrar(ChatService chatService) {
		this.chatService = chatService;
		// Add listener
		this.chatService.addListener(this);
	}

	public void addListener(NicknameRegistrarListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Registers the current nickname to nickserv
	 * @return
	 */
	public void registerNick(String nickname) {
		this.nickname = nickname;
		this.chatService.connect(this.nickname);
	}

	@Override
	public void onLeave() {
	}

	@Override
	public void onJoining(Building building) {
	}

	@Override
	public void onErrorBuildingFetch() {
	}

	@Override
	public void onStartConnecting() {
		Log.v(TAG, "Connecting");
	}

	@Override
	public void onStartDisconnecting() {
		Log.v(TAG, "Disconnecting");
	}

	@Override
	public void onConnect() {
		Log.v(TAG, "Connected");
		this.chatService.sendMessage("NickServ", "register " + this.password);
	}

	@Override
	public void onDisconnect() {
		Log.v(TAG, "Disconnected");
		for(NicknameRegistrarListener l : this.listeners) {
			l.onDisconnect();
		}
	}

	@Override
	public void onNotice(Notice notice) {
		Log.v(TAG, notice.message);
		if(notice.nickname.equals("NickServ"))
		{
			if(notice.message.contains("already registered")) {
				Log.v(TAG, "Notice contains already registered");
				// Failure
				this.chatService.disconnect();
				for(NicknameRegistrarListener l : this.listeners) {
					l.onErrorNicknameInUse();
				}
			} else if(notice.message.contains("registered and protected")) {
			} else if(notice.message.contains("registered")) {
				// Success
				for(NicknameRegistrarListener l : this.listeners) {
					l.onNicknameRegistered(this.nickname, this.password);
				}
			}
		}
	}

	@Override
	public void onErrorNicknameInUse() {
		// Failure
		this.chatService.disconnect();
		for(NicknameRegistrarListener l : this.listeners) {
			l.onErrorNicknameInUse();
		}
	}

	@Override
	public void onErrorCouldNotConnect() {
		// Failure
		this.chatService.disconnect();
		for(NicknameRegistrarListener l : this.listeners) {
			l.onErrorCouldNotConnect();
		}
	}

	@Override
	public void onErrorUnknown(Exception e) {
		// Failure
		this.chatService.disconnect();
		for(NicknameRegistrarListener l : this.listeners) {
			l.onErrorUnknown();
		}
	}

	@Override
	public void onMessage(Chat chat) {
	}

	@Override
	public void onIssueProviderDisabled() {
	}

	@Override
	public void onUserJoined(String nickname) {
	}

	@Override
	public void onUserParted(String nickname) {
	}

}
