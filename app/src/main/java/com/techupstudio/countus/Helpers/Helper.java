package com.techupstudio.countus.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class Helper {

    private Helper(){};

    public static String getString(EditText field){
        return  field.getText().toString().trim();
    }

    public static boolean isEmpty(String... words){
        for (String word : words){
            if (TextUtils.isEmpty(word)){
                return true;
            }
        }
        return false;
    }

    public static void shortToast(Context context, String message, String... position){
        String location = "";
        if (position.length > 0){ location = position[0];}
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        switch (location){
            case "center":
                toast.setGravity(Gravity.CENTER, 0, 0);break;
            case "top":
                toast.setGravity(Gravity.TOP, 0, 0);break;
            case "left":
                toast.setGravity(Gravity.START, 0, 0);break;
            case "right":
                toast.setGravity(Gravity.END, 0, 0);break;
            case "bottom":
                toast.setGravity(Gravity.BOTTOM, 0, 0);break;
            default:
                break;
        }
        toast.show();
    }

    public static void longToast(Context context, String message, String... position){
        String location = "";
        if (position.length > 0){ location = position[0];}
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        switch (location){
            case "center":
                toast.setGravity(Gravity.CENTER, 0, 0);break;
            case "top":
                toast.setGravity(Gravity.TOP, 0, 0);break;
            case "left":
                toast.setGravity(Gravity.LEFT, 0, 0);break;
            case "right":
                toast.setGravity(Gravity.RIGHT, 0, 0);break;
            case "bottom":
                toast.setGravity(Gravity.BOTTOM, 0, 0);break;
            default:
                break;
        }
        toast.show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.d("NetworkCheck", "isNetworkAvailable: No");
            return false;
        }

        NetworkInfo[] info = connectivity.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    Log.d("NetworkCheck", "isNetworkAvailable: Yes");
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isValidEmailId(String email){
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public static void log(String... messages){
        for (String message : messages){
            Log.d("TechupStudio.CountUs", message);
        }
    }

}
