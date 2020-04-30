package de.fs.fintech.geogame.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fs.fintech.geogame.data.PortalInstallation;

/**
 * Created by axel on 28.05.17.
 */

public class PortalInstallationsParcel extends PortalInstallation implements Parcelable {
    private static final Logger log = LoggerFactory.getLogger(PortalInstallationsParcel.class);

    public PortalInstallationsParcel() {
        super();
    }

    protected PortalInstallationsParcel(Parcel in) {
        resos = new int[8];
        //in.readIntArray(resos);

    }

    public static final Parcelable.Creator<PortalInstallationsParcel> CREATOR = new Parcelable.Creator<PortalInstallationsParcel>() {
        @Override
        public PortalInstallationsParcel createFromParcel(Parcel in) {
            return new PortalInstallationsParcel(in);
        }

        @Override
        public PortalInstallationsParcel[] newArray(int size) {
            return new PortalInstallationsParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeIntArray(resos);
    }
}
