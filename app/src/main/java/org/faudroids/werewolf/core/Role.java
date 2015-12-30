package org.faudroids.werewolf.core;


import android.content.Context;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;

import org.faudroids.werewolf.R;

public enum Role {

	WEREWOLF(R.raw.icon_werewolf, R.string.role_werewolf_name, R.string.role_werewolf_goal),
	VILLAGER(R.raw.icon_villager, R.string.role_villager_name, R.string.role_villager_goal),
	SEER(R.raw.icon_seer, R.string.role_seer_name, R.string.role_seer_goal),
	DOCTOR(R.raw.icon_doctor, R.string.role_doctor_name, R.string.role_doctor_goal),
	HUNTER(R.raw.icon_hunter, R.string.role_hunter_name, R.string.role_hunter_goal),
	WITCH(R.raw.icon_witch, R.string.role_witch_name, R.string.role_witch_goal),
	PRIEST(R.raw.icon_priest, R.string.role_priest_name, R.string.role_priest_goal),
	AMOR(R.raw.icon_amor, R.string.role_amor_name, R.string.role_amor_goal),
	DRUNK(R.raw.icon_drunk, R.string.role_drunk_name, R.string.role_drunk_goal);

	@RawRes private final int iconId;
	@StringRes private final int nameId;
	@StringRes private final int goalId;

	private Role(@RawRes int iconId, @StringRes int nameId, @StringRes int goalId) {
		this.iconId = iconId;
		this.nameId =  nameId;
		this.goalId = goalId;
	}

	public int getIconId() {
		return iconId;
	}

	public int getNameId() {
		return nameId;
	}

	public int getGoalId() {
		return goalId;
	}

	public String getName(Context context) {
		return context.getString(nameId);
	}

	public String getGoal(Context context) {
		return context.getString(goalId);
	}

}
