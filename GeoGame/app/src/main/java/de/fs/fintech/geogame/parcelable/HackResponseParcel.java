package de.fs.fintech.geogame.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.fs.fintech.geogame.data.HackResponse;
import de.fs.fintech.geogame.data.InventoryItem;
import de.fs.fintech.geogame.data.PortalUnique;

/**
 * Created by axel on 21.05.17.
 */

public class HackResponseParcel extends HackResponse implements Parcelable {

    @JsonIgnore
    public PortalUnique usersUnique;

    public HackResponseParcel() {
        super();
    }

    protected HackResponseParcel(Parcel in) {
        int len=in.readInt();
        String[] ids = new String[len];
        int[] counts = new int[len];
        in.readStringArray(ids);
        in.readIntArray(counts);

        hackedItems=new InventoryItem[len];
        for(int i=0;i<len;i++) {
            hackedItems[i]=new InventoryItem();
            hackedItems[i].type=ids[i];
            hackedItems[i].count=counts[i];
        }
    }

    public static final Parcelable.Creator<HackResponseParcel> CREATOR = new Parcelable.Creator<HackResponseParcel>() {
        @Override
        public HackResponseParcel createFromParcel(Parcel in) {
            return new HackResponseParcel(in);
        }

        @Override
        public HackResponseParcel[] newArray(int size) {
            return new HackResponseParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(hackedItems.length);
        String[] ids=new String[hackedItems.length];
        int[] counts=new int[hackedItems.length];
        dest.writeStringArray(ids);
        dest.writeIntArray(counts);
    }
}
