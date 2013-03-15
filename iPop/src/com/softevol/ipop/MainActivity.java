package com.softevol.ipop;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_SELECT_CONTENT = 1;
    public static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_RECORD_VIDEO = 3;
    private static final int REQUEST_IS_VIDEO_OK = 4;
    private static final int REQUEST_IS_IMAGE_OK = 5;

    private static final int DIALOG_UNKNOWN_ERROR = 1;

    private static final String KEY_SAVE_CREATING_CONTENT_FILE = "KEY_SAVE_CREATING_CONTENT_FILE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        if (savedInstanceState != null && savedInstanceState.getString(KEY_SAVE_CREATING_CONTENT_FILE) != null) {
            mCreatingContentFile = new File(savedInstanceState.getString(KEY_SAVE_CREATING_CONTENT_FILE));
        } else {
            mCreatingContentFile = null;
        }

        Log.d(TAG, "onCreate: " + mCreatingContentFile);

        mTabHome = findViewById(R.id.tab_home);
        mTabEarn = findViewById(R.id.tab_earn);
        mTabAbout = findViewById(R.id.tab_about);

        WebView earnWebView = (WebView) findViewById(R.id.earn_web_view);
        WebView aboutWebView = (WebView) findViewById(R.id.about_web_view);
        earnWebView.setBackgroundColor(0);
        aboutWebView.setBackgroundColor(0);
        earnWebView.loadUrl("file:///android_asset/earn.html");
        aboutWebView.loadUrl("file:///android_asset/about.html");

        WebSettings settings = earnWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings = aboutWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");

        aboutWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "WebBrowser not found, please contact support.", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + mCreatingContentFile);
        if (!mRetaking) {
            if (mCreatingContentFile != null) {
                openFileForPreview(mCreatingContentFile, true);
            }
            mCreatingContentFile = null;
        } else {
            mRetaking = false;
        }
    }

    public void takePhoto(View view) {
        System.gc();
        mCreatingContentFile = FileHelper.getNextFile(FileHelper.FileType.IMAGE);
        Uri outputFileUri = Uri.fromFile(mCreatingContentFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);

        Log.d(TAG, "start taking photo: " + mCreatingContentFile);
    }

    public void recordVideo(View view) {
        System.gc();
        mCreatingContentFile = FileHelper.getNextFile(FileHelper.FileType.VIDEO);
        Uri outputFileUri = Uri.fromFile(mCreatingContentFile);
        Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(mIntent, REQUEST_RECORD_VIDEO);

        Log.d(TAG, "start recording video: " + mCreatingContentFile);
    }

    public void select(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/* video/*");
        try {
            startActivityForResult(galleryIntent, REQUEST_SELECT_CONTENT);
        } catch (ActivityNotFoundException e) {
            galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("*/*");
            try {
                startActivityForResult(galleryIntent, REQUEST_SELECT_CONTENT);
            } catch (ActivityNotFoundException e1) {
                e1.printStackTrace();
                Toast.makeText(this, "Can't find app to select video or image. Please contact support.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int type = -1;
        int result = -1;
        if (data != null && data.hasExtra(BaseContentActivity.EXTRA_TYPE) && data.hasExtra(BaseContentActivity.EXTRA_RESULT)) {
            type = data.getIntExtra(BaseContentActivity.EXTRA_TYPE, -1);
            result = data.getIntExtra(BaseContentActivity.EXTRA_RESULT, -1);
        }
        switch (requestCode) {
            case REQUEST_SELECT_CONTENT:
                if (data != null) {
                    openFileForPreview(getFilePathFromContentUri(data.getData(), getContentResolver()), false);
                } else {
                    showDialog(DIALOG_UNKNOWN_ERROR);
                }
                break;
            case REQUEST_IS_VIDEO_OK:
                Log.d(TAG, "onActivityResult| isVideoOk: " + resultCode);
                if (result == BaseContentActivity.RESULT_RETAKE && type == BaseContentActivity.TYPE_VIDEO_FROM_CAMERA) {
                    mRetaking = true;
                    recordVideo(null);
                } else if (result == BaseContentActivity.RESULT_UPLOAD) {
                    Intent intent = new Intent(this, UploadActivity.class);
                    intent.putExtra(UploadActivity.EXTRA_CONTENT_PATH, data.getStringExtra(BaseContentActivity.EXTRA_CONTENT_PATH));
                    startActivity(intent);
                }

                break;
            case REQUEST_IS_IMAGE_OK:
                Log.d(TAG, "onActivityResult| isImageOk: " + resultCode);
                if (result == BaseContentActivity.RESULT_RETAKE && type == BaseContentActivity.TYPE_IMAGE_FROM_CAMERA) {
                    mRetaking = true;
                    takePhoto(null);
                } else if (result == BaseContentActivity.RESULT_UPLOAD) {
                    Intent intent = new Intent(this, UploadActivity.class);
                    intent.putExtra(UploadActivity.EXTRA_CONTENT_PATH, data.getStringArrayExtra(BaseContentActivity.EXTRA_CONTENT_PATH));
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCreatingContentFile != null) {
            outState.putString(KEY_SAVE_CREATING_CONTENT_FILE, mCreatingContentFile.getAbsolutePath());
        }
    }

    /**
     * Gets the corresponding path to a file from the given content:// URI
     *
     * @param selectedVideoUri The content:// URI to find the file path from
     * @param contentResolver  The content resolver to use to perform the query.
     * @return the file  or null
     */
    public static  File getFilePathFromContentUri(Uri selectedVideoUri, ContentResolver contentResolver) {
        String filePath = null;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        if (cursor.moveToFirst()) {

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }

        File file = new File(filePath);
        if (file.exists() && file.canRead()) {
            return file;
        } else {
            return null;
        }
    }

    private void openFileForPreview(File file, boolean fromCamera) {
        Log.d(TAG, "openFileForPreview: " + file);
        switch (FileHelper.getFileType(file)) {
            case IMAGE: {
                Intent intent = new Intent(this, ImageViewActivity.class);
                intent.putExtra(BaseContentActivity.EXTRA_CONTENT_PATH, file.getAbsolutePath());
                intent.putExtra(BaseContentActivity.EXTRA_TYPE, fromCamera ?
                        BaseContentActivity.TYPE_IMAGE_FROM_CAMERA : BaseContentActivity.TYPE_IMAGE_FROM_GALLERY);
                startActivityForResult(intent, REQUEST_IS_IMAGE_OK);
                break;
            }
            case VIDEO: {
                Intent intent = new Intent(this, VideoViewActivity.class);
                intent.putExtra(BaseContentActivity.EXTRA_CONTENT_PATH, file.getAbsolutePath());
                intent.putExtra(BaseContentActivity.EXTRA_TYPE, fromCamera ?
                        BaseContentActivity.TYPE_VIDEO_FROM_CAMERA : BaseContentActivity.TYPE_VIDEO_FROM_GALLERY);
                startActivityForResult(intent, REQUEST_IS_VIDEO_OK);
                break;
            }
            case UNKNOWN:
                // Something strange, let's show dialog about error null File var
                showDialog(DIALOG_UNKNOWN_ERROR);
                break;
        }
    }

    public void tabHome(View view) {
        mTabHome.setVisibility(View.VISIBLE);
        mTabEarn.setVisibility(View.GONE);
        mTabAbout.setVisibility(View.GONE);
    }

    public void tabEarn(View view) {
        mTabHome.setVisibility(View.GONE);
        mTabEarn.setVisibility(View.VISIBLE);
        mTabAbout.setVisibility(View.GONE);
    }

    public void tabAbout(View view) {
        mTabHome.setVisibility(View.GONE);
        mTabEarn.setVisibility(View.GONE);
        mTabAbout.setVisibility(View.VISIBLE);
    }

    private File mCreatingContentFile;
    private boolean mRetaking;

    private View mTabHome;
    private View mTabEarn;
    private View mTabAbout;
}
