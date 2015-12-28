package org.faudroids.werewolf.core;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

import timber.log.Timber;

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

    public void test(){

        Random r = new Random();
        List<Player> players = new ArrayList<Player>();

        for(int i = 0; i <= 5; i++){
            int randomRole = r.nextInt(Role.values().length);
            Player p = new Player(Role.values()[randomRole]);
            players.add(p);
        }

        Timber.d(players.toString());

        savePlayers(players);

        List<Player> myPlayers = getPlayers();

        Timber.d(myPlayers.toString());

    }
}
