package csvparser.testforbeata.com.csvparserdemoapp.Utils;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nauman on 13/04/2018.
 */

public class ParserUtility {

    public static List<String> parseCSVForColumnTitles(Uri path, Context context){
        String line = "";
        String cvsSplitBy = ",";
        List<String> _dataToReply = new ArrayList<>();
        try {

            InputStream inputStream = context.getContentResolver().openInputStream(path);

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            if (br!=null){
                String headers = br.readLine();
                String[] headerArray = headers.split(cvsSplitBy);
                _dataToReply = Arrays.asList(headerArray);
                String a = "";
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return  _dataToReply;
    }
    public static ArrayList<String[]> parseCSVForData(Uri path, Context context){
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<String[]> _dataToReply = new ArrayList<>();
        try {

            InputStream inputStream = context.getContentResolver().openInputStream(path);

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));


            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] columns = line.split(cvsSplitBy);
                _dataToReply.add(columns);
                //System.out.println("Country [code= " + columns[4] + " , name=" + columns[5] + "]");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  _dataToReply;
    }
}
