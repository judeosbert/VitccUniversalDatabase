package com.example.kleptomaniac.vitccuniversaldatabase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import java.util.ArrayList;
import java.util.Date;
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
    public ContentRequest object;



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_list_row,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final ContentRequest contentRequest = requestList.get(position);
        object = new ContentRequest(contentRequest);
        holder.movieName.setText(contentRequest.getMovieName());
        holder.fileType.setText("Language: "+contentRequest.getFileLanguage());
        holder.minQuality.setText("Minimum Quality: "+contentRequest.getMinQuality());
        holder.requestUserFullName.setText(contentRequest.getRequestingUser());
        holder.year.setText(contentRequest.getYear());

//        Log.e("VITCC PEERS",contentRequest.getPeers().toString());



        holder.iHaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                view = v;
                final String key = user.getEmail().toLowerCase().replace(".", ",").replace(" ", "");
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("users");
                ref.child(key).child("answered").child(contentRequest.getRequestType()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("VITCC Answered Duplicate",dataSnapshot.toString());
                        if (dataSnapshot.hasChild(contentRequest.getKey()))
                        {
                            Snackbar.make(view,"You have already responded to the request",Snackbar.LENGTH_SHORT).show();
                        }
                        else
                        {
                            showAlertDialog(contentRequest,key,view);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("VITCC ","Check answer duplicate routine cancelled");
                    }
                });








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
                DatabaseReference ref = database.getReference("users");
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String key = user.getEmail().toLowerCase().replace(".", ",").replace(" ", "");
                //We check if the user has already subscribed to this item for listening.
                ref.child(key).child("listening").addListenerForSingleValueEvent(new ValueEventListener() {
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

                        handleEvent(subscribed,contentKey,requestType,contentRequest.getPeers());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });


        new DownloadImageTask(holder.requestUserPic).execute(contentRequest.getRequestingUserPic());


    }
    public void showAlertDialog(final ContentRequest contentRequest, final String key, final View view)
    {
        AlertDialog confirmResponse = new AlertDialog.Builder(context).setTitle("Heads up Buddy")
                .setMessage("Are you sure you have this content with you?")
                .setIcon(R.drawable.ic_alert_circle)
                .setPositiveButton("Yes I Am", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                       FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("requests");
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                        Date date = new Date();
                        Answers answer = new Answers(contentRequest.getMovieName(),contentRequest.getRequestType(),contentRequest.getRequestTime(),date.toString(),user.getDisplayName(),user.getEmail().toLowerCase().replace(".",","),user.getPhotoUrl().toString(),contentRequest.getRequestingUser(),contentRequest.getRequestingUserPic(),contentRequest.getRequestingUser());
                        final String pushKey = ref.child(contentRequest.getRequestType()).child(contentRequest.getKey()).child("answers").push().getKey();
                        ref.child(contentRequest.getRequestType()).child(contentRequest.getKey()).child("answers").child(pushKey).setValue(answer, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError == null) {
                                    new GroupFCMSender((ArrayList<String>) contentRequest.getPeers(),contentRequest.getMovieName()).execute();
                                    addtoAnswered(contentRequest.getRequestType(),contentRequest.getKey(),pushKey);
                                    Snackbar.make(view, "Your response has been recorded. Cheers.", Snackbar.LENGTH_SHORT).show();
//                                    showSnackBar(view);
                                }
                                else
                                {
                                    Log.e("VITCC","Error on adding response to a request");
                                }
                            }
                        });



                    }
                }).setNegativeButton("No I dont",null).show();
    }

    private void addtoAnswered(String requestType, String key, String pushKey) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userKey = user.getEmail().toLowerCase().replace(".",",").replace(" ","");
        ref.child(userKey).child("answered").child(requestType).child(key).setValue(pushKey, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.e("VITCC"," Added to answered self account");
            }
        });


    }

    private void showSnackBar(View view) {

        Snackbar.make(view,"Users will be alerted regarding this",Snackbar.LENGTH_SHORT).show();
    }


    private void handleEvent(boolean subscribed, final String contentKey, final String requestType, final List<String> peers) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
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

            final DatabaseReference reference = database.getReference("requests");
            AlertDialog unsubscribe = new AlertDialog.Builder(context).setTitle("You have already subscribed to the alerts")
                    .setIcon(R.drawable.ic_alert_circle)
                    .setMessage("Do you want to unsubscribe?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            reference.child(requestType).child(contentKey).child("peers").addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    reference.child(requestType).child(contentKey).child("peers").setValue(latestPeers);
                                    removeFromListening(requestType,contentKey);


                                    Snackbar.make(view,"You have unsubscribed from the alerts",Snackbar.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("VITCC","Database error on unsubscribe");
                                }
                            });

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Snackbar.make(view,"You are still on the alerts",Snackbar.LENGTH_SHORT).show();
                        }
                    }).show();

        }
    }

    private void removeFromListening(String requestType, String contentKey) {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String key = user.getEmail().toLowerCase().replace(".",",").replace(" ","");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");
        ref.child(key).child("listening").child(contentKey).setValue(null);
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
        ref.child(key).child("listening").child(requestCode).setValue(true);

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
