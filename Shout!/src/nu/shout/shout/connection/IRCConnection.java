package nu.shout.shout.connection;

import java.io.IOException;

import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;

import android.os.AsyncTask;
import android.util.Log;

public class IRCConnection extends PircBotX {
	private static final String TAG = "IRCConnection";
	
	// The current channel (Shout will only connect to one channel)
	private String channel;

	public IRCConnection() {
		super();
		
		this.setName("ShoutUser");
	}
	
	public void sendMessage(String message) {
		this.sendMessage(channel, message);
	}
	
	public void connect() {
		AsyncTask<Void, Void, Void> connectTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					Log.v(TAG, "Connecting");
					IRCConnection.this.connect("shout.nu");
					Log.v(TAG, "Connected");
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
				return null;
			}
		};
		connectTask.execute();
	}

	@Override
	public void joinChannel(String channel) {
		super.joinChannel(channel);
		this.channel = channel;
	}
	
	/*
	@Override
	public void connect() {
		// TODO: IN BACKGROUND
		this.bot.connect("shout.nu", 6667);
		this.bot.joinChannel("#koning");
	}

	@Override
	public void disconnect() {
		//this.session.close("I won't be back");
	}

	@Override
	public void sendChat(String message) {
		//this.session.sayChannel(this.session.getChannel("#koning"), message);
	}*/
}
