package org.faudroids.werewolf.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.faudroids.werewolf.R;
import org.faudroids.werewolf.core.GameManager;
import org.faudroids.werewolf.core.MigrationManager;

import javax.inject.Inject;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_main)
public class MainActivity extends AbstractActivity {

	private static final int REQUEST_START_GAME = 42;

	@InjectView(R.id.btn_new_game) private Button newGameBtn;
	@InjectView(R.id.btn_continue_game) private Button continueGameBtn;
  @InjectView(R.id.layout_ad) private RelativeLayout myAdventView;

	@Inject private GameManager gameManager;
	@Inject private MigrationManager migrationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		migrationManager.onStart();

		newGameBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(MainActivity.this, GameSetupActivity.class), REQUEST_START_GAME);
			}
		});

		setupContinueBtnVisibility();
		continueGameBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gameManager.loadPlayers();
				startActivity(new Intent(MainActivity.this, ShowRolesActivity.class));
			}
		});

    myAdventView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.myadvent.net/?ref=org.faudroids.werewolf")));
      }
    });
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_START_GAME:
				setupContinueBtnVisibility();
				return;
		}
	}

	private void setupContinueBtnVisibility() {
		continueGameBtn.setVisibility(gameManager.existsSavedGame() ? View.VISIBLE : View.GONE);
	}
}
