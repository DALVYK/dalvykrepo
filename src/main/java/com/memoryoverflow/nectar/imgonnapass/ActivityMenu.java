package com.memoryoverflow.nectar.imgonnapass;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Objects;

public class ActivityMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Helpers.changeFragment(this, Helpers.DEFAULT_HOLDER, new FragmentHome(), null, null, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeSupportActionBar();
        initializeActionBar();
        initializeNavigationDrawer();
        initializeNavigationHeader();
    }

    void initializeSupportActionBar() {
        Toolbar toolBarMainMenu = findViewById(R.id.tool_bar_main_menu);
        setSupportActionBar(toolBarMainMenu);
    }

    void initializeActionBar() {
        ActionBar actionbar = Objects.requireNonNull(getSupportActionBar());
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
    }

    void initializeNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    void initializeNavigationHeader() {
        int[] attrs = new int[]{R.attr.actionBarSize};
        TypedArray ta = this.obtainStyledAttributes(attrs);
        int toolBarHeight = ta.getDimensionPixelSize(0, -1);
        ta.recycle();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        LinearLayout linearLayout = view.findViewById(R.id.navigation_header);
        ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
        layoutParams.height = toolBarHeight;
        linearLayout.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
                //changeFragment(new FragmentHome());
                Helpers.changeFragment(this, Helpers.DEFAULT_HOLDER, new FragmentHome(), null, null, false);
                break;
            case R.id.nav_reviewers:
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_reviewers);
                //changeFragment(new FragmentReviewers());
                Helpers.changeFragment(this, Helpers.DEFAULT_HOLDER, new FragmentReviewers(), null, null, false);
                break;
            case R.id.nav_performance:
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_performance);
                //changeFragment(new FragmentPerformance());
                Helpers.changeFragment(this, Helpers.DEFAULT_HOLDER, new FragmentPerformance(), null, null, false);
                break;
            case R.id.nav_settings:
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_settings);
                //changeFragment(new FragmentSettings());
                Helpers.changeFragment(this, Helpers.DEFAULT_HOLDER, new FragmentSettings(), null, null, false);
                break;
            case R.id.nav_exit:
                finishAndRemoveTask();
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
