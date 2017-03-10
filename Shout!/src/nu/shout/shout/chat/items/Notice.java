/* This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *   * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package nu.shout.shout.chat.items;

public class Notice extends Item {
	public String nickname;
	public String message;

	public Notice(String nickname, String message) {
		this.nickname = nickname;
		this.message = message;
	}
}
