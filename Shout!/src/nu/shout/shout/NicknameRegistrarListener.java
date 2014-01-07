package nu.shout.shout;

public interface NicknameRegistrarListener {
	public void onNicknameRegistered(String password);
	public void onNicknameInUse();
}
