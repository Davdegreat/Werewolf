package org.faudroids.werewolf.core;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class GameManager {

    private final String PLAYERS_FILENAME = "Players";

    private final Context context;
	private final Gson gson = new Gson();
	private final Type playersType = new TypeToken<List<Player>>(){}.getType();

	private List<Player> playersCache = null;

    @Inject
    GameManager(Context context){
        this.context = context;
    }


	public boolean existsOldGame() {
		return getPlayersFile().exists();
	}


    public List<Player> loadPlayers() {
		if (playersCache != null) return playersCache;
        try {
			playersCache = gson.fromJson(new FileReader(getPlayersFile()), playersType);
			return playersCache;
        } catch (FileNotFoundException e) {
			Timber.w(e, "failed to find players file");
            return null;
        }
    }


	public boolean savePlayers(List<Player> players) {
		this.playersCache = players;
		String json = gson.toJson(players, playersType);
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(PLAYERS_FILENAME, Context.MODE_PRIVATE);
			fos.write(json.getBytes());
			fos.close();
		} catch (IOException e) {
			Timber.e(e, "failed to save players");
			return false;
		}
		return true;
	}


	public boolean savePlayer(Player player) {
		loadPlayers();
		playersCache.remove(player.getId());
		playersCache.add(player.getId(), player);
		return savePlayers(playersCache);
	}


	public Player findPlayerByName(String name) {
		loadPlayers();
		for (Player player : playersCache) {
			if (player.getName().equalsIgnoreCase(name)) return player;
		}
		return null;
	}

	private File getPlayersFile() {
		return new File(context.getFilesDir(), PLAYERS_FILENAME);
	}

}
