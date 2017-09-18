package com.denweisenseel.scotlandsaarexperimental;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.denweisenseel.scotlandsaarexperimental.adapter.ChatMessageAdapter;
import com.denweisenseel.scotlandsaarexperimental.api.RequestBuilder;
import com.denweisenseel.scotlandsaarexperimental.data.ChatDataParcelable;
import com.denweisenseel.scotlandsaarexperimental.data.VolleyRequestQueue;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LobbyActivity extends AppCompatActivity {


    private ChatMessageAdapter cAdapater;
    private final ArrayList<ChatDataParcelable> chatList =  new ArrayList();

    private BroadcastReceiver pushUpdateReceiver;

    private final String TAG = "GameLobby";

    String firebaseToken;
    String gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);

        gameId = String.valueOf(getSharedPreferences(getString(R.string.gameData),Context.MODE_PRIVATE).getLong(getString(R.string.gameId), 0));
        firebaseToken = FirebaseInstanceId.getInstance().getToken();


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

                    sendChatMessage(chatMessageInput.getText().toString());
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


            launchGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   startGame();
                }
            });
        }

        pushUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(getString(R.string.LOBBY_PLAYER_JOIN))) {
                    ArrayList<String> argList = intent.getStringArrayListExtra(getString(R.string.BROADCAST_DATA));

                    String playerName = argList.get(0);
                    chatList.add(new ChatDataParcelable(playerName,"joined the lobby", new SimpleDateFormat("HH.mm").format(new Date())));
                    cAdapater.notifyDataSetChanged();
                } else if(intent.getAction().equals(getString(R.string.LOBBY_PLAYER_MESSAGE))) {
                    ArrayList<String> argList = intent.getStringArrayListExtra(getString(R.string.BROADCAST_DATA));
                    chatList.add(new ChatDataParcelable(argList.get(0),argList.get(1),argList.get(2)));
                    cAdapater.notifyDataSetChanged();
                } else if(intent.getAction().equals(getString(R.string.LOBBY_GAME_START))) {
                    Log.v(TAG, "Game Start!");
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(pushUpdateReceiver, new IntentFilter(getString(R.string.LOBBY_PLAYER_JOIN)));
        LocalBroadcastManager.getInstance(this).registerReceiver(pushUpdateReceiver, new IntentFilter(getString(R.string.LOBBY_PLAYER_MESSAGE)));
        LocalBroadcastManager.getInstance(this).registerReceiver(pushUpdateReceiver, new IntentFilter(getString(R.string.LOBBY_GAME_START)));



    }

    private void startGame() {
        String[] args = {gameId, firebaseToken};
        JsonObjectRequest gameRequest = new JsonObjectRequest(Request.Method.POST, RequestBuilder.buildRequestUrl(RequestBuilder.START_GAME, args ),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Start Game " + gameId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });

        VolleyRequestQueue.getInstance(this).addToRequestQueue(gameRequest);
    }

    private void sendChatMessage(final String message) {

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        String gameId = String.valueOf(getSharedPreferences(getString(R.string.gameData),Context.MODE_PRIVATE).getLong(getString(R.string.gameId), 0));
        String[] args = {gameId,firebaseToken, message};


        JsonObjectRequest gameRequest = new JsonObjectRequest(Request.Method.POST, RequestBuilder.buildRequestUrl(RequestBuilder.CHAT_MESSAGE, args ),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Message sent " + message);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });

        VolleyRequestQueue.getInstance(this).addToRequestQueue(gameRequest);

    }
}
