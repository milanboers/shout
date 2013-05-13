package nu.shout.shout.chat.box;

import java.util.ArrayList;
import java.util.List;

import nu.shout.shout.chat.box.items.ChatBoxChat;
import nu.shout.shout.chat.box.items.ChatBoxItem;
import nu.shout.shout.chat.box.items.ChatBoxNotice;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ChatBox extends ListView {
	private List<ChatBoxItem> chatBoxItems = new ArrayList<ChatBoxItem>();
	
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
	public void addNotice(final String text) {
		final Activity activity = (Activity) getContext();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ChatBox.this.chatBoxItems.add(new ChatBoxNotice(text));
				((BaseAdapter) ChatBox.this.getAdapter()).notifyDataSetChanged();
			}
		});
	}
	
	public void addVerboseNotice(final String text) {
		if(this.prefs.getBoolean("verbose", false))
			this.addNotice("VERBOSE: " + text);
	}
    
	public void addChat(final String name, final String text) {
		final Activity activity = (Activity) getContext();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ChatBox.this.chatBoxItems.add(new ChatBoxChat(name, text));
				((BaseAdapter) ChatBox.this.getAdapter()).notifyDataSetChanged();
			}
		});
	}
}
