package com.example.kleptomaniac.vitccuniversaldatabase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;

public class UserProfile extends AppCompatActivity {
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseDatabase database;
    public TextView fullName,mobile,room;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new DownloadImageTask((ImageView) findViewById(R.id.profileImage)).execute(String.valueOf(user.getPhotoUrl()));

        fullName = (TextView) findViewById(R.id.fullName);
//        mobile = (TextView) findViewById(R.id.mobileNumber);
//        room   = (TextView) findViewById(R.id.roomNo);

        getDataFromDatabase();
    }

    public void getDataFromDatabase(){
        String key = user.getEmail().toLowerCase().replace(".",",");
        DatabaseReference ref = database.getReference("users/"+key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                fullName.setText(user.fullName);
                if(user.mobileNumber.length() == 0)
                {
                    mobile.setText("Add your Mobile Number");
                }
                else
                    mobile.setText(user.mobileNumber);
                if(user.roomNo.length() == 0)
                {
                    room.setText("Add your room no");
                }
                else
                    room.setText(user.roomNo);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("VITCC","Cancelled from user profile");

            }
        });
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
