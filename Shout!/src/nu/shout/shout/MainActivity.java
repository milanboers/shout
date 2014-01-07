package nu.shout.shout;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import nu.shout.shout.chat.ChatActivity;
import nu.shout.shout.chat.ChatService;
import nu.shout.shout.chat.ChatService.LocalBinder;
import nu.shout.shout.settings.SettingsActivity;

import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends SherlockActivity implements NicknameRegistrarListener {
	@SuppressWarnings("unused")
	private static final String TAG = "MainActivity";
	
	private EditText nicknameView;
	
	private ChatService chatService;
	private ServiceConnection chatServiceConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.nicknameView = (EditText) findViewById(R.id.main_nickname);
		
		Button b = (Button) findViewById(R.id.main_button_chat);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String nickname = MainActivity.this.nicknameView.getText().toString();
				
				NicknameRegistrar nr = new NicknameRegistrar(MainActivity.this.chatService, nickname);
				nr.addListener(MainActivity.this);
				nr.registerNick();
			}
		});
		
		setupService();
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		if(settings.getString("nickname", null) != null && settings.getString("password", null) != null)
			goToChat();
	}
	
	private void setupService() {
        Intent serviceIntent = new Intent(this, ChatService.class);
        startService(serviceIntent);
        
        this.chatServiceConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.v(TAG, "Service connected");
				ChatService.LocalBinder binder = (LocalBinder) service;
				MainActivity.this.chatService = binder.getService();
			}
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.v(TAG, "Service disconnected");
			}
        };
        bindService(serviceIntent, this.chatServiceConnection, BIND_AUTO_CREATE);
    }
	
	private void goToChat() {
		Intent intent = new Intent(MainActivity.this, ChatActivity.class);
		startActivity(intent);
		// Stop the activity so people can't go back
		MainActivity.this.finish();
	}
	
	@Override
	public void onDestroy() {
		Log.v(TAG, "Unbinding connection");
		unbindService(this.chatServiceConnection);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case R.id.menu_settings:
	    		Intent i = new Intent(this, SettingsActivity.class);
	    		startActivity(i);
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }

	@Override
	public void onNicknameRegistered(String nickname, String password) {
		Log.v(TAG, "Nickname registered " + password);
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		Editor ed = settings.edit();
		ed.putString("nickname", nickname);
		ed.putString("password", password);
		ed.commit();
		
		goToChat();
	}

	@Override
	public void onNicknameInUse() {
		Log.v(TAG, "Nickname in use");
	}

}
