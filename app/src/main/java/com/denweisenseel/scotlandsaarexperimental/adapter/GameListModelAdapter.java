package com.denweisenseel.scotlandsaarexperimental.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.denweisenseel.scotlandsaarexperimental.data.GameListInfoParcelable;
import com.denweisenseel.scotlandsaarexperimental.R;

import java.util.ArrayList;

/**
 * Created by denwe on 25.07.2017.
 */

public class GameListModelAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Activity activity;
    private static ArrayList<GameListInfoParcelable> gameListInfoParcelables;

    public GameListModelAdapter(Activity activity, ArrayList<GameListInfoParcelable> cm) {
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gameListInfoParcelables = cm;

    }

    @Override
    public int getCount() {
        return gameListInfoParcelables.size();
    }

    @Override
    public Object getItem(int i) {
        return gameListInfoParcelables.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView = view;
        if(convertView == null) {
            convertView = (LinearLayout) inflater.inflate(R.layout.game_list_item, null);

        } else {

        }

        TextView gameName = convertView.findViewById(R.id.game_list_item_gameName);
        TextView hostName = convertView.findViewById(R.id.game_list_item_hostName);
        TextView playerCount = convertView.findViewById(R.id.game_list_item_playerCount);


        gameName.setText(gameListInfoParcelables.get(i).getName());
        hostName.setText(gameListInfoParcelables.get(i).getHostName());
        playerCount.setText(String.valueOf(gameListInfoParcelables.get(i).getPlayerCount()));
        Log.v("GameListItem", hostName.getText().toString());
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        for(GameListInfoParcelable m : gameListInfoParcelables) {
            Log.v("VALUE",m.toString());
        }
        System.out.println(gameListInfoParcelables.toString());
        super.notifyDataSetChanged();
    }
}
