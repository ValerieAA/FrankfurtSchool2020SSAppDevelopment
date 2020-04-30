package de.fs.fintech.geogame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fs.fintech.geogame.touch.TouchPaintActivity;

public class MainActivity extends AppCompatBaseActivity implements View.OnClickListener {

    private static  Logger log = LoggerFactory.getLogger(MainActivity.class);
    private static boolean isToastLifecycleEnabled=false;
    private int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.debug("onCreate debug"); //
        log.info("onCreate info");
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main_simple_linear);
        setContentView(R.layout.activity_main);

        Activity activity=this;
        Context context =activity;// oder auch Service
        // preferences der App (für alle Activities, Services etc)
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        // preferences der Activity
        SharedPreferences activityPref = activity.getPreferences(Context.MODE_PRIVATE);
        // preferences der Activity
        SharedPreferences privatePref = context.getSharedPreferences("mypackagekey",Context.MODE_PRIVATE);

        counter=sharedPref.getInt("counter",0);
        log.info("get counter "+counter);

        String email = sharedPref.getString("google.plus.email",null);
        setTitle(getTitle()+" "+email);

        /**
         * #1 einzelne Buttons an this.onClick() delegieren
         */
        Button btnNorth = (Button) findViewById(R.id.button_north);
        btnNorth.setOnClickListener(this);

        Button btnWest = (Button) findViewById(R.id.button_west);
        btnWest.setOnClickListener(this);

        /**
         * #2 eigene anonymous inner class pro Button
         */
        Button btnSouth= (Button) findViewById(R.id.button_south);
        btnSouth.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                counter--;
                updatePrefs();
                Log.d("MainActivity","-counter="+counter);
                log.debug("slf4j --counter="+counter);



            }

        });

        Button btnNext= (Button) findViewById(R.id.button_next);
        btnNext.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }

        });

        /**
         * #3 gemeinsame static inner class als Click-Dispatcher (eigenes Objekt)
         */
        Button btnLogin= (Button) findViewById(R.id.button_login);
        btnLogin.setOnClickListener(new ClickDispatcher());

        Button btnLoginFB= (Button) findViewById(R.id.button_login_firebase);
        btnLoginFB.setOnClickListener(new ClickDispatcher());


        /**
         * #4 gemeinsame static inner class als Click-Dispatcher (gemeinsames Objekt)
         */
        ClickDispatcher dispatcher=new ClickDispatcher();
        Button btnInvite= (Button) findViewById(R.id.button_profile);
        btnInvite.setOnClickListener(dispatcher);

        Button btnIntro= (Button) findViewById(R.id.button_intro);
        btnIntro.setOnClickListener(dispatcher);

        /**
         * #5 gemeinsame static inner class als Click-Dispatcher (gemeinsames Objekt)
         * ohne lokale Variable für den "found view"
         */
        ((Button) findViewById(R.id.button_map)).setOnClickListener(dispatcher);
        ((Button) findViewById(R.id.button_accelerometer)).setOnClickListener(dispatcher);

        /**
         * #6 Iteration über ids gleichartig zu behandelnder Views
         */
        int[] ids={
                R.id.button_touchpaint,
                R.id.button_navdrawer,
                R.id.button_cursor_adapter,
                R.id.button_recycler

        };
        for(int j=0;j<ids.length;j++) {
            ((Button) findViewById(ids[j])).setOnClickListener(dispatcher);
        }
    }

    @Override
    public void onClick(View v) {
        /** hier kann switch oder if()..else if.. verwendet werden
         * Achtung! R.id.xxx sind in App-Projekten Konstanten und können deswegen in switch verwendet werden.
         * Bei Library-Projekten sind R.id.xxx nur statisch und nicht-Konstant. Daher kann dort kein(!)
         * switch verwendet werden.
         */
        if(v.getId()==R.id.button_north) {
            counter++;
        } else if (v.getId()==R.id.button_west) {
            counter/=2;
        }
        updatePrefs();
        Log.d("MainActivity","+counter="+counter);
        log.info("counter="+counter);
    }


    public void buttonEastPress(View v) {
        counter*=2;
        updatePrefs();
        Log.d("MainActivity","+counter="+counter);
    }

    private void updatePrefs() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences activityPref = this.getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("counter", counter);
        editor.commit();
        log.info("store counter "+counter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isToastLifecycleEnabled) Toast.makeText(getBaseContext(),"onStart "+counter,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isToastLifecycleEnabled) Toast.makeText(getBaseContext(),"onResume",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isToastLifecycleEnabled) Toast.makeText(getBaseContext(),"onPause",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isToastLifecycleEnabled) Toast.makeText(getBaseContext(),"onStop",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isToastLifecycleEnabled) Toast.makeText(getBaseContext(),"onDestroy",Toast.LENGTH_SHORT).show();
    }

    private class ClickDispatcher implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Class target;
            String url;
            /**
             * switch mit R.id.xxx kann nicht in Library Projekten verwendet werden.
             */
            switch(v.getId()) {
                default:
                case R.id.button_login:
                    target=LoginActivity.class;
                    break;
                case R.id.button_login_firebase:
                    target=GoogleSignInActivity.class;
                    break;
                case R.id.button_intro:
                    target=IntroDynActivity.class;
                    break;
                case R.id.button_map:
                    target=MapsActivity.class;
                    break;
                case R.id.button_profile:
                    target=ProfileActivity.class;
                    break;
                case R.id.button_portal:
                    target=PortalDetailsActivity.class;
                    break;
                case R.id.button_accelerometer:
                    target=AccelerometerPlayActivity.class;
                    break;
                case R.id.button_touchpaint:
                    target=TouchPaintActivity.class;
                    break;
                case R.id.button_navdrawer:
                    target=NavDrawerActivity.class;
                    break;
                case R.id.button_selectfaction:
                    target=SelectFactionFromListActivity.class;
                    break;
                case R.id.button_cursor_adapter:
                    target = CursorAdapterActivity.class;
                    break;
                case R.id.button_recycler:
                    target = RecyclerActivity.class;
                    break;
            }
            Intent i=new Intent(MainActivity.this,target);
            startActivity(i);
        }
    }


}
