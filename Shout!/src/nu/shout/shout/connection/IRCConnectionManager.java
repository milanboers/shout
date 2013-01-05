package nu.shout.shout.connection;

import java.io.IOException;

import nu.shout.shout.irc.IRCBot;
import nu.shout.shout.irc.IRCBotObserver;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import android.util.Log;

public class IRCConnectionManager extends ConnectionManager implements IRCBotObserver {
	private static final String TAG = "IRCManagerTask";

	private ConnectionManagerObserver observer;
	private IRCBot bot;

	public IRCConnectionManager(ConnectionManagerObserver observer) {
		this.observer = observer;
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

	@Override
	public void onIrcBotMessage(String channel, String sender, String login, String hostname, String message) {
		this.observer.onConManMessage(channel, sender, login, hostname, message);
	}

	@Override
	public void onIrcBotConnect() {
		this.observer.onConManConnect();
	}

	@Override
	public void onIrcBotDisconnect() {
		this.observer.onConManDisconnect();
	}
}
