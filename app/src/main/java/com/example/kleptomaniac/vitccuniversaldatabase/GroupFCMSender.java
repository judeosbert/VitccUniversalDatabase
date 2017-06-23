package com.example.kleptomaniac.vitccuniversaldatabase;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kleptomaniac on 17/6/17.
 */

public class GroupFCMSender extends AsyncTask<String,String,String> {
    private String itemName;
    private List<String> peers = new ArrayList<String>();

    public GroupFCMSender () {

    }

    public GroupFCMSender (ArrayList<String> peers , String itemName) {
        this.peers = peers;
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
            JSONArray registrationIds = new JSONArray();
            try {
                notificationBody.put("title","Response for your request");
                notificationBody.put("body","A response for your request for "+itemName+" has been marked. Tap to see it now.");
                notificationBody.put("sound","default");
                notificationBody.put("icon","R.drawable.ic_checked");
                jsonObject.put("notification",notificationBody);
                for(int i = 0 ;i <peers.size();i++)
                {
                    registrationIds.put(peers.get(i));
                }
                jsonObject.put("registration_ids",registrationIds);

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
            Log.e("VITCC GROUP SENDER", String.valueOf(responseCode));
            Log.e("VITCC GROUP SENDER",urlConnection.getResponseMessage());
            if(responseCode == 200)
            {

            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
