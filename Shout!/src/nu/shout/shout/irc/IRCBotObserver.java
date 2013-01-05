package nu.shout.shout.irc;

public interface IRCBotObserver {
	public void onIrcBotMessage(String channel, String sender, String login, String hostname, String message);
	public void onIrcBotConnect();
	public void onIrcBotDisconnect();
}
