package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class LeaderboardListAdapter extends ArrayAdapter<Player> {

    // Script Slide 181/182
    private static class ViewHolder {
        private TextView tvPlayername;
        private TextView tvPlayerrank;
        private TextView tvPlayerSoBe;
        private TextView tvPlayerpoints;
    }


    private static final String TAG = "PlayerListAdapter";

    private Context mContext;
    int mResource;

    public LeaderboardListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Player> objects) {
        super(context, resource, objects);
        this.mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.i("LeaderboardListAdapter", "getView started info");

        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            // Script Slide 181/182
            viewHolder = new ViewHolder();
            viewHolder.tvPlayerrank = convertView.findViewById(R.id.textView1);
            viewHolder.tvPlayername = convertView.findViewById(R.id.textView2);
            viewHolder.tvPlayerpoints = convertView.findViewById(R.id.textView3);
            viewHolder.tvPlayerSoBe = convertView.findViewById(R.id.textView4);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get Player information
        Player player = getItem(position);

        String playername = player.getPlayername();
        String points = String.valueOf(player.getPoints());
        String sobeValue = String.valueOf(player.getSoBe());
        String rank = String.valueOf(player.getRank());
        String p = mContext.getString(R.string.points);
        String sobe = mContext.getString(R.string.sobe);
        String sobeShow = sobeValue + sobe;
        String pointsShow = points + p;
        // Script Slide 181/182
        if (player != null) {
            viewHolder.tvPlayername.setText(playername);
            viewHolder.tvPlayerrank.setText(rank);
            viewHolder.tvPlayerpoints.setText(pointsShow);
            viewHolder.tvPlayerSoBe.setText(sobeShow);
        } else {
            viewHolder.tvPlayername.setVisibility(View.INVISIBLE);
            viewHolder.tvPlayerrank.setVisibility(View.INVISIBLE);
            viewHolder.tvPlayerpoints.setVisibility(View.INVISIBLE);
            viewHolder.tvPlayerSoBe.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
