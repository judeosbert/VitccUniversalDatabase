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

import java.io.InputStream;
import java.util.List;

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
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final ContentRequest contentRequest = requestList.get(position);
        object = new ContentRequest(contentRequest);
        holder.movieName.setText(contentRequest.getMovieName());
        holder.fileType.setText("Language: "+contentRequest.getFileLanguage());
        holder.minQuality.setText("Minimum Quality: "+contentRequest.getMinQuality());
        holder.requestUserFullName.setText(contentRequest.getRequestingUser());
        holder.year.setText(contentRequest.getYear());
        new DownloadImageTask(holder.requestUserPic).execute(contentRequest.getRequestingUserPic());

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
            requestTime = (TextView) view.findViewById(R.id.requestMovieYear);
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
