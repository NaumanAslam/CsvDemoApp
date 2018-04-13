package csvparser.testforbeata.com.csvparserdemoapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import csvparser.testforbeata.com.csvparserdemoapp.R;
import csvparser.testforbeata.com.csvparserdemoapp.Utils.ParserUtility;
import csvparser.testforbeata.com.csvparserdemoapp.Utils.SmsSender;


public class MainActivity extends Activity implements View.OnClickListener{
    static  int smsSentCounter=0,totalRunningTimer=0;
    EditText etMessage,etTimer,etRandStartRange,etRandEndRange;
    Button btnBrowse,btnSend;TimerTask timerTask;
    public final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1,MY_PERMISSIONS_REQUEST_SEND_SMS=2,MY_PERMISSIONS_REQUEST_RECEIVED_FILE=3;
    Intent intent;ArrayList<String[]> _data =new ArrayList();List<String> _columnTitles = new ArrayList<>();
    TextView tvAvailVars,tvDataSize; private Timer timer;
    RadioGroup radioGroup;      int timerIntLocal=0;

    int counter=0, randomTimerInt = 0;Random random;
    ;
    ArrayList<String> _matchedTags = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }
    private void initViews(){
        random = new SecureRandom();
        etMessage=findViewById(R.id.et_message);
        etRandStartRange=findViewById(R.id.et_rand_start);
        etRandEndRange=findViewById(R.id.et_rand_end);
        etTimer=findViewById(R.id.et_time);
        btnBrowse=findViewById(R.id.btnBrowse);
        btnSend=findViewById(R.id.btnSend);
        tvAvailVars=findViewById(R.id.tv_avai_vars);
        tvDataSize=findViewById(R.id.tv_datassize);
        radioGroup=findViewById(R.id.radio_group);

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
                            new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_STATE},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
                else {

                    manageSMSservice();
                    // Permission has already been granted
                }
                break;
            }
        }
    }
    private void manageSMSservice(){
        if (validatedSms()&&_data.size()>0){
            counter = 0;
            Log.e("Validated","yes");
            if (radioGroup.getCheckedRadioButtonId()==R.id.by_timer){
                stop();
                prepareAndSendSms(false);
                // startTimer(false);
            }
            else{
                Log.e("RandomSelected",randomTimerInt+"");
                stop();
                prepareAndSendSms(true);
                //startTimer(true);
            }
        }
        else {
            if (_data.size()==0) {
                Toast.makeText(getApplicationContext(), "CSV not loaded",Toast.LENGTH_SHORT).show();
            }

            Log.e("Validated","no");

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
                    Toast.makeText(getApplicationContext(),"Sorry! you didn't allow :(",Toast.LENGTH_SHORT).show();

                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    manageSMSservice();
                } else {
                    Toast.makeText(getApplicationContext(),"Sorry! you didn't allow :(",Toast.LENGTH_SHORT).show();

                }
                return;
            }

        }
    }
    Boolean validatedSms(){
        if (radioGroup.getCheckedRadioButtonId()==R.id.by_timer){
            if (!etTimer.getText().toString().equals("")){

                return true;
            }
            else {
                etTimer.setError("Required");
            }
        }
        else {
            if (!etRandStartRange.getText().toString().equals("")&&!etRandEndRange.getText().toString().equals("")){

                return true;
            }
            else {
                if (etRandStartRange.getText().toString().equals(""))
                    etRandStartRange.setError("Required");
                if (etRandEndRange.getText().toString().equals(""))
                    etRandEndRange.setError("Required");

            }

        }
        return false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch(requestCode){

            case MY_PERMISSIONS_REQUEST_RECEIVED_FILE:

                if(resultCode==RESULT_OK){

                    Uri PathHolder = data.getData();
                    //Toast.makeText(MainActivity.this, PathHolder.toString() , Toast.LENGTH_LONG).show();

                    Uri uri = data.getData();
                    _columnTitles= ParserUtility.parseCSVForColumnTitles(uri,MainActivity.this);
                    _data= ParserUtility.parseCSVForData(uri,MainActivity.this);

                    String strAvailVars="";
                    for (int i =0 ; i<_columnTitles.size();i++){
                        strAvailVars=strAvailVars+"{"+_columnTitles.get(i)+"} ; ";
                    }
                    tvAvailVars.setText("Available variables "+strAvailVars);
                    tvDataSize.setText("Imported contacts "+(_data.size()>0 ? _data.size()-1:_data.size()));
                }
                break;

        }
    }

    private  int generateRandom(){

        int max = Integer.parseInt(etRandEndRange.getText().toString());
        int min = Integer.parseInt(etRandStartRange.getText().toString());
        return randomTimerInt = random.nextInt((max-min)+1)+min;

    }
    private void startTimer(final Boolean isRandomSelected, final String message) {
        final String timerStrLocal = etTimer.getText().toString();
        if (!timerStrLocal.equals("")) {
           timerIntLocal = Integer.parseInt(etTimer.getText().toString());
        }
        counter = 0;

        if (isRandomSelected) {
            randomTimerInt = generateRandom();
        }
        timerTask = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        counter++;
                        totalRunningTimer++;
                        if (totalRunningTimer/60==5){
                            notifyThis("CsvDemo","Total "+smsSentCounter+ " SMS sent!");
                            totalRunningTimer=0;
                        }
                        if (isRandomSelected) {
                            if (counter == randomTimerInt) {
                                Log.e("SmsGoing", "Work " + counter);
                                SmsSender.sendSMS(message,MainActivity.this);
                                ++smsSentCounter;
                                counter = 0;
                                randomTimerInt = generateRandom();
                            }
                        } else {
                            if (counter ==timerIntLocal ) {
                                Log.e("SmsGoing", "Work " + counter);
                                SmsSender. sendSMS(message,MainActivity.this);
                                ++smsSentCounter;

                                counter = 0;
                            }
                        }
                        Log.e("Timer", "Work " + counter);
                    }

                });

            }
        };

        start();
    }
    private  void prepareAndSendSms(Boolean isRandomSelected){
        smsSentCounter=0;
        String messageBody = etMessage.getText().toString();
        final Pattern pattern = Pattern.compile("\\{(.+?)\\}");
        final Matcher matcher = pattern.matcher(messageBody);
        _matchedTags.clear();
        while (matcher.find()){
            _matchedTags.add(matcher.group(1));
        }
        HashSet hs = new HashSet();
        hs.addAll(_matchedTags);
        _matchedTags.clear();
        _matchedTags.addAll(hs);

        for (int i=0;i<_matchedTags.size();i++){

            messageBody=messageBody.replace("{"+_matchedTags.get(i)+"}",fetchTextAgainstTag(_matchedTags.get(i)));
        }
        Toast.makeText(getApplicationContext(),"Starting sms service",Toast.LENGTH_SHORT).show();
        Log.e("Prepared",_matchedTags.size()+"");
        startTimer(isRandomSelected,messageBody);

    }
    String fetchTextAgainstTag(String tag){
        int columnPosInt = 0;
        for (int i=0;i<_columnTitles.size();i++){
            if (_columnTitles.get(i).equals(tag))
                columnPosInt = i;
        }
        if (_data.size()>1) {
            String[] _tempDataInt = _data.get(1);//we can add a random method here to get random rows everytime
            return  _tempDataInt[columnPosInt];

        }
        return "";
    }
    public void start() {
        if(timer != null) {
            return;
        }
        timer = new Timer();

        timer.scheduleAtFixedRate(timerTask, 0, 1000);

    }

    public void stop() {
        if (timer !=null)
            timer.cancel();
        if (timerTask!=null)
            timerTask.cancel();
        timer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    public void notifyThis(String title, String message) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(MainActivity.this,"1");
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(message)
                .setContentTitle(title)
                .setContentText(message)
                .setContentInfo("INFO");
  
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, b.build());
    }
}
