package nu.shout.shout.chats;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;

import android.util.Log;

/**
 * Keeps track of all received and sent chats, and the connection
 * 
 * @author Milan Boers
 * 
 */
public class ChatsManager extends Observable {
	private static final String TAG = "ChatsManager";

	private static ChatsManager instance;

	private ConnectionManager conManager;

	private List<Chat> allChats;
	private Queue<Chat> newChats;

	private ChatsManager() {
		this.conManager = new IRCConnectionManager(this);

		this.allChats = new ArrayList<Chat>();
		this.newChats = new LinkedList<Chat>();
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
		this.conManager = new IRCConnectionManager(this);
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
		this.newChats.offer(chat);

		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Get all chats
	 * 
	 * @return
	 */
	public List<Chat> getChats() {
		return allChats;
	}

	/**
	 * Get new chats
	 * 
	 * @return
	 */
	public List<Chat> getNewChats() {
		List<Chat> fb = new ArrayList<Chat>();

		Chat nextChat = newChats.poll();
		while (nextChat != null) {
			fb.add(nextChat);
			nextChat = newChats.poll();
		}

		return fb;
	}

	public void onConnect() {

	}

	public void onDisconnect() {

	}
}
