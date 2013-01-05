package nu.shout.shout.chats;

import android.os.AsyncTask;

public abstract class ConnectionManager extends AsyncTask<Void, Void, Void> {
	public abstract void connect();
	public abstract void disconnect();
	public abstract void sendChat(String message);
}
