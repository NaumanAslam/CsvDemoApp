package csvparser.testforbeata.com.csvparserdemoapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener{
    EditText etMessage,etTimer,etRandStartRange,etRandEndRange;
    Button btnBrowse,btnSend;
    public final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1,MY_PERMISSIONS_REQUEST_SEND_SMS=2,MY_PERMISSIONS_REQUEST_RECEIVED_FILE=3;
    Intent intent;ArrayList<String[]> _data =new ArrayList();List<String> _columnTitles = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }
    private void initViews(){
        etMessage=findViewById(R.id.et_message);
        etRandStartRange=findViewById(R.id.et_rand_start);
        etRandEndRange=findViewById(R.id.et_rand_end);
        etTimer=findViewById(R.id.et_time);
        btnBrowse=findViewById(R.id.btnBrowse);
        btnSend=findViewById(R.id.btnSend);
        btnBrowse.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }
    private void openFileExplorer(){
        intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, MY_PERMISSIONS_REQUEST_RECEIVED_FILE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBrowse:{
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_STORAGE);
                }
                else {
                    openFileExplorer();
                }
                break;
            }
            case R.id.btnSend:{
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
                else {
                    // Permission has already been granted
                }
                break;
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    openFileExplorer();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch(requestCode){

            case MY_PERMISSIONS_REQUEST_RECEIVED_FILE:

                if(resultCode==RESULT_OK){

                    Uri PathHolder = data.getData();
                    Toast.makeText(MainActivity.this, PathHolder.toString() , Toast.LENGTH_LONG).show();




                    Uri uri = data.getData();

                    // String pathToFile = getFilePathFromURI(MainActivity.this,uri);

                    csvParser(uri);
                }
                break;

        }
    }
    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(Environment.getExternalStorageDirectory()+"/" + File.separator + fileName);
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void csvParser(Uri path){
        String line = "";
        String cvsSplitBy = ",";
        try {

            InputStream inputStream = getContentResolver().openInputStream(path);

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            if (br!=null){
                String headers = br.readLine();
                String[] headerArray = headers.split(cvsSplitBy);
               _columnTitles = Arrays.asList(headerArray);
                String a = "";
            }
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] columns = line.split(cvsSplitBy);
                _data.add(columns);
                System.out.println("Country [code= " + columns[4] + " , name=" + columns[5] + "]");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
String oo="";

    }
}
