package com.example.kleptomaniac.vitccuniversaldatabase;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class SearchActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<SuggestionsClass> suggestionList;
    SuggestionsAdapter suggestionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        suggestionList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.suggestionRecyclerView);
        suggestionsAdapter = new SuggestionsAdapter(this,suggestionList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        recyclerView.setAdapter(suggestionsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));
        new getDataFromIndex().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        final MenuItem searchItem = menu.findItem(R.id.search_lens);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                suggestionsAdapter.filter(newText);
                return true;
            }
        });


        searchView.setQueryHint("Search for Movies,Series,Games and more...");

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Intent intent = new Intent(searchView.getContext(),UserDashboard.class);
                searchView.getContext().startActivity(intent);
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem,new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Intent intent = new Intent(searchView.getContext(),UserDashboard.class);
                searchView.getContext().startActivity(intent);
                return true;
            }
        });

        searchItem.expandActionView();









        return true;
    }


    private class getDataFromIndex extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... params) {
            String line,response="";
            String urlString = "https://vitccudb.000webhostapp.com/getResults.php";
            Log.e("VITCC","Search Activity");
            URL url;

            try {
                url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                int responseCode = urlConnection.getResponseCode();
                BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                while((line = rd.readLine()) != null)
                {
                   response = line;
                }



                Log.e("VITCC","Search Activity"+responseCode);
                Log.e("VITCC","Search Activity"+response);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }


        @Override
        protected  void onPostExecute(String response)
        {

            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i= 0 ; i<jsonArray.length();i++)
                {
                    JSONObject object = jsonArray.getJSONObject(i);
                   String itemName = object.getString("itemName");
                    String keyCode = object.getString("keyCode");


                    SuggestionsClass suggestion = new SuggestionsClass(itemName,keyCode);

                    suggestionList.add(suggestion);
                    suggestionsAdapter.notifyDataSetChanged();


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
