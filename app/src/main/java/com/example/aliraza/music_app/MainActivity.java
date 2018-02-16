

/*

  Created By Adil Raza (22681), JingWen Yu (20614), Tasneem Kankroliwala (22202)
* Distributed Systems Project 2018
* Project: Music Player
* Date: 30 Jan 2018
* Submitted to Mr. Pedro Ribeiro.
* HSRW, KampLintfort
*

*/

/*
    For FireBase Database Access
* Username: crazybeats2018@gmail.com
* Password: crazybeats12345678
* Url: firebase.google.com
*
* */



package com.example.aliraza.music_app;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    // Database Save and Retrieve Functionality Variables
    FirebaseDatabase database;
    DatabaseReference myRef;
    public static List<DatabaseCRUD> myList = new ArrayList<DatabaseCRUD>();
    public static List<DatabaseCRUD> myList2 = new ArrayList<DatabaseCRUD>();
    public static DataSnapshot gDatasnapshot;
    public static String sNumA;
    public static boolean dCheck = false;
    // Sort of the data
    public static List<Integer> Sort = new ArrayList<Integer>();
    //Max Votes Mechanism
    public static int maxVote;
    public static int indexMaxVote;
    //Media Player Functionality
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static Button playButton;
    public static Button stopButton;
    public static Button pauseButton;
    public static Button adminLogoutButton;
    public static Button nLogoutButton;
    public static TextView currentSongA;
    public static int duration;
    public static boolean stopped = false;
    int position = 0;


    //For Adapter
    public static List<Integer> adapterMaxSortIndex = new ArrayList<Integer>();
    List<DatabaseCRUD> adapterDisplayList = new ArrayList<DatabaseCRUD>();


    //For Like/Dislike Buttons
    public static ArrayList<Boolean> stateMemoryL = new ArrayList<Boolean>();

    // DatabaseCRUD making zero
    public static String zeroRoomNo;

    //Current Song check
    public static boolean boolCurrentSong = false;


    //Update Current Song Check;
    public static String currentSongRoomNo;

    //Login UI Elements
    EditText eRoom;
    EditText eAdmin;

    //User/Room UI Logic
    public static String tempRoomNo;
    ListView listView;
    int dummy = 1;


    //Replay Mechanism
    ArrayList<String> checkReplay = new ArrayList<String>();
    int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_ui);
        Log.wtf("My First", "Git");

        //Logo Title Bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_foreground);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


//Main UI fields
        eRoom = (EditText) findViewById(R.id.eRoom);
        eAdmin = (EditText) findViewById(R.id.eRoomAdmin);

        Button eButton = (Button) findViewById(R.id.button2);
        final Button nButton = (Button) findViewById(R.id.button3);
        Button adminButton = (Button) findViewById(R.id.buttonAdmin1);

//--------------------------------------------- Enter Room Key Functionality-------------------------------------------------------------------
        eButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final List<DatabaseCRUD> displayList = new ArrayList<DatabaseCRUD>();
                final List<String> tempSort = new ArrayList<String>();
                final List<DatabaseCRUD> tempDisplayList = new ArrayList<DatabaseCRUD>();
                final String roomNO = eRoom.getText().toString();
                final List<String> retrieveSongs = new ArrayList<String>();
                zeroRoomNo = roomNO;


                if (!roomNO.equals("")) {// Error Handling

                    //Database Access
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("Users");

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            Log.wtf("encountered", "DataSnapshot");


                            if (dataSnapshot.hasChild(roomNO)) {


                                Log.w("Status", "Yes");
                                tempRoomNo = roomNO;
                                currentSongRoomNo = roomNO;

                                boolCurrentSong = true;
                                myRef = database.getReference("Users").child(roomNO);


                                myRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
//Clearing for next operation (i.e. Retrieving new data from the database )
                                        displayList.clear();
                                        tempDisplayList.clear();
                                        tempSort.clear();
                                        Sort.clear();
                                        myList.clear();
                                        myList2.clear();

                                        for (DataSnapshot child : dataSnapshot.getChildren()) {//Loop to retrieve data from Database

                                            DatabaseCRUD databaseCRUD = child.getValue(DatabaseCRUD.class);

                                            displayList.add(databaseCRUD);

                                            tempSort.add(databaseCRUD.getVotes());
                                            retrieveSongs.add(databaseCRUD.getSongName());

                                        }


                                        myList.addAll(listOrder(tempSort, displayList));
                                        myList2.addAll(myList);

                                        if (dummy == 1) {
                                            stateMemoryL.clear();
                                            for (int i = 0; i < myList.size(); i++) {

                                                stateMemoryL.add(true);

                                                dummy++;
                                            }
                                        }
//Switching to the User Activity
                                        setContentView(R.layout.user_panel);

                                        getCurrentSongIndex(retrieveSongs);

// Showing the dynamic ListView
                                        listView = (ListView) findViewById(R.id.list);

                                        SongListAdapter adapter = new SongListAdapter(MainActivity.this, R.layout.user_listview, myList2);

                                        listView.setAdapter(adapter);


//Logout Button Functionality
                                        nLogoutButton = (Button) findViewById(R.id.logout);
                                        nLogoutButton.setText("Logout " + roomNO);

                                        nLogoutButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                recreate();


                                            }
                                        });


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            } else {
                                Log.d("Status", "NO");


                                dialogMsg("Invalid Room No", "Please try again!");

                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {

                    dialogMsg("Please Enter the Room No!", "");


                }


            }//onclick ebutton ends


        });//ebutton ends


//------------------------------------------------------------ Mechanism of the New Room Creation------------------------------------------------------------------------


        nButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!dCheck) {
                    directoryCheck(nButton); //Songs added in the directory. CHECK!!


                } else if (dCheck) {
                    dCheck = !dCheck;
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();


                    //Admin Key Generation

                    int minA = 1001;
                    int maxA = 10000;
                    Random rA = new Random();
                    int rNumA = rA.nextInt(maxA - minA + 1) + minA;
                    sNumA = (String) String.valueOf(rNumA);


                    // Room Number Generation

                    int min = 100;
                    int max = 1000;
                    Random r = new Random();
                    int rNum = r.nextInt(max - min + 1) + min;
                    final String sNum = Integer.toString(rNum);

//Database connection
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("Admin").child(sNumA);
                    myRef.child("RoomNo").setValue(sNum);


                    alertDialog.setTitle("Your Admin Key is " + sNumA);
                    alertDialog.setMessage("Your Room Number is " + sNum);

                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    String tempKeyMerger = "Admin:" + sNumA + " Room:" + sNum;
                                    snapshotKeys(tempKeyMerger, sNumA); //Saving keys on the device.


                                    database = FirebaseDatabase.getInstance();
                                    myRef = database.getReference("Users");


                                    File sd = Environment.getExternalStorageDirectory();// Device Memory Access
                                    File location = new File(sd.getAbsolutePath() + "/MusicPlayer/AudioFiles");

                                    File[] dirFiles = location.listFiles();


                                    for (int i = 0; i < dirFiles.length; i++) {//Adding Songs in the Database

                                        String path = dirFiles[i].toString();
                                        String child = path.substring(path.lastIndexOf("/") + 1);
                                        int pos = child.lastIndexOf(".");
                                        if (pos > 0 && pos < (child.length() - 1)) {
                                            child = child.substring(0, pos);
                                        }
                                        String index = Integer.toString(i);
                                        myRef = database.getReference("Users").child(sNum).child(index);
                                        myRef.child("songName").setValue(child);
                                        int temp = 0;
                                        myRef.child("votes").setValue(Integer.toString(temp));

                                    }


                                    dialog.dismiss();

                                    retrieveData(sNum); // Retrieving Data from the Database to show in the next Activity
                                    setContentView(R.layout.admin_panel);// Admin Activity

                                    //Media Functionlity Buttons Initialization
                                    playButton = (Button) findViewById(R.id.play);
                                    pauseButton = (Button) findViewById(R.id.pause);
                                    stopButton = (Button) findViewById(R.id.stop);
                                    adminLogoutButton = (Button) findViewById(R.id.adminlogout);
                                    adminLogoutButton.setText("Logout " + sNum);

// Call to Media Functionality button Method
                                    buttonFunctionality(playButton, pauseButton, stopButton, adminLogoutButton);


                                }
                            });
                    alertDialog.show();

                }
            }
        });

// ---------------------------------------------------Functionality of Admin Access---------------------------------------------------------------------------------
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String adminKey = eAdmin.getText().toString();

                if (!adminKey.equals("")) {// Blank error check

                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("Admin");

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(adminKey)) {// Admin Key exist in database check
                                myRef = myRef.child(adminKey);

                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        Map<String, String> map = (Map<String, String>) dataSnapshot.getValue(); // Retrieving assigned RoomNo to the admin key

                                        String roomNo = map.get("RoomNo");
                                        Log.wtf("Room", roomNo);

                                        retrieveData(roomNo); //Retrieving Songlist
                                        setContentView(R.layout.admin_panel);// Changing Activity

                                        //Media Player Button Initialization
                                        playButton = (Button) findViewById(R.id.play);
                                        pauseButton = (Button) findViewById(R.id.pause);
                                        stopButton = (Button) findViewById(R.id.stop);
                                        adminLogoutButton = (Button) findViewById(R.id.adminlogout);
                                        adminLogoutButton.setText("Logout " + roomNo);
//Media Control Functionality Method Call
                                        buttonFunctionality(playButton, pauseButton, stopButton, adminLogoutButton);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            } else {
                                Log.d("Status", "NO");


                                dialogMsg("Invalid Admin Key", "Please try again!");

                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {

                    dialogMsg("Please Enter the Admin Key!", "");

                }


            }
        });


    }


    //----------------------------------------------------------------METHODS-----------------------------------------------------------------------------------------------------------------

    // Media Control
    public void buttonFunctionality(final Button playButton, final Button pauseButton, final Button stopButton, final Button adminLogoutButton) {


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (stopped == true) {
                    stopped = false;
                    mediaPlayer.seekTo(duration);
                    mediaPlayer.start();
                    playButton.setEnabled(false);
                    pauseButton.setEnabled(true);
                    stopButton.setEnabled(true);

                } else {
                    maxVotesRetrieve(gDatasnapshot); // Finding the Song with Max Votes; DataSnaphot contains the data retrieved from the Database.
                    pauseButton.setEnabled(true);
                    stopButton.setEnabled(true);
                    playButton.setEnabled(false);
                }

            }

        });


        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pauseButton.setEnabled(false);
                playButton.setEnabled(true);

                pauseMusic();


            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pauseButton.setEnabled(false);
                stopButton.setEnabled(false);
                playButton.setEnabled(true);
                FirebaseDatabase tempDatabaseAccess = FirebaseDatabase.getInstance();
                DatabaseReference tempMyRef = tempDatabaseAccess.getReference("CurrentSong").child(currentSongRoomNo);
                tempMyRef.child("CurrentIndex").setValue("Stay Tuned!"); // Current Song Status changes if the Music stops for User
                currentSongA = (TextView) findViewById(R.id.currentSongA);
                currentSongA.setText("Play the Playlist!"); // Current Song Status for Admin
                stopMusic();
                if (mediaPlayer.isPlaying())
                    stopButton.performClick();


            }
        });


        adminLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer != null) {

                    stopMusic();
                    if (mediaPlayer.isPlaying())
                        adminLogoutButton.performClick();
                    stopButton.performClick();
                    recreate();

                } else {

                    recreate();

                }


            }
        });


    }

    //Dialog box for the Instructions or Warnings
    public void dialogMsg(String tittle, String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(tittle);
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        eRoom.setText("");
                        eAdmin.setText("");
                        dialog.dismiss();
                    }
                });
        alertDialog.show();


    }

    // Retrieving the Data from Database using Room No.
    public void retrieveData(String sNum) {


        final List<DatabaseCRUD> displayList = new ArrayList<DatabaseCRUD>();
        final List<String> tempSort = new ArrayList<String>();
        final String roomNO = sNum;
        currentSongRoomNo = sNum;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Log.d("Status", "Yes");
                tempRoomNo = roomNO;


                myRef = database.getReference("Users").child(roomNO);


                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//Clearing for next operation (i.e. Retrieving new data from the database )
                        displayList.clear();
                        tempSort.clear();
                        Sort.clear();
                        myList.clear();
                        for (DataSnapshot child : dataSnapshot.getChildren()) { //Loop to Retrieve data from the database

                            DatabaseCRUD databaseCRUD = child.getValue(DatabaseCRUD.class);
                            displayList.add(databaseCRUD);

                            tempSort.add(databaseCRUD.getVotes());


                        }


                        myList.addAll(listOrder(tempSort, displayList));


//Showing of the Dynamic ListView in the Activity
                        listView = (ListView) findViewById(R.id.adminlist);

                        AdminAdapter adapter;
                        adapter = new AdminAdapter(MainActivity.this, R.layout.admin_listview, myList);
                        listView.setAdapter(adapter);
                        gDatasnapshot = dataSnapshot;


                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }// retrieve data function

    // Logic for finding the song with the max votes
    public void maxVotesRetrieve(DataSnapshot dataSnapshot) {

        final List<String> retrieveVotes = new ArrayList<String>();
        final List<Integer> intRetrievevotes = new ArrayList<Integer>();
        final List<String> retrieveSongNames = new ArrayList<String>();

        // Access the TextView of the different Activity
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(R.layout.user_panel, null);
        TextView currentSong = (TextView) vi.findViewById(R.id.textView5);

//Clearing for next operation (i.e. Retrieving new data from the database )
        retrieveVotes.clear();
        intRetrievevotes.clear();
        retrieveSongNames.clear();
        for (DataSnapshot child : dataSnapshot.getChildren()) { //Loop to retrieve data from the database

            DatabaseCRUD databaseCRUD = child.getValue(DatabaseCRUD.class);

            retrieveVotes.add(databaseCRUD.getVotes());
            retrieveSongNames.add(databaseCRUD.getSongName());
        }

// Song with max votes logic
        Log.d("Retrieve DatabaseCRUD", retrieveVotes.toString());
        for (String s : retrieveVotes) intRetrievevotes.add(Integer.valueOf(s));
        maxVote = Collections.max(intRetrievevotes);
        indexMaxVote = intRetrievevotes.indexOf(maxVote);
        Log.d("Max Vote", Integer.toString(maxVote));
        Log.d("Max Vote Index", Integer.toString(indexMaxVote));

        List<String> list;
        list = new ArrayList<>();

// Access to the Memory
        File sd = Environment.getExternalStorageDirectory();
        File location = new File(sd.getAbsolutePath() + "/MusicPlayer/AudioFiles");

        File[] dirFiles = location.listFiles();

//Media Player
        for (int i = 0; i < dirFiles.length; i++) {

            String path = dirFiles[i].toString();
            list.add(path);// List containing the path of the songs in the Playlist.


        }

        mediaPlayer = new MediaPlayer();

        try {

// Votes to Zero, if the song has already been played.
            myRef.child(Integer.toString(indexMaxVote)).child("votes").setValue(Integer.toString(0));


            //Songs with no votes
            if (intRetrievevotes.get(indexMaxVote) == 0) {

                int index = replayMethod(retrieveSongNames, indexMaxVote);
                mediaPlayer.setDataSource(list.get(index));
                indexMaxVote = index;


            } else {
//song with votes
                mediaPlayer.setDataSource(list.get(indexMaxVote));

            }
// Textview field of current playing song (Status changes)
            currentSongA = (TextView) findViewById(R.id.currentSongA);
            currentSongA.setText("Currently Playing: " + retrieveSongNames.get(indexMaxVote));


            mediaPlayer.prepare();
            //   stateMemoryL.set(adapterMaxSortIndex.get(indexMaxVote), true);
            database = FirebaseDatabase.getInstance();
// Writing the current song index to the database.
            FirebaseDatabase tempDatabaseAccess = FirebaseDatabase.getInstance();
            DatabaseReference tempMyRef = tempDatabaseAccess.getReference("CurrentSong").child(currentSongRoomNo);
            tempMyRef.child("CurrentIndex").setValue(Integer.toString(indexMaxVote));


            mediaPlayer.start();


        } catch (Exception ex) {


            Log.d("Problem", ex.toString());

        }


        Log.d("max votes", Integer.toString(maxVote));

// Next song after first is completed (Recursive Call)
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {


                maxVotesRetrieve(gDatasnapshot);


            }
        });


    }

    public void pauseMusic() {


        stopped = true;
        duration = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();


    }


    public void stopMusic() {


        mediaPlayer.stop();


    }

    // Creating image (picture) of the keys. !CODE COPIED!
    public void snapshotKeys(String keys, String SnumA) {

        float textSize = 30;
        String text = keys;
        TextPaint tp = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        tp.setColor(Color.WHITE);
        tp.setTextSize(textSize);
        Rect bounds = new Rect();
        tp.getTextBounds(text, 0, text.length(), bounds);
        StaticLayout sl = new StaticLayout(text, tp, bounds.width() + 5,
                Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);

        Bitmap bmp = Bitmap.createBitmap(bounds.width() + 5, bounds.height() + 5,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        sl.draw(canvas);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);


        byte[] image = stream.toByteArray();

        saveImage(bmp, SnumA);


    }

    // Storing the image of the keys in the device memory.
    public void saveImage(Bitmap bmp, String SnumA) {

        Log.i("SAVE IMAGE", "start save");
        File sd = Environment.getExternalStorageDirectory();

        File createFolder = new File(sd.getAbsolutePath() + "/MusicPlayer/AudioFiles");
        if (!createFolder.exists()) {
            createFolder.mkdirs();
        }

        File location = new File(sd.getAbsolutePath() + "/MusicPlayer/keys");

        if (!location.exists()) {
            location.mkdirs();
        }

        location.mkdir();
        File dest = new File(location, SnumA + ".PNG");

        try {

            Log.i("SAVE IMAGE", "trying to save: " + dest.getPath());
            FileOutputStream fos = new FileOutputStream(dest);

            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("error", e.getMessage());
            e.printStackTrace();

        }


    }

    // Directory has Music Check
    public void directoryCheck(final Button nButton) {


        boolean check = false;
        final File sd = Environment.getExternalStorageDirectory();

        final File createFolder = new File(sd.getAbsolutePath() + "/MusicPlayer/AudioFiles");

        if (!createFolder.exists()) { // Directory Already exist check
            createFolder.mkdirs(); // If not, create new.
        }


        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        final AlertDialog alertDialog2 = new AlertDialog.Builder(MainActivity.this).create();

        alertDialog.setTitle("Move mp3 to MusicPlayer/AudioFiles folder");
        alertDialog.setMessage("then click OK!");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        File location = new File(sd.getAbsolutePath() + "/MusicPlayer/AudioFiles");

                        File[] dirFiles = location.listFiles();

                        if (dirFiles.length == 0) { // Check if the Audio Files exists
                            alertDialog2.setTitle("AudioFiles folder found empty!");
                            alertDialog2.setMessage("Please try again!");

                            alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();
                                            recreate();

                                        }
                                    });
                            alertDialog2.show();


                        } else {

                            dCheck = true;
                            nButton.performClick();

                        }
                    }
                });
        alertDialog.show();


    }

    // List Order Logic
    public List<DatabaseCRUD> listOrder(List<String> tempSort, List<DatabaseCRUD> displayList) {
        adapterMaxSortIndex.clear();

//Sorting
        for (String s : tempSort) Sort.add(Integer.valueOf(s));

        List<Integer> temp = new ArrayList<Integer>();
        List<Integer> maxSortIndex = new ArrayList<Integer>();
        List<DatabaseCRUD> tempDisplayList = new ArrayList<DatabaseCRUD>();
        temp.addAll(Sort);


        int max;
        int indexMax;
// The song with Max votes
        for (int i = 0; i < temp.size(); i++) {
            max = Collections.max(temp);
            indexMax = temp.indexOf(max);
            maxSortIndex.add(indexMax);
            temp.set(indexMax, -10);

        }

        for (int j = 0; j < temp.size(); j++) {

            tempDisplayList.add(displayList.get(maxSortIndex.get(j)));

        }
        adapterDisplayList.addAll(displayList);
        adapterMaxSortIndex.addAll(maxSortIndex);

        return tempDisplayList;

    }


    // Current Song Index Logic for the Current Song TextView Display
    public void getCurrentSongIndex(final List<String> SongList) {

// Retrieving Data from Database
        FirebaseDatabase tempDatabaseAccess = FirebaseDatabase.getInstance();
        final DatabaseReference tempMyRef = tempDatabaseAccess.getReference("CurrentSong");

        tempMyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(currentSongRoomNo)) {

                    DatabaseReference tempMyRef1 = tempMyRef.child(currentSongRoomNo);

                    tempMyRef1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                            String currentSongIndex = map.get("CurrentIndex");//currentSongRoom);


                            TextView currentSong = (TextView) findViewById(R.id.textView5);
// When nothing has been played.
                            if (currentSongIndex.equals("Stay Tunned!")||currentSongIndex.equals("Stay Tuned!"))
                                currentSong.setText("Currently Playing: " + currentSongIndex);
                            else {
                                // Using Song List retrieving the Song Name to display.
                                Log.wtf("Checkkkk", currentSongIndex);//currentSongIndex);
                                currentSong.setText("Currently Playing: " + SongList.get(Integer.parseInt(currentSongIndex)));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {

// Recursive call if nothing has been played.
                    getCurrentSongIndex(SongList);


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    // Replay SongList when there are Zero Votes
    public int replayMethod(List<String> songList, int indexMaxVote) {


// Simple one-by-one Logic

        if (checkReplay.size() == songList.size()) {


            checkReplay.clear();
            checkReplay.add(songList.get(0));

        } else
            checkReplay.add(songList.get(indexMaxVote));

        i = checkReplay.size() - 1;

        return i;


    }


}//class ends





