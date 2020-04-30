package de.fs.android.appdevfs19;

import android.app.Application;
import android.util.Log;

/**
 * Created by axel on 02.04.17.
 */

public class Fs19Application extends Application {

    public Fs19Application() {
        /**
         * http://stackoverflow.com/questions/19984838/how-to-get-slf4j-android-to-honor-logcat-logging-level
         * http://stackoverflow.com/questions/34998237/slf4j-android-1-7-not-logging-on-logcat
         * https://jira.qos.ch/browse/SLF4J-314
         *
         */
      //  HandroidLoggerAdapter.DEBUG = BuildConfig.DEBUG;
        System.setProperty("log.tag.de.fs.android.appdevfs19.MainActivity", String.valueOf(Log.VERBOSE));
        System.setProperty("log.tag.MainActivity", String.valueOf(Log.VERBOSE));

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
