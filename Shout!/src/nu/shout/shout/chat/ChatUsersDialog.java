package nu.shout.shout.chat;

import nu.shout.shout.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class ChatUsersDialog extends SherlockDialogFragment {
	private String[] usernames;
	private String channelName;
	
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	
	public void setUsernames(String[] usernames) {
		this.usernames = usernames;
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setTitle(getString(R.string.users_in) + " " + this.channelName)
    		.setItems(this.usernames, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Lekker niks doen
				}
			})
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Terug dan!
				}
			});
    	return builder.create();
    }
}
