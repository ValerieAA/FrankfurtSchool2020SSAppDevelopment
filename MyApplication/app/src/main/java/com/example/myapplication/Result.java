package com.example.myapplication;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class is used for the database, the t_result table is declared here
 * tablename: default is the classname, here: Result, changed the tableName here to t_result
 * sources:
 * official ORMlite documentation:
 * https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Use-With-Android
 * https://www.eforce21.com/grundlage-android-entwicklung-mit-ormlite/
 */

@DatabaseTable(tableName = "t_result")

public class Result {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int roundnumber;

    @DatabaseField
    private int playerID_white;

    @DatabaseField
    private int playerID_black;

    @DatabaseField
    private String result;

    Result() {
        // needed by ormlite
    }

    public Result(int roundnumber, int playerID_white, int playerID_black, String result) {
        // super();
        this.roundnumber = roundnumber;
        this.playerID_white = playerID_white;
        this.playerID_black = playerID_black;
        this.result = result;
    }

    @Override
    public String toString() {
        return "Result [id=" + id + ", roundnumber=" + roundnumber + ", playerID_white=" + playerID_white + ", playerID_black=" + playerID_black + ", result=" + result + "]";
    }


    public int getRoundnumber() {
        return roundnumber;
    }

    public void setRoundnumber(int roundnumber) {
        this.roundnumber = roundnumber;
    }

    public int getPlayerID_white() {
        return playerID_white;
    }

    public void setPlayerID_white(int playerID_white) {
        this.playerID_white = playerID_white;
    }

    public int getPlayerID_black() {
        return playerID_black;
    }

    public void setPlayerID_black(int playerID_black) {
        this.playerID_black = playerID_black;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

}
