package nu.shout.shout.chats;

import java.io.IOException;

import nu.shout.shout.irc.IRCBot;
import nu.shout.shout.irc.IRCBotObserver;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import android.os.AsyncTask;
import android.util.Log;

public class IRCConnectionManager extends ConnectionManager implements IRCBotObserver {
	private static final String TAG = "IRCManagerTask";

	private ChatsManager chatsManager;
	private IRCBot bot;

	public IRCConnectionManager(ChatsManager chatsManager) {
		this.chatsManager = chatsManager;
		this.bot = new IRCBot(this);
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

	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		this.chatsManager.addChat(new Chat(sender, message));
	}

	@Override
	public void onConnect() {
		this.chatsManager.onConnect();
	}

	@Override
	public void onDisconnect() {
		this.chatsManager.onDisconnect();
	}
}
