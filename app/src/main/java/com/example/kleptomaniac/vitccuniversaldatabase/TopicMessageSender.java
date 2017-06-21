package com.example.kleptomaniac.vitccuniversaldatabase;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kleptomaniac on 17/6/17.
 */

public class TopicMessageSender extends AsyncTask<String,String,String> {
    private String itemName;
    private String topic;

    public TopicMessageSender() {

    }

    public TopicMessageSender(String topic,String itemName) {
        this.topic = topic;
        this.itemName = itemName;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = "https://fcm.googleapis.com/fcm/send";

        InputStream in = null;
        String severKey = "key="+"AAAAnHdTTKY:APA91bE6EWTMaVspo0zwUSOMXzinDmJAOu2-AFqlO4NsMpIn-48F_enCVhH2KSBGi8syyRKqhaqINTmZNaezuleqj1ezcJs2PBlS9vi8vUgW3FJwnNCezLpBZpglzyu-a6scv3sTvQ8g";


        try {
            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("Authorization",severKey);

            JSONObject jsonObject  = new JSONObject();
            JSONObject notification = new JSONObject();
            JSONObject notificationBody = new JSONObject();
            try {
                notificationBody.put("title","New Request in "+topic+" category");
                notificationBody.put("body","A new request has been made for "+itemName+". Do you have it or need it? Respond now.");
                notificationBody.put("sound","default");
                notificationBody.put("icon","R.drawable.ic_checked");
                jsonObject.put("notification",notificationBody);
                jsonObject.put("to","/topics/music");

            } catch (JSONException e) {
                e.printStackTrace();
            }


            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
            dataOutputStream.writeBytes(jsonObject.toString());
            dataOutputStream.flush();
            dataOutputStream.close();


            int responseCode  = urlConnection.getResponseCode();
            Log.e("VITCC TOPIC SENDER", String.valueOf(responseCode));


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
