package com.memoryoverflow.nectar.imgonnapass;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.io.File;
import java.util.Objects;

public class FragmentSettings extends Fragment implements InternetEventCallback {
    private Button buttonCheckForUpdate;
    private SharedPreferences sharedPreferences;
    private Switch switchRandomize;

    private ProgressAlertDialog progressAlertDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createBackButton();
        //Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Settings");
    }

    @Override
    public void onStart() {
        sharedPreferences = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        View view = Objects.requireNonNull(getView());
        initializeCheckUpdateButton(view);
        initializeResetButton(view);
        initializeSwitchButton(view);
        super.onStart();
    }

    void initializeCheckUpdateButton(View view) {
        buttonCheckForUpdate = view.findViewById(R.id.buttonCheckForUpdate);
        buttonCheckForUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                buttonCheckForUpdate.setEnabled(false);
                checkUpdate();
            }
        });
    }

    void createBackButton() {
        Button.OnClickListener listenerBack = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout mDrawerLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.drawer_layout);
                mDrawerLayout.openDrawer(Gravity.START);
            }
        };
        Helpers.inflateTemplateButton(getContext(), R.id.linearLayoutSettingContainer,
                R.layout.template_button_dark, R.id.template_button_dark, "BACK", listenerBack,
                null);
    }

    void initializeResetButton(View view) {
        switchRandomize = view.findViewById(R.id.switchRandomize);
        Button buttonResetApp = view.findViewById(R.id.buttonResetApp);
        buttonResetApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchRandomize.setChecked(false);
                clearPreferences();
                clearFiles();
                Helpers.showToast(getContext(), getString(R.string.reset_complete), true);
            }
        });
    }

    void initializeSwitchButton(View view) {
        final String id = Objects.requireNonNull(getContext()).getString(R.string.preference_doShuffle);
        switchRandomize = view.findViewById(R.id.switchRandomize);
        Boolean path = sharedPreferences.getBoolean(id, false);
        if (path) switchRandomize.setChecked(true);
        else switchRandomize.setChecked(false);
        switchRandomize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
                if (b) sharedPrefEditor.putBoolean(id, true);
                else sharedPrefEditor.putBoolean(id, false);
                sharedPrefEditor.apply();
            }
        });
    }

    private void checkUpdate() {
        UpdateHandler updateHandler = new UpdateHandler(Objects.requireNonNull(getActivity()), getContext(), this);
        updateHandler.checkUpdate();
    }

    private void showProgressDialog() {
        if (progressAlertDialog == null) {
            progressAlertDialog = new ProgressAlertDialog();
        }
        progressAlertDialog.setCancelable(false);
        progressAlertDialog.show(Objects.requireNonNull(getFragmentManager()), "tag");
    }

    private void dismissProgressBarEnableButton() {
        progressAlertDialog.dismiss();
        buttonCheckForUpdate.setEnabled(true);
    }

    private void clearPreferences() {
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity())
                .getPreferences(Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    private void clearFiles() {
        for (File child : Objects.requireNonNull(getContext()).getFilesDir().listFiles()) {
            String result = child.delete() ? "Deleted" : "Not Deleted";
            Log.d("debug", result);
        }
    }

    @Override
    public void internetCallback(boolean haveInternet) {
        if (!haveInternet) {
            dismissProgressBarEnableButton();
        }
    }

    @Override
    public void updateCallback() {
        dismissProgressBarEnableButton();
    }
}
