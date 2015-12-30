package org.faudroids.werewolf.ui;


import android.os.Bundle;
import android.view.MenuItem;

import roboguice.activity.RoboActionBarActivity;

abstract class AbstractActivity extends RoboActionBarActivity {

	private final boolean showActionBarBackButton;

	public AbstractActivity() {
		this(false);
	}

	AbstractActivity(boolean showActionBarBackButton) {
		this.showActionBarBackButton = showActionBarBackButton;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!showActionBarBackButton) return;
		if (getSupportActionBar() == null) return;
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!showActionBarBackButton) return super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
