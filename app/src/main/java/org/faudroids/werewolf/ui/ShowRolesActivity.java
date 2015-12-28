package org.faudroids.werewolf.ui;


import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.eftimoff.androipathview.PathView;

import org.faudroids.werewolf.R;
import org.faudroids.werewolf.core.Role;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_show_roles)
public class ShowRolesActivity extends AbstractActivity {

	@InjectView(R.id.img_icon) private PathView iconView;
	@InjectView(R.id.txt_role_name) private TextView roleNameText;
	@InjectView(R.id.txt_role_description) private TextView roleDescriptionText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Role role = getRandomRole();

		roleNameText.setText(role.getNameId());
		roleDescriptionText.setText(role.getGoalId());

		iconView.setSvgResource(role.getIconId());
		iconView.getPathAnimator()
				.delay(100)
				.duration(2000)
				.interpolator(new AccelerateDecelerateInterpolator())
				.start();
	}

	private Role getRandomRole() {
		List<Role> roles = new ArrayList<>();
		roles.addAll(EnumSet.allOf(Role.class));
		return roles.get((int) (Math.random() * roles.size()));
	}
}
