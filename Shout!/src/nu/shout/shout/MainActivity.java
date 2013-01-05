package nu.shout.shout;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import nu.shout.shout.chats.Chat;
import nu.shout.shout.chats.ChatsManager;
import nu.shout.shout.chats.ChatsManager.OnConnectListener;
import nu.shout.shout.chats.ChatsManager.OnDisconnectListener;
import nu.shout.shout.chats.ChatsManager.OnNewChatListener;

import android.os.Bundle;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	
	private ChatsManager chatsManager;
	
	private EditText chatLine;
	private TextView chatBox;
	private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.chatsManager = ChatsManager.getInstance();
        
        this.chatLine = (EditText) findViewById(R.id.chatLine);
        this.chatBox = (TextView) findViewById(R.id.chatBox);
        this.sendButton = (Button) findViewById(R.id.sendButton);
        
        setupUI();
        
        connectListeners();
        
        // Add existing chats to chatbox
        for(Chat chat : this.chatsManager.getChats()) {
        	this.addChatToBox(chat);
        }
    }
    
    private void setupUI() {
    	// Make chatbox scrollable
        this.chatBox.setMovementMethod(new ScrollingMovementMethod());
        
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

    private void connectListeners() {
    	this.chatsManager.setOnNewChatListener(new OnNewChatListener() {
			@Override
			public void onNewChat(Chat chat) {
				MainActivity.this.addChatToBox(chat);
			}
    	});
    	this.chatsManager.setOnConnectListener(new OnConnectListener() {
			@Override
			public void onConnect() {
				MainActivity.this.addNoticeToBox(getString(R.string.notice_connected));
			}
    	});
    	this.chatsManager.setOnDisconnectListeners(new OnDisconnectListener() {
			@Override
			public void onDisconnect() {
				MainActivity.this.addNoticeToBox(getString(R.string.notice_disconnected));
			}
    	});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.menu_connect:
    			this.addNoticeToBox(getString(R.string.notice_connecting));
    			chatsManager.connect();
    			return true;
    		case R.id.menu_disconnect:
    			this.addNoticeToBox(getString(R.string.notice_disconnecting));
    			chatsManager.disconnect();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }

    /**
     * Fired when send button is hit. Sends the current line in the chatLine.
     */
    private void send() {
		this.chatsManager.sendChat(new Chat("me", this.chatLine.getText().toString()));
		this.chatLine.setText("");
    }
	
	/**
	 * Adds a chat to the chatbox
	 * @param chat chat to be added
	 */
	public void addChatToBox(final Chat chat) {
		runOnUiThread(new Runnable() {
			public void run() {
				MainActivity.this.chatBox.append("\n<" + chat.nickname + "> " + chat.text);
			}
		});
	}
    
	/**
	 * Adds a text to the chatbox
	 * @param text
	 */
	public void addNoticeToBox(final String text) {
		runOnUiThread(new Runnable() {
			public void run() {
				MainActivity.this.chatBox.append("\n" + text);
			}
		});
	}
}
