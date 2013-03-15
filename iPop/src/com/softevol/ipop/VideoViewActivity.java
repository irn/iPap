package com.softevol.ipop;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;
import com.softevol.ipop.adapter.VideoPagerAdapter;

public class VideoViewActivity extends BaseContentActivity {
    private static final String TAG = VideoViewActivity.class.getSimpleName();

    private VideoPagerAdapter videoPagerAdapter;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pager = (ViewPager) findViewById(R.id.pager);

        videoPagerAdapter = new VideoPagerAdapter(getSupportFragmentManager(), files);
        pager.setAdapter(videoPagerAdapter);


    }

    public void addCamera(View view) {
        mCreatingContentFile = FileHelper.getNextFile(FileHelper.FileType.VIDEO);
        Uri outputFileUri = Uri.fromFile(mCreatingContentFile);
        Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(mIntent, MainActivity.REQUEST_TAKE_PHOTO);
    }

    /**
     * Add video from gallery
     * @param view button which pressed
     */
    public void addGallery(View view) {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("video/*");
        try {
            startActivityForResult(galleryIntent, MainActivity.REQUEST_SELECT_CONTENT);
        } catch (ActivityNotFoundException e) {
            galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("*/*");
            try {
                startActivityForResult(galleryIntent, MainActivity.REQUEST_SELECT_CONTENT);
            } catch (ActivityNotFoundException e1) {
                e1.printStackTrace();
                Toast.makeText(this, "Can't find app to select video or image. Please contact support.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
//        if (resultCode == Activity.RESULT_OK){
        videoPagerAdapter = new VideoPagerAdapter(getSupportFragmentManager(), files);
        pager.setAdapter(videoPagerAdapter);
//        }

    }


}
