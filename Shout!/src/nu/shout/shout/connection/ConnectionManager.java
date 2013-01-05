package nu.shout.shout.connection;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.os.AsyncTask;

public abstract class ConnectionManager extends AsyncTask<Void, Void, Void> {
	protected List<OnMessageReceivedListener> onMessageReceivedListeners;
	public static interface OnMessageReceivedListener {
		public void onMessageReceived(String channel, String sender, String login, String hostname, String message);
	}
	public void setOnMessageReceivedListener(OnMessageReceivedListener l) {
		this.onMessageReceivedListeners.add(l);
	}
	
	protected List<OnConnectListener> onConnectListeners;
	public static interface OnDisconnectListener {
		public void onDisconnect();
	}
	public void setOnDisconnectListener(OnDisconnectListener l) {
		this.onDisconnectListeners.add(l);
	}

	protected List<OnDisconnectListener> onDisconnectListeners;
	public static interface OnConnectListener {
		public void onConnect();
	}
	public void setOnConnectListener(OnConnectListener l) {
		this.onConnectListeners.add(l);
	}
	
	public ConnectionManager() {
		this.onMessageReceivedListeners = new CopyOnWriteArrayList<OnMessageReceivedListener>();
		this.onConnectListeners = new CopyOnWriteArrayList<OnConnectListener>();
		this.onDisconnectListeners = new CopyOnWriteArrayList<OnDisconnectListener>();
	}
	
	public abstract void connect();
	public abstract void disconnect();
	public abstract void sendChat(String message);
}
