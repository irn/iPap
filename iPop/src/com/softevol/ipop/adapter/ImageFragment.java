package com.softevol.ipop.adapter;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.softevol.widget.ThumbnailView;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: android
 * Date: 14.03.13
 * Time: 17:16
 * To change this template use File | Settings | File Templates.
 */
public class ImageFragment extends android.support.v4.app.Fragment {

    public static final String EXTRA_FILE = "extra_file";
    private String filePath;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.
        if (getArguments() != null && getArguments().containsKey(EXTRA_FILE)){
            filePath = getArguments().getString(EXTRA_FILE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ThumbnailView view = new ThumbnailView(getActivity());

        if (filePath != null)
            view.setImageURI(Uri.fromFile(new File(filePath)));

        return view;
    }
}
