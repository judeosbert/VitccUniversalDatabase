package com.example.kleptomaniac.vitccuniversaldatabase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by kleptomaniac on 29/6/17.
 */

public class ProfileRequestsAdapter extends RecyclerView.Adapter<ProfileRequestsAdapter.MyViewHolder> {
    private List<ContentRequest> requestList;
    private Context context;
    public View view;
    public boolean subscribed;
    public ProfileRequestsAdapter(List<ContentRequest> requestList)
    {
        this.requestList = requestList;
    }
    public ContentRequest object;




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_request_list_row,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final ContentRequest contentRequest = requestList.get(position);
        object = new ContentRequest(contentRequest);
        holder.movieName.setText(contentRequest.getMovieName());
        holder.fileType.setText("Language: "+contentRequest.getFileLanguage());
        holder.minQuality.setText("Minimum Quality: "+contentRequest.getMinQuality());
        holder.requestUserFullName.setText(contentRequest.getRequestingUser());
        holder.year.setText(contentRequest.getYear());
        Date currentDate = new Date();
        Date requestDate = new Date(contentRequest.getRequestTime());

        long diff = currentDate.getTime() - requestDate.getTime();
        long days = diff / 1000/60/60/24;

        if(days == 0)
            holder.requestTime.setText("Today");
        else
            holder.requestTime.setText(days +" days ago");


        new DownloadImageTask(holder.requestUserPic).execute(contentRequest.getRequestingUserPic());

        holder.removeRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String contentKey = contentRequest.getKey();

                /*
                Remove a request means
                1) Remove from the listening field
                2)Remove from the requests according to
                    a) If the no of peers == 1 and he is the only peer set the contekey value to null
                    b)If there are other peers to it remove this peer only
                Due to some it aint working so what we will do is to try set the whole path as null and put in a try catch. and set listening.

                 */

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                String userKey = user.getEmail().toLowerCase().replace(".",",");
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference ref = database.getReference("users");
                ref.child(userKey).child("listening").child(contentRequest.getKey()).setValue(null);


                final String contentType = classify(contentRequest.getKey());

                final DatabaseReference reference  = database.getReference("requests");
                reference.child(contentType).child(contentKey).child("peers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> latestPeers = (List<String>) dataSnapshot.getValue();
                        String token = FirebaseInstanceId.getInstance().getToken();
//                                    Log.e("VITCC Unsubscribe", String.valueOf(latestPeers.size()));
                        if(latestPeers != null && latestPeers.size() > 0 && latestPeers.contains(token))
                        {
                            latestPeers.remove(token);
                        }
                        object.removePeer(token);

                        Log.e("VITCC Unsubscribe", String.valueOf(latestPeers.size()));
                        if(latestPeers.size() == 0)
                        {
                            reference.child(contentType).child(contentKey).setValue(null);
                        }
                        else
                            reference.child(contentType).child(contentKey).child("peers").setValue(latestPeers);

                        holder.year.setText("");
                        holder.requestUserFullName.setVisibility(GONE);
                        holder.removeRequestButton.setVisibility(GONE);
                        holder.viewResponsesButton.setVisibility(GONE);
                        holder.requestUserPic.setVisibility(GONE);
                        holder.movieName.setText("Request Removed");
                        holder.requestTime.setVisibility(GONE);
                        holder.fileType.setVisibility(GONE);
                        holder.minQuality.setVisibility(GONE);



                        }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView requestUserFullName,movieName,year,minQuality,fileType,requestTime;
        public ImageView requestUserPic;
        public Button viewResponsesButton,removeRequestButton;
        public MyViewHolder(View view) {
            super(view);
            requestUserPic = (ImageView) view.findViewById(R.id.requestUserPic);
            requestUserFullName = (TextView) view.findViewById(R.id.requestUserName);
            movieName  = (TextView) view.findViewById(R.id.requestMovieName);
            year = (TextView) view.findViewById(R.id.requestMovieYear);
            requestTime = (TextView) view.findViewById(R.id.requestTime);
            minQuality = (TextView) view.findViewById(R.id.requestMinQuality);
            fileType = (TextView) view.findViewById(R.id.requestFileLanguage);
            viewResponsesButton = (Button) view.findViewById(R.id.viewResponsesButton);
            removeRequestButton = (Button) view.findViewById(R.id.removeRequestButton);
        }
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
