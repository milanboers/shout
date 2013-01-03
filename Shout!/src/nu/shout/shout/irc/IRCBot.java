package nu.shout.shout.irc;

import org.jibble.pircbot.PircBot;

import android.util.Log;

public class IRCBot extends PircBot {
	private static final String TAG = "IRCBot";
	
	private IRCManagerTask manager;
	
	public IRCBot(IRCManagerTask manager) {
		this.setName("ShoutUser");
		
		this.manager = manager;
	}
	
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		this.manager.progress(message);
	}
}
