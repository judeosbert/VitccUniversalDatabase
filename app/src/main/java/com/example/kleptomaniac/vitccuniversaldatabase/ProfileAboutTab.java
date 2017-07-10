package com.example.kleptomaniac.vitccuniversaldatabase;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kleptomaniac on 9/7/17.
 */

public class ProfileAboutTab extends Fragment {


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,Bundle saveInstance)
    {
        View view = layoutInflater.inflate(R.layout.profile_about_tab,container,false);

        return view;
    }


}


