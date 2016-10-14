package com.fuzzyapps.breakingteach;

import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;
import it.sephiroth.android.library.bottomnavigation.BadgeProvider;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class MainActivity extends BaseActivity implements BottomNavigation.OnMenuItemSelectionListener{
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final int statusbarHeight = getStatusBarHeight();
        final boolean translucentStatus = hasTranslucentStatusBar();

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.CoordinatorLayout01);

        if (translucentStatus) {
            Log.d("asd", "hasTranslucentStatusBar");
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) coordinatorLayout.getLayoutParams();
            params.topMargin = -statusbarHeight;

            params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            params.topMargin = statusbarHeight;
        }
        initializeBottomNavigation(savedInstanceState);
    }
    protected void initializeBottomNavigation(final Bundle savedInstanceState) {
        if (null == savedInstanceState) {
            getBottomNavigation().setDefaultSelectedIndex(0);
            setFragmentInView(0);
            final BadgeProvider provider = getBottomNavigation().getBadgeProvider();
        }
    }
    @Override
    public void onMenuItemSelect(@IdRes final int itemId, final int position) {
        setFragmentInView(position);
        Toast.makeText(MainActivity.this,""+position,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onMenuItemReselect(@IdRes final int itemId, final int position) {
        Toast.makeText(MainActivity.this,"2. "+position,Toast.LENGTH_SHORT).show();
    }
    private void setFragmentInView(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        switch (position){
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new nav_firstFragment())
                        .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new nav_secondFragment())
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new nav_thirdFragment())
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new nav_fourthFragment())
                        .commit();
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new nav_fifthFragment())
                        .commit();
                break;
        }
    }
}
