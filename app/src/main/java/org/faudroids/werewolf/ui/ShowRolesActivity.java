package org.faudroids.werewolf.ui;


import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.eftimoff.androipathview.PathView;

import org.faudroids.werewolf.R;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_show_roles)
public class ShowRolesActivity extends AbstractActivity {

	@InjectView(R.id.img_icon) private PathView iconView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		iconView.getPathAnimator()
				.delay(100)
				.duration(2000)
				.interpolator(new AccelerateDecelerateInterpolator())
				.start();
	}
}
