package com.example.kleptomaniac.vitccuniversaldatabase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_APPEND;

/**
 * Created by kleptomaniac on 1/7/17.
 */

public class ResponsesAdapter extends RecyclerView.Adapter<ResponsesAdapter.MyViewHolder> {
    private List<Answers> answersList;
    private Context context;
    private View view;
    public ResponsesAdapter(List<Answers> answersList)
    {
        this.answersList = answersList;
    }





    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View answerView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.response_layout,parent,false);
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




    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView responseTime,requestUserFullName;
        public ImageView requestUserPic;
        public Button viewProfile;


        public MyViewHolder(View answerView) {
            super(answerView);

            responseTime = (TextView) answerView.findViewById(R.id.responseTime);
            requestUserFullName = (TextView) answerView.findViewById(R.id.requestUserName);
            requestUserPic = (ImageView) answerView.findViewById(R.id.requestUserPic);
            viewProfile = (Button) answerView.findViewById(R.id.viewProfile);




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
