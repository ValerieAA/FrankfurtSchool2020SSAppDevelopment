package de.fs.fintech.geogame;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Created by axel on 10.05.17.
 */

public class AppCompatBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Set the local night mode to some value
            getDelegate().setLocalNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);

            // Now recreate for it to take effect
            recreate();
        }
    }
}
