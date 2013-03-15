package com.softevol.ipop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BaseContentActivity extends FragmentActivity {
    private static final String TAG = BaseContentActivity.class.getSimpleName();

    public static final String EXTRA_CONTENT_PATH = "EXTRA_CONTENT_PATH";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_RESULT = "EXTRA_RESULT";

    protected static final int DIALOG_INCORRECT_FILE = 1;

    public static final int RESULT_RETAKE = 33;
    public static final int RESULT_UPLOAD = 34;

    public static final int TYPE_IMAGE_FROM_GALLERY = 1;
    public static final int TYPE_IMAGE_FROM_CAMERA = 2;
    public static final int TYPE_VIDEO_FROM_GALLERY = 3;
    public static final int TYPE_VIDEO_FROM_CAMERA = 4;

    protected List<String> files = new ArrayList<String>();

    protected File mCreatingContentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_content_activity_flipper);

        String filePath = getIntent().getStringExtra(EXTRA_CONTENT_PATH);
        files.add(filePath);

        mType = getIntent().getIntExtra(EXTRA_TYPE, -1);

        Log.d(TAG, "mType: " + mType);

        if (mType < 1 || mType > 4) throw new IllegalArgumentException("mType < 1 || mType > 4");

        Button retryButton = (Button) findViewById(R.id.retake_button);
        Button uploadButton = (Button) findViewById(R.id.upload_button);

        switch (mType) {
            case TYPE_IMAGE_FROM_GALLERY:
//                retryButton.setVisibility(View.GONE);
                retryButton.setText("Cancel");
                break;
            case TYPE_IMAGE_FROM_CAMERA:
                retryButton.setText("Retake photo");
                break;
            case TYPE_VIDEO_FROM_GALLERY:
//                retryButton.setVisibility(View.GONE);
                retryButton.setText("Cancel");
                break;
            case TYPE_VIDEO_FROM_CAMERA:
                retryButton.setText("Rerecord video");
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_INCORRECT_FILE:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Sorry, incorrect file. Please contact support.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
        }
        return super.onCreateDialog(id);
    }

    /**
     * Add image or video from gallery
     * @param view button which pressed
     */
    public void addGallery(View view) {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/* video/*");
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

//        if (resultCode != Activity.RESULT_OK){
//            return;
//        }

        switch (requestCode){
            case MainActivity.REQUEST_SELECT_CONTENT:
                if (data != null && data.getData() != null) {
                    String file = MainActivity.getFilePathFromContentUri(data.getData(), getContentResolver()).getAbsolutePath();
                    if (file != null)
                        files.add(file);
                }
                break;
            case MainActivity.REQUEST_TAKE_PHOTO:
                if (mCreatingContentFile != null
                        && mCreatingContentFile.exists()
                        && !files.contains(mCreatingContentFile.getAbsolutePath())){
                    files.add(mCreatingContentFile.getAbsolutePath());
                }

                break;
        }
    }

    public void retake(View view) {
        if (mType == TYPE_IMAGE_FROM_GALLERY || mType == TYPE_VIDEO_FROM_GALLERY) {
            setResult(RESULT_CANCELED);
        } else {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_TYPE, mType);
            intent.putExtra(EXTRA_RESULT, RESULT_RETAKE);
//            intent.putExtra(EXTRA_CONTENT_PATH, getIntent().getStringExtra(EXTRA_CONTENT_PATH));
            setResult(RESULT_RETAKE, intent);
        }
        finish();
    }

    public void upload(View view) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TYPE, mType);
        intent.putExtra(EXTRA_RESULT, RESULT_UPLOAD);
//        intent.putExtra(EXTRA_CONTENT_PATH, getIntent().getStringExtra(EXTRA_CONTENT_PATH));
        String[] result = new String[files.size()];
        for (int i=0; i< result.length; i++){
            result[i] = files.get(i);
        }
        intent.putExtra(EXTRA_CONTENT_PATH, result);
        setResult(RESULT_UPLOAD, intent);
        finish();
    }

    protected int mType;
}
