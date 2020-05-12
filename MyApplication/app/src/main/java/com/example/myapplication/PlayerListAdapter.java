package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class PlayerListAdapter extends ArrayAdapter<Player> {

    // Skript Slide 181/182
    private static class ViewHolder {
        private TextView tvPlayername;
        private TextView tvParticipate;
    }

    private static final String TAG = "PlayerListAdapter";

    private Context mContext;
    int mResource;

    public PlayerListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Player> objects) {
        super(context, resource, objects);
        this.mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.i("PlayerListAdapter", "getView started info");
        // Pattern Skript Slide 181 / 182
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            // Skript Slide 181/182
            viewHolder = new ViewHolder();
            viewHolder.tvPlayername = convertView.findViewById(R.id.textView1);
            viewHolder.tvParticipate = convertView.findViewById(R.id.textView2);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        // get Player informationen
        String playername = getItem(position).getPlayername();
        Boolean participate = getItem(position).getParticipation();
        String participatestring = String.valueOf(participate); //der Boolean participate wird in einen String umgewandelt, um im Textfeld angezeigt werden zu können
        Player player = new Player(playername, participate);

        // Skript Slide 181/182
        if (player != null) {
            viewHolder.tvPlayername.setText(playername);
            if (participatestring == "true") {
                viewHolder.tvParticipate.setText(R.string.participate);
            } else viewHolder.tvParticipate.setText(R.string.pause);
        } else {
            viewHolder.tvPlayername.setVisibility(View.INVISIBLE);
            viewHolder.tvParticipate.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
