package nu.shout.shout.chat.items;

public class Notice extends Item {
	public String nickname;
	public String message;
	
	public Notice(String nickname, String message) {
		this.nickname = nickname;
		this.message = message;
	}
}
