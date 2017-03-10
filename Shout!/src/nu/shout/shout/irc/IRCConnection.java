/* This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *   * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package nu.shout.shout.irc;

import java.io.IOException;


import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;

import android.util.Log;

public class IRCConnection extends PircBotX {
	@SuppressWarnings("unused")
	private static final String TAG = "IRCConnection";

	// The current channel (Shout will only connect to one channel)
	private String channelName;

	public IRCConnection() {
		super();

		this.setAutoReconnect(true);
	}

	public void sendMessage(String message) throws NotInChannelException {
		if(channelName == null) {
			throw new NotInChannelException();
		}
		this.sendMessage(channelName, message);
	}

	/**
	 * Leave all channels
	 */
	public void partAllChannels() {
		for(Channel c : this.getChannels())
			this.partChannel(c);
		this.channelName = null;
	}

	@Override
	public void connect(String hostname) throws NickAlreadyInUseException, IOException, IrcException {
		super.connect(hostname);
		if(this.channelName != null)
			this.joinChannel(IRCConnection.this.channelName);
	}

	/**
	 * Join channel and leave all other channels
	 */
	public void joinChannel(String channelName) {
		this.partAllChannels();
		super.joinChannel(channelName);
		this.channelName = channelName;
	}

	public Channel getChannel() {
		Log.v(TAG, "channelName " + this.channelName);
		for(Channel c : this.getChannels())
		{
			Log.v(TAG, c.getName());
			if(c.getName().equalsIgnoreCase(this.channelName))
				return c;
		}
		return null;
	}
}
