/* This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *   * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package nu.shout.shout.irc;


import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PartEvent;

/**
 * Because PircBotX is stupid and decided to made ListenerAdapter an abstract
 * class instead of an interface, activities or views can't be made into
 * listeners and this ugly solution has to be used.
 *
 * @author Milan Boers
 *
 */
public class IRCListenerAdapter extends ListenerAdapter<IRCConnection> {
	private IRCListener listener;

	public IRCListenerAdapter(IRCListener listener) {
		this.listener = listener;
	}

	@Override
	public void onMessage(MessageEvent<IRCConnection> event) {
		this.listener.onMessage(event);
	}

	@Override
	public void onNotice(NoticeEvent<IRCConnection> event) {
		this.listener.onNotice(event);
	}

	@Override
	public void onConnect(ConnectEvent<IRCConnection> event) {
		this.listener.onConnect(event);
	}

	@Override
	public void onDisconnect(DisconnectEvent<IRCConnection> event) {
		this.listener.onDisconnect(event);
	}

	@Override
	public void onJoin(JoinEvent<IRCConnection> event) {
		this.listener.onJoin(event);
	}

	public void onPart(PartEvent<IRCConnection> event) {
		this.listener.onPart(event);
	}
}
