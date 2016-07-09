package org.faudroids.werewolf.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.faudroids.werewolf.R;
import org.faudroids.werewolf.core.GameManager;
import org.faudroids.werewolf.core.Player;
import org.faudroids.werewolf.core.Role;
import org.faudroids.werewolf.core.RoleFactory;
import org.roboguice.shaded.goole.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.inject.Inject;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_game_setup)
public class GameSetupActivity extends AbstractActivity {

	private static final int DEFAULT_PLAYER_COUNT = 10;

	@InjectView(R.id.cnf_auto_assign_cb) private CheckBox mAutoAssignCb;
	@InjectView(R.id.cnf_add_role) private Button mAddRoleBtn;

	@InjectView(R.id.cnf_player_count_picker) private NumberPickerView mPlayerCountPicker;

	@InjectView(R.id.cnf_layout_roles) private LinearLayout mRolesLayout;
	@InjectView(R.id.cnf_werewolf_count_picker) private NumberPickerView mWerewolfCountPicker;
	@InjectView(R.id.cnf_villager_count_picker) private NumberPickerView mVillagerCountPicker;
	@InjectView(R.id.cnf_seer_count_picker) private NumberPickerView mSeerCountPicker;
	@InjectView(R.id.cnf_doctor_count_picker) private NumberPickerView mDoctorCountPicker;
	@InjectView(R.id.cnf_hunter_count_picker) private NumberPickerView mHunterCountPicker;
	@InjectView(R.id.cnf_witch_count_picker) private NumberPickerView mWitchCountPicker;
	@InjectView(R.id.cnf_priest_count_picker) private NumberPickerView mPriestCountPicker;
	@InjectView(R.id.cnf_amor_count_picker) private NumberPickerView mAmorCountPicker;
	@InjectView(R.id.cnf_drunk_count_picker) private NumberPickerView mDrunkCountPicker;

	@InjectView(R.id.cnf_start_btn) private Button mStartButton;

	@Inject private GameManager mGameManager;
	@Inject private RoleFactory roleFactory;

	private final Map<Role, NumberPickerView> allPickers = new HashMap<>();
	private List<NumberPickerView> specialRolePickers;

	private NumberPickerView.OnValueChangeListener onRoleCountChangeListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		allPickers.put(roleFactory.werewolf(), mWerewolfCountPicker);
		allPickers.put(roleFactory.villager(), mVillagerCountPicker);
		allPickers.put(roleFactory.seer(), mSeerCountPicker);
		allPickers.put(roleFactory.doctor(), mDoctorCountPicker);
		allPickers.put(roleFactory.hunter(), mHunterCountPicker);
		allPickers.put(roleFactory.witch(), mWitchCountPicker);
		allPickers.put(roleFactory.priest(), mPriestCountPicker);
		allPickers.put(roleFactory.amor(), mAmorCountPicker);
		allPickers.put(roleFactory.drunk(), mDrunkCountPicker);

		specialRolePickers = Lists.newArrayList(
				mSeerCountPicker,
				mDoctorCountPicker,
				mHunterCountPicker,
				mWitchCountPicker,
				mPriestCountPicker,
				mAmorCountPicker,
				mDrunkCountPicker
		);

		for (NumberPickerView np : allPickers.values()) {
			np.setMinValue(0);
			np.setEnabled(false);
		}

		// setup auto assign
		mAutoAssignCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for (NumberPickerView pickerView : allPickers.values()) {
					pickerView.setEnabled(!isChecked);
				}
				if (isChecked) autoAssignRoles();
			}
		});

		// setup auto update picker values on picker value change
		onRoleCountChangeListener = new NumberPickerView.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPickerView picker, int oldValue, int newValue) {
				int playerCount = getPlayerCount();
				int assignedPlayers = getAssignedPlayerCount();

				// too many assigned roles, iterate through assigned Roles trying to decrement special roles first
				if (playerCount < assignedPlayers && !mAutoAssignCb.isChecked()) {
					int playerSurplus = assignedPlayers - playerCount;
					ListIterator<NumberPickerView> it = new LinkedList<>(allPickers.values()).listIterator(allPickers.size());
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

		mPlayerCountPicker.setOnValueChangeListener(onRoleCountChangeListener);
		for (NumberPickerView np : allPickers.values()) {
			np.setOnValueChangeListener(onRoleCountChangeListener);
		}

		// default player count
		mPlayerCountPicker.setMinValue(3);
		mPlayerCountPicker.setMaxValue(100);
		mPlayerCountPicker.setValue(DEFAULT_PLAYER_COUNT);

		// setup adding new roles
		mAddRoleBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShowNewRoleInput();
			}
		});

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
		for (NumberPickerView pickerView : allPickers.values()) {
			pickerView.setMaxValue(pickerView.getValue() + unassignedPlayers);
		}
	}

	private int getPlayerCount() {
		return mPlayerCountPicker.getValue();
	}

	private int getAssignedPlayerCount() {
		int playerCount = 0;
		for (NumberPickerView pickerView : allPickers.values()) {
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
		for (Map.Entry<Role, NumberPickerView> rolePicker : allPickers.entrySet()) {
			createPlayers(players, rolePicker.getValue(), rolePicker.getKey());
		}
		Collections.shuffle(players);
		for (int i = 0; i < players.size(); ++i) {
			Player player = players.get(i);
			player.setId(i);
			player.setName(getString(R.string.default_player_name, i + 1));
		}

		if (mGameManager.savePlayers(players)) {
			startActivity(new Intent(GameSetupActivity.this, ShowRolesActivity.class));
			finish();
		} else {
			Toast.makeText(this, "Something went wrong...", Toast.LENGTH_LONG).show();
		}

	}

	private int createPlayers(List<Player> players, NumberPickerView pickerView, Role role) {
		for (int i = 0; i < pickerView.getValue(); i++) {
			players.add(new Player(0, false, role, null));
		}
		return pickerView.getValue();
	}

	private void onShowNewRoleInput() {
		InputDialog inputDialog = new InputDialog(this, R.string.cnf_add_new_role);
		inputDialog.setInputListener(new InputDialog.InputListener() {
			@Override
			public InputDialog.ValidationResult isInputValid(String newRoleName) {
				if (newRoleName.isEmpty()) {
					return new InputDialog.ValidationResult(false, R.string.error_empty_name);
				}
				return new InputDialog.ValidationResult(true, 0);
			}

			@Override
			public void onConfirm(String newRoleName) {
				onAddNewRoleInput(newRoleName);
			}
		});
		inputDialog.show();
	}

	private void onAddNewRoleInput(String newRoleName) {
		// setup view
		View roleLayout = getLayoutInflater().inflate(R.layout.layout_role, null);
		TextView labelView= (TextView) roleLayout.findViewById(R.id.txt_label);
		labelView.setText(newRoleName);
		NumberPickerView picker = (NumberPickerView) roleLayout.findViewById(R.id.picker);
		mRolesLayout.addView(roleLayout);

		// init picker
		allPickers.put(roleFactory.customRole(newRoleName), picker);
		specialRolePickers.add(picker);
		picker.setMinValue(0);
		if (mAutoAssignCb.isChecked()) picker.setEnabled(false);
		picker.setOnValueChangeListener(onRoleCountChangeListener);
		onRoleCountChangeListener.onValueChange(picker, 0, 0);
	}

}
