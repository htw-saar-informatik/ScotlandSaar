package com.denweisenseel.scotlandsaarexperimental;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.denweisenseel.scotlandsaarexperimental.adapter.ChatMessageAdapter;
import com.denweisenseel.scotlandsaarexperimental.data.ChatDataParcelable;

import java.util.ArrayList;

public class LobbyActivity extends AppCompatActivity {


    private ChatMessageAdapter cAdapater;
    private final ArrayList<ChatDataParcelable> chatList =  new ArrayList();

    private BroadcastReceiver pushUpdateReceiver;

    private long gameId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);

        cAdapater = new ChatMessageAdapter(this, chatList);

        final ListView lv = (ListView) findViewById(R.id.Lobby_ChatList);
        lv.setAdapter(cAdapater);
        lv.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lv.setStackFromBottom(true);

        final EditText chatMessageInput = (EditText) findViewById(R.id.Lobby_Chat_Message);
        chatMessageInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Log.v("ChatMessage","Send:"+chatMessageInput.getText().toString());

                    sendChatMessage();
                    chatMessageInput.setText("");
                    chatMessageInput.findFocus();


                    return true;
                }
                return false;
            }
        });


        if(getIntent().getBooleanExtra(getString(R.string.host), false)) {
            Button launchGame = (Button) findViewById(R.id.Lobby_Start_Game);
            launchGame.setVisibility(View.VISIBLE);
        }
    }

    private void sendChatMessage() {
        chatList.add(new ChatDataParcelable("1","2","3"));
    }
}
