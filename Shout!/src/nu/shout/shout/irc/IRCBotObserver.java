package nu.shout.shout.irc;

public interface IRCBotObserver {
	public void onMessage(String channel, String sender, String login, String hostname, String message);
	public void onConnect();
	public void onDisconnect();
}
