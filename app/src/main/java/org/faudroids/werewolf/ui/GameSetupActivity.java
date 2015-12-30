package org.faudroids.werewolf.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.faudroids.werewolf.R;
import org.faudroids.werewolf.core.GameManager;
import org.faudroids.werewolf.core.Player;
import org.faudroids.werewolf.core.Role;
import org.roboguice.shaded.goole.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.inject.Inject;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_game_setup)
public class GameSetupActivity extends AbstractActivity {

	private static final int DEFAULT_PLAYER_COUNT = 10;

	@InjectView(R.id.cnf_auto_assign_cb) private CheckBox mAutoAssignCb;

	@InjectView(R.id.cnf_player_count_picker) private NumberPickerView mPlayerCountPicker;

	@InjectView(R.id.cnf_werewolf_count_picker) private NumberPickerView mWerewolfCountPicker;
	@InjectView(R.id.cnf_villager_count_picker) private NumberPickerView mVillagerCountPicker;
	@InjectView(R.id.cnf_seer_count_picker) private NumberPickerView mSeerCountPicker;
	@InjectView(R.id.cnf_doctor_count_picker) private NumberPickerView mDoctorCountPicker;
	@InjectView(R.id.cnf_hunter_count_picker) private NumberPickerView mHunterCountPicker;
	@InjectView(R.id.cnf_witch_count_picker) private NumberPickerView mWitchCountPicker;
	@InjectView(R.id.cnf_priest_count_picker) private NumberPickerView mPriestCountPicker;
	@InjectView(R.id.cnf_amor_count_picker) private NumberPickerView mAmorCountPicker;

	@InjectView(R.id.cnf_start_btn) private Button mStartButton;

	@Inject private GameManager mGameManager;

	private List<NumberPickerView> allPickers;
	private List<NumberPickerView> specialRolePickers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		allPickers = Lists.newArrayList(
				mWerewolfCountPicker,
				mVillagerCountPicker,
				mSeerCountPicker,
				mDoctorCountPicker,
				mHunterCountPicker,
				mWitchCountPicker,
				mPriestCountPicker,
				mAmorCountPicker
		);

		specialRolePickers = Lists.newArrayList(
				mSeerCountPicker,
				mDoctorCountPicker,
				mHunterCountPicker,
				mWitchCountPicker,
				mPriestCountPicker,
				mAmorCountPicker
		);

		for (NumberPickerView np : allPickers) {
			np.setMinValue(0);
			np.setEnabled(false);
		}

		// setup auto assign
		mAutoAssignCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for (NumberPickerView pickerView : allPickers) {
					pickerView.setEnabled(!isChecked);
				}
				if (isChecked) autoAssignRoles();
			}
		});

		// setup auto update picker values on picker value change
		NumberPickerView.OnValueChangeListener listener = new NumberPickerView.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPickerView picker, int oldValue, int newValue) {
				int playerCount = getPlayerCount();
				int assignedPlayers = getAssignedPlayerCount();

				// too many assigned roles, iterate through assigned Roles trying to decrement special roles first
				if (playerCount < assignedPlayers && !mAutoAssignCb.isChecked()) {
					int playerSurplus = assignedPlayers - playerCount;
					ListIterator<NumberPickerView> it = allPickers.listIterator(allPickers.size());
					while (it.hasPrevious()) {
						NumberPickerView currentPicker = it.previous();
						int value = currentPicker.getValue();
						if (value == 0) continue;
						currentPicker.setValue(Math.max(0, value - playerSurplus));
						playerSurplus -= value - currentPicker.getValue();
						if (playerSurplus == 0) break;
					}
				}

				// update picker + start game button
				updatePickersMax();
				int unassignedPlayers = getUnassignedPlayerCount();
				if (unassignedPlayers > 0 && !mAutoAssignCb.isChecked()) {
					mStartButton.setEnabled(false);
					mStartButton.setText(getString(R.string.cnf_x_players_unassigned, unassignedPlayers));
				} else {
					mStartButton.setEnabled(true);
					mStartButton.setText(R.string.cnf_start_btn_label);
				}

				// check for auto assign mode
				if (mAutoAssignCb.isChecked()) autoAssignRoles();
			}
		};

		mPlayerCountPicker.setOnValueChangeListener(listener);
		for (NumberPickerView np : allPickers) {
			np.setOnValueChangeListener(listener);
		}

		// default player count
		mPlayerCountPicker.setMinValue(3);
		mPlayerCountPicker.setMaxValue(100);
		mPlayerCountPicker.setValue(DEFAULT_PLAYER_COUNT);

		// click to start game
		mStartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startGame();
			}
		});
	}

	private void updatePickersMax() {
		int unassignedPlayers = getUnassignedPlayerCount();
		for (NumberPickerView pickerView : allPickers) {
			pickerView.setMaxValue(pickerView.getValue() + unassignedPlayers);
		}
	}

	private int getPlayerCount() {
		return mPlayerCountPicker.getValue();
	}

	private int getAssignedPlayerCount() {
		int playerCount = 0;
		for (NumberPickerView pickerView : allPickers) {
			playerCount += pickerView.getValue();
		}
		return playerCount;
	}

	private int getUnassignedPlayerCount() {
		return getPlayerCount() - getAssignedPlayerCount();
	}

	private void autoAssignRoles() {
		int playerCount = getPlayerCount();
		int werewolfCount = playerCount / 2;
		int villagersCount = playerCount - werewolfCount;

		for (NumberPickerView np : specialRolePickers) {
			if (villagersCount > 2) {
				villagersCount--;
				np.setValue(1);
			} else {
				np.setValue(0);
			}
		}

		mWerewolfCountPicker.setValue(werewolfCount);
		mVillagerCountPicker.setValue(villagersCount);
	}

	private void startGame() {
		List<Player> players = new ArrayList<>(mPlayerCountPicker.getValue());

		int playerId = 1;

		for (int i = 0; i < mWerewolfCountPicker.getValue(); i++) {
			players.add(new Player(playerId++, false, Role.WEREWOLF, null));
		}
		for (int i = 0; i < mVillagerCountPicker.getValue(); i++) {
			players.add(new Player(playerId++, false, Role.VILLAGER, null));
		}
		for (int i = 0; i < mSeerCountPicker.getValue(); i++) {
			players.add(new Player(playerId++, false, Role.SEER, null));
		}
		for (int i = 0; i < mDoctorCountPicker.getValue(); i++) {
			players.add(new Player(playerId++, false, Role.DOCTOR, null));
		}
		for (int i = 0; i < mHunterCountPicker.getValue(); i++) {
			players.add(new Player(playerId++, false, Role.HUNTER, null));
		}
		for (int i = 0; i < mWitchCountPicker.getValue(); i++) {
			players.add(new Player(playerId++, false, Role.WITCH, null));
		}
		for (int i = 0; i < mPriestCountPicker.getValue(); i++) {
			players.add(new Player(playerId++, false, Role.PRIEST, null));
		}
		for (int i = 0; i < mAmorCountPicker.getValue(); i++) {
			players.add(new Player(playerId++, false, Role.AMOR, null));
		}

		if (mGameManager.savePlayers(players)) {
			Intent intent = new Intent(GameSetupActivity.this, ShowRolesActivity.class);
			GameSetupActivity.this.finish();
			GameSetupActivity.this.startActivity(intent);
		} else {
			Toast.makeText(this, "Something went wrong...", Toast.LENGTH_LONG).show();
		}


	}
}
