package com.softevol.ipop;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;
import com.softevol.ipop.adapter.ImagePagerAdapter;

public class ImageViewActivity extends BaseContentActivity {


    private ImagePagerAdapter imagePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        imagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), files);
        pager.setAdapter(imagePagerAdapter);

    }


    public void addCamera(View view) {
        mCreatingContentFile = FileHelper.getNextFile(FileHelper.FileType.IMAGE);
        Uri outputFileUri = Uri.fromFile(mCreatingContentFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, MainActivity.REQUEST_TAKE_PHOTO);
    }

    /**
     * Add image from gallery
     * @param view button which pressed
     */
    public void addGallery(View view) {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
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
        if (resultCode == Activity.RESULT_OK){
            imagePagerAdapter.notifyDataSetChanged();
        }

    }

}
