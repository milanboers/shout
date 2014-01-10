package nu.shout.shout.chat.items;


public class Chat extends Item {
	public long timestamp;
	public String nickname;
	public String message;
	
	public Chat(long timestamp, String nickname, String message) {
		this.timestamp = timestamp;
		this.nickname = nickname;
		this.message = message;
	}
}
