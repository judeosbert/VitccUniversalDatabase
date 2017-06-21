package com.example.kleptomaniac.vitccuniversaldatabase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.InputStream;
import java.util.List;

/**
 * Created by kleptomaniac on 13/6/17.
 */

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.MyViewHolder> {

    private List<ContentRequest> requestList;
    private Context context;
    public View view;
    public boolean subscribed;
    public ContentAdapter(List<ContentRequest> requestList)
    {
        this.requestList = requestList;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_list_row,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final ContentRequest contentRequest = requestList.get(position);
        holder.movieName.setText(contentRequest.getMovieName());
        holder.fileType.setText("Language: "+contentRequest.getFileLanguage());
        holder.minQuality.setText("Minimum Quality: "+contentRequest.getMinQuality());
        holder.requestUserFullName.setText(contentRequest.getRequestingUser());
        holder.iHaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),contentRequest.getKey(),Toast.LENGTH_SHORT).show();

            }
        });

        holder.iNeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String contentKey = contentRequest.getKey();
                final String requestType = contentRequest.getRequestType();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                view = v;
                Log.e("VITCC", "I need it handler");
                subscribed = false;
                DatabaseReference ref = database.getReference("users/");
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String key = user.getEmail().toLowerCase().replace(".", ",").replace(" ", "");
                //We check if the user has already subscribed to this item for listening.
                ref.child(key).child("listening").child(requestType).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("VITCC LISTENING",dataSnapshot.toString());
                        Log.e("VITCC LISTENING",contentKey);
//                        Log.e("VITCC LISTENING",dataSnapshot.getValue().toString());
//                        Log.e("VITCC LISTENING", String.valueOf(dataSnapshot.hasChild(contentKey)));
                        if (dataSnapshot.hasChild(contentKey)) {
                            subscribed = true;
                            Log.e("VITCC", String.valueOf(subscribed));

                        }

                        handleEvent(subscribed,contentKey,requestType);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });
        new DownloadImageTask(holder.requestUserPic).execute(contentRequest.getRequestingUserPic());


    }

    private void handleEvent(boolean subscribed, final String contentKey, final String requestType) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref;
        if (!subscribed) {
            ref = database.getReference("requests");
            ref.child(requestType).child(contentKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ContentRequest request = dataSnapshot.getValue(ContentRequest.class);
                    String messageToken = FirebaseInstanceId.getInstance().getToken();
                    request.addPeer(messageToken);
                    addNewPeer(request, contentKey, requestType);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Snackbar.make(view,"You have already subscribed to this alerts",Snackbar.LENGTH_SHORT).show();
        }
    }

    private void addNewPeer(ContentRequest request, final String requestCode, final String finalRequestTypeValue)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("requests");
        ref.child(finalRequestTypeValue).child(requestCode).child("peers").setValue(request.getPeers(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null) {
                    addToListening(requestCode,finalRequestTypeValue);
                    Snackbar.make(view, "Your have subscribed to the list requesting this item.", Snackbar.LENGTH_SHORT).show();

                }
            }
        });

    }
    private void addToListening(String requestCode, String finalRequestTypeValue) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null)
        {
            Log.e("VITCC","NO user logged in.Goining to sign in");

        }
        DatabaseReference ref = database.getReference("users");
        String key = user.getEmail().toLowerCase().replace(".",",").replace(" ","");
        ref.child(key).child("listening").child(finalRequestTypeValue).child(requestCode).setValue(true);

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

    @Override
    public int getItemCount() {

        return requestList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView requestUserFullName,movieName,year,minQuality,fileType,requestTime;
        public ImageView requestUserPic;
        public Button iHaveButton,iNeedButton;

        public MyViewHolder(View view) {
            super(view);
            requestUserPic = (ImageView) view.findViewById(R.id.requestUserPic);
            requestUserFullName = (TextView) view.findViewById(R.id.requestUserName);
            movieName  = (TextView) view.findViewById(R.id.requestMovieName);
            year = (TextView) view.findViewById(R.id.requestMovieYear);
            requestTime = (TextView) view.findViewById(R.id.requestMovieYear);
            minQuality = (TextView) view.findViewById(R.id.requestMinQuality);
            fileType = (TextView) view.findViewById(R.id.requestFileType);
            iHaveButton = (Button) view.findViewById(R.id.iHaveButton);
            iNeedButton = (Button) view.findViewById(R.id.iNeedButton);




        }


    }




}
