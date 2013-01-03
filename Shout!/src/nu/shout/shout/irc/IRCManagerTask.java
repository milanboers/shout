package nu.shout.shout.irc;

import java.io.IOException;

import nu.shout.shout.Chat;
import nu.shout.shout.ChatsManager;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import android.os.AsyncTask;
import android.util.Log;

public class IRCManagerTask extends AsyncTask<Void, String, Void> {
	private static final String TAG = "IRCManagerTask";
	
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
	
	public void progress(String message) {
		this.publishProgress(message);
	}
	
	public void onProgressUpdate(String... message) {
		this.chatsManager.addChat(new Chat("Server", message[0]));
	}

	@Override
	protected Void doInBackground(Void... params) {
		IRCBot bot = new IRCBot(this);
		try {
			bot.connect("irc.freenode.net");
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
		bot.joinChannel("##pytest");
		return null;
	}
}
