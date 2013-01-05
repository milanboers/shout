package nu.shout.shout.irc;

import org.jibble.pircbot.PircBot;

import android.util.Log;

public class IRCBot extends PircBot {
	private static final String TAG = "IRCBot";

	private IRCBotObserver observer;

	public IRCBot(IRCBotObserver observer) {
		this.setName("ShoutUser");
		
		this.observer = observer;
	}

	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		this.observer.onIrcBotMessage(channel, sender, login, hostname, message);
	}
	
	@Override
	public void onConnect() {
		this.observer.onIrcBotConnect();
	}
	
	@Override
	public void onDisconnect() {
		this.observer.onIrcBotDisconnect();
	}
}
