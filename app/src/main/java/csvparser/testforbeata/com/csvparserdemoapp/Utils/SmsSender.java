package csvparser.testforbeata.com.csvparserdemoapp.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.util.ArrayList;

import csvparser.testforbeata.com.csvparserdemoapp.Activities.MainActivity;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by nauman on 13/04/2018.
 */

public class SmsSender {

    public static void sendSMS(String message, Context context,String number) {

          //  TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
           // String number = tm.getLine1Number();
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(message);

            sms.sendMultipartTextMessage(number, null, parts, null, null);

        
    }
}
