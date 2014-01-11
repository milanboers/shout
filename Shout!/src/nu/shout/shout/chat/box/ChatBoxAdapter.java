package nu.shout.shout.chat.box;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.shout.shout.R;
import nu.shout.shout.chat.items.Chat;
import nu.shout.shout.chat.items.Error;
import nu.shout.shout.chat.items.Item;
import nu.shout.shout.chat.items.Report;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatBoxAdapter extends BaseAdapter {
	private Context ctx;
	
	private List<Item> items;
	
	protected Map<String, Integer> smiley_map = new HashMap<String, Integer>();
	
	public ChatBoxAdapter(Context context, List<Item> items) {
		this.ctx = context;
		this.items = items;
		
		this.smiley_map.put(":-?\\)", R.drawable.smiley);
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
		
		TextView nick = (TextView) v.findViewById(R.id.list_chat_chat_nickname);
		// TODO: in layout scheiden
		nick.setText(i.nickname + ":" + " ");
		
		TextView msg = (TextView) v.findViewById(R.id.list_chat_chat_message);
		msg = setChatToTextView(msg, i.message);
		
		return v;
	}
	
	private TextView setChatToTextView(TextView v, String chatText) {
		SpannableString text = new SpannableString(chatText);
		
		for(Entry<String, Integer> kvp : this.smiley_map.entrySet()) {
			Pattern pattern = Pattern.compile(kvp.getKey());
			Matcher matcher = pattern.matcher(chatText);
			while(matcher.find()) {
				text.setSpan(new ImageSpan(this.ctx, kvp.getValue()), matcher.start(), matcher.end(), 0);
			}
		}
		v.setText(text);
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
