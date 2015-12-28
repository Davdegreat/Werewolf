package org.faudroids.werewolf.core;

/**
 * Created by dex on 12/28/15.
 */
public class Player {

    private static int playerCount = 0;

    private int id = 0;
    private boolean isSeen = false;
    private Role role = null;

    public Player(){
        id = ++Player.playerCount;
    }

    public Player(Role r){
        this();
        role = r;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String toString(){
        String out = "Player:";
        out += " id: " + id;
        out += " isSeen: " + isSeen;
        out += " role: " + role;
        return out;
    }


}
