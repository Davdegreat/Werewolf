package org.faudroids.werewolf.core;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.faudroids.werewolf.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.inject.Inject;

/**
 * Created by dex on 12/28/15.
 */
public class GameManager {

    private final String PLAYERS_FILENAME = "Players";

    Context context = null;

    @Inject
    GameManager(Context ctx){
        context = ctx;
    }

    public boolean savePlayers(List<Player> players){

        Gson gson = new Gson();

        Type collectionType = new TypeToken<List<Player>>(){}.getType();
        String json = gson.toJson(players, collectionType);

        FileOutputStream fos = null;

        try {
            fos = context.openFileOutput(PLAYERS_FILENAME, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public List<Player> getPlayers(){

        FileInputStream fis = null;
        String json = "";

        try {
            fis = context.openFileInput(PLAYERS_FILENAME);

            Scanner sc = new Scanner(fis);

            while(sc.hasNext()){
                json += sc.next();
            }

            sc.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if(json.isEmpty()){
            return null;
        }

        Gson gson = new Gson();

        Type collectionType = new TypeToken<List<Player>>(){}.getType();
        return gson.fromJson(json, collectionType);
    }


	public void createRandomTestPlayers(int playerCount) {
		Random random = new Random();
		List<Player> players = new ArrayList<>();
		for(int i = 0; i < playerCount; ++i) {
			int randomRole = random.nextInt(Role.values().length);
			players.add(new Player(i, false, Role.values()[randomRole], context.getString(R.string.default_player_name, (i + 1))));
		}
		savePlayers(players);
	}

}
