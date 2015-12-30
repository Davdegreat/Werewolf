package org.faudroids.werewolf.ui;


import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eftimoff.androipathview.PathView;

import org.faudroids.werewolf.R;
import org.faudroids.werewolf.core.GameManager;
import org.faudroids.werewolf.core.Player;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import timber.log.Timber;

@ContentView(R.layout.activity_show_roles)
public class ShowRolesActivity extends AbstractActivity {

	// optional extra if only one player role should be shown
	public static final String EXTRA_PLAYER = "EXTRA_PLAYER";

	@InjectView(R.id.layout_instructions) private View instructionsLayout;
	@InjectView(R.id.txt_player_name) private TextView playerNameText;
	@InjectView(R.id.btn_edit_player_name) private View editPlayerNameBtn;
	@InjectView(R.id.btn_reveal) private View revealButton;

	@InjectView(R.id.layout_role) private View roleLayout;
	@InjectView(R.id.img_icon) private PathView iconView;
	@InjectView(R.id.txt_role_name) private TextView roleNameText;
	@InjectView(R.id.txt_role_description) private TextView roleDescriptionText;

	@InjectView(R.id.layout_nav) private View navLayout;
	@InjectView(R.id.btn_back) private View backButton;
	@InjectView(R.id.btn_next) private View nextButton;

	@Inject private GameManager gameManager;

	// not null if viewing only one player
	private Player singlePlayer = null;

	// not null if viewing all players
	private List<Player> allPlayers = null;
	private int currentPlayerIdx;

	@Inject private InputMethodManager inputMethodManager;

	public ShowRolesActivity() {
		super(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		singlePlayer = (Player) getIntent().getSerializableExtra(EXTRA_PLAYER);
		if (singlePlayer != null) {
			nextButton.setVisibility(View.GONE);
			backButton.setVisibility(View.GONE);
			backButton.setVisibility(View.GONE);
			playerNameText.setText(singlePlayer.getName());

		} else {
			// get current player
			allPlayers = gameManager.loadPlayers();
			int idx = -1;
			for (int i = 0; i < allPlayers.size(); ++i) {
				if (!allPlayers.get(i).isSeen()) {
					idx = i;
					break;
				}
			}
			if (idx == -1) {
				Timber.d("all players have seen their role");
				finish();
				return;
			}
			setCurrentPlayerIdx(idx, false);
		}

		// start pulse animation on reveal button
		revealButton.startAnimation(loadAnimation(R.anim.pulse));

		// setup click to reveal
		revealButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				revealButton.setEnabled(false);

				// update player
				Player player = getCurrentPlayer();
				player.setIsSeen(true);
				gameManager.savePlayers(allPlayers);

				// toggle layouts
				instructionsLayout.startAnimation(loadAnimation(R.anim.fade_out));
				navLayout.startAnimation(loadAnimation(R.anim.fade_out));
				roleLayout.startAnimation(loadAnimation(R.anim.fade_in));
				roleLayout.setVisibility(View.VISIBLE);

				// show role text
				roleNameText.setText(player.getRole().getNameId());
				roleDescriptionText.setText(player.getRole().getGoalId());

				// show role icon
				assertPathView();
				iconView.setSvgResource(player.getRole().getIconId());
				iconView.setPaths(new ArrayList<Path>());
				iconView.getPathAnimator()
						.delay(100)
						.duration(1000)
						.interpolator(new AccelerateDecelerateInterpolator())
						.start();

				instructionsLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						revealButton.setEnabled(true);

						// toggle layouts
						instructionsLayout.startAnimation(loadAnimation(R.anim.fade_in));
						navLayout.startAnimation(loadAnimation(R.anim.fade_in));
						roleLayout.startAnimation(loadAnimation(R.anim.fade_out));

						// enable showing next player role
						nextButton.setEnabled(true);
					}
				}, 3000);

			}
		});

		// set nav buttons
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToNextRole();
			}
		});
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToPreviousRole();
			}
		});

		// setup edit player name
		playerNameText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onEditPlayerName();
			}
		});
		editPlayerNameBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onEditPlayerName();
			}
		});
	}


	private void goToNextRole() {
		instructionsLayout.startAnimation(loadAnimation(R.anim.move_forward));
		setCurrentPlayerIdx(currentPlayerIdx + 1, true);
	}


	private void goToPreviousRole() {
		instructionsLayout.startAnimation(loadAnimation(R.anim.move_backward));
		setCurrentPlayerIdx(currentPlayerIdx - 1, true);
	}


	private void setCurrentPlayerIdx(int currentPlayerIdx, boolean delaySetPlayerName) {
		this.currentPlayerIdx = currentPlayerIdx;
		if (currentPlayerIdx >= allPlayers.size()) {
			Timber.d("done viewing all players");
			startActivity(new Intent(this, PlayersOverviewActivity.class));
			finish();
			return;
		}

		final Player player = allPlayers.get(currentPlayerIdx);
		nextButton.setEnabled(player.isSeen());
		backButton.setVisibility(currentPlayerIdx != 0 ? View.VISIBLE : View.GONE);
		int delay = delaySetPlayerName ? getResources().getInteger(R.integer.anim_swipe_role_duration) : 0;
		playerNameText.postDelayed(new Runnable() {
			@Override
			public void run() {
				playerNameText.setText(player.getName());
			}
		}, delay);
	}


	private void onEditPlayerName() {
		final Player player = getCurrentPlayer();
		View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_name, null);
		final EditText editText = (EditText) dialogView.findViewById(R.id.name_edit);
		editText.setText(player.getName());
		final TextView errorView = (TextView) dialogView.findViewById(R.id.error_txt);
		final View okBtn = dialogView.findViewById(R.id.btn_ok);
		final View cancelBtn = dialogView.findViewById(R.id.btn_cancel);

		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				boolean isError = false;
				String newPlayerName = editText.getText().toString();
				if (newPlayerName.isEmpty()) {
					errorView.setText(R.string.error_empty_name);
					isError = true;
				}
				for (int i = 0; i < allPlayers.size(); ++i) {
					if (i == currentPlayerIdx) continue;
					if (allPlayers.get(i).getName().equals(newPlayerName)) {
						errorView.setText(R.string.error_duplicate_name);
						isError = true;
						break;
					}
				}
				errorView.setVisibility(isError ? View.VISIBLE : View.GONE);
				okBtn.setEnabled(!isError);
			}
		});

		final AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.change_player_name)
				.setView(dialogView)
				.show();
		editText.requestFocus();
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				player.setName(editText.getText().toString());
				playerNameText.setText(player.getName());
				gameManager.savePlayers(allPlayers);
				inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				dialog.dismiss();
			}
		});
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				dialog.dismiss();
			}
		});
	}


	@Override
	public void onBackPressed() {
		// if only one player then exit
		if (singlePlayer != null) {
			super.onBackPressed();
			return;
		}

		// if multiple players try going back one role
		if (backButton.getVisibility() == View.VISIBLE && backButton.isEnabled()) {
			goToPreviousRole();
		} else {
			super.onBackPressed();
		}
	}


	private Player getCurrentPlayer() {
		return allPlayers != null ? allPlayers.get(currentPlayerIdx) : singlePlayer;
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


	private Animation loadAnimation(@AnimRes int animationRes) {
		return AnimationUtils.loadAnimation(this, animationRes);
	}

}
