package org.faudroids.werewolf.core;


import android.support.annotation.RawRes;
import android.support.annotation.StringRes;

import org.faudroids.werewolf.R;

import java.io.Serializable;

public class Role implements Serializable {

	public static final Role
			WEREWOLF = new Role(R.raw.icon_werewolf, R.string.role_werewolf_name, R.string.role_werewolf_goal),
			VILLAGER = new Role(R.raw.icon_villager, R.string.role_villager_name, R.string.role_villager_goal),
			SEER = new Role(R.raw.icon_seer, R.string.role_seer_name, R.string.role_seer_goal),
			DOCTOR = new Role(R.raw.icon_doctor, R.string.role_doctor_name, R.string.role_doctor_goal),
			HUNTER = new Role(R.raw.icon_hunter, R.string.role_hunter_name, R.string.role_hunter_goal),
			WITCH = new Role(R.raw.icon_witch, R.string.role_witch_name, R.string.role_witch_goal),
			PRIEST = new Role(R.raw.icon_priest, R.string.role_priest_name, R.string.role_priest_goal),
			AMOR = new Role(R.raw.icon_amor, R.string.role_amor_name, R.string.role_amor_goal),
			DRUNK = new Role(R.raw.icon_drunk, R.string.role_drunk_name, R.string.role_drunk_goal);

	public static Role createCustomRole(String roleName) {
		return new Role(R.raw.icon_drunk, roleName, R.string.role_drunk_goal);
	}

	@RawRes private final int iconId;
	@StringRes private final int nameId;
	private final String name;
	@StringRes private final int goalId;

	private Role(@RawRes int iconId, @StringRes int nameId, @StringRes int goalId) {
		this.iconId = iconId;
		this.nameId =  nameId;
		this.name = null;
		this.goalId = goalId;
	}

	private Role(@RawRes int iconId, String name, @StringRes int goalId) {
		this.iconId = iconId;
		this.name = name;
		this.nameId = -1;
		this.goalId = goalId;
	}

	public int getIconId() {
		return iconId;
	}

	public int getNameId() {
		return nameId;
	}

	public String getName() {
		return name;
	}

	public int getGoalId() {
		return goalId;
	}

}
