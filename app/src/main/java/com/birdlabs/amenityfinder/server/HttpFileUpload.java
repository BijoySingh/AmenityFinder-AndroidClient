package com.birdlabs.amenityfinder.server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.birdlabs.amenityfinder.activity.AddPhotoActivity;
import com.birdlabs.amenityfinder.util.Preferences;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Upload the file
 * Created by bijoy on 2/12/16.
 */
public class HttpFileUpload extends AsyncTask<String, String, String> {

    private static final String BOUNDARY = "*****";
    private static final String LINE_END = "\r\n";
    private static final String TWO_HYPHENS = "--";

    Context context;
    URL connectURL;
    String responseString;
    Boolean isAnonymous;
    Preferences preferences;
    String filename;
    AddPhotoActivity activity;

    ByteArrayInputStream byteInputStream = null;

    public HttpFileUpload(String urlString, Boolean isAnonymous,
                          Context context, AddPhotoActivity activity) {
        try {
            this.connectURL = new URL(urlString);
            this.isAnonymous = isAnonymous;
            this.context = context;
            this.preferences = new Preferences(context);
            this.activity = activity;
        } catch (Exception exception) {
            Log.e(HttpFileUpload.class.getSimpleName(), "URL not formatted", exception);
        }
    }

    public void setupSend(ByteArrayInputStream byteInputStream, String filename) {
        this.byteInputStream = byteInputStream;
        this.filename = filename;
    }

    private HttpURLConnection getConnection() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("token-auth", preferences.load(Preferences.Keys.AUTH_TOKEN));
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
        return conn;
    }

    private DataOutputStream getDataOutput(HttpURLConnection conn, String filename) throws IOException {
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        dos.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
        dos.writeBytes("Content-Disposition: form-data; name=\"is_anonymous\"" + LINE_END);
        dos.writeBytes(LINE_END);
        dos.writeBytes(isAnonymous.toString());
        dos.writeBytes(LINE_END);
        dos.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
        dos.writeBytes("Content-Disposition: form-data; name=\"picture\";filename=\""
            + filename + "\"" + LINE_END);
        dos.writeBytes(LINE_END);

        return dos;
    }

    private void addData(DataOutputStream dos) throws IOException {
        int bytesAvailable = byteInputStream.available();
        int maxBufferSize = 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // read file and write it into form...
        int bytesRead = byteInputStream.read(buffer, 0, bufferSize);
        while (bytesRead > 0) {
            dos.write(buffer, 0, bufferSize);
            bytesAvailable = byteInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = byteInputStream.read(buffer, 0, bufferSize);
        }
        dos.writeBytes(LINE_END);
        dos.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);

        byteInputStream.close();
        dos.flush();
    }

    private String sending(String filename) {
        responseString = "";
        try {
            Log.d(HttpFileUpload.class.getSimpleName(), "Starting Http File sending to URL");

            // Open a HTTP connection to the URL
            HttpURLConnection conn = getConnection();
            DataOutputStream dos = getDataOutput(conn, filename);
            addData(dos);

            // Read the response
            InputStream inputStream = conn.getInputStream();
            int characterResponse;
            StringBuilder characterBuffer = new StringBuilder();
            while ((characterResponse = inputStream.read()) != -1) {
                characterBuffer.append((char) characterResponse);
            }
            responseString = characterBuffer.toString();

        } catch (MalformedURLException ex) {
            Log.e(HttpFileUpload.class.getSimpleName(), "URL error", ex);
        } catch (IOException ioe) {
            Log.e(HttpFileUpload.class.getSimpleName(), "IO error", ioe);
        }

        return responseString;
    }

    @Override
    protected String doInBackground(String... strings) {
        return sending(filename);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (activity != null) {
            activity.handleResponse(s);
        }
    }
}
