package com.denweisenseel.scotlandsaarexperimental.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.denweisenseel.scotlandsaarexperimental.R;
import com.denweisenseel.scotlandsaarexperimental.data.ChatDataParcelable;

import java.util.ArrayList;

/**
 * Created by denwe on 23.07.2017.
 */

public class ChatMessageAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private static ArrayList<ChatDataParcelable> chatMessageList;

    public ChatMessageAdapter(Activity activity, ArrayList<ChatDataParcelable> cm) {
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        chatMessageList = cm;

    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int i) {
        return chatMessageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return chatMessageList.indexOf(chatMessageList.get(i));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView = view;
        if(convertView == null) {
            convertView = (LinearLayout) inflater.inflate(R.layout.chat_message_item, null);



        } else {

        }

        TextView name = convertView.findViewById(R.id.ChatName);
        TextView message = convertView.findViewById(R.id.ChatMessage);
        TextView time = convertView.findViewById(R.id.ChatTime);

        name.setText(chatMessageList.get(i).getName());
        message.setText(chatMessageList.get(i).getMessage());
        time.setText(chatMessageList.get(i).getTime());


        return convertView;
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

}
