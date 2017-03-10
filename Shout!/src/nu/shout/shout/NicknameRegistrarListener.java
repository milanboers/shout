/* This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *   * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package nu.shout.shout;

public interface NicknameRegistrarListener {
	public void onNicknameRegistered(String nickname, String password);
	public void onDisconnect();
	// Errors
	public void onErrorNicknameInUse();
	public void onErrorCouldNotConnect();
	public void onErrorUnknown();
}
