/* This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *   * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

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
import nu.shout.shout.chat.items.Error;
import nu.shout.shout.chat.items.Notice;
import nu.shout.shout.chat.items.Report;
import nu.shout.shout.irc.NotInChannelException;
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

	public static boolean running = false;

	private EditText chatLine;
	private Button sendButton;
	private ChatBox chatBox;

	private ChatService chatService;
	private ServiceConnection chatServiceConnection;

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

    @Override
    protected void onResume() {
    	running = true;
    	super.onResume();
    }

    @Override
    protected void onPause() {
    	running = false;
    	super.onPause();
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
				if(ChatActivity.this.chatService.getCurrentBuilding() != null) {
					ChatActivity.this.setTitleToBuilding(ChatActivity.this.chatService.getCurrentBuilding());
				}

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
    protected void send() {
		try {
			this.chatService.sendMessage(this.chatLine.getText().toString());
			this.chatLine.setText("");
		} catch (NotInChannelException e) {
			this.chatBox.addItem(new Error(getString(R.string.error_notinchannel)));
		}
    }

    /**
     * Get list of users in the channel
     */
    protected void showUsers() {
    	// Null = not in channel
    	if(this.chatService.getCurrentBuilding() == null) {
    		this.chatBox.addItem(new Error(getString(R.string.error_notinchannel)));
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

    protected void setTitleToBuilding(Building building) {
		setTitle(building.shortcut + " - " + building.name);
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
        	Log.v(TAG, "isbusy: " + this.chatService.isBusy());
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
    				if(this.chatService.isConnected()) {
    					this.chatService.disconnect();
    				} else {
    					this.chatService.connect();
    				}
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
		this.chatBox.addItem(chat);
	}

	@Override
	public void onConnect() {
		this.chatBox.addItem(new Report(getString(R.string.report_connected)));
		supportInvalidateOptionsMenu();
	}

	// IN THREAD
	@Override
	public void onDisconnect() {
		Log.v(TAG, "3");
		this.chatBox.addItem(new Report(getString(R.string.report_disconnected)));
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onLeave() {
		this.chatBox.addItem(new Error(getString(R.string.error_nobuildings)));
    	setTitle(R.string.title_activity_chat);
	}

	@Override
	public void onJoining(Building building) {
		setTitleToBuilding(building);
	}


	@Override
	public void onErrorBuildingFetch() {
		this.chatBox.addItem(new Report(getString(R.string.error_buildingfetch)));
	}

	@Override
	public void onStartConnecting() {
		this.chatBox.addItem(new Report(getString(R.string.report_connecting)));
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onStartDisconnecting() {
		this.chatBox.addItem(new Report(getString(R.string.report_disconnecting)));
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onErrorNicknameInUse() {
		this.chatBox.addItem(new Error(getString(R.string.error_nickname_in_use)));
	}

	@Override
	public void onErrorCouldNotConnect() {
		this.chatBox.addItem(new Error(getString(R.string.error_could_not_connect)));
	}

	@Override
	public void onErrorUnknown(Exception e) {
		this.chatBox.addItem(new Error(getString(R.string.error_unknown)));
	}

	@Override
	public void onNotice(Notice notice) {
		//
	}

	@Override
	public void onIssueProviderDisabled() {
		this.chatBox.addItem(new Report(getString(R.string.report_provider_disabled)));
	}

	@Override
	public void onUserJoined(String nickname) {
		if(nickname.equals(this.chatService.getNick())) {
			ChatActivity.this.chatBox.addItem(new Report(String.format(getString(R.string.report_you_joined), ChatActivity.this.chatService.getCurrentBuilding().name)));
		} else {
			this.chatBox.addItem(
					new Report(
							String.format(getString(R.string.report_user_joined), nickname, this.chatService.getCurrentBuilding().name)));
		}
	}

	@Override
	public void onUserParted(String nickname) {
		if(!nickname.equals(this.chatService.getNick())) {
			this.chatBox.addItem(
				new Report(
					String.format(getString(R.string.report_user_parted), nickname, this.chatService.getCurrentBuilding().name)));
		}
	}
}
