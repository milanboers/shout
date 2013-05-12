package nu.shout.shout.chat;

import java.io.IOException;
import java.util.List;

import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import nu.shout.shout.R;
import nu.shout.shout.chat.box.ChatBox;
import nu.shout.shout.irc.IRCConnection;
import nu.shout.shout.irc.IRCListener;
import nu.shout.shout.irc.IRCListenerAdapter;
import nu.shout.shout.location.Building;
import nu.shout.shout.location.BuildingFetcher;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ChatActivity extends SherlockActivity implements IRCListener, LocationListener {
	@SuppressWarnings("unused")
	private static final String TAG = "ChatActivity";
	
	// Min. time between location updates (in milliseconds)
	private static final int TIME_BETWEEN_LOC = 5000;
	// Min. distance between location updates (in meters)
	private static final int DIST_BETWEEN_LOC = 1;
	
	private IRCConnection irc;
	
	private EditText chatLine;
	private Button sendButton;
	private ChatBox chatBox;
	
	private LocationManager lm;
	
	private BuildingFetcher bf;
	
	private SharedPreferences prefs;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_chat);
        
        this.bf = new BuildingFetcher();
        
        this.prefs = getSharedPreferences("nu.shout.shout", Context.MODE_PRIVATE);
        
        this.irc = new IRCConnection(getIntent().getExtras().getString("nickname"));
        IRCListenerAdapter adapter = new IRCListenerAdapter(this);
        this.irc.getListenerManager().addListener(adapter);
        
        this.chatLine = (EditText) findViewById(R.id.chatLine);
        this.sendButton = (Button) findViewById(R.id.sendButton);
        
        this.chatBox = (ChatBox) findViewById(R.id.chatBox);
        
        this.lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        this.lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_BETWEEN_LOC, DIST_BETWEEN_LOC, this);
        // Pick last known location
        this.onLocationChanged(this.lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        
        setupUI();
        
        connect();
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.activity_chat, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.menu_connect:
    			connect();
    			return true;
    		case R.id.menu_disconnect:
    			disconnect();
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
    	this.chatBox.addChat("me", this.chatLine.getText().toString());
		this.chatLine.setText("");
    }
    
    public void connect() {
    	if(this.irc.isConnected()) {
    		this.chatBox.addNotice(getString(R.string.notice_already_connected));
    		return;
    	}
    	setProgressBarIndeterminateVisibility(true);
		this.chatBox.addNotice(getString(R.string.notice_connecting));
		this.irc.connect();
    }
    
    public void disconnect() {
    	if(!this.irc.isConnected()) {
    		this.chatBox.addNotice(getString(R.string.notice_not_connected));
    		return;
    	}
    	setProgressBarIndeterminateVisibility(true);
		this.chatBox.addNotice(getString(R.string.notice_disconnecting));
		this.irc.disconnect();
    }

    // IN THREAD
	@Override
	public void onMessage(final MessageEvent<IRCConnection> event) {
		this.chatBox.addChat(event.getUser().getNick(), event.getMessage());
	}

	// IN THREAD
	@Override
	public void onConnect(ConnectEvent<IRCConnection> event) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setProgressBarIndeterminateVisibility(false);
			}
		});
		this.chatBox.addNotice("Connected!");
	}

	// IN THREAD
	@Override
	public void onDisconnect(DisconnectEvent<IRCConnection> event) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setProgressBarIndeterminateVisibility(false);
			}
		});
		this.chatBox.addNotice("Disconnected!");
	}

	@Override
	public void onLocationChanged(final Location loc) {
		Log.v(TAG, "DEBUG: New location lat " + loc.getLatitude() + " lon " + loc.getLongitude());
		if(this.prefs.getBoolean("debug", false))
			this.chatBox.addNotice("DEBUG: New location lat " + loc.getLatitude() + " lon " + loc.getLongitude());
		
		new AsyncTask<Void, Void, List<Building>>() {
			@Override
			protected List<Building> doInBackground(Void... arg0) {
				try {
					return ChatActivity.this.bf.getBuildings(loc);
				} catch (IOException e) {
					// TODO: afvangen
					Log.v(TAG, "IOException");
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(List<Building> buildings) {
				if(buildings == null) {
				} else if(buildings.size() == 0) {
					// TODO: misschien niet elke keer weergeven maar 1 keer?
					ChatActivity.this.irc.partAllChannels();
					ChatActivity.this.chatBox.addNotice(getString(R.string.error_nobuildings));
				} else if(!buildings.get(0).ircroom.equals(ChatActivity.this.irc.getChannel())) {
					ChatActivity.this.irc.joinChannel(buildings.get(0).ircroom);
					ChatActivity.this.chatBox.addNotice("Joined channel " + buildings.get(0).ircroom);
				}
			}
		}.execute();
	}

	@Override
	public void onProviderDisabled(String arg0) {
	}

	@Override
	public void onProviderEnabled(String arg0) {
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}
}
