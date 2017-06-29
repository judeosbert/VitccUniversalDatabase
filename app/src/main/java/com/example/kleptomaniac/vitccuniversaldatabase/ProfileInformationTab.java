package com.example.kleptomaniac.vitccuniversaldatabase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import java.util.Random;

import static android.view.View.GONE;


/**
 * Created by kleptomaniac on 24/6/17.
 */

public class ProfileInformationTab extends Fragment {
    public TextView userDisplayName,userEmail,userPhone,userLocation;
    ProgressDialog loadingInfo;
    BottomSheetDialog mobileBottomSheetDialog,roomBottomSheetDialog;
    public User nowUser;
    public View mobileSheetView,roomSheetView;
    public String currentUserEmail;
    Button signOutButton,verifyButton,verifyOTPButton,updateRoomBottom;
    public EditText newMobile,verfiyOTPText,roomNo;
    Spinner blockSpinner;
    int OTPCode;
    String newPhone;
    ImageView profileImage;


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState)
    {
        View view = layoutInflater.inflate(R.layout.profile_information_tab,container,false);

        setDetails(view);

        return  view;
    }

    public void setDetails(View view)
    {
        SharedPreferences sharedPreferences = ((Activity) view.getContext()).getSharedPreferences("PROFILE_VISIT",Context.MODE_APPEND);
        currentUserEmail = sharedPreferences.getString("EMAIL","");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        userDisplayName = (TextView)view.findViewById(R.id.userDisplayName);
        userPhone = (TextView) view.findViewById(R.id.userPhone);
        userEmail = (TextView) view.findViewById(R.id.userEmail);
        userLocation = (TextView)view.findViewById(R.id.userLocation);
        signOutButton = (Button) view.findViewById(R.id.signOutButton);
        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        getAndSetFromDB(view);
        mobileBottomSheetDialog = new BottomSheetDialog(view.getContext());
        roomBottomSheetDialog = new BottomSheetDialog(view.getContext());



        LayoutInflater layoutInflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mobileSheetView = layoutInflater.inflate(R.layout.bottomsheet_profile_mobile,null);
        roomSheetView = layoutInflater.inflate(R.layout.bottomsheet_profile_room,null);
        mobileBottomSheetDialog.setContentView(mobileSheetView);
        roomBottomSheetDialog.setContentView(roomSheetView);


        verifyButton = (Button) mobileSheetView.findViewById(R.id.verifyPhone);
        newMobile = (EditText) mobileSheetView.findViewById(R.id.newUserMobile);

        roomNo = (EditText)  roomSheetView.findViewById(R.id.roomNoEditText);
        blockSpinner = (Spinner) roomSheetView.findViewById(R.id.blockSpinner);
        updateRoomBottom = (Button) roomSheetView.findViewById(R.id.updateRoomButton);

        final List<String> spinnerValue = new ArrayList<String>();
        spinnerValue.add("A Block");
        spinnerValue.add("B Block Girls Hostel");
        spinnerValue.add("B Block Boys Hostel");
        spinnerValue.add("C Block");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(roomSheetView.getContext(),android.R.layout.simple_spinner_dropdown_item,spinnerValue);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blockSpinner.setAdapter(arrayAdapter);



        userPhone.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Toast.makeText(v.getContext(),"Hello World",Toast.LENGTH_SHORT).show();

//                mobileBottomSheetDialog.setContentView(mobileSheetView);
                mobileSheetView.findViewById(R.id.newMobileSection).setVisibility(View.VISIBLE);
                mobileSheetView.findViewById(R.id.verfiyOTPSection).setVisibility(GONE);
                mobileSheetView.findViewById(R.id.callMobile).setVisibility(GONE);
                mobileBottomSheetDialog.show();

                return true;
            }
        });
        userLocation.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                roomBottomSheetDialog.show();
                return true;
            }
        });



        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
//                // TODO: 29/6/17  Unsubscribe user from all category topics other than topic info


                Intent intent = new Intent(v.getContext(),LoginActivity.class);
                v.getContext().startActivity(intent);
            }
        });
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPhone = newMobile.getText().toString();
                new verfiyPhone().execute(newPhone);
                mobileSheetView.findViewById(R.id.newMobileSection).setVisibility(GONE);
                mobileSheetView.findViewById(R.id.verfiyOTPSection).setVisibility(View.VISIBLE);
                mobileSheetView.findViewById(R.id.callMobile).setVisibility(GONE);
                mobileSheetView.findViewById(R.id.OTPText).requestFocus();

            }
        });



        updateRoomBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog addRoom = new ProgressDialog(roomSheetView.getContext());
                addRoom.setMessage("Adding your room info");
                addRoom.setCancelable(false);
                addRoom.show();
                String part1 = roomNo.getText().toString();
                String part2 = blockSpinner.getSelectedItem().toString();
                final String result= "Room No "+part1+", "+part2;
                String userKey = currentUser.getEmail().replace(".",",");
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("users");
                ref.child(userKey).child("roomNo").setValue(result, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        addRoom.hide();
                        if(databaseError == null)
                        {
                            userLocation.setTextColor(Color.BLACK);
                            userLocation.setText(result);
                            roomNo.setText("");
                            roomBottomSheetDialog.hide();
                        }
                        else
                        {
                            Log.e("VITCC ROOM UPDATER","Database error on updating");
                        }
                    }
                });
            }
        });


        verfiyOTPText = (EditText) mobileBottomSheetDialog.findViewById(R.id.OTPText);
        verifyOTPButton = (Button) mobileBottomSheetDialog.findViewById(R.id.verifyOTP);
        mobileBottomSheetDialog.findViewById(R.id.resendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new verfiyPhone().execute(newPhone);
            }
        });

        verifyOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpText = verfiyOTPText.getText().toString();
                if(Integer.parseInt(otpText) == OTPCode)
                {
                    Log.e("VITCC","Verfiy OTP Verfied");
                    final ProgressDialog addingPhone = new ProgressDialog(mobileSheetView.getContext());
                    addingPhone.setMessage("Adding your phone details");
                    addingPhone.setCancelable(false);
                    addingPhone.show();
                    FirebaseDatabase database  =FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("users");
                    String userKey = currentUser.getEmail().toLowerCase().replace(".",",");
                    ref.child(userKey).child("mobileNumber").setValue(newPhone, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            addingPhone.hide();
                            if(databaseError == null)
                            {
                                userPhone.setTextColor(Color.BLACK);
                                userPhone.setText(newPhone);
                                mobileBottomSheetDialog.hide();
                                verfiyOTPText.setText("");
                                newMobile.setText("");
                            }
                            else
                            {
                                Log.e("VITCC PHONE UPDATER","Error in database");
                            }
                        }
                    });
                }
                else
                {
                    verfiyOTPText.setError("Invalid OTP Code");
                    Log.e("VITCC","Verfiy OTP Invalid");
                }
            }
        });






    }

    private void getAndSetFromDB(final View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String key = currentUserEmail.toLowerCase().replace(".",",");
        DatabaseReference ref = database.getReference("users/"+key);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userDisplayName.setText(user.fullName);
                userEmail.setText(user.email);
                String phone = user.mobileNumber;
                FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
                FirebaseUser activeUser = firebaseAuth.getCurrentUser();
                new DownloadImageTask(profileImage).execute(user.photoURL);
                Log.e("VITCC PROFILE","currentUserEmail:"+currentUserEmail);
                Log.e("VITCC PROFILE","activeUserEmail:"+activeUser.getEmail());
                Log.e("VITCC PROFILE","comparison"+currentUserEmail.equals(activeUser.getEmail()));

                if(currentUserEmail.equals(activeUser.getEmail()))
                {
                    Log.e("VITCC PROFILE","Comparison true");
                    mobileSheetView.findViewById(R.id.newMobileSection).setVisibility(View.VISIBLE);
                    mobileSheetView.findViewById(R.id.callMobile).setVisibility(GONE);

                    //Section for the same user
                    if(phone.length() == 0)
                    {
                        userPhone.setText("Please add your phone");
                        userPhone.setClickable(true);
                        userPhone.setTextColor(Color.BLUE);
                    }
                    else {
                        userPhone.setText(phone);
                        userPhone.setClickable(true);
                    }


                    if (user.roomNo.length() == 0)
                    {
                        userLocation.setText("Please update your room details");
                        userLocation.setClickable(true);
                        userLocation.setTextColor(Color.BLUE);
                    }
                    else
                    {
                        userLocation.setTextColor(Color.BLACK);
                        userLocation.setText(user.roomNo);
                        userLocation.setClickable(true);
                    }







                }
                else
                {
                    Log.e("VITCC PROFILE","Comparison false");
                    mobileSheetView.findViewById(R.id.newMobileSection).setVisibility(GONE);
                    mobileSheetView.findViewById(R.id.callMobile).setVisibility(View.VISIBLE);
                    signOutButton.setVisibility(GONE);
                    //Section for visiting profile

                    if(phone.length() == 0)
                    {
                        userPhone.setText("No Phone number given");
                        userPhone.setClickable(false);
                        userPhone.setTextColor(Color.BLUE);

                    }
                    else {
                        userPhone.setText(phone);
                        userPhone.setClickable(false);
                        userPhone.setEnabled(false);
                        userPhone.setTextIsSelectable(true);
                    }


                    if (user.roomNo.length() == 0)
                    {
                        userLocation.setText("No room info specified");
                        userLocation.setClickable(false);
                        userLocation.setEnabled(false);
                        userLocation.setTextColor(Color.BLUE);
                    }
                    else
                    {
                        userLocation.setTextColor(Color.BLACK);
                        userLocation.setText(user.roomNo);
                        userLocation.setClickable(false);
                        userLocation.setEnabled(false);
                    }





                }


                nowUser = new User(user);
                setOnClickandOnTouchListneresBottomSheet();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("VITCC","Cancelled from user profile");

            }
        });
    }

    private void setOnClickandOnTouchListneresBottomSheet() {
        mobileSheetView.findViewById(R.id.callMobile).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String text = nowUser.mobileNumber;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+text));
                startActivity(intent);
                return true;
            }
        });

    }


    private class verfiyPhone extends AsyncTask<String, Void, String>
    {
        ProgressDialog progressDialog;
        @Override
        protected  void onPreExecute()
        {
            progressDialog = new ProgressDialog(mobileSheetView.getContext());
            progressDialog.setMessage("Sending OTP");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }
        @Override
        protected String doInBackground(String... params) {
            String newPhone = params[0];
            Log.e("VITCC Verify Phone",newPhone);
            JSONObject parameters = new JSONObject();
            String urlString = "http://smsgateway.me/api/v3/messages/send";
            InputStream in = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                parameters.put("email","judeosby@gmail.com");
                parameters.put("password","123456789");
                parameters.put("number",newPhone);
                Random random = new Random();
                int otp = 100000+random.nextInt(999999);
                OTPCode = otp;
                parameters.put("message","Your otp for VITCC Universal Database is "+String.valueOf(otp));
                parameters.put("device","51216");

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
                dataOutputStream.writeBytes(parameters.toString());
                Log.e("VITCC POST Parameters",parameters.toString());
                dataOutputStream.flush();
                dataOutputStream.close();
                
                //Response handler
                
                int responseCode = urlConnection.getResponseCode();
                
                Log.e("VITCC Verify Phone","Reponse Messaage"+urlConnection.getResponseMessage());
                Log.e("VITCC Verify Phone","Reponse Code"+urlConnection.getResponseCode());
                if(responseCode == 200)
                {
                    return "success";
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "fail";
        }
        @Override
        protected void onPostExecute(String a)
        {   progressDialog.hide();

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
