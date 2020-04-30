package de.fs.fintech.geogame;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.appinvite.AppInviteInvitation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fs.fintech.geogame.service.AlarmReceiver;

public class NavDrawerActivity extends AppCompatBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final Logger log = LoggerFactory.getLogger(NavDrawerActivity.class);

    private static final int REQUEST_INVITE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Alternative auch Bottom-Sheets
                // https://material.io/guidelines/components/bottom-sheets.html
                Snackbar.make(view, "Create Portal", Snackbar.LENGTH_LONG)
                        .setAction("Create Portal here", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                createPortalHere();
                            }
                        }).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String name = prefs.getString("google.plus.displayName",null);
        String email = prefs.getString("google.plus.email",null);
        TextView drawerName= (TextView) headerLayout.findViewById(R.id.drawerName);
        drawerName.setText(name);
        TextView drawerEmail= (TextView) headerLayout.findViewById(R.id.drawerEmail);
        drawerEmail.setText(email);

    }

    private void createPortalHere() {
        Intent i=new Intent(this,PortalEditorActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_timer) {
            scheduleTimerAlarm(10);
            return true;
        } else if (id == R.id.action_debug) {
            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_inv) {
            Intent intent=new Intent(this, TabInventoryActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent=new Intent(this, PortalEditorActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_gallery) {
            Intent intent=new Intent(this, PortalListActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_help) {
            Intent intent=new Intent(this, ScrollingHelpActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_share) {
            Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                    .setMessage(getString(R.string.invitation_message))
                    .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                    .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                    .setCallToActionText(getString(R.string.invitation_cta))
                    .build();
            startActivityForResult(intent, REQUEST_INVITE);
            return true;
        } else if (id == R.id.nav_slideshow) {
            Intent intent=new Intent(this, ImportPortalCsvActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_mapoverview) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            double radius = Double.parseDouble(prefs.getString("map.overview.radius", "5"));

            Intent intent=new Intent(this, MapsActivity.class);
            intent.putExtra(MapsActivity.EXTRA_MAP_RADIUS,radius);
            intent.putExtra(MapsActivity.EXTRA_UPDATE_INTERVAL,-1L);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_passcode) {
            Intent intent=new Intent(this, PasscodeActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_send) {
            Intent intent=new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void scheduleTimerAlarm(int duration) {
        log.info("scheduleTimerAlarm "+duration);
        // Calculate the time when it expires.
        long wakeupTime = System.currentTimeMillis() + duration*1000L;

        Intent myIntent = new Intent(this, AlarmReceiver.class);
        myIntent.putExtra("text","Message from "+getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent,0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, wakeupTime, pendingIntent);
    }
}
