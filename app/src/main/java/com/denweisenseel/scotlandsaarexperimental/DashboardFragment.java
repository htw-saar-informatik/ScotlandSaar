package com.denweisenseel.scotlandsaarexperimental;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DashboardFragment extends Fragment {

    private DashboardInteractionListener mListener;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button startGame = (Button) view.findViewById(R.id.dashboard_startGame);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartGameButtonPressed();
            }
        });

        Button makeMove = (Button) view.findViewById(R.id.dashboard_makeMoveTest);
        makeMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onMakeMove();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    public void onStartGameButtonPressed() {
        if (mListener != null) {
            mListener.onStartGame();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DashboardInteractionListener) {
            mListener = (DashboardInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DashboardInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface DashboardInteractionListener {
        void onStartGame();
        void onMakeMove();
    }

    public void setGameState(String player){
        TextView gameState = (TextView) this.getView().findViewById(R.id.dashboard_game_state);
        if (player.equals(getString(R.string.TURN_START_PLAYER))){
            gameState.setText("Player Turn");
        } else if (player.equals(getString(R.string.GAME_TURN_START_X))){
            gameState.setText("Mr X Turn");
        }
    }

    public void setPlayerType(Boolean isMrX){
        TextView playerState = (TextView) this.getView().findViewById(R.id.dashboard_player_type);
        if (isMrX){
            playerState.setText("You are Mr. X");
        } else{
            playerState.setText("You are a detective");
        }
    }

    public void showGameState(){
        TextView gameState = (TextView) this.getView().findViewById(R.id.dashboard_game_state);
        gameState.setVisibility(View.VISIBLE);

        TextView playerState = (TextView) this.getView().findViewById(R.id.dashboard_player_type);
        playerState.setVisibility(View.VISIBLE);
    }

    public void updateDistance(Float distance){
        TextView gameState = (TextView) this.getView().findViewById(R.id.dashboard_distance);
        gameState.setText(distance.toString()+"m");
    }
}
