package com.seven.fzuborrow;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.seven.fzuborrow.ui.community.CommunityFragment;
import com.seven.fzuborrow.ui.home.HomeFragment;
import com.seven.fzuborrow.ui.mine.MineFragment;
import com.seven.fzuborrow.ui.notifications.NotificationsFragment;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private HomeFragment homeFragment = new HomeFragment();
    private CommunityFragment communityFragment = new CommunityFragment();
    private NotificationsFragment notificationsFragment = new NotificationsFragment();
    private MineFragment mineFragment = new MineFragment();
    BottomNavigationView navView;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        viewPager = findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(4);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return homeFragment;
                    case 1:
                        return communityFragment;
                    case 2:
                        return notificationsFragment;
                    case 3:
                        return mineFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 4;
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "新事务通知";
            String description = "新的借出/借入事务";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("10086", name, importance);
            mChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        viewPager.setCurrentItem(menuItem.getOrder());
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        navView.getMenu().getItem(position).setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
