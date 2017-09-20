package com.denweisenseel.scotlandsaarexperimental;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.denweisenseel.scotlandsaarexperimental.adapter.BottomBarAdapter;
import com.denweisenseel.scotlandsaarexperimental.customView.CustomViewPager;
import com.denweisenseel.scotlandsaarexperimental.data.ChatDataParcelable;

public class GameActivity extends AppCompatActivity implements ChatFragment.ChatFragmentInteractionListener {

    BottomBarAdapter adapter;
    CustomViewPager pager;
    ChatFragment chatFragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        pager = (CustomViewPager) findViewById(R.id.viewpager);
        pager.setPagingEnabled(false);

        BottomBarAdapter bottomBarAdapter = new BottomBarAdapter(getSupportFragmentManager());
        chatFragment = ChatFragment.newInstance("null","null");
        bottomBarAdapter.addFragments(chatFragment);

        pager.setAdapter(bottomBarAdapter);

        pager.setCurrentItem(0);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //TODO Beim Back Button drücken soll ein Quit Game Dialog angezeigt werden.

    }

    @Override
    public void onFragmentInteraction(ChatDataParcelable chatDataParcelable) {
        //TODO save chat messages (1 hour)
    }
}
