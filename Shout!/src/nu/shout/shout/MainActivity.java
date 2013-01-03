package nu.shout.shout;

import java.util.Observable;
import java.util.Observer;

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

public class MainActivity extends Activity implements Observer {
	private static final String TAG = "MainActivity";
	
	private ChatsManager chatsManager;
	private IRCManagerTask connectionManager;
	
	private EditText chatLine;
	private TextView chatBox;
	private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.chatsManager = ChatsManager.getInstance();
        this.connectionManager = IRCManagerTask.getInstance();
        
        this.chatLine = (EditText) findViewById(R.id.chatLine);
        this.chatBox = (TextView) findViewById(R.id.chatBox);
        this.sendButton = (Button) findViewById(R.id.sendButton);
        
        setupUI();
        
        // Register yourself to ChatsManager updates
        ChatsManager.getInstance().addObserver(this);
        
        // Add existing chats to chatbox
        for(Chat chat : ChatsManager.getInstance().getChats()) {
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
    
    /**
     * Fired when send button is hit. Sends the current line in the chatLine.
     */
    private void send() {
		this.chatsManager.addChat(new Chat("me", this.chatLine.getText().toString()));
		this.chatLine.setText("");
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
    			connectionManager.connect();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }

    /**
     * Called when ChatsManager is updated
     */
	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 instanceof ChatsManager) {
			Chat lastChat = this.chatsManager.getLastChat();
			addChatToBox(lastChat);
		}
	}
	
	/**
	 * Adds a chat to the chatbox
	 * @param chat chat to be added
	 */
	public void addChatToBox(Chat chat) {
		this.chatBox.append("\n<" + chat.nickname + "> " + chat.text);
	}
    
}
