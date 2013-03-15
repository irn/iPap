package com.softevol.ipop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UploadActivity extends Activity {
    public static final String EXTRA_CONTENT_PATH = "EXTRA_CONTENT_PATH";

    private static final int DIALOG_UNKNOWN_ERROR = 1;
    private static final int DIALOG_ENTER_REQUIRED_DATA = 2;
    private static final int DIALOG_NO_INTERNET_CONNECTION = 3;

    private static final String KEY_NAME = "KEY_NAME";
    private static final String KEY_EMAIL = "KEY_EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_activity);

        mNameEditText = (EditText) findViewById(R.id.name_edit_text);
        mEmailEditText = (EditText) findViewById(R.id.name_email_text);
        mDescriptionEditText = (EditText) findViewById(R.id.name_description_text);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPreferences.getString(KEY_NAME, "");
        String email = sharedPreferences.getString(KEY_EMAIL, "");

        mNameEditText.setText(name);
        mEmailEditText.setText(email);
    }

    public void cancel(View view) {
        finish();
    }

    public void upload(View view) {
        String name = mNameEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String description = mDescriptionEditText.getText().toString();
//        String fileName = getIntent().getStringExtra(EXTRA_CONTENT_PATH);
        String[] fileNames = getIntent().getStringArrayExtra(BaseContentActivity.EXTRA_CONTENT_PATH);

        if (haveInternet()) {
            if (fileNames == null || fileNames.length <= 0) {
                showDialog(DIALOG_UNKNOWN_ERROR);
            } else {
                if (email == null || email.length() <= 0) {
                    showDialog(DIALOG_ENTER_REQUIRED_DATA);
                } else {
                    Toast.makeText(this, "File is uploading...", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(this, UploadService.class);
//                    intent.putExtra(UploadService.EXTRA_NAME, name);
//                    intent.putExtra(UploadService.EXTRA_EMAIL, email);
//                    intent.putExtra(UploadService.EXTRA_DESCRIPTION, description);
//                    intent.putExtra(UploadService.EXTRA_FILE_NAMES, fileNames);
                    UploadData uploadData = new UploadData();
                    uploadData.setName(name);
                    uploadData.setEmail(email);
                    uploadData.setDescription(description);
                    for (String file : fileNames){
                        uploadData.addFile(file);
                    }
                    intent.putExtra(UploadService.EXTRA_UPLOAD_DATA, uploadData);

                    startService(intent);

                    // Save entered data to prevent user from entering it couple of times
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    editor.putString(KEY_NAME, name);
                    editor.putString(KEY_EMAIL, email);
                    editor.commit();

                    finish();
                }
            }
        } else {
            showDialog(DIALOG_NO_INTERNET_CONNECTION);
        }
    }

    //Checks if we have a valid Internet Connection on the device.
    public boolean haveInternet() {
        NetworkInfo info = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {
            return false;
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case DIALOG_UNKNOWN_ERROR:
                builder.setTitle("Error");
                builder.setMessage("Unknown error occupied, please contact support.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                return builder.create();
            case DIALOG_ENTER_REQUIRED_DATA:
                builder.setTitle("Error");
                builder.setMessage("Please enter you email or phone. This info is needed to contact you.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                return builder.create();
            case DIALOG_NO_INTERNET_CONNECTION:
                builder.setTitle("Error");
                builder.setMessage("You are not connected to the internet, please connect and try again.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ;
                    }
                });
                return builder.create();
        }
        return super.onCreateDialog(id);
    }


    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mDescriptionEditText;
}
