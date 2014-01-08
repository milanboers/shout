package nu.shout.shout.chat.box;

import java.util.ArrayList;
import java.util.List;

import nu.shout.shout.chat.items.Chat;
import nu.shout.shout.chat.items.Item;
import nu.shout.shout.chat.items.Notice;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ChatBox extends ListView {
	private List<Item> chatBoxItems = new ArrayList<Item>();
	
	private SharedPreferences prefs;

	public ChatBox(Context context, AttributeSet attrs) {
		super(context, attrs);
        
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
		
		this.setAdapter(new ChatBoxAdapter(context, this.chatBoxItems));
		this.setStackFromBottom(true);
		this.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	}
	
	/**
	 * Adds a text to the chatbox
	 * @param text
	 */
	public void addNotice(final Notice notice) {
		final Activity activity = (Activity) getContext();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ChatBox.this.chatBoxItems.add(notice);
				((BaseAdapter) ChatBox.this.getAdapter()).notifyDataSetChanged();
			}
		});
	}
	
	public void addVerboseNotice(final String text) {
		if(this.prefs.getBoolean("verbose", false))
			this.addNotice(new Notice("system", "VERBOSE: " + text));
	}
    
	public void addChat(final Chat chat) {
		final Activity activity = (Activity) getContext();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ChatBox.this.chatBoxItems.add(chat);
				((BaseAdapter) ChatBox.this.getAdapter()).notifyDataSetChanged();
			}
		});
	}
}
