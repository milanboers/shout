package nu.shout.shout.location;

import com.google.gson.annotations.SerializedName;

public class Building {
	@SerializedName("shortcut")
	public String shortcut;
	
	@SerializedName("ircroom")
	public String ircroom;
	
	@SerializedName("name")
	public String name;
	
	@SerializedName("distance")
	public String distance;
}
