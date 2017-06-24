package com.example.kleptomaniac.vitccuniversaldatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class CategoryChooserActivity extends AppCompatActivity {
    private CheckBox musicBox,movieBox,seriesBox,gameBox,documentBox,otherBox;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = mAuth.getCurrentUser();
        setContentView(R.layout.activity_category_chooser);


        musicBox = (CheckBox)findViewById(R.id.musicBox);
        movieBox = (CheckBox)findViewById(R.id.movieBox);
        seriesBox = (CheckBox) findViewById(R.id.seriesBox);
        gameBox = (CheckBox) findViewById(R.id.gamesBox);
        documentBox = (CheckBox) findViewById(R.id.documentBox);
        otherBox  = (CheckBox) findViewById(R.id.othersBox);




    }



    public void next(View view)
    {
        SharedPreferences userData = getSharedPreferences("USERDATA",MODE_APPEND);
        String token = FirebaseInstanceId.getInstance().getToken();
        User user  =  new User(userData.getString("fullName",""),userData.getString("email",""),userData.getString("photoURL",""),token);
        user.addTastes(musicBox.isChecked(),movieBox.isChecked(),seriesBox.isChecked(),gameBox.isChecked(),documentBox.isChecked(),otherBox.isChecked(),gameBox.isChecked());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String key = userData.getString("email","").toLowerCase().replace(".",",");
        DatabaseReference ref  = database.getReference("users/"+key);
        ref.setValue(user);
        //Adding userkey to each category


            ref = database.getReference("category/music/"+key);
            ref.setValue(musicBox.isChecked());
            if(musicBox.isChecked())
            {
                FirebaseMessaging.getInstance().subscribeToTopic("music");
            }



            ref = database.getReference("category/movie/"+key);
            ref.setValue(movieBox.isChecked());
        if(movieBox.isChecked())
        {
            FirebaseMessaging.getInstance().subscribeToTopic("movie");
        }

            ref = database.getReference("category/series/"+key);
            ref.setValue(seriesBox.isChecked());
        if(seriesBox.isChecked())
        {
            FirebaseMessaging.getInstance().subscribeToTopic("series");
        }



            ref = database.getReference("category/game/"+key);
            ref.setValue(gameBox.isChecked());
        if(gameBox.isChecked())
        {
            FirebaseMessaging.getInstance().subscribeToTopic("game");
        }



            ref = database.getReference("category/document/"+key);
            ref.setValue(documentBox.isChecked());
        if(documentBox.isChecked())
        {
            FirebaseMessaging.getInstance().subscribeToTopic("document");
        }



            ref = database.getReference("category/other/"+key);
            ref.setValue(otherBox.isChecked());
        if(otherBox.isChecked())
        {
            FirebaseMessaging.getInstance().subscribeToTopic("other");
        }


        Intent intent = new Intent(this,UserAdded.class);
        startActivity(intent);
        finish();


    }
}
