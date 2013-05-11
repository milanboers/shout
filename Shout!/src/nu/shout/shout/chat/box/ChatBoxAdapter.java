package nu.shout.shout.chat.box;

import java.util.List;

import nu.shout.shout.R;
import nu.shout.shout.chat.box.items.ChatBoxChat;
import nu.shout.shout.chat.box.items.ChatBoxItem;
import nu.shout.shout.chat.box.items.ChatBoxNotice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatBoxAdapter extends BaseAdapter {
	private Context ctx;
	
	private List<ChatBoxItem> items;
	
	public ChatBoxAdapter(Context context, List<ChatBoxItem> items) {
		this.ctx = context;
		this.items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatBoxItem i = this.getItem(position);
		if(i != null) {
			if(i instanceof ChatBoxChat) {
				return getChatView((ChatBoxChat) i);
			} else if(i instanceof ChatBoxNotice) {
				return getNoticeView((ChatBoxNotice) i);
			}
		}
		return convertView;
	}
	
	public View getChatView(ChatBoxChat i) {
		LayoutInflater vi = LayoutInflater.from(this.ctx);
		View v = vi.inflate(R.layout.list_chat_chat, null);
		
		TextView msg = (TextView) v.findViewById(R.id.list_chat_chat_message);
		msg.setText("<" + i.name + "> " + i.message);
		
		return v;
	}
	
	public View getNoticeView(ChatBoxNotice i) {
		LayoutInflater vi = LayoutInflater.from(this.ctx);
		View v = vi.inflate(R.layout.list_chat_notice, null);
		
		TextView msg = (TextView) v.findViewById(R.id.list_chat_notice_message);
		msg.setText(i.message);
		
		return v;
	}

	@Override
	public int getCount() {
		return this.items.size();
	}

	@Override
	public ChatBoxItem getItem(int position) {
		return this.items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
