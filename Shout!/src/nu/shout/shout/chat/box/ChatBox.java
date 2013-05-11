package nu.shout.shout.chat.box;

import java.util.ArrayList;
import java.util.List;

import nu.shout.shout.chat.box.items.ChatBoxChat;
import nu.shout.shout.chat.box.items.ChatBoxItem;
import nu.shout.shout.chat.box.items.ChatBoxNotice;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ChatBox extends ListView {
	private List<ChatBoxItem> chatBoxItems = new ArrayList<ChatBoxItem>();

	public ChatBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setAdapter(new ChatBoxAdapter(context, this.chatBoxItems));
	}
	
	/**
	 * Adds a text to the chatbox
	 * @param text
	 */
	public void addNotice(final String text) {
		Activity activity = (Activity) getContext();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ChatBox.this.chatBoxItems.add(new ChatBoxNotice(text));
				((BaseAdapter) ChatBox.this.getAdapter()).notifyDataSetChanged();
			}
		});
	}
    
	public void addChat(final String name, final String text) {
		Activity activity = (Activity) getContext();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ChatBox.this.chatBoxItems.add(new ChatBoxChat(name, text));
				((BaseAdapter) ChatBox.this.getAdapter()).notifyDataSetChanged();
			}
		});
	}
}
