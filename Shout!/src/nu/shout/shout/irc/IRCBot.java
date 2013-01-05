package nu.shout.shout.irc;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jibble.pircbot.PircBot;

import android.util.Log;

public class IRCBot extends PircBot {
	private static final String TAG = "IRCBot";

	private List<OnMessageListener> onMessageListeners;
	public static interface OnMessageListener {
		public void onMessage(String channel, String sender, String login, String hostname, String message);
	}
	public void setOnMessageListener(OnMessageListener l) {
		this.onMessageListeners.add(l);
	}
	
	private List<OnConnectListener> onConnectListeners;
	public static interface OnConnectListener {
		public void onConnect();
	}
	public void setOnConnectListener(OnConnectListener l) {
		this.onConnectListeners.add(l);
	}
	
	private List<OnDisconnectListener> onDisconnectListeners;
	public static interface OnDisconnectListener {
		public void onDisconnect();
	}
	public void setOnDisconnectListener(OnDisconnectListener l) {
		this.onDisconnectListeners.add(l);
	}

	public IRCBot() {
		this.setName("ShoutUser");

		this.onMessageListeners = new CopyOnWriteArrayList<OnMessageListener>();
		this.onConnectListeners = new CopyOnWriteArrayList<OnConnectListener>();
		this.onDisconnectListeners = new CopyOnWriteArrayList<OnDisconnectListener>();
	}

	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		for(OnMessageListener l : this.onMessageListeners) {
			l.onMessage(channel, sender, login, hostname, message);
		}
	}
	
	@Override
	public void onConnect() {
		Log.v(TAG, "got connection - ircbot");
		for(OnConnectListener l : this.onConnectListeners) {
			l.onConnect();
		}
	}
	
	@Override
	public void onDisconnect() {
		for(OnDisconnectListener l : this.onDisconnectListeners) {
			l.onDisconnect();
		}
	}
}
