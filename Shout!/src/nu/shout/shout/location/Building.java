/* This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *   * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

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

	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		if(o instanceof Building) {
			Building other = (Building) o;

			if(this.ircroom.equalsIgnoreCase(other.ircroom))
				return true;
		}
		return false;
	}
}
