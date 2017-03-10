/* This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *   * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package nu.shout.shout.chat;

import nu.shout.shout.chat.items.Chat;
import nu.shout.shout.chat.items.Notice;
import nu.shout.shout.location.Building;

public interface ChatServiceListener {
	public void onLeave();
	public void onJoining(Building building);
	public void onMessage(Chat chat);
	public void onNotice(Notice notice);
	public void onStartConnecting();
	public void onStartDisconnecting();
	public void onConnect();
	public void onDisconnect();
	public void onUserJoined(String nickname);
	public void onUserParted(String nickname);
	// Location issues
	public void onIssueProviderDisabled();
	// Errors
	public void onErrorBuildingFetch();
	public void onErrorNicknameInUse();
	public void onErrorCouldNotConnect();
	public void onErrorUnknown(Exception e);
}
