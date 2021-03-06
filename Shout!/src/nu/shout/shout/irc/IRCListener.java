/* This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *   * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package nu.shout.shout.irc;


import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PartEvent;

public interface IRCListener {
	public void onMessage(MessageEvent<IRCConnection> event);
	public void onNotice(NoticeEvent<IRCConnection> event);
	public void onConnect(ConnectEvent<IRCConnection> event);
	public void onDisconnect(DisconnectEvent<IRCConnection> event);
	public void onJoin(JoinEvent<IRCConnection> event);
	public void onPart(PartEvent<IRCConnection> event);
}
