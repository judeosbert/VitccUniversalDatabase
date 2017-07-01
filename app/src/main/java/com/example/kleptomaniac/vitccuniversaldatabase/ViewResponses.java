package com.example.kleptomaniac.vitccuniversaldatabase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.ScaleInTopAnimator;

import static android.view.View.GONE;

public class ViewResponses extends AppCompatActivity {
    public Toolbar toolbar;
    String contentKey;

    private List<Answers> answerList = new ArrayList<>();
    RecyclerView recyclerView;
    ResponsesAdapter responsesAdapter;
    LinearLayout noResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_responses);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        Intent intent  = getIntent();
        contentKey = intent.getStringExtra("CONTENTKEY");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("View Responses");

        recyclerView = (RecyclerView) findViewById(R.id.responsesRecylerView);
        responsesAdapter = new ResponsesAdapter(answerList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        noResponse = (LinearLayout) findViewById(R.id.noResponses);
        noResponse.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new ScaleInTopAnimator());
        recyclerView.setAdapter(responsesAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));


        populateData();






    }

    private void populateData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("requests");
        String requestType = classify(contentKey);
        ref.child(requestType).child(contentKey).child("answers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 0)
                {
                    noResponse.setVisibility(View.VISIBLE);
                }
                else
                {
                    noResponse.setVisibility(GONE);
                }
                for (DataSnapshot d:dataSnapshot.getChildren())
                {
                    Log.e("VITCC"," Responses Value"+d.toString());
                    Answers answer = d.getValue(Answers.class);
                    answerList.add(answer);
                    responsesAdapter.notifyDataSetChanged();

                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private String classify(String contentKey) {
        String[] categories = new String[]{"music","movie","series","game","document","other"};
        for(String cat:categories)
        {
            if(contentKey.contains(cat))
                return cat;
        }
        return null;
    }

}
