package nu.shout.shout;

public interface NicknameRegistrarListener {
	public void onNicknameRegistered(String nickname, String password);
	public void onNicknameInUse();
}
