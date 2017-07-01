package com.example.kleptomaniac.vitccuniversaldatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
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
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class UserDashboard extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private SharedPreferences userData;
    private FirebaseDatabase database;
    public User userClass;
    public BottomNavigationView navigation;
    String subNewCat = null;
    private List<ContentRequest> requestListMusic=new ArrayList<>(),requestListMovie=new ArrayList<>(),requestListDocument=new ArrayList<>(),requestListSeries=new ArrayList<>(),requestListOther=new ArrayList<>(),requestListGame = new ArrayList<>();
    private RecyclerView recyclerViewMusic,recyclerViewMovie,recyclerViewDocument,recyclerViewSeries,recyclerViewOther,recyclerViewGame;
    private ContentAdapter contentAdapterMusic,contentAdapterMovie,contentAdapterDocument,contentAdapterSeries,contentAdapterOther,contentAdapterGame;
    private BottomSheetDialog bottomSheetDialog;
    private View sheetView;
    private Boolean resuming = false,duplicate = false;
    private String reqType = null,itemNameString = null;
    private List<String> listnerAdded = new ArrayList<>();


//    SharedPreferences latestPulls = getSharedPreferences("LATESTDATA",MODE_APPEND);
//    SharedPreferences.Editor edit = latestPulls.edit();



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            try {
                switch (item.getItemId()) {
                    case R.id.navigation_music:
                        if (userClass.music) {
                            showContent("music");

                            recyclerViewMusic.setVisibility(View.VISIBLE);
                            recyclerViewMovie.setVisibility(View.GONE);
                            recyclerViewDocument.setVisibility(View.GONE);
                            recyclerViewSeries.setVisibility(View.GONE);
                            recyclerViewOther.setVisibility(View.GONE);
                            recyclerViewGame.setVisibility(View.GONE);

                        } else {
                            showSubscribeButton("music");
                        }
                        break;
                    case R.id.navigation_movies:
                        if (userClass.movie) {
                            showContent("movie");

                            recyclerViewMusic.setVisibility(View.GONE);
                            recyclerViewMovie.setVisibility(View.VISIBLE);
                            recyclerViewDocument.setVisibility(View.GONE);
                            recyclerViewSeries.setVisibility(View.GONE);
                            recyclerViewOther.setVisibility(View.GONE);
                            recyclerViewGame.setVisibility(View.GONE);
                        } else {
                            showSubscribeButton("movie");
                        }
                        break;
                    case R.id.navigation_series:
                        if (userClass.series) {
                            showContent("series");
                            recyclerViewMusic.setVisibility(View.GONE);
                            recyclerViewMovie.setVisibility(View.GONE);
                            recyclerViewDocument.setVisibility(View.GONE);
                            recyclerViewSeries.setVisibility(View.VISIBLE);
                            recyclerViewOther.setVisibility(View.GONE);
                            recyclerViewGame.setVisibility(View.GONE);
                        } else {
                            showSubscribeButton("series");
                        }
                        break;
                    case R.id.navigation_games:
                        if (userClass.game) {
                            showContent("game");
                            recyclerViewMusic.setVisibility(View.GONE);
                            recyclerViewMovie.setVisibility(View.GONE);
                            recyclerViewDocument.setVisibility(View.GONE);
                            recyclerViewSeries.setVisibility(View.GONE);
                            recyclerViewOther.setVisibility(View.GONE);
                            recyclerViewGame.setVisibility(View.VISIBLE);
                        } else {
                            showSubscribeButton("game");
                        }
                        break;
                    case R.id.navigation_settings:
                        if (userClass.other) {
                            showContent("other");
                            recyclerViewMusic.setVisibility(View.GONE);
                            recyclerViewMovie.setVisibility(View.GONE);
                            recyclerViewDocument.setVisibility(View.GONE);
                            recyclerViewSeries.setVisibility(View.GONE);
                            recyclerViewOther.setVisibility(View.VISIBLE);
                            recyclerViewGame.setVisibility(View.GONE);
                        } else {
                            showSubscribeButton("other");
                        }
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Stop hurting me :(", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
            catch (NullPointerException e)
            {
                Toast.makeText(getApplicationContext(), "Give me a second. Will you?", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);



        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Music");

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user  = mAuth.getCurrentUser();
        userData = getSharedPreferences("USERDATA",MODE_APPEND);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        String userKey = user.getEmail().toLowerCase().replace(".", ",");
        DatabaseReference ref = database.getReference("users/" + userKey);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userClass = dataSnapshot.getValue(User.class);
                //Log.e("VITCC", String.valueOf(userClass.music));
                findViewById(R.id.loadingBar).setVisibility(View.GONE);
//                    progressDialog.hide();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
//                    progressDialog.hide();
            }
        });


        recyclerViewMusic = (RecyclerView) findViewById(R.id.recyclerViewMusic);
        contentAdapterMusic = new ContentAdapter(requestListMusic);
        RecyclerView.LayoutManager layoutManagerMusic = new LinearLayoutManager(getApplicationContext());

        recyclerViewMusic.setLayoutManager(layoutManagerMusic);
        recyclerViewMusic.setItemAnimator(new SlideInUpAnimator());
        recyclerViewMusic.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        recyclerViewMusic.setAdapter(contentAdapterMusic);



        recyclerViewMovie = (RecyclerView) findViewById(R.id.recyclerViewMovie);
        contentAdapterMovie = new ContentAdapter(requestListMovie);
        RecyclerView.LayoutManager layoutManagerMovie = new LinearLayoutManager(getApplicationContext());
        recyclerViewMovie.setLayoutManager(layoutManagerMovie);
        recyclerViewMovie.setItemAnimator(new SlideInLeftAnimator());
        recyclerViewMovie.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        recyclerViewMovie.setAdapter(contentAdapterMovie);


        recyclerViewDocument = (RecyclerView) findViewById(R.id.recyclerViewDocument);
        contentAdapterDocument = new ContentAdapter(requestListDocument);
        RecyclerView.LayoutManager layoutManagerDocument = new LinearLayoutManager(getApplicationContext());
        recyclerViewDocument.setLayoutManager(layoutManagerDocument);
        recyclerViewDocument.setItemAnimator(new SlideInUpAnimator());
        recyclerViewDocument.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        recyclerViewDocument.setAdapter(contentAdapterDocument);

        recyclerViewSeries = (RecyclerView) findViewById(R.id.recyclerViewSeries);
        contentAdapterSeries = new ContentAdapter(requestListSeries);
        RecyclerView.LayoutManager layoutManagerSeries = new LinearLayoutManager(getApplicationContext());
        recyclerViewSeries.setLayoutManager(layoutManagerSeries);
        recyclerViewSeries.setItemAnimator(new SlideInUpAnimator());
        recyclerViewSeries.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        recyclerViewSeries.setAdapter(contentAdapterSeries);

        recyclerViewOther = (RecyclerView) findViewById(R.id.recyclerViewOther);
        contentAdapterOther = new ContentAdapter(requestListOther);
        RecyclerView.LayoutManager layoutManagerOther = new LinearLayoutManager(getApplicationContext());
        recyclerViewOther.setLayoutManager(layoutManagerOther);
        recyclerViewOther.setItemAnimator(new SlideInUpAnimator());
        recyclerViewOther.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        recyclerViewOther.setAdapter(contentAdapterOther);

        recyclerViewGame = (RecyclerView) findViewById(R.id.recyclerViewGame);
        contentAdapterGame = new ContentAdapter(requestListGame);
        RecyclerView.LayoutManager layoutManagerGame = new LinearLayoutManager(getApplicationContext());
        recyclerViewGame.setLayoutManager(layoutManagerGame);
        recyclerViewGame.setItemAnimator(new SlideInUpAnimator());
        recyclerViewGame.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        recyclerViewGame.setAdapter(contentAdapterGame);


        //Demo

        //









        
        bottomSheetDialog = new BottomSheetDialog(this);
        sheetView = this.getLayoutInflater().inflate(R.layout.activity_add_new_request,null);
        bottomSheetDialog.setContentView(sheetView);



        sheetView.findViewById(R.id.newRequestButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Spinner requestType,fileLanguage,fileQuality;
                EditText itemName,year;

                requestType = (Spinner) sheetView.findViewById(R.id.requestTypes);
                fileLanguage = (Spinner) sheetView.findViewById(R.id.requestFileType);
                fileQuality = (Spinner) sheetView.findViewById(R.id.minQualityExpected);
//        Log.e("VITCC",requestTypeValue);
                itemName = (EditText) sheetView.findViewById(R.id.itemName);
                year = (EditText) sheetView.findViewById(R.id.releaseYear);

                String requestTypeValue=requestType.getSelectedItem().toString();
                String fileQualityValue=fileQuality.getSelectedItem().toString();
                String fileLanguageValue=fileLanguage.getSelectedItem().toString();
                String itemNameValue = itemName.getText().toString();
                if(itemNameValue.length() == 0)
                {
                    itemName.setError("Don't you know what you are looking for?");
                    return;
                }
                itemNameString  = itemNameValue;
//                Log.e("VITCC",itemNameValue);
                String yearValue = year.getText().toString();


                if(yearValue.length() != 4 || !yearValue.matches("/[0-9]{4}"))
                {
                    yearValue = "";

                }
                itemName.setText("");
                year.setText("");
                databaseAdder(requestTypeValue,itemNameValue,yearValue,fileLanguageValue,fileQualityValue);
            }
        });



       retreiveData("music");


    }



    private void retreiveData(String cat) {
        int insertIndex = 0;
        String capCat = cat.substring(0,1).toUpperCase()+cat.substring(1);
        getSupportActionBar().setTitle(capCat);
        if(findViewById(R.id.loadingBar).getVisibility() == View.GONE)
            showNoActivity();
        if(cat == "d&b")
        {
            cat = "document";
        }
        try {

            if (listnerAdded.contains(cat)) {

                hideNoActivity();
                insertIndex = 0;
                return;
            }
        }
        catch (NullPointerException e)
        {
            Log.e("VITCC","No data in listnerAdded array");
        }
        final String finalCat = cat;


            DatabaseReference ref = database.getReference("requests");
            ref.child(cat).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                    Log.e("VITCC Child Listener",dataSnapshot.getValue().toString());
                    ContentRequest contentRequest = dataSnapshot.getValue(ContentRequest.class);



                           if (finalCat == "music")
                               requestListMusic.add(0,contentRequest);
                           else if (finalCat == "movie")
                               requestListMovie.add(0,contentRequest);
                           else if (finalCat == "series")
                               requestListSeries.add(0,contentRequest);
                           else if (finalCat == "document") {
                               requestListDocument.add(0,contentRequest);
                           } else if (finalCat == "other") {
                               requestListOther.add(0,contentRequest);
                           }
                           else if(finalCat == "game")
                           {
                               requestListGame.add(0,contentRequest);
                           }
                    if(finalCat == "music") {

//                        listnerAdded.add(finalCat);
                        contentAdapterMusic.notifyItemInserted(0);
//                        contentAdapterMusic.notifyDataSetChanged();
                    }
                    else if(finalCat == "movie") {

//                        listnerAdded.add(finalCat);
//                        Collections.reverse(requestListMovie);
                        contentAdapterMovie.notifyItemInserted(0);
                    }
                    else if(finalCat == "series") {

//                        listnerAdded.add(finalCat);
//                        Collections.reverse(requestListSeries);
                        contentAdapterSeries.notifyItemInserted(0);
                    }
                    else if(finalCat == "document") {

//                        listnerAdded.add(finalCat);
//                        Collections.reverse(requestListDocument);
                        contentAdapterDocument.notifyItemInserted(0);
                    }
                    else if(finalCat == "other") {

//                        listnerAdded.add(finalCat);
//                        Collections.reverse(requestListOther);
                        contentAdapterOther.notifyItemInserted(0);
                    }
                    else if(finalCat == "game")
                    {
//                        listnerAdded.add(finalCat);
                        contentAdapterGame.notifyItemInserted(0);
                    }


                    hideNoActivity();
                    Log.e("VITCC","Listner added"+listnerAdded.toString());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        }





    private void showContent(String cat) {

        findViewById(R.id.imageView12).setVisibility(View.GONE);
        findViewById(R.id.textView5).setVisibility(View.GONE);
        findViewById(R.id.button5).setVisibility(View.GONE);


        retreiveData(cat);

    }

    private void showSubscribeButton(String cat)
    {
        findViewById(R.id.imageView12).setVisibility(View.VISIBLE);
        findViewById(R.id.textView5).setVisibility(View.VISIBLE);
        findViewById(R.id.button5).setVisibility(View.VISIBLE);


        recyclerViewMusic.setVisibility(View.GONE);
        recyclerViewMovie.setVisibility(View.GONE);
        recyclerViewDocument.setVisibility(View.GONE);
        recyclerViewSeries.setVisibility(View.GONE);
        recyclerViewOther.setVisibility(View.GONE);
        recyclerViewGame.setVisibility(View.GONE);

        hideNoActivity();


        subNewCat = cat;

    }
    private void hideSubscribeButton()
    {
        findViewById(R.id.imageView12).setVisibility(View.GONE);
        findViewById(R.id.textView5).setVisibility(View.GONE);
        findViewById(R.id.button5).setVisibility(View.GONE);

    }
    public void subscribeTo(View view)
    {
        String userKey = user.getEmail().toLowerCase().replace(".", ",");
        if(subNewCat != "d&b")
        {
            DatabaseReference ref = database.getReference("users/"+userKey+"/"+subNewCat);
            ref.setValue(true);

        }
        hideSubscribeButton();
        retreiveData(subNewCat);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.custom_menu,menu);
        final MenuItem searchItem = menu.findItem(R.id.search_lens);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        final MenuItem profileButton = menu.findItem(R.id.profile_photo);
        profileButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                takeToUserProfile();
                return true;
            }
        });
        return true;
    }

    private void takeToUserProfile() {
        Intent intent = new Intent(this,UserProfile.class);
        SharedPreferences sharedPreferences = getSharedPreferences("PROFILE_VISIT",MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("EMAIL",user.getEmail());
//        editor.putString("EMAIL","bridgitom@gmail.com");//Testing
        editor.commit();
        startActivity(intent);

    }

    public void dummy(View view)
    {
        Toast.makeText(getApplicationContext(),"Dummy",Toast.LENGTH_SHORT).show();
    }

    public void addNewRequest(View v)
    {

        bottomSheetDialog.show();

    }

    public void databaseAdder(String requestTypeValue, final String itemNameValue, String yearValue, String fileLanguageValue, String fileQualityValue)
    {
        requestTypeValue = requestTypeValue.toLowerCase();
        reqType = requestTypeValue;
        final String requestCode = itemNameValue.concat(yearValue).concat(requestTypeValue).concat(fileQualityValue).concat(fileLanguageValue).toLowerCase().replace(".","").replace(" ","");

        String key =  user.getEmail().toLowerCase().replace(".",",");
        String messageToken = FirebaseInstanceId.getInstance().getToken();
        ContentRequest request =  new ContentRequest(requestCode,requestTypeValue,key,user.getDisplayName(),user.getPhotoUrl().toString(),itemNameValue,yearValue,fileQualityValue,fileLanguageValue,messageToken);


        final ContentRequest innerRequest = new ContentRequest(request);
         DatabaseReference ref = database.getReference("requests/"+requestTypeValue);
        Log.e("VITCC",requestCode);

        //Checking if child exists
        final String finalRequestTypeValue = requestTypeValue;
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("VITCC", "Has Child "+String.valueOf(dataSnapshot.child(requestCode).exists()));

                duplicate = dataSnapshot.child(requestCode).exists();
                DatabaseReference newRef = database.getReference("requests/"+finalRequestTypeValue);
                    if(duplicate)
                    {
                        duplicate = false;
                        handleDuplicate(requestCode,finalRequestTypeValue);

                    }
                    else
                    {
                        newRef.child(requestCode).setValue(innerRequest, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                sendNotification(reqType,itemNameString);
                                showSnackBar("Your request has been added");
//                                addToOwnerShip(requestCode,finalRequestTypeValue);
                            }
                        });
                    }
                addToListening(requestCode,finalRequestTypeValue);

                hideBottomSheet();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });








    }

    private void addToOwnerShip(String requestCode, String finalRequestTypeValue) {
        DatabaseReference ref = database.getReference("users");
        String key = user.getEmail().toLowerCase().replace(".",",").replace(" ","");
        ref.child(key).child("owner").child(requestCode).setValue(true);
    }

    private void addToListening(String requestCode, String finalRequestTypeValue) {
        DatabaseReference ref = database.getReference("users");
        String key = user.getEmail().toLowerCase().replace(".",",").replace(" ","");
        ref.child(key).child("listening").child(requestCode).setValue(true);

    }

    private void hideBottomSheet() {
        bottomSheetDialog.dismiss();
    }
    private  void showSnackBar(String s){Snackbar.make(getWindow().getDecorView().findViewById(R.id.container),s,Snackbar.LENGTH_SHORT).show();}
    private void handleDuplicate(final String requestCode, final String finalRequestTypeValue) {

        Log.e("VITCC","Duplicate Handler");
        DatabaseReference ref = database.getReference("requests");

        ref.child(finalRequestTypeValue).child(requestCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ContentRequest request = dataSnapshot.getValue(ContentRequest.class);
                String messageToken = FirebaseInstanceId.getInstance().getToken();
                request.addPeer(messageToken);
                addNewPeer(request,requestCode,finalRequestTypeValue);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addNewPeer(ContentRequest request, final String requestCode, final String finalRequestTypeValue)
    {
        DatabaseReference ref = database.getReference("requests");
        ref.child(finalRequestTypeValue).child(requestCode).child("peers").setValue(request.getPeers(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                addToListening(requestCode,finalRequestTypeValue);
                showSnackBar("Your have subscribed to the list for the requested item.");
            }
        });

    }

    void sendNotification(String topic,String itemNameString)
    {
        new TopicMessageSender(topic,itemNameString).execute();
    }
    void hideNoActivity()
    {
        findViewById(R.id.noActivitySign).setVisibility(View.GONE);
        findViewById(R.id.noActivityText).setVisibility(View.GONE);
    }
    void showNoActivity()
    {
        findViewById(R.id.noActivitySign).setVisibility(View.VISIBLE);
        findViewById(R.id.noActivityText).setVisibility(View.VISIBLE);

    }




}
