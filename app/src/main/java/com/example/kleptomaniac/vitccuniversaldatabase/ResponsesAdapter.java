package com.example.kleptomaniac.vitccuniversaldatabase;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_APPEND;

/**
 * Created by kleptomaniac on 1/7/17.
 */

public class ResponsesAdapter extends RecyclerView.Adapter<ResponsesAdapter.MyViewHolder> {
    private List<Answers> answersList;
    private Context context;
    public View view;
    public ResponsesAdapter(List<Answers> answersList)
    {
        this.answersList = answersList;
    }





    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View answerView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.response_layout,parent,false);
        this.view  = answerView;
        return  new MyViewHolder(answerView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final Answers answer = answersList.get(position);
        holder.requestUserFullName.setText(answer.getAnswerPersonName());
        new DownloadImageTask(holder.requestUserPic).execute(answer.getAnswerPersonPhoto());

        Date currentDate = new Date();
        Date answerDate = new Date(answer.getAnswerDate());
        long diff = currentDate.getTime() - answerDate.getTime();

        long days = diff/1000/60/60/24;
        if(days == 0)
        {
            holder.responseTime.setText("Responded Today");
        }
        else
        {
            holder.responseTime.setText("Responded "+days+" ago");
        }

        holder.viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("PROFILE_VISIT",MODE_APPEND);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("EMAIL",answer.getAnswerPersonKey().replace(",","."));
                editor.commit();
                Intent intent = new Intent(v.getContext(),UserProfile.class);
                v.getContext().startActivity(intent);
            }
        });

        holder.verifyResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final String responseKey = keyMaker(answer);
                AlertDialog alertDiaglog = new AlertDialog.Builder(v.getContext()).setTitle("Are you sure?").setMessage("Verfiying a response will make it appear on search results for others. Proceed only if you have received the requested Item")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Add response to the firebase

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                final DatabaseReference ref = database.getReference("verified");
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild(responseKey)) {

                                            Snackbar.make(v,"This response has already been verified.Cheers",Snackbar.LENGTH_SHORT).show();

                                        }
                                        else {
                                            ref.child(responseKey).setValue(true, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    if (databaseError == null)
                                                        new AddtoIndex(responseKey, answer, v).execute();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }).setNegativeButton("No",null).show();




            }});




    }

    private String keyMaker(Answers answer) {
        String key;
        key = answer.getItemName()+answer.getYear()+answer.getMinQuality()+answer.getAnswerPersonKey();
        key = key.toLowerCase().replace(".",",");
        return key;
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView responseTime,requestUserFullName;
        public ImageView requestUserPic;
        public Button viewProfile,verifyResponse;


        public MyViewHolder(View answerView) {
            super(answerView);

            responseTime = (TextView) answerView.findViewById(R.id.responseTime);
            requestUserFullName = (TextView) answerView.findViewById(R.id.requestUserName);
            requestUserPic = (ImageView) answerView.findViewById(R.id.requestUserPic);
            viewProfile = (Button) answerView.findViewById(R.id.viewProfile);
            verifyResponse = (Button) answerView.findViewById(R.id.verifyResponse);




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

    private class AddtoIndex extends AsyncTask<Void, Void, Integer> {
        private String responseKey;
        private Answers answer;
        private int responseCode;
        ProgressDialog progressDialog;
        public AddtoIndex(String responseKey, Answers answer, View v) {
        this.responseKey  = responseKey;
        this.answer = answer;

        }

        protected  void onPreExecute()
        {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Adding to response to verified List");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected Integer doInBackground(Void... params) {
            String urlString= "https://vitccudb.000webhostapp.com/addNewIndex.php?itemName="+answer.getItemName()+"&keyCode="+answer.getRequestCode()+"&quality="+answer.getMinQuality()+"&year="+answer.getYear();

            InputStream in = null;
                Log.e("VITCC","Add to Index Execute to url"+urlString);
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                responseCode = urlConnection.getResponseCode();
                String responseMessage = urlConnection.getResponseMessage();
                Log.e("VITCC","Reponse COde from addSearchIndex is "+responseCode +" and Message is "+responseMessage);



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return responseCode;
        }

        protected void onPostExecute(Integer resultCode)
        {
            progressDialog.setMessage("Verified");
            progressDialog.setIcon(R.drawable.ic_check);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.hide();;
                }
            },2000);

        }

    }


}
