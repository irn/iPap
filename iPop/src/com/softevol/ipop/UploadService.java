package com.softevol.ipop;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadService extends Service {
    private static final String TAG = UploadService.class.getSimpleName();

    public static final String EXTRA_UPLOAD_DATA = "EXTRA_UPLOAD_DATA";
    public static final String EXTRA_NAME = "EXTRA_NAME";
    public static final String EXTRA_EMAIL = "EXTRA_EMAIL";
    public static final String EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION";
    public static final String EXTRA_FILE_NAMES = "EXTRA_FILE_NAMES";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String[] info = new String[4];
        info[0] = intent.getStringExtra(EXTRA_NAME);
        info[1] = intent.getStringExtra(EXTRA_EMAIL);
        info[2] = intent.getStringExtra(EXTRA_DESCRIPTION);
        String[] files = intent.getStringArrayExtra(EXTRA_FILE_NAMES);
        UploadData data = (UploadData) intent.getSerializableExtra(EXTRA_UPLOAD_DATA);

        new UploadAsyncTask().execute(data);

        mTasks.add(startId);

        return 0;
    }


    private class UploadAsyncTask extends AsyncTask<UploadData, Void, Void> {
        @Override
        protected Void doInBackground(UploadData... data) {
            UploadData uploadData = data[0];
//            String name = info[0];
//            String email = info[1];
//            String description = info[2];
//            String fileName = info[3];

            try {
                HttpClient httpclient = new DefaultHttpClient();
                httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

                HttpPost httppost = new HttpPost("http://theleeryderband.co.uk/isoimages/tR28z512.php");
//                File file = new File(uploadData.getFiles().get(0));

                MultipartEntity mpEntity = new MultipartEntity();
                mpEntity.addPart("name", new StringBody(uploadData.getName()));
                mpEntity.addPart("email", new StringBody(uploadData.getEmail()));
                mpEntity.addPart("message", new StringBody(uploadData.getDescription()));
                int i=0;
                for (String fileName : uploadData.getFiles()){
                    File file = new File(fileName);
                    mpEntity.addPart("userfile_"+i, new FileBody(file, "image/*"));
                    i++;
                }
//                mpEntity.addPart("userfile_0", new FileBody(file, "image/*"));

                httppost.setEntity(mpEntity);
                System.out.println("executing request " + httppost.getRequestLine());
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();

                System.out.println(response.getStatusLine());
                if (resEntity != null) {
                    System.out.println(EntityUtils.toString(resEntity));
                }
                if (resEntity != null) {
                    resEntity.consumeContent();
                }

                httpclient.getConnectionManager().shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*Log.d(TAG, "start uploading of " + fileName);

            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;
            DataInputStream inputStream = null;

            String pathToOurFile = fileName; //complete path of file from your android device
            String urlServer = "http://ec2-184-73-35-209.compute-1.amazonaws.com/isoimage/tR28z512.php";// complete path of server
            String lineEnd = "m";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            try {
                FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));

                URL url = new URL(urlServer);
                connection = (HttpURLConnection) url.openConnection();

                // Allow Inputs & Outputs
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                // Enable POST method
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; " +
                        "name=\"" + name + "\";" +
                        "message=\"" + description + "\";" +
                        "email=\"" + email+ "\"" +
                        "" + lineEnd);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

                Log.d(TAG, "finish uploading of " + fileName);
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, "ERROR " + ex.toString());
            }*/
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mTasks.remove(0);

            if (mTasks.size() == 0) {
                UploadService.this.stopSelf();
            }
        }
    }

    private List<Integer> mTasks = new ArrayList<Integer>();
}
