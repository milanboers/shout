package nu.shout.shout.chats;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import nu.shout.shout.connection.ConnectionManager;
import nu.shout.shout.connection.ConnectionManager.OnConnectListener;
import nu.shout.shout.connection.ConnectionManager.OnDisconnectListener;
import nu.shout.shout.connection.ConnectionManager.OnMessageReceivedListener;
import nu.shout.shout.connection.IRCConnectionManager;

import android.util.Log;

/**
 * Keeps track of all received and sent chats, and the connection
 * 
 * @author Milan Boers
 * 
 */
public class ChatsManager {
	private static final String TAG = "ChatsManager";

	private static ChatsManager instance;

	private ConnectionManager conManager;

	private List<Chat> allChats;
	
	/*
	 * Events
	 */
	private List<OnNewChatListener> onNewChatListeners;
	public static interface OnNewChatListener {
		public void onNewChat(Chat chat);
	}
	public void setOnNewChatListener(OnNewChatListener l) {
		this.onNewChatListeners.add(l);
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
	public void setOnDisconnectListeners(OnDisconnectListener l) {
		this.onDisconnectListeners.add(l);
	}

	private ChatsManager() {
		this.conManager = new IRCConnectionManager();
		
		this.allChats = new ArrayList<Chat>();
		
		/*
		 * Events
		 */
		this.onNewChatListeners = new CopyOnWriteArrayList<OnNewChatListener>();
		this.onConnectListeners = new CopyOnWriteArrayList<OnConnectListener>();
		this.onDisconnectListeners = new CopyOnWriteArrayList<OnDisconnectListener>();
		
		this.connectListeners();
	}
	
	private void connectListeners() {
		connectConnectionManagerListeners();
	}
	
	/**
	 * Connects the listeners of ConnectionManager. Separated because these need to be reconnected if connection resets.
	 */
	private void connectConnectionManagerListeners() {
		this.conManager.setOnMessageReceivedListener(new OnMessageReceivedListener() {
			@Override
			public void onMessageReceived(String channel, String sender, String login, String hostname, String message) {
				ChatsManager.this.addChat(new Chat(sender, message));
			}
		});
		this.conManager.setOnConnectListener(new ConnectionManager.OnConnectListener() {
			@Override
			public void onConnect() {
				for(OnConnectListener l : ChatsManager.this.onConnectListeners) {
					l.onConnect();
				}
			}
		});
		this.conManager.setOnDisconnectListener(new ConnectionManager.OnDisconnectListener() {
			@Override
			public void onDisconnect() {
				for(OnDisconnectListener l : ChatsManager.this.onDisconnectListeners) {
					l.onDisconnect();
				}
			}
		});
	}

	public static ChatsManager getInstance() {
		if (instance == null)
			instance = new ChatsManager();
		return instance;
	}

	/**
	 * Makes the connection connect
	 */
	public void connect() {
		this.conManager = new IRCConnectionManager();
		
		// Need to reconnect the ConnectionManager listeners
		this.connectConnectionManagerListeners();
		
		this.conManager.connect();
	}

	/**
	 * Makes the connection disconnect
	 */
	public void disconnect() {
		this.conManager.disconnect();
	}

	/**
	 * Adds a chat to the manager and sends it over the connection
	 * 
	 * @param chat
	 */
	public void sendChat(Chat chat) {
		this.addChat(chat);
		this.conManager.sendChat(chat.text);
	}
	
	/**
	 * Adds a chat to the manager
	 * 
	 * @param chat
	 */
	public void addChat(Chat chat) {
		this.allChats.add(chat);

		for(OnNewChatListener l : this.onNewChatListeners) {
			l.onNewChat(chat);
		}
	}

	/**
	 * Get all chats
	 * 
	 * @return
	 */
	public List<Chat> getChats() {
		return allChats;
	}
}
