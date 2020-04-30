package de.fs.fintech.navigation;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

abstract class BaseNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            // Show home icon in the action bar
            actionBar.setDisplayHomeAsUpEnabled(true);


        }

        // add a listener to the navigation menu
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        // This adds and handles the "Burger" icon in the action bar
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        // must be called after the drawerToggle was added to the drawerLayout to sync both
        drawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // close the drawers
        drawerLayout.closeDrawers();

        Class<? extends Activity> target = null;
        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.nav_dest_main:
                target = MainActivity.class;
                break;
            case R.id.nav_dest_sub:
                target = SubActivity.class;
                break;
        }

        if (null != target) {
            Intent intent = new Intent(this, target);
            // If the activity is already on the stack, remove every activity above it.
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // If the activity is already on the top, do nothing (will invoke onNewIntent(Intent) )
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // wichtig!
            return true;
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        // close drawers on backpress, if they are open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)
                || drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawers();
            return;
        }

        super.onBackPressed();
    }
}
