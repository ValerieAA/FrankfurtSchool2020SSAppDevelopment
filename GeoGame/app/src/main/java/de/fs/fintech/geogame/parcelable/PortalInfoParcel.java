package de.fs.fintech.geogame.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fs.fintech.geogame.NavDrawerMapActivity;
import de.fs.fintech.geogame.data.PortalInfo;
import de.fs.fintech.geogame.data.PortalInstallation;
import de.fs.fintech.geogame.data.PortalUnique;

/**
 * Created by axel on 01.05.17.
 */

public class PortalInfoParcel extends PortalInfo implements Parcelable {
    private static final Logger log = LoggerFactory.getLogger(PortalInfoParcel.class);

    /** set with data from User.uniques when portal enters map.
	 * used by PortalMapService to highlight unique status */
	@JsonIgnore
	public PortalUnique usersUnique;

    public PortalInstallationsParcel installations=new PortalInstallationsParcel();

    public PortalInfoParcel() {
        super();
    }

    public PortalInfoParcel(double lon,double lat,String title) {
        super(lon,lat,title);
    }

    protected PortalInfoParcel(Parcel in) {
        String[] data = new String[4];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.id = data[0];
        this.title = data[1];
        this.description = data[2];
        this.urlPhoto = data[3];

        this.lon=in.readDouble();
        this.lat=in.readDouble();

        this.approved=in.readInt()==1;
        /* BEWARE
        boolean[] data2=new boolean[3];
        this.approved=data2[0];
        */
        // https://stackoverflow.com/questions/5905105/how-to-serialize-null-value-when-using-parcelable-interface
        //BEWARE this.installations= in.readParcelable(PortalInstallationsParcel.class.getClassLoader());
        //BEWARE this.installations= (PortalInstallationsParcel) in.readValue(PortalInstallationsParcel.class.getClassLoader());
        int count=in.readInt();
        log.info("R "+count+" installations for "+id);
        if(count==1) {
            this.installations = in.readParcelable(PortalInstallationsParcel.class.getClassLoader());
        }
    }

    public static final Creator<PortalInfoParcel> CREATOR = new Creator<PortalInfoParcel>() {
        @Override
        public PortalInfoParcel createFromParcel(Parcel in) {
            return new PortalInfoParcel(in);
        }

        @Override
        public PortalInfoParcel[] newArray(int size) {
            return new PortalInfoParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeStringArray(new String[]{this.id,
                this.title,
                this.description,
                this.urlPhoto});
        dest.writeDouble(lon);
        dest.writeDouble(lat);
        dest.writeInt(approved?1:0);
        /* BEWARE
        dest.writeBooleanArray(new boolean[]{
                this.approved,
                true,true
        });
        */


        //BEWARE dest.writeValue(installations);
        if(installations==null) {
            dest.writeInt(0);
            log.info("W 0 installations for "+id);
        } else {
            dest.writeInt(1);
            log.info("W 1 installations for "+id);
            dest.writeParcelable(installations,flags);
        }
    }
}
