package com.jainjo.ideafood;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jainjo.ideafood.admin.AdminFragment;
import com.jainjo.ideafood.log.LogFragment;
import com.jainjo.ideafood.login.LoginActivity;
import com.jainjo.ideafood.util.AppPreferences;
import com.jainjo.ideafood.util.AppUtils;

public class MainActivity extends AppCompatActivity {
    public static AppUtils utils;

    MaterialToolbar topNavBar;
    BottomNavigationView bottomNavBar;
    FragmentManager fm = getSupportFragmentManager();

    private final BottomNavigationView.OnNavigationItemSelectedListener listener = (MenuItem item) -> {
        Fragment fragment;
        switch(item.getItemId()) {
            case R.id.item_admin:
                topNavBar.setTitle(getResources().getString(R.string.admin));
                fragment = AdminFragment.newInstance();
                break;
            case R.id.item_home:
                topNavBar.setTitle(getResources().getString(R.string.home));
                fragment = HomeFragment.newInstance("a","b");
                break;
            case R.id.item_menu:
                topNavBar.setTitle(getResources().getString(R.string.menu));
                fragment = MenuFragment.newInstance("a","b");
                break;
            case R.id.item_bitacora:
                topNavBar.setTitle(getResources().getString(R.string.log));
                fragment = LogFragment.newInstance();
                break;
            default:
                return false;
        }
        updateFragment(fragment);
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppPreferences.setPreferences(this);
        utils = AppUtils.getInstance(this);
        utils.loadPreferences();

        topNavBar = findViewById(R.id.topAppBar);
        bottomNavBar = findViewById(R.id.bottom_navigation);
        bottomNavBar.setOnNavigationItemSelectedListener(listener);
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.main_frame_layout, HomeFragment.newInstance("a","b"));
        ft.commit();
        bottomNavBar.setSelectedItemId(R.id.item_home);

        if( utils.getBearerTokenHeader().size() == 0 ) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void updateFragment(Fragment fragment) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_frame_layout, fragment, fragment.toString());
        ft.addToBackStack(fragment.toString());
        ft.commit();
    }

}