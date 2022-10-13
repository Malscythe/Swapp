package com.example.Swapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import Swapp.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog dialog;

    LoadingDialog(Activity myActivity){
        activity = myActivity;

    }

    void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loadingdialog, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    void DismissDialog() {
        dialog.dismiss();
    }
}
