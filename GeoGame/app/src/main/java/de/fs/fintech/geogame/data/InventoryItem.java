package de.fs.fintech.geogame.data;

/**
 * Created by axel on 20.05.17.
 */

public class InventoryItem {

    public String type;
    public int count;
    /** e.g. media URL */
    public String payload;


    public InventoryItem() {}
    public InventoryItem(InventoryType.Type type,int count) {this.type=type.toString(); this.count=count; }
    public InventoryItem(InventoryType.Type type,int count,String payload) {this.type=type.toString(); this.count=count; this.payload=payload;}

//    public static KeyItem(String portalId, int count);

}
