
/*

  Created By Adil Raza (22681), JingWen Yu (20614), Tasneem Kankroliwala (22202)
* Distributed Systems Project 2018
* Project: Music Player
* Date: 30 Jan 2018
* Submitted to Mr. Pedro Ribeiro.
* HSRW, Kamp-Lintfort
*

*/


// This Class is used to create a dynamic ListView in the User UI.


package com.example.aliraza.music_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;


public class SongListAdapter extends ArrayAdapter<DatabaseCRUD> {


    FirebaseDatabase database;
    DatabaseReference myRef;
    static MainActivity mActivity = new MainActivity();


    private Context mContext;
    int mResource;
    TextView vote;


    static boolean check = false;

    public static int positions;
    public static int value;

    public SongListAdapter(Context context, int resource, List<DatabaseCRUD> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;


    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

//Database access for Like and Dislike Button Functionality
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users").child(mActivity.tempRoomNo);


        String songName = getItem(position).getSongName();
        String Votes = getItem(position).getVotes();


        DatabaseCRUD databaseCRUD = new DatabaseCRUD(songName, Votes);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        TextView sName = (TextView) convertView.findViewById(R.id.textView);
        vote = (TextView) convertView.findViewById(R.id.textView2);
        final Button sbutton = (Button) convertView.findViewById(R.id.button5);
        final Button sbutton2 = (Button) convertView.findViewById(R.id.button4);

// Logic of Like and Dislike (Enabling and Disabling)
        if (Integer.parseInt(Votes) == 0) {

            mActivity.stateMemoryL.set(mActivity.adapterMaxSortIndex.get(position), true);


        }


        if (mActivity.stateMemoryL.get(mActivity.adapterMaxSortIndex.get(position))) {

            sbutton2.setEnabled(false);

        } else {

            sbutton.setEnabled(false);


        }
        sName.setText(songName);
        vote.setText(Votes);

//Like Button Functionality
        sbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                VotingSystem(position);


                check = true;

                mActivity.stateMemoryL.set(mActivity.adapterMaxSortIndex.get(position), false);


            }
        });
//DisLike button Logic
        sbutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DownVotingSystem(position);

                mActivity.stateMemoryL.set(mActivity.adapterMaxSortIndex.get(position), true);
                check = false;


            }
        });


        return convertView;


    }

    // Like Button Logic Method
    public void VotingSystem(final int position) {
        Toast.makeText(mContext, "Button was clicked " + position, Toast.LENGTH_SHORT).show();


        //Database Access to add a vote
        myRef = database.getReference("Users").child(mActivity.tempRoomNo).child(Integer.toString(mActivity.adapterMaxSortIndex.get(position)));


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                String values = map.get("votes");


                value = Integer.parseInt(values);
                value = value + 1;
                positions = position;
                mActivity.position = position;

                myRef.child("votes").setValue(Integer.toString(value));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    //Dislike Button Logic Method
    public void DownVotingSystem(final int position) {
        Toast.makeText(mContext, "Button was clicked " + position, Toast.LENGTH_SHORT).show();


        //Database access to subtract the vote
        myRef = database.getReference("Users").child(mActivity.tempRoomNo).child(Integer.toString(mActivity.adapterMaxSortIndex.get(position)));


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                String values = map.get("votes");


                value = Integer.parseInt(values);
                value = value - 1;
                positions = position;


                myRef.child("votes").setValue(Integer.toString(value));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}

