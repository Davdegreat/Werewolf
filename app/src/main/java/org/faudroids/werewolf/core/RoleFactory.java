package org.faudroids.werewolf.core;


import android.content.Context;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;

import org.faudroids.werewolf.R;

import java.io.Serializable;

import javax.inject.Inject;

public class RoleFactory implements Serializable {

	private final Context context;

	@Inject
	RoleFactory(Context context) {
		this.context = context;
	}

	public Role werewolf() {
		return newRole(R.raw.icon_werewolf, R.string.role_werewolf_name, R.string.role_werewolf_goal);
	}

	public Role villager() {
		return newRole(R.raw.icon_villager, R.string.role_villager_name, R.string.role_villager_goal);
	}

	public Role seer() {
		return newRole(R.raw.icon_seer, R.string.role_seer_name, R.string.role_seer_goal);
	}

	public Role doctor() {
		return newRole(R.raw.icon_doctor, R.string.role_doctor_name, R.string.role_doctor_goal);
	}

	public Role hunter() {
		return newRole(R.raw.icon_hunter, R.string.role_hunter_name, R.string.role_hunter_goal);
	}
	public Role witch() {
		return newRole(R.raw.icon_witch, R.string.role_witch_name, R.string.role_witch_goal);
	}
	public Role priest() {
		return newRole(R.raw.icon_priest, R.string.role_priest_name, R.string.role_priest_goal);
	}

	public Role amor() {
		return newRole(R.raw.icon_amor, R.string.role_amor_name, R.string.role_amor_goal);
	}
	public Role drunk() {
		return newRole(R.raw.icon_drunk, R.string.role_drunk_name, R.string.role_drunk_goal);
	}

	public Role customRole(String roleName) {
		return new Role(R.raw.icon_drunk, roleName, "");
	}

	private Role newRole(@RawRes int icon, @StringRes int nameId, @StringRes int goalId) {
		return new Role(icon, toString(nameId), toString(goalId));
	}

	private String toString(@StringRes int stringRes) {
		return context.getString(stringRes);
	}

}
