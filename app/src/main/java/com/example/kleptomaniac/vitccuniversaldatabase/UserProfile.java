package com.example.kleptomaniac.vitccuniversaldatabase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfile extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseDatabase database;
    private String currentUserEmail;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Information"));
        tabLayout.addTab(tabLayout.newTab().setText("Requests"));
        tabLayout.addTab(tabLayout.newTab().setText("Settings"));
        tabLayout.addTab(tabLayout.newTab().setText("About"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        ProfileTabAdapter adapter = new ProfileTabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.setOnTabSelectedListener(this);
        tabLayout.setupWithViewPager(viewPager);



        database = FirebaseDatabase.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPreferences = this.getSharedPreferences("PROFILE_VISIT", Context.MODE_APPEND);
        currentUserEmail = sharedPreferences.getString("EMAIL","");
        String[] userName = currentUserEmail.split("@");
        getSupportActionBar().setTitle(userName[0]);


    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("PROFILE_VISIT", Context.MODE_APPEND);
        currentUserEmail = sharedPreferences.getString("EMAIL","");
        Log.e("VITCC","Profile Tab On Selected"+tab.getPosition()+"CURRENT USER "+user.getDisplayName());
        Log.e("VITCC ","Current User"+currentUserEmail);
        if(!currentUserEmail.equals(user.getEmail()) && tab.getPosition() != 3)
        {
            viewPager.setCurrentItem(0);
//            Toast.makeText(getApplicationContext(),"That doesn't concern you",Toast.LENGTH_SHORT).show();
        }
        else
            viewPager.setCurrentItem(tab.getPosition());


    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }




}
