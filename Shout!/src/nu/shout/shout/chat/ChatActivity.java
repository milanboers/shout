package nu.shout.shout.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.pircbotx.User;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import nu.shout.shout.R;
import nu.shout.shout.chat.ChatService.LocalBinder;
import nu.shout.shout.chat.box.ChatBox;
import nu.shout.shout.chat.items.Chat;
import nu.shout.shout.chat.items.Notice;
import nu.shout.shout.location.Building;
import nu.shout.shout.settings.SettingsActivity;
import android.os.Bundle;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ChatActivity extends SherlockFragmentActivity implements ChatServiceListener {
	@SuppressWarnings("unused")
	private static final String TAG = "ChatActivity";
	
	private EditText chatLine;
	private Button sendButton;
	private ChatBox chatBox;
	
	private ChatService chatService;
	private ServiceConnection chatServiceConnection;
	
	//private boolean busy;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_chat);
        
        this.chatLine = (EditText) findViewById(R.id.chatLine);
        this.sendButton = (Button) findViewById(R.id.sendButton);
        
        this.chatBox = (ChatBox) findViewById(R.id.chatBox);
        
        setupUI();
        
        setupService();
    }
    
    private void setupUI() {
        this.sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				send();
			}
		});
        
        this.chatLine.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					send();
					handled = true;
				}
				return handled;
			}
        });
    }
    
    private void setupService() {
        Intent serviceIntent = new Intent(this, ChatService.class);
        startService(serviceIntent);
        
        this.chatServiceConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.v(TAG, "Service connected");
				// Bind service
				ChatService.LocalBinder binder = (LocalBinder) service;
				ChatActivity.this.chatService = binder.getService();
				// Restore old chats
				for(Chat chat : ChatActivity.this.chatService.getMemory()) {
					ChatActivity.this.onMessage(chat);
				}
				// Add listener
				ChatActivity.this.chatService.addListener(ChatActivity.this);
				// Connect if not connected
				if(!ChatActivity.this.chatService.isConnected())
					ChatActivity.this.chatService.connect();
				// Update UI according to building it is currently in
				if(ChatActivity.this.chatService.getCurrentBuilding() != null)
					ChatActivity.this.onJoin(ChatActivity.this.chatService.getCurrentBuilding());
				
				supportInvalidateOptionsMenu();
			}
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.v(TAG, "Service disconnected");
			}
        };
        bindService(serviceIntent, this.chatServiceConnection, BIND_AUTO_CREATE);
    }
    
    /**
     * Sends the current line in the chatBox
     */
    private void send() {
		this.chatService.sendMessage(this.chatLine.getText().toString());
		
		this.chatLine.setText("");
    }
    
    /**
     * Get list of users in the channel
     */
    private void showUsers() {
    	// Null = not in channel
    	if(this.chatService.getCurrentBuilding() == null) {
    		this.chatBox.addNotice(new Notice(null, getString(R.string.error_notinchannel)));
    		return;
    	}
    	
    	Set<User> users = this.chatService.getUsers();
    	List<String> usernames = new ArrayList<String>(users.size());
    	for(User user : users)
    		usernames.add(user.getNick());
    	
    	ChatUsersDialog f = new ChatUsersDialog();
    	f.setUsernames(usernames.toArray(new String[usernames.size()]));
    	f.setChannelName(this.chatService.getCurrentBuilding().name);
    	f.show(getSupportFragmentManager(), "users");
    }
    
    @Override
    protected void onDestroy() {
    	unbindService(this.chatServiceConnection);
		super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.activity_chat, menu);
        Log.v(TAG, "Creating options menu");
        
        if(this.chatService != null && this.chatService.getCurrentBuilding() == null)
        {
        	MenuItem usersSelect = menu.findItem(R.id.menu_users);
        	usersSelect.setVisible(false);
        }
        
        MenuItem toggleConnect = menu.findItem(R.id.menu_connect_toggle);
        toggleConnect.setTitle(R.string.menu_connect);
        
        if(this.chatService != null)
        {
        	setSupportProgressBarIndeterminateVisibility(this.chatService.isBusy());
        	
        	if(this.chatService.isBusy()) {
        		toggleConnect.setVisible(false);
        	} else if(this.chatService.isConnected()) {
	        	toggleConnect.setTitle(R.string.menu_disconnect);
	        } else {
	        	toggleConnect.setTitle(R.string.menu_connect);
	        }
        }
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case R.id.menu_settings:
	    		Intent i = new Intent(this, SettingsActivity.class);
	    		startActivity(i);
	    		return true;
    		case R.id.menu_connect_toggle:
    			if(this.chatService != null)
    				if(this.chatService.isConnected())
    					this.chatService.disconnect();
    				else
    					this.chatService.connect();
				return true;
    		case R.id.menu_users:
				showUsers();
				return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
    
	@Override
	public void onMessage(Chat chat) {
		this.chatBox.addChat(chat);
	}
	
	@Override
	public void onConnect() {
		// Update UI
		/*runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ChatActivity.this.setBusy(false);
			}
		});*/
		this.chatBox.addNotice(new Notice(null, getString(R.string.notice_connected)));
		supportInvalidateOptionsMenu();
	}

	// IN THREAD
	@Override
	public void onDisconnect() {
		// Update UI
		/*runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ChatActivity.this.setBusy(false);
			}
		});*/
		this.chatBox.addNotice(new Notice(null, getString(R.string.notice_disconnected)));
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onLeave() {
		this.chatBox.addNotice(new Notice(null, getString(R.string.error_nobuildings)));
    	setTitle(R.string.title_activity_chat);
	}

	@Override
	public void onJoin(Building building) {
		setTitle(building.shortcut + " - " + building.name);
		this.chatBox.addNotice(new Notice(null, getString(R.string.notice_joined_channel) + " " + building.name));
	}

	
	@Override
	public void onError(String message) {
		ChatActivity.this.chatBox.addNotice(new Notice(null, message));
	}

	@Override
	public void onStartConnecting() {
		this.chatBox.addNotice(new Notice(null, getString(R.string.notice_connecting)));
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onStartDisconnecting() {
		this.chatBox.addNotice(new Notice(null, getString(R.string.notice_disconnecting)));
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onNicknameInUse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNotice(Notice notice) {
		// TODO Auto-generated method stub
		
	}
}
