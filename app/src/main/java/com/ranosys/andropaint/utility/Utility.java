package com.ranosys.andropaint.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by ranosys-pankaj on 18/11/15.
 */
public class Utility {

    private static ProgressDialog progressDialog;

    public static void showToast(Context context, String messageToShow )
    {
        Toast.makeText(context,messageToShow,Toast.LENGTH_SHORT).show();
    }

    public static void showProgress(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(false);
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();
    }

    public static void hideProgress(){
        progressDialog.dismiss();
    }
}
