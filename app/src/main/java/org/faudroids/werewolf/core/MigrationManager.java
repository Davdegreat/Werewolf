package org.faudroids.werewolf.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import javax.inject.Inject;

import timber.log.Timber;

public class MigrationManager {

	private static final String PREFS_MIGRATION = "org.faudroids.werewolf.migration";
	private static final String KEY_VERSION = "version";

	private final Context context;
	private final GameManager gameManager;

	@Inject
	MigrationManager(Context context, GameManager gameManager) {
		this.context = context;
		this.gameManager = gameManager;
	}

	public void onStart() {
		migrateLastGameState();
		SharedPreferences.Editor editor = getPrefs().edit();
		editor.putInt(KEY_VERSION, getCurrentVersion());
		editor.apply();
	}

	private void migrateLastGameState() {
		if (getLastVersion() > 1) return;
		if (getCurrentVersion() < 2) return;
		Timber.d("Migrating game state");
		gameManager.deleteSavedGame();
	}

	private int getCurrentVersion() {
		try {
			return context
					.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0)
					.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			Timber.e(e, "Failed to get get version");
			return -1;
		}
	}

	private int getLastVersion() {
		return getPrefs().getInt(KEY_VERSION, 1);
	}

	private SharedPreferences getPrefs() {
		return context.getSharedPreferences(PREFS_MIGRATION, Context.MODE_PRIVATE);
	}
}
