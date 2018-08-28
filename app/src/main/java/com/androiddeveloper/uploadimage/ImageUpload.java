package com.androiddeveloper.uploadimage;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUpload extends AppCompatActivity {
    ProgressDialog prgDialog;
      String imgPath, fileName;

    private static int RESULT_LOAD_IMG = 1;
    private int serverResponseCode;
    private String serverResponseMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);

//        new UploadFileAsync().execute();
    }





    // When Image is selected from Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgPath));
                // Get the Image's file name
                String fileNameSegments[] = imgPath.split("/");
                fileName = fileNameSegments[fileNameSegments.length - 1];
                Log.i("","Paths"  +imgPath);
                // Put file name in Async Http Post Param which will used in Php web app
//                params.put("file", fileName);


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }



    // When Upload button is clicked
    public void uploadImage(View v) {
        // When Image is selected from Gallery
        if (imgPath != null && !imgPath.isEmpty()) {
            prgDialog.setMessage("Uploading image to server....");
            prgDialog.show();
            // Convert image to String using Base64
//            encodeImagetoString();
            new UploadFileAsync().execute(imgPath);
            // When Image is not selected from Gallery
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "You must select image from gallery before you try to upload",
                    Toast.LENGTH_LONG).show();
        }
    }





    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // Dismiss the progress bar when application is closed
        if (prgDialog != null) {
            prgDialog.dismiss();
        }
    }


    public void loadImage(View view) {

             loadImagefromGallery();

    }

    public void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }


//    public String postImageToImagga(String filepath) throws Exception {
//        HttpURLConnection connection = null;
//        DataOutputStream outputStream = null;
//        InputStream inputStream = null;
//
//        String twoHyphens = "--";
//        String boundary =  "*****"+Long.toString(System.currentTimeMillis())+"*****";
//        String lineEnd = "\r\n";
//
//        int bytesRead, bytesAvailable, bufferSize;
//        byte[] buffer;
//        int maxBufferSize = 1*1024*1024;
//
//        String filefield = "image";
//
//        String[] q = filepath.split("/");
//        int idx = q.length - 1;
//
//        File file = new File(filepath);
//        FileInputStream fileInputStream = new FileInputStream(file);
//
//        URL url = new URL("http://10.11.0.24/my/upload/index.php");
//        connection = (HttpURLConnection) url.openConnection();
//
//        connection.setDoInput(true);
//        connection.setDoOutput(true);
//        connection.setUseCaches(false);
//
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Connection", "Keep-Alive");
//        connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
//        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
//        connection.setRequestProperty("Authorization", "<insert your own Authorization e.g. Basic YWNjX>");
//
//        outputStream = new DataOutputStream(connection.getOutputStream());
//        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; file=\"" + q[idx] +"\"" + lineEnd);
//        outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
//        outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
//        outputStream.writeBytes(lineEnd);
//
//        bytesAvailable = fileInputStream.available();
//        bufferSize = Math.min(bytesAvailable, maxBufferSize);
//        buffer = new byte[bufferSize];
//
//        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//        while(bytesRead > 0) {
//            outputStream.write(buffer, 0, bufferSize);
//            bytesAvailable = fileInputStream.available();
//            bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//        }
//
//        outputStream.writeBytes(lineEnd);
//        outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//        inputStream = connection.getInputStream();
//
//        int status = connection.getResponseCode();
//        if (status == HttpURLConnection.HTTP_OK) {
//            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//
//            inputStream.close();
//            connection.disconnect();
//            fileInputStream.close();
//            outputStream.flush();
//            outputStream.close();
//
//            return response.toString();
//        } else {
//            throw new Exception("Non ok response returned");
//        }
//    }




    private class UploadFileAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String str = params[0];

            try {

//                String sourceFileUri = "/storage/emulated/0/Pictures/Screenshots/Screenshot_20180821-202558.png";

                String sourceFileUri = str;
                Log.i("","ImagePath in BAckground " +sourceFileUri);

                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                File sourceFile = new File(sourceFileUri);

                if (sourceFile.isFile()) {

                    try {
                        String upLoadServerUri = "http://10.11.0.24/my/upload/index.php?";

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        URL url = new URL(upLoadServerUri);

                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE",
                                "multipart/form-data");
                        conn.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("file", sourceFileUri);

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                                + sourceFileUri + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math
                                    .min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,
                                    bufferSize);

                        }

                        // send multipart form data necesssary after file
                        // data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);

                        // Responses from the server (code and message)
                        serverResponseCode = conn.getResponseCode();
                          serverResponseMessage = conn.getResponseMessage();

                        if (serverResponseCode == 200) {

                            // messageText.setText(msg);
                            //Toast.makeText(ctx, "File Upload Complete.",
                            //      Toast.LENGTH_SHORT).show();

                            // recursiveDelete(mDirectory1);
                            Log.i("uploadFile", "HTTP Response is : "
                                    + serverResponseMessage + ": " + serverResponseCode);
                        }

                        // close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();


                        //Get Response
                        InputStream is = conn.getInputStream();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        String line;
                        StringBuffer response = new StringBuffer();
                        Log.i("","response From server" + response);
                        while ((line = rd.readLine()) != null) {
                            response.append(line);
                            response.append('\r');
                        }
                        rd.close();




                    } catch (Exception e) {

                        // dialog.dismiss();
                        e.printStackTrace();

                    }
                    // dialog.dismiss();

                } // End else block


            } catch (Exception ex) {
                // dialog.dismiss();

                ex.printStackTrace();
            }
            return serverResponseMessage;
        }

        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            if (prgDialog != null) {
                prgDialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}