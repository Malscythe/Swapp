package com.example.Swapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import Swapp.R;

public class LoggingInDialog {

    private Activity activity;
    private AlertDialog dialog;

    LoggingInDialog(Activity myActivity){
        activity = myActivity;

    }

    void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loggingindialog, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    void DismissDialog() {
        dialog.dismiss();
    }
}
