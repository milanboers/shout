package nu.shout.shout.chat;

import nu.shout.shout.location.Building;

public interface ChatServiceListener {
	public void onLeave();
	public void onJoin(Building building);
	public void onMessage(String nickname, String message);
	public void onError(String message);
	public void onStartConnecting();
	public void onStartDisconnecting();
	public void onConnect();
	public void onDisconnect();
}
