/*

  Created By Adil Raza (22681), JingWen Yu (20614), Tasneem Kankroliwala (22202)
* Distributed Systems Project 2018
* Project: Music Player
* Date: 30 Jan 2018
* Submitted to Mr. Pedro Ribeiro.
* HSRW, Kamp-Lintfort
*

*/



/*

  Created By Adil Raza (22681), JingWen Yu (20614), Tasneem Kankroliwala (22202)
* Distributed Systems Project 2018
* Project: Music Player
* Date: 30 Jan 2018
* Submitted to Mr. Pedro Ribeiro.
* HSRW, Kamp-Lintfort
*

*/



//This Class is used to perform operations in the Database
//This Class is used to make objects of a defined database structure to save the data retrieved from the database.


package com.example.aliraza.music_app;
import android.widget.Button;

import com.google.firebase.database.Exclude;


public class DatabaseCRUD {

    public String songName;
    public String votes;
    public Button button;

    public Button getButton2() {
        return button2;
    }

    public void setButton2(Button button2) {
        this.button2 = button2;
    }

    public Button button2;


    public DatabaseCRUD() {
    }

    public DatabaseCRUD(String songName, String votes/*, Button button*/) {
        this.songName = songName;
        this.votes = votes;

    }


    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }


    public String getSongName() {
        return songName;
    }

    public String getVotes() {
        return votes;
    }

    public Button getButton() {
        return button;
    }

}
