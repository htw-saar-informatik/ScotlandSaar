package com.denweisenseel.scotlandsaarexperimental;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.denweisenseel.scotlandsaarexperimental.adapter.BottomBarAdapter;
import com.denweisenseel.scotlandsaarexperimental.customView.CustomViewPager;
import com.denweisenseel.scotlandsaarexperimental.data.ChatDataParcelable;
import com.denweisenseel.scotlandsaarexperimental.data.GameModelParcelable;
import com.denweisenseel.scotlandsaarexperimental.data.Player;
import com.denweisenseel.scotlandsaarexperimental.dialogFragments.QuitGameFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GameActivity extends AppCompatActivity implements ChatFragment.ChatFragmentInteractionListener, QuitGameFragment.OnFragmentInteractionListener{

    BottomBarAdapter adapter;
    CustomViewPager pager;
    //CHAT
    ChatFragment chatFragment;
    BroadcastReceiver chatMessageReceiver;
    ArrayList<ChatDataParcelable> chatList;

    GameModel gameModel = new GameModel();


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
    private String TAG = "GameActivity";

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
        //TODO Beim Back Button drücken soll ein Quit Game Dialog angezeigt werden. (2 Hours), Luca


        chatList = new ArrayList<ChatDataParcelable>();
        chatMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(getString(R.string.LOBBY_PLAYER_JOIN))) {
                    ArrayList<String> argList = intent.getStringArrayListExtra(getString(R.string.BROADCAST_DATA));

                    String playerName = argList.get(0);
                    ChatDataParcelable chatMessage = new ChatDataParcelable(playerName,"joined the lobby", new SimpleDateFormat("HH.mm").format(new Date()));
                    sendToChatFragment(chatMessage);
                    chatList.add(chatMessage);

                } else if(intent.getAction().equals(getString(R.string.LOBBY_PLAYER_MESSAGE))) {
                    ArrayList<String> argList = intent.getStringArrayListExtra(getString(R.string.BROADCAST_DATA));
                    ChatDataParcelable chatMessage = new ChatDataParcelable(argList.get(0),argList.get(1),argList.get(2));
                    chatList.add(chatMessage);
                    sendToChatFragment(chatMessage);
                } else if(intent.getAction().equals(getString(R.string.LOBBY_GAME_START))) {
                    Log.v(TAG, "Game Start!");
                }
            }
        };

        //Player wants to quit the game
        requestQuitGameDialog();

        LocalBroadcastManager.getInstance(this).registerReceiver(chatMessageReceiver, new IntentFilter(getString(R.string.LOBBY_PLAYER_JOIN)));
        LocalBroadcastManager.getInstance(this).registerReceiver(chatMessageReceiver, new IntentFilter(getString(R.string.LOBBY_PLAYER_MESSAGE)));
        LocalBroadcastManager.getInstance(this).registerReceiver(chatMessageReceiver, new IntentFilter(getString(R.string.LOBBY_GAME_START)));

    }

    public void requestQuitGameDialog() {
        FragmentManager fm = getFragmentManager();
        QuitGameFragment editGamenameDialogFragment = QuitGameFragment.newInstance();
        editGamenameDialogFragment.show(fm, "QuitGameDialog");
    }

    private void sendToChatFragment(ChatDataParcelable chatMessage) {
        chatFragment.sendMessage(chatMessage);
    }

    @Override
    public void onFragmentInteraction(ChatDataParcelable chatDataParcelable) {
        //TODO save chat messages (1 hour)
    }


    @Override
    public void onYesButtonClicked() {
        //TODO Actually kick the player out of the game.
        finish();
    }
}
