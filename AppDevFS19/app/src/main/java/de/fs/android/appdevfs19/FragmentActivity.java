package de.fs.android.appdevfs19;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.fs.android.appdevfs19.ui.frgagment.FragmentFragment;

public class FragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, FragmentFragment.newInstance())
                    .commitNow();
        }
    }
}
