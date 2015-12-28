package org.faudroids.werewolf.ui;


import android.graphics.Path;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

	@InjectView(R.id.layout_instructions) private View instructionsLayout;
	@InjectView(R.id.btn_reveal) private View revealButton;

	@InjectView(R.id.layout_role) private View roleLayout;
	@InjectView(R.id.img_icon) private PathView iconView;
	@InjectView(R.id.txt_role_name) private TextView roleNameText;
	@InjectView(R.id.txt_role_description) private TextView roleDescriptionText;
	@InjectView(R.id.btn_back) private View backButton;
	@InjectView(R.id.btn_next) private View nextButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		revealButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));

		// setup click to reveal
		revealButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				instructionsLayout.startAnimation(AnimationUtils.loadAnimation(ShowRolesActivity.this, R.anim.fade_out));
				roleLayout.startAnimation(AnimationUtils.loadAnimation(ShowRolesActivity.this, R.anim.fade_in));
				roleLayout.setVisibility(View.VISIBLE);

				Role role = getRandomRole();

				roleNameText.setText(role.getNameId());
				roleDescriptionText.setText(role.getGoalId());

				assertPathView();
				iconView.setSvgResource(role.getIconId());
				iconView.setPaths(new ArrayList<Path>());
				iconView.getPathAnimator()
						.delay(100)
						.duration(1000)
						.interpolator(new AccelerateDecelerateInterpolator())
						.start();

				instructionsLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						instructionsLayout.startAnimation(AnimationUtils.loadAnimation(ShowRolesActivity.this, R.anim.fade_in));
						roleLayout.startAnimation(AnimationUtils.loadAnimation(ShowRolesActivity.this, R.anim.fade_out));
					}
				}, 3000);
			}
		});

		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(ShowRolesActivity.this, "next!", Toast.LENGTH_SHORT).show();
			}
		});
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(ShowRolesActivity.this, "back!", Toast.LENGTH_SHORT).show();
			}
		});
	}


	private Role getRandomRole() {
		List<Role> roles = new ArrayList<>();
		roles.addAll(EnumSet.allOf(Role.class));
		return roles.get((int) (Math.random() * roles.size()));
	}


	/**
	 * Hack to reset the path view internal state, since setting the svg source
	 * only works when setting it the first time.
	 */
	private void assertPathView() {
		ViewGroup parent = getParent(iconView);
		PathView newIconView = (PathView) getLayoutInflater().inflate(R.layout.role_icon, null).findViewById(R.id.img_icon);
		final int index = parent.indexOfChild(iconView);
		removeView(iconView);
		removeView(newIconView);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				getResources().getDimensionPixelSize(R.dimen.role_icon_size),
				getResources().getDimensionPixelSize(R.dimen.role_icon_size));
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		parent.addView(newIconView, index, layoutParams);
		iconView = newIconView;
	}


	private void removeView(View view) {
		ViewGroup parent = getParent(view);
		if(parent != null) {
			parent.removeView(view);
		}
	}


	private ViewGroup getParent(View view) {
		return (ViewGroup)view.getParent();
	}

}
