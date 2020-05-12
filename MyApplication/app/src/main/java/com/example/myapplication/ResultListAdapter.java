package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ResultListAdapter extends ArrayAdapter<Result> {

    // Skript Slide 181/182
    private static class ViewHolder {
        private TextView tvPlayerID_white;
        private TextView tvPlayerID_black;
        private TextView tvRoundresult;
    }

    private static final String TAG = "ResultListAdapter";

    private Context mContext;
    int mResource;

    public ResultListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Result> objects) {
        super(context, resource, objects);
        this.mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Pattern Slide 181/182
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvPlayerID_white = convertView.findViewById(R.id.textView1);
            viewHolder.tvPlayerID_black = convertView.findViewById(R.id.textView2);
            viewHolder.tvRoundresult = convertView.findViewById(R.id.textView3);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int roundnumber = getItem(position).getRoundnumber();
        int playerID_white = getItem(position).getPlayerID_white(); //
        int playerID_black = getItem(position).getPlayerID_black();
        String result = getItem(position).getResult();


        Result result_obj = new Result(roundnumber, playerID_white, playerID_black, result);

        //Skript Slide 181/182
        if (result_obj != null) {
            viewHolder.tvPlayerID_white.setText(getplayernamebyid(playerID_white));
            viewHolder.tvPlayerID_black.setText(getplayernamebyid(playerID_black));
            viewHolder.tvRoundresult.setText(result);
        } else {
            viewHolder.tvPlayerID_white.setVisibility(View.INVISIBLE);
            viewHolder.tvPlayerID_black.setVisibility(View.INVISIBLE);
            viewHolder.tvRoundresult.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }


    private String getplayernamebyid(int id) {
        AddPlayersActivity Objtemp = new AddPlayersActivity();
        List<Player> list = Objtemp.doPlayerDataStuff(true);
        for (Player p : list) {
            if (p.getID() == id)
                return p.getPlayername();
        }
        return null;
    }

}
