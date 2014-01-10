package nu.shout.shout.chat.box;

import java.util.List;

import nu.shout.shout.R;
import nu.shout.shout.chat.items.Chat;
import nu.shout.shout.chat.items.Error;
import nu.shout.shout.chat.items.Item;
import nu.shout.shout.chat.items.Report;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatBoxAdapter extends BaseAdapter {
	private Context ctx;
	
	private List<Item> items;
	
	public ChatBoxAdapter(Context context, List<Item> items) {
		this.ctx = context;
		this.items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Item i = this.getItem(position);
		if(i != null) {
			if(i instanceof Chat) {
				return getChatView(convertView, (Chat) i);
			} else if(i instanceof Report) {
				return getReportView(convertView, (Report) i);
			} else if(i instanceof Error) {
				return getErrorView(convertView, (Error) i);
			}
		}
		return convertView;
	}
	
	public View getChatView(View v, Chat i) {
		LayoutInflater vi = LayoutInflater.from(this.ctx);
		v = vi.inflate(R.layout.list_chat_chat, null);
		
		TextView msg = (TextView) v.findViewById(R.id.list_chat_chat_message);
		msg.setText("<" + i.nickname + "> " + i.message);
		
		return v;
	}
	
	public View getReportView(View v, Report i) {
		LayoutInflater vi = LayoutInflater.from(this.ctx);
		v = vi.inflate(R.layout.list_chat_report, null);
		
		TextView msg = (TextView) v.findViewById(R.id.list_chat_report_message);
		msg.setText(i.message);
		
		return v;
	}
	
	public View getErrorView(View v, Error i) {
		LayoutInflater vi = LayoutInflater.from(this.ctx);
		v = vi.inflate(R.layout.list_chat_error, null);
		
		TextView msg = (TextView) v.findViewById(R.id.list_chat_error_message);
		msg.setText(i.message);
		
		return v;
	}

	@Override
	public int getCount() {
		return this.items.size();
	}

	@Override
	public Item getItem(int position) {
		return this.items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
