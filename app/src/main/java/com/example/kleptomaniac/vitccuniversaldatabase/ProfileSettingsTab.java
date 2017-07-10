package com.example.kleptomaniac.vitccuniversaldatabase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by kleptomaniac on 9/7/17.
 */

public class ProfileSettingsTab extends Fragment {

    public View rootView;
    Switch music,movie,series,game,document,other;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_settings_tab,container,false);
        this.rootView  = view;
        setDetails(view);
        return view;
    }

    private void setDetails(View view) {

        music = (Switch) view.findViewById(R.id.switch1);
        movie = (Switch) view.findViewById(R.id.switch2);
        series = (Switch) view.findViewById(R.id.switch3);
        document = (Switch) view.findViewById(R.id.switch4);
        game = (Switch) view.findViewById(R.id.switch5);
        other = (Switch) view.findViewById(R.id.switch6);

        FirebaseAuth mAUth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAUth.getCurrentUser();
        String userKey = user.getEmail().toLowerCase().replace(".",",");

        FirebaseDatabase database  = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");

        ref.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userClass = dataSnapshot.getValue(User.class);
                Log.e("VITCC","Settings tab"+userClass.music);

                if(userClass.music)
                {
                    music.setChecked(true);
                }
                if(userClass.movie)
                {
                    movie.setChecked(true);
                }
                if(userClass.series)
                {
                    series.setChecked(true);
                }
                if(userClass.game)
                {
                    game.setChecked(true);
                }
                if(userClass.document)
                {
                    document.setChecked(true);
                }
                
                setListeners();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(databaseError != null)
                    Log.e("VITCC","Database operation read user profile for notification settings cancelled");
            }
        });

    }

    private void setListeners() {
        
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("users");
        FirebaseAuth mAUth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAUth.getCurrentUser();
        final String userKey = user.getEmail().toLowerCase().replace(".",",");
        
        
        
        
        music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ref.child(userKey).child("music").setValue(isChecked);        
            }
        });

        movie.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ref.child(userKey).child("movie").setValue(isChecked);
            }
        });
        
        series.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ref.child(userKey).child("series").setValue(isChecked);
            }
        });

        document.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ref.child(userKey).child("document").setValue(isChecked);
            }
        });

        game.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ref.child(userKey).child("game").setValue(isChecked);
            }
        });

        other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ref.child(userKey).child("other").setValue(isChecked);
            }
        });
    
    
    }
}
