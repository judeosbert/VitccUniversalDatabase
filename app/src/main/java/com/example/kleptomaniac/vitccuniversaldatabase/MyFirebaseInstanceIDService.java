package com.example.kleptomaniac.vitccuniversaldatabase;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by kleptomaniac on 16/6/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseDatabase database;
    @Override
    public void onTokenRefresh()
    {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }

    public void sendRegistrationToServer(String refreshedToken) {

        mAuth  = FirebaseAuth.getInstance();
        if(user != null) {
            user = mAuth.getCurrentUser();
            database = FirebaseDatabase.getInstance();
            String key = user.getEmail().toLowerCase().replace(".", ",");
            DatabaseReference ref = database.getReference("users/" + key + "/messageToken");
            ref.setValue(refreshedToken);
        }
        else
        {
            Log.e("VITCC","New user signing Up.TOken generated during login process;");
        }

    }
}
