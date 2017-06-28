package com.example.kleptomaniac.vitccuniversaldatabase;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kleptomaniac on 25/6/17.
 */

public class ProfileRequestsTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,Bundle savedInstance)
    {
        return layoutInflater.inflate(R.layout.profile_requests_tab,container,false);
    }

}
