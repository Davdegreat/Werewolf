package org.faudroids.werewolf.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.faudroids.werewolf.R;
import org.faudroids.werewolf.core.GameManager;

import javax.inject.Inject;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_main)
public class MainActivity extends AbstractActivity {

	@InjectView(R.id.btn_test_roles) private Button testRolesButton;

	@Inject
	private GameManager mGameManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		testRolesButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, ShowRolesActivity.class));
			}
		});

		mGameManager.test();
	}
}
