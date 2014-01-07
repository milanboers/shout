package nu.shout.shout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.util.Log;
import nu.shout.shout.chat.ChatService;
import nu.shout.shout.chat.ChatServiceListener;
import nu.shout.shout.location.Building;

public class NicknameRegistrar implements ChatServiceListener {
	@SuppressWarnings("unused")
	private static final String TAG = "NicknameRegistrar";
	
	private ChatService chatService;
	
	private List<NicknameRegistrarListener> listeners = new ArrayList<NicknameRegistrarListener>();
	
	private String password = "p" + UUID.randomUUID().toString().replaceAll("\\-", "").substring(0, 25);
	private String nickname;
	
	public NicknameRegistrar(ChatService chatService, String nickname) {
		this.chatService = chatService;
		this.nickname = nickname;
		// Add listener
		this.chatService.addListener(this);
	}
	
	public void addListener(NicknameRegistrarListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Registers the current nickname to nickserv
	 * @return
	 */
	public void registerNick() {
		if(this.chatService.isConnected()) {
			Log.v(TAG, "ERROR: Was already connected");
			onConnect();
		}
		
		this.chatService.connect(this.nickname);
	}

	@Override
	public void onLeave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onJoin(Building building) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(String nickname, String message) {
	}

	@Override
	public void onError(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartConnecting() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartDisconnecting() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnect() {
		Log.v(TAG, "Connected");
		Log.v(TAG, "sending nickserv message " + this.password);
		this.chatService.sendMessage("NickServ", "register " + this.password);
	}

	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNicknameInUse() {
		for(NicknameRegistrarListener l : this.listeners) {
			l.onNicknameInUse();
		}
	}

	@Override
	public void onNotice(String nickname, String notice) {
		Log.v(TAG, "FROM: " + nickname);
		Log.v(TAG, notice);
		if(nickname.equals("NickServ"))
		{
			if(notice.contains("already registered")) {
				for(NicknameRegistrarListener l : this.listeners) {
					l.onNicknameInUse();
				}
			} else if(notice.contains("registered and protected")) {
			} else if(notice.contains("registered")) {
				for(NicknameRegistrarListener l : this.listeners) {
					l.onNicknameRegistered(this.nickname, this.password);
				}
			}
		}
	}
	
}
