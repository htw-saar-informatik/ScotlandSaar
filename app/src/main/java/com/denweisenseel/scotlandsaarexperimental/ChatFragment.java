package com.denweisenseel.scotlandsaarexperimental;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ChatFragment extends Fragment {

    private ChatFragmentInteractionListener mListener;
    private ChatMessageAdapter cAdapater;
    private final ArrayList<ChatDataParcelable> chatList =  new ArrayList();



    private BroadcastReceiver pushUpdateReceiver;

    private final String TAG = "GameChat";

    String firebaseToken;
    String gameId;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        //Bundle args = new Bundle();
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }


    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);


        ListView listView = v.findViewById(R.id.chat_fragment_listview);

        cAdapater = new ChatMessageAdapter(getActivity(), chatList);
        chatList.add(new ChatDataParcelable("Test","Test","Test"));



        cAdapater.notifyDataSetChanged();
        listView.setAdapter(cAdapater);

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setStackFromBottom(true);


        final EditText chatMessageInput = v.findViewById(R.id.chat_fragment_chatMessage);
        Log.i("TEST", "Test");

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



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    public void saveChatMessage(ChatDataParcelable message) {
        if (mListener != null) {
            mListener.onFragmentInteraction(message);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mListener.retrieveChatMessages();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChatFragmentInteractionListener) {
            mListener = (ChatFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ChatFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void sendMessage(ChatDataParcelable chatMessage) {
        chatList.add(chatMessage);
        cAdapater.notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ChatFragmentInteractionListener {
        void onFragmentInteraction(ChatDataParcelable chatDataParcelable);

        void retrieveChatMessages();
    }


    private void sendChatMessage(final String message) {

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        String gameId = String.valueOf(getActivity().getSharedPreferences(getString(R.string.gameData),Context.MODE_PRIVATE).getLong(getString(R.string.gameId), 0));
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

        VolleyRequestQueue.getInstance(getActivity()).addToRequestQueue(gameRequest);

    }
}
