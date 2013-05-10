package nu.shout.shout;

import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import nu.shout.shout.connection.IRCConnection;

import android.os.Bundle;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity implements IRCListener {
	@SuppressWarnings("unused")
	private static final String TAG = "MainActivity";
	
	private IRCConnection irc;
	
	private EditText chatLine;
	private TextView chatBox;
	private Button sendButton;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.irc = new IRCConnection();
        IRCListenerAdapter adapter = new IRCListenerAdapter(this);
        this.irc.getListenerManager().addListener(adapter);
        
        this.chatLine = (EditText) findViewById(R.id.chatLine);
        this.chatBox = (TextView) findViewById(R.id.chatBox);
        this.sendButton = (Button) findViewById(R.id.sendButton);
        
        setupUI();
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
    			this.irc.connect();
    			return true;
    		case R.id.menu_disconnect:
    			this.addNoticeToBox(getString(R.string.notice_disconnecting));
    			this.irc.disconnect();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }

    /**
     * Fired when send button is hit. Sends the current line in the chatLine.
     */
    private void send() {
    	this.irc.sendMessage(this.chatLine.getText().toString());
    	this.addChatToBox("me", this.chatLine.getText().toString());
		this.chatLine.setText("");
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
	
	public void addChatToBox(final String name, final String text) {
		runOnUiThread(new Runnable() {
			public void run(){ 
				MainActivity.this.chatBox.append("\n<" + name + "> " + text);
			}
		});
	}

	@Override
	public void onMessage(final MessageEvent<IRCConnection> event) {
		addChatToBox(event.getUser().getNick(), event.getMessage());
	}

	@Override
	public void onConnect(ConnectEvent<IRCConnection> event) {
		addNoticeToBox("Connected!");
		this.irc.joinChannel("#koning");
	}

	@Override
	public void onDisconnect(DisconnectEvent<IRCConnection> event) {
		addNoticeToBox("Disconnected!");
	}
}
