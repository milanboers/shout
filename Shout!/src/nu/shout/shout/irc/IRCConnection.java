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
	private String channelName;

	public IRCConnection(String nickname) {
		super();
		
		this.setName(nickname);
		this.setAutoReconnect(true);
	}
	
	public void sendMessage(String message) {
		// TODO: wat als channel == null?
		this.sendMessage(channelName, message);
	}
	
	public void connect() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					Log.v(TAG, "Connecting");
					// TODO: ergens hardcoden
					// TODO: geeft nullpointerexception als niet kan connecten
					IRCConnection.this.connect("server.shout.nu");
				} catch (NickAlreadyInUseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IrcException e) {
					// You have not registered
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullPointerException e) {
					// TODO: kon niet connecten
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void v) {
				if(IRCConnection.this.channelName != null)
					IRCConnection.this.joinChannel(IRCConnection.this.channelName);
			}
		}.execute();
	}
	
	public void partAllChannels() {
		for(Channel c : this.getChannels())
			this.partChannel(c);
		this.channelName = null;
	}

	public void joinChannel(String channelName) {
		this.partAllChannels();
		super.joinChannel(channelName);
		this.channelName = channelName;
	}
	
	public Channel getChannel() {
		for(Channel c : this.getChannels())
		{
			if(c.getName().equalsIgnoreCase(this.channelName))
				return c;
		}
		return null;
	}
}
