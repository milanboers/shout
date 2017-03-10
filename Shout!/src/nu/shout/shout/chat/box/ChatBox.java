/* This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *   * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package nu.shout.shout.chat.box;

import java.util.ArrayList;
import java.util.List;

import nu.shout.shout.chat.items.Item;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ChatBox extends ListView {
	private List<Item> chatBoxItems = new ArrayList<Item>();

	public ChatBox(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.setAdapter(new ChatBoxAdapter(context, this.chatBoxItems));
		this.setStackFromBottom(true);
		this.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	}

	public void addItem(final Item item) {
		final Activity activity = (Activity) getContext();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ChatBox.this.chatBoxItems.add(item);
				((BaseAdapter) ChatBox.this.getAdapter()).notifyDataSetChanged();
			}
		});
	}
}
