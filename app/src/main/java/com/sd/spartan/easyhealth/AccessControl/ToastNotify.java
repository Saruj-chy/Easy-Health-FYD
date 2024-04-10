package com.sd.spartan.easyhealth.AccessControl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class ToastNotify {

    public static void NetConnectionNotify(Context mCtx) {
        ConnectivityManager connManager = (ConnectivityManager) mCtx.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connManager .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected() || mobile.isConnected()){
            Toast.makeText(mCtx, RELOAD_ENG, Toast.LENGTH_SHORT).show();
        }else{
            if(AppAccess.languageEng){
                Toast.makeText(mCtx, CHECK_CONNECTION_ENG, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(mCtx, CHECK_CONNECTION_BAN, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void ToastLanguage(Context mCtx, String lanEng, String lanBan){
        if(AppAccess.languageEng){
            Toast.makeText(mCtx, lanEng, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mCtx, lanBan, Toast.LENGTH_SHORT).show();
        }
    }





    public static void ShowToast(Context mCtx, String msg){
        Toast.makeText(mCtx, msg, Toast.LENGTH_SHORT).show();
    }




}
