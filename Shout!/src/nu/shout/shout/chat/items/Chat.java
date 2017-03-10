/* This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *   * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package nu.shout.shout.chat.items;


public class Chat extends Item {
	public long timestamp;
	public String nickname;
	public String message;

	public Chat(long timestamp, String nickname, String message) {
		this.timestamp = timestamp;
		this.nickname = nickname;
		this.message = message;
	}
}
