package nu.shout.shout;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import nu.shout.shout.chat.ChatActivity;
import nu.shout.shout.settings.SettingsActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends SherlockActivity {
	@SuppressWarnings("unused")
	private static final String TAG = "MainActivity";
	
	private EditText nicknameView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.nicknameView = (EditText) findViewById(R.id.main_nickname);
		
		Button b = (Button) findViewById(R.id.main_button_chat);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, ChatActivity.class);
				Log.v(TAG, MainActivity.this.nicknameView.getText().toString());
				intent.putExtra("nickname", MainActivity.this.nicknameView.getText().toString());
				startActivity(intent);
				// Stop the activity so people can't go back
				MainActivity.this.finish();
			}
		});
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

}
