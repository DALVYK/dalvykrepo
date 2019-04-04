package com.memoryoverflow.nectar.imgonnapass;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import java.util.Objects;

public class ProgressAlertDialog extends DialogFragment {
    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new
                AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.template_circle_progressbar, null));
        return builder.create();
    }
}
