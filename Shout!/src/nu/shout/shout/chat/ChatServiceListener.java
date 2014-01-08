package nu.shout.shout.chat;

import nu.shout.shout.chat.items.Chat;
import nu.shout.shout.chat.items.Notice;
import nu.shout.shout.location.Building;

public interface ChatServiceListener {
	public void onLeave();
	public void onJoin(Building building);
	public void onMessage(Chat chat);
	public void onNotice(Notice notice);
	public void onError(String message);
	public void onStartConnecting();
	public void onStartDisconnecting();
	public void onConnect();
	public void onDisconnect();
	public void onNicknameInUse();
}
