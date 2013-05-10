package nu.shout.shout;

import nu.shout.shout.irc.IRCConnection;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

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
	public void onConnect(ConnectEvent<IRCConnection> event) {
		this.listener.onConnect(event);
	}
	
	@Override
	public void onDisconnect(DisconnectEvent<IRCConnection> event) {
		this.listener.onDisconnect(event);
	}
}
