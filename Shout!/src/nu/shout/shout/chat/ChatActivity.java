package nu.shout.shout.chat;

import java.io.IOException;
import java.util.List;

import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import nu.shout.shout.IRCListener;
import nu.shout.shout.IRCListenerAdapter;
import nu.shout.shout.R;
import nu.shout.shout.irc.IRCConnection;
import nu.shout.shout.location.Building;
import nu.shout.shout.location.BuildingFetcher;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class ChatActivity extends Activity implements IRCListener, LocationListener {
	@SuppressWarnings("unused")
	private static final String TAG = "ChatActivity";
	
	// Min. time between location updates (in milliseconds)
	private static final int TIME_BETWEEN_LOC = 5000;
	// Min. distance between location updates (in meters)
	private static final int DIST_BETWEEN_LOC = 1;
	
	private IRCConnection irc;
	
	private EditText chatLine;
	private TextView chatBox;
	private Button sendButton;
	
	private LocationManager lm;
	
	private BuildingFetcher bf;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        this.bf = new BuildingFetcher();
        
        this.irc = new IRCConnection(getIntent().getExtras().getString("nickname"));
        IRCListenerAdapter adapter = new IRCListenerAdapter(this);
        this.irc.getListenerManager().addListener(adapter);
        
        this.chatLine = (EditText) findViewById(R.id.chatLine);
        this.chatBox = (TextView) findViewById(R.id.chatBox);
        this.sendButton = (Button) findViewById(R.id.sendButton);
        
        this.lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = this.lm.getBestProvider(new Criteria(), false);
        this.lm.requestLocationUpdates(provider, TIME_BETWEEN_LOC, DIST_BETWEEN_LOC, this);
        // Pick last known location
        this.onLocationChanged(this.lm.getLastKnownLocation(provider));
        
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
        getMenuInflater().inflate(R.menu.activity_chat, menu);
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
				ChatActivity.this.chatBox.append("\n" + text);
			}
		});
	}
	
	public void addChatToBox(final String name, final String text) {
		runOnUiThread(new Runnable() {
			public void run(){ 
				ChatActivity.this.chatBox.append("\n<" + name + "> " + text);
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
		this.irc.joinChannel(this.irc.getChannel());
	}

	@Override
	public void onDisconnect(DisconnectEvent<IRCConnection> event) {
		addNoticeToBox("Disconnected!");
	}

	@Override
	public void onLocationChanged(final Location loc) {
		Log.v(TAG, "Lat: " + loc.getLatitude() + " lon: " + loc.getLongitude());
		
		AsyncTask<Void, Void, List<Building>> locTask = new AsyncTask<Void, Void, List<Building>>() {
			@Override
			protected List<Building> doInBackground(Void... arg0) {
				try {
					return ChatActivity.this.bf.getBuildings(loc);
				} catch (IOException e) {
					Log.v(TAG, "IOException");
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(List<Building> buildings) {
				if(buildings == null) {
				}
				else if(buildings.size() == 0) {
					// TODO: misschie niet elke keer weergeven maar 1 keer?
					ChatActivity.this.irc.partAllChannels();
					ChatActivity.this.addNoticeToBox(getString(R.string.error_nobuildings));
				}
				else if(ChatActivity.this.irc.getChannel() != buildings.get(0).ircroom) {
					ChatActivity.this.irc.joinChannel(buildings.get(0).ircroom);
				}
			}
		};
		locTask.execute();
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
