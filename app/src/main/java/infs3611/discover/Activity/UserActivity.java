package infs3611.discover.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import infs3611.discover.Activity.Fragment.DiscoverSocFragment;
import infs3611.discover.Activity.Fragment.UserProfileFragment;
import infs3611.discover.R;

public class UserActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    TabLayout tabLayout;
    Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        frameLayout = findViewById(R.id.simpleFrameLayout);
        tabLayout = findViewById(R.id.simpleTabLayout);

        TabLayout.Tab profileTab = tabLayout.newTab();
        profileTab.setIcon(R.drawable.profile_icon);

        TabLayout.Tab discoverTab = tabLayout.newTab();
        discoverTab.setIcon(R.drawable.discover_icon);

        TabLayout.Tab eventTab = tabLayout.newTab();
        eventTab.setIcon(R.drawable.event_icon);

        TabLayout.Tab messageTab = tabLayout.newTab();
        messageTab.setIcon(R.drawable.message_icon);

        TabLayout.Tab moreTab = tabLayout.newTab();
        moreTab.setIcon(R.drawable.more_icon);

        tabLayout.addTab(profileTab);
        tabLayout.addTab(discoverTab);
        tabLayout.addTab(eventTab);
        tabLayout.addTab(messageTab);
        tabLayout.addTab(moreTab);

    }

    @Override
    protected void onStart() {
        super.onStart();

//        TabLayoutOnPageChangeListener = new TabLayout.TabLayoutOnPageChangeListener(tabLayout);

        if (tabLayout.getSelectedTabPosition() == 0) {
            fragment = new UserProfileFragment();
            performTransaction();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new UserProfileFragment();
                        break;
                    case 1:
                        fragment = new DiscoverSocFragment();
                        break;
                }
                performTransaction();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void performTransaction() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.simpleFrameLayout, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }
}
