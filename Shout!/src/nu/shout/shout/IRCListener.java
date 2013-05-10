package nu.shout.shout;

import nu.shout.shout.connection.IRCConnection;

import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

public interface IRCListener {
	public void onMessage(MessageEvent<IRCConnection> event);
	public void onConnect(ConnectEvent<IRCConnection> event);
	public void onDisconnect(DisconnectEvent<IRCConnection> event);
}
