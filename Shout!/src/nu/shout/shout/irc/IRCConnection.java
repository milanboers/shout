package nu.shout.shout.irc;

import java.io.IOException;

import org.pircbotx.Channel;
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
		// TODO: wat als channel == null?
		this.sendMessage(channel, message);
	}
	
	public void connect() {
		AsyncTask<Void, Void, Void> connectTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					Log.v(TAG, "Connecting");
					IRCConnection.this.connect("shout.nu");
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
			
			@Override
			protected void onPostExecute(Void v) {
				if(IRCConnection.this.channel != null)
					IRCConnection.this.joinChannel(IRCConnection.this.channel);
			}
		};
		connectTask.execute();
	}

	@Override
	public void joinChannel(String channel) {
		if(!this.getChannelsNames().contains(channel)) {
			for(Channel c : this.getChannels())
				this.partChannel(c);
			super.joinChannel(channel);
			this.channel = channel;
			Log.v(TAG, "Changed channel to " + channel);
		}
	}
	
	public String getChannel() {
		return this.channel;
	}
}
