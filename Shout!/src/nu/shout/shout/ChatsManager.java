package nu.shout.shout;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import android.util.Log;

public class ChatsManager extends Observable {
	private static final String TAG = "ChatsManager";
	
	private static ChatsManager instance;
	
	private List<Chat> chats;
	
	private ChatsManager() {
		chats = new ArrayList<Chat>();
	}
	
	public static ChatsManager getInstance() {
		if(instance == null) instance = new ChatsManager();
		return instance;
	}
	
	public void addChat(Chat chat) {
		chats.add(chat);
		
		this.setChanged();
		this.notifyObservers();
	}
	
	public List<Chat> getChats() {
		return chats;
	}
	
	public Chat getLastChat() {
		return chats.get(chats.size() - 1);
	}
}
