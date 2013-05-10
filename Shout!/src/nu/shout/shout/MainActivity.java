package nu.shout.shout;

import nu.shout.shout.chat.ChatActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
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
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
