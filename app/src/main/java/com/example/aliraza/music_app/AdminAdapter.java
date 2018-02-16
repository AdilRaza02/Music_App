
/*

  Created By Adil Raza (22681), JingWen Yu (20614), Tasneem Kankroliwala (22202)
* Distributed Systems Project 2018
* Project: Music Player
* Date: 30 Jan 2018
* Submitted to Mr. Pedro Ribeiro.
* HSRW, Kamp-Lintfort
*

*/


// This Class is used to create a dynamic ListView in the Admin UI.

package com.example.aliraza.music_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class AdminAdapter extends ArrayAdapter<DatabaseCRUD> {


    FirebaseDatabase database;
    DatabaseReference myRef;
    MainActivity mActivity = new MainActivity();


    private Context mContext;
    int mResource;
    TextView vote;


    public AdminAdapter(Context context, int resource, List<DatabaseCRUD> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;


    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Retrieve data from the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users").child(mActivity.tempRoomNo);

        String songName = getItem(position).getSongName();
        String Votes = getItem(position).getVotes();


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        TextView sName = (TextView) convertView.findViewById(R.id.textView);
        vote = (TextView) convertView.findViewById(R.id.textView2);


        sName.setText(songName);
        vote.setText(Votes);


        return convertView;
    }
}

