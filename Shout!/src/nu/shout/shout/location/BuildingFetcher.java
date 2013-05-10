package nu.shout.shout.location;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.location.Location;

public class BuildingFetcher {
	private Gson gson;
	
	public BuildingFetcher() {
		this.gson = new Gson();
	}
	
	private Reader get(Location loc) throws IOException {
		InputStream input = new URL("http://shout.nu/location.php?lat=" + loc.getLatitude() + "&lon=" + loc.getLongitude()).openStream();
		Reader r = new InputStreamReader(input, "UTF-8");
		return r;
	}
	
	public List<Building> getBuildings(Location loc) throws IOException {
		Reader r = get(loc);
		List<Building> buildingList = this.gson.fromJson(r, new TypeToken<List<Building>>(){}.getType());
		return buildingList;
	}
}
