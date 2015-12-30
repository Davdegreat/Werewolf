package org.faudroids.werewolf.core;


import android.support.annotation.NonNull;

import java.io.Serializable;

public class Player implements Comparable<Player>, Serializable {

    private int id = 0;
    private boolean isSeen = false;
    private Role role = null;
	private String name;

	// serialization constructor
	public Player() {
	}

	public Player(int id, boolean isSeen, Role role, String name) {
		this.id = id;
		this.isSeen = isSeen;
		this.role = role;
		this.name = name;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
    public String toString(){
        String out = "Player:";
        out += " id: " + id;
        out += " isSeen: " + isSeen;
        out += " role: " + role;
        return out;
    }

	@Override
	public int compareTo(@NonNull Player another) {
		return Integer.valueOf(id).compareTo(another.id);
	}

}
