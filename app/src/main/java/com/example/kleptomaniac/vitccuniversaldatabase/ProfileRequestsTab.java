package com.example.kleptomaniac.vitccuniversaldatabase;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by kleptomaniac on 25/6/17.
 */

public class ProfileRequestsTab extends Fragment {
    private List<ContentRequest> requestList = new ArrayList<>();
    public RecyclerView recyclerView;
    public ProfileRequestsAdapter profileRequestsAdapter;
    public View view;
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,Bundle savedInstance)
    {
        View view = layoutInflater.inflate(R.layout.profile_requests_tab,container,false);
        this.view = view;
        start(view);
        return  view;

    }

    private void start(View view) {
    recyclerView = (RecyclerView) view.findViewById(R.id.userRequestsRecyclerView);
        profileRequestsAdapter = new ProfileRequestsAdapter(requestList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        recyclerView.setAdapter(profileRequestsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),LinearLayoutManager.VERTICAL));

        populateData();


    }

    private void populateData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userKey = user.getEmail().replace(".",",");
        DatabaseReference ref = database.getReference("users");
        ref.child(userKey).child("listening").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 0)
                {
                    Snackbar.make(view,"Your have not made any request",Snackbar.LENGTH_LONG).show();
                    return;
                }
                for(DataSnapshot d:dataSnapshot.getChildren()) {
                    String contentKey = d.getKey();
                    String contentType = classify(contentKey);
                    if(contentType == null)
                    {
                        return;
                    }

                    DatabaseReference dbReference = database.getReference("requests");
                    dbReference.child(contentType).child(contentKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String lang=null,key=null,minQuality=null,itemName=null,requestTime=null,type=null,requestingUser=null,requestingUserEmail=null,requestingUserPic=null,year = null;
                            for(DataSnapshot snap:dataSnapshot.getChildren())
                            {
                                Log.e("VITCC","Nested rewquest puller"+snap);
//                                ContentRequest request = snap.getValue(ContentRequest.class);
//                                requestList.add(request);

                                switch (snap.getKey())
                                {
                                    case "fileLanguage":
                                        lang = (String) snap.getValue();
                                        break;
                                    case "key":
                                        key = (String) snap.getValue();
                                        break;
                                    case "minQuality":
                                        minQuality = (String) snap.getValue();
                                        break;
                                    case "movieName":
                                        itemName = (String) snap.getValue();
                                        break;
                                    case "requestTime":
                                        requestTime = (String) snap.getValue();
                                        break;
                                    case "requestType":
                                        type = (String) snap.getValue();
                                        break;
                                    case "requestingUser":
                                        requestingUser = (String) snap.getValue();
                                        break;
                                    case "requestingUserEmail":
                                        requestingUserEmail = (String) snap.getValue();
                                        break;
                                    case "requestingUserPic":
                                        requestingUserPic = (String) snap.getValue();
                                        break;
                                    case "year":
                                        year = (String) snap.getValue();
                                        break;
                                }

                            }

                            ContentRequest request = new ContentRequest(key,type,requestingUserEmail,requestingUser,requestingUserPic,itemName,year,minQuality,lang,null);
                            requestList.add(request);
                            Log.e("VITCC","Data preparation List size"+requestList.size());
                            Log.e("VITCC","Notifier from profile");
                            profileRequestsAdapter.notifyItemInserted(requestList.size()-1);
//                            profileRequestsAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }


                });
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
