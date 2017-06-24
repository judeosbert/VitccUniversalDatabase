package com.example.kleptomaniac.vitccuniversaldatabase;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by kleptomaniac on 7/6/17.
 */

public class User {
    public String fullName,email,photoURL,roomNo,mobileNumber,messageToken;
    public boolean music,movie,series,book,document,other;
    public boolean game;

    public User()
    {

    }
    public User(String fullName,String email,String photoURL)
    {
        this.fullName  = fullName;
        this.email = email;
        this.photoURL  = photoURL;
        this.roomNo = "";
        this.mobileNumber = "";
        this.messageToken = FirebaseInstanceId.getInstance().getToken();

    }
    public User(String fullName,String email,String photoURL,String messageToken)
    {
        this.fullName  = fullName;
        this.email = email;
        this.photoURL  = photoURL;
        this.roomNo = "";
        this.mobileNumber = "";
        this.messageToken = messageToken;


    }


    public void addTastes(boolean checked, boolean checked1, boolean checked2, boolean checked3, boolean checked4, boolean checked5,boolean game) {
        this.music =checked;
        this.movie  = checked1;
        this.series = checked2;
        this.book =  checked3;
        this.document = checked4;
        this.other = checked5;
        this.game = game;


    }
}
