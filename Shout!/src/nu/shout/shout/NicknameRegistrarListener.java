package nu.shout.shout;

public interface NicknameRegistrarListener {
	public void onNicknameRegistered(String nickname, String password);
	// Errors
	public void onErrorNicknameInUse();
	public void onErrorCouldNotConnect();
	public void onErrorUnknown();
}
