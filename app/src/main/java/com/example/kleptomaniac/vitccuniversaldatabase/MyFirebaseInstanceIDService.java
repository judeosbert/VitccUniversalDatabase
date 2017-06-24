package com.example.kleptomaniac.vitccuniversaldatabase;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by kleptomaniac on 16/6/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseDatabase database;
    public String oldToken;
    public String refreshedToken;
    @Override
    public void onTokenRefresh()
    {
        sendRegistrationToServer();
    }

    public void sendRegistrationToServer() {
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("VITCC ","TOken generator");
        mAuth  = FirebaseAuth.getInstance();

            user = mAuth.getCurrentUser();
        if(user != null) {
            Log.e("VITCC","User is not null in token generator");
            database = FirebaseDatabase.getInstance();
            String key = user.getEmail().toLowerCase().replace(".", ",");
            final DatabaseReference ref = database.getReference("users/" + key + "/messageToken");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    oldToken  = (String) dataSnapshot.getValue();
                    ref.setValue(refreshedToken);

                    //Set value to all listening requests and answered requests
                    Log.e("VITCC ", "Starting refershner for requests");

                    new refreshListeningRequests().execute();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });






        }
        else
        {
            Log.e("VITCC", "TOken generator user is null");
        }



    }




    private String classifyContent(String contentKey) {
    String [] categories = {"music","movie","series","game","other","document"};
        for(String cat:categories)
        {
            if(contentKey.contains(cat))
            {
                return cat;
            }
        }


    return null;
    }

    public class refreshListeningRequests extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("VITCC Refershner","Refershner background");
            user = mAuth.getCurrentUser();
            DatabaseReference reference = database.getReference("users");
            String userKey = user.getEmail().toLowerCase().replace(".",",").replace(" ","");
            reference.child(userKey).child("listening").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot d :dataSnapshot.getChildren())
                    {
                        final String contentKey = d.getKey();
                        final String requestType = classifyContent(contentKey);
                        Log.e("VITCC",contentKey+"Classified to "+requestType);

                        final DatabaseReference ref = database.getReference("requests");
                        ref.child(requestType).child(contentKey).child("peers").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot d:dataSnapshot.getChildren())
                                {
                                    Log.e("VITCC", String.valueOf(d.getValue()));
                                    Log.e("VITCC","Old Token Saved"+d.getValue());
                                    Log.e("VITCC","Variable OldTOken"+oldToken);
                                    Log.e("VITCC","Compairson"+d.getValue().equals(oldToken));
                                    if(d.getValue().equals(oldToken))
                                    {
                                        DatabaseReference reference = database.getReference("requests");
                                        reference.child(requestType).child(contentKey).child("peers").child(d.getKey()).setValue(refreshedToken, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if(databaseError == null)
                                                {
                                                    Log.e("VITCC","Completed updating the listening requests");
                                                }
                                                else
                                                {
                                                    Log.e("VITCC","Error on updating the listening requests");
                                                }
                                            }
                                        });
                                    }
                                }
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


            return null;
        }
    }
}
