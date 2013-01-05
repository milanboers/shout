package nu.shout.shout.chats;

public interface ChatsManagerObserver {
	public void onChatsManagerNewChat(Chat chat);
	public void onChatsManagerConnect();
	public void onChatsManagerDisconnect();
}
