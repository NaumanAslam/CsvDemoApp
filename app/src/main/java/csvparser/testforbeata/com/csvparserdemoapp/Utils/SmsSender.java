package csvparser.testforbeata.com.csvparserdemoapp.Utils;

import android.content.Context;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by nauman on 13/04/2018.
 */

public class SmsSender  {

    public static void sendSMS(String phoneNumber, String message, Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
        String number = tm.getLine1Number();
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

}
