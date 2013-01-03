package nu.shout.shout;

import android.os.AsyncTask;
import android.util.Log;
import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.events.IRCEvent;
import jerklib.listeners.IRCEventListener;

public class IRCManagerTask extends AsyncTask<Void, String, Void> implements IRCEventListener {
	private static final String TAG = "IRCManager";
	
	private static IRCManagerTask instance;
	
	private ChatsManager chatsManager;
	
	private IRCManagerTask() {
		this.chatsManager = ChatsManager.getInstance();
	}
	
	public static IRCManagerTask getInstance() {
		if(instance == null) instance = new IRCManagerTask();
		return instance;
	}
	
	public void connect() {
		this.execute();
	}
	
	public void receiveEvent(IRCEvent e) {
		Log.v(TAG, e.getType() + " : " + e.getRawEventData());
		publishProgress(e.getType() + " : " + e.getRawEventData());
	}
	
	public void onProgressUpdate(String... message) {
		this.chatsManager.addChat(new Chat("Server", message[0]));
	}

	@Override
	protected Void doInBackground(Void... params) {
		ConnectionManager conman = new ConnectionManager(new Profile("testbot"));
		conman.requestConnection("irc.freenode.net").addIRCEventListener(this);
		return null;
	}
}
