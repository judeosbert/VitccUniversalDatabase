package com.example.kleptomaniac.vitccuniversaldatabase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        findViewById(R.id.signIn).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("VITCC", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("VITCC", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

    }
    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            updateUI(currentUser,false);
        }
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.signIn)
        {
            startGoogleSignIn();
        }

    }

    private void startGoogleSignIn() {


        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);


    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding user info");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user,true);
                    progressDialog.hide();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Authentication failed with Google",Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                    updateUI(null,false);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user,boolean newUser) {

    if(user != null)
    {
        String fullName = user.getDisplayName();
        String email = user.getEmail();
        String photoURL = user.getPhotoUrl().toString();
        SharedPreferences userData = getSharedPreferences("USERDATA",MODE_APPEND);
        SharedPreferences.Editor editor = userData.edit();
        editor.putString("fullName",fullName);
        editor.putString("email",email);
        editor.putString("photoURL",photoURL);
        editor.apply();

        if(newUser)
        {
            addUserToFirebaseDatabase(fullName,email,photoURL);
        }
        else {
            Toast.makeText(getApplicationContext(),"Welcome back "+user.getDisplayName(),Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,UserDashboard.class));
        }
        }


    }

    private void addUserToFirebaseDatabase(String fullName, String email, String photoURL) {

        String messageToken  = FirebaseInstanceId.getInstance().getToken();
        User user = new User(fullName,email,photoURL,messageToken);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userKey = email.toLowerCase().replace(".",",");
        DatabaseReference ref = database.getReference("users/"+userKey);
        ref.setValue(user);
        Intent intent = new Intent(this,CategoryChooserActivity.class);
        startActivity(intent);
        finish();


    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(getApplicationContext(),"Failed to connect to Database. Please check your internet connection",Toast.LENGTH_SHORT).show();

    }
}
