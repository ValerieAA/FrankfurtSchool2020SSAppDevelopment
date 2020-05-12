package com.example.myapplication;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * necessary for the database configuration
 * sources:
 * https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Use-With-Android
 * https://www.eforce21.com/grundlage-android-entwicklung-mit-ormlite/
 */


public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[]{Player.class, Result.class};


    public static void main(String[] args) throws IOException, SQLException {

        writeConfigFile(new File("C:/Users/Admin/AndroidStudioProjects/MyApplication/app/src/main/res/raw/ormlite_config.txt"), classes);

    }
}

