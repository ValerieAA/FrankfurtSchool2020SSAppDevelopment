package com.example.myapplication;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class is used for the database, the t_player table is declared here
 * tablename: default is the classname, here: Player, changed the tableName here to t_player
 * sources:
 * official ORMlite documentation:
 * https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Use-With-Android
 * https://www.eforce21.com/grundlage-android-entwicklung-mit-ormlite/
 */

@DatabaseTable(tableName = "t_player")

public class Player {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String playername;

    @DatabaseField
    boolean participation;


    private int nr; //number for the particpants
    private float points;
    private float sobe; //Feinwertung
    private int rank;


    Player() {
        // needed by ormlite
    }

    public Player(String playername, Boolean participation) {
        // super();
        this.playername = playername;
        this.participation = participation;
    }

    @Override
    public String toString() {
        return "Player [playerid=" + id + ", playername=" + playername + ", participation=" + participation + "]";
    }

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String playername) {
        this.playername = playername;
    }

    public Boolean getParticipation() {
        return participation;
    }

    public void setParticipation(Boolean participation) {
        this.participation = participation;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public float getSoBe() {
        return sobe;
    }

    public void setSoBe(float sobe) {
        this.sobe = sobe;
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

}
