package org.faudroids.werewolf.core;


import android.support.annotation.RawRes;

import java.io.Serializable;

public class Role implements Serializable {

	@RawRes private final int iconId;
	private final String name;
	private final String goal;

	public Role(@RawRes int iconId, String name, String goal) {
		this.iconId = iconId;
		this.name = name;
		this.goal = goal;
	}

	public int getIconId() {
		return iconId;
	}

	public String getName() {
		return name;
	}

	public String getGoal() {
		return goal;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Role)) return false;
		Role role = (Role) o;
		if (iconId != role.iconId) return false;
		return !(name != null ? !name.equals(role.name) : role.name != null)
				&& !(goal != null ? !goal.equals(role.goal) : role.goal != null);

	}

	@Override
	public int hashCode() {
		int result = iconId;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (goal != null ? goal.hashCode() : 0);
		return result;
	}
}
