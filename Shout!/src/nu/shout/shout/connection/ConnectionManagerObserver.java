package nu.shout.shout.connection;

public interface ConnectionManagerObserver {
	public void onConManMessage(String channel, String sender, String login, String hostname, String message);
	public void onConManConnect();
	public void onConManDisconnect();
}
