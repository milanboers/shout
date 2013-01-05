package nu.shout.shout.connection;

import java.io.IOException;

import nu.shout.shout.irc.IRCBot;
import nu.shout.shout.irc.IRCBot.OnMessageListener;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import android.util.Log;

public class IRCConnectionManager extends ConnectionManager {
	private static final String TAG = "IRCManagerTask";

	private IRCBot bot;

	public IRCConnectionManager() {
		super();
		
		this.bot = new IRCBot();
		
		this.connectListeners();
	}
	
	public void connectListeners() {
		this.bot.setOnMessageListener(new OnMessageListener() {
			@Override
			public void onMessage(String channel, String sender, String login, String hostname, String message) {
				for(OnMessageReceivedListener l : IRCConnectionManager.this.onMessageReceivedListeners) {
					l.onMessageReceived(channel, sender, login, hostname, message);
				}
			}
		});
		this.bot.setOnConnectListener(new IRCBot.OnConnectListener() {
			@Override
			public void onConnect() {
				for(OnConnectListener l : IRCConnectionManager.this.onConnectListeners) {
					l.onConnect();
				}
			}
		});
		this.bot.setOnDisconnectListener(new IRCBot.OnDisconnectListener() {
			@Override
			public void onDisconnect() {
				for(OnDisconnectListener l : IRCConnectionManager.this.onDisconnectListeners) {
					l.onDisconnect();
				}
			}
		});
	}

	@Override
	public void connect() {
		this.execute();
	}

	@Override
	public void disconnect() {
		this.bot.disconnect();
	}

	@Override
	public void sendChat(String message) {
		this.bot.sendMessage("##pytest", message);
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		try {
			this.bot.connect("irc.freenode.net");
		} catch (NickAlreadyInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IrcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.bot.joinChannel("##pytest");
		return null;
	}
}
