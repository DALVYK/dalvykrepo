package com.memoryoverflow.nectar.imgonnapass;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

interface InternetEventCallback {
    void internetCallback(boolean haveInternet);

    void updateCallback();
}

class UpdateHandler implements InternetEventCallback {
    private Context context;
    private InternetEventCallback internetEventCallback;
    private SharedPreferences sharedPref;

    UpdateHandler(Activity activity, Context context, InternetEventCallback internetEventCallback) {
        //this.activity = activity;
        this.context = context;
        this.internetEventCallback = internetEventCallback;
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
    }

    void checkUpdate() {
        if (checkRequiredPermissions()) checkNetworkAndInternet();
        else handleDeniedPermissions();

    }

    private boolean checkRequiredPermissions() {
        final String[] permissions = {
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET
        };
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void checkNetworkAndInternet() {
        if (isNetworkAvailable()) {
            new CheckInternet(this).execute();
        } else {
            handleNoInternet();
        }
    }

    private void handleDeniedPermissions() {
        //HANDLE PERMISSION STUFF HERE
        internetEventCallback.internetCallback(false);
        showToast(context, context.getString(R.string.permission_denied), true);
        showToast(context, context.getString(R.string.please_grant), true);
    }

    private void compareVersions(Long queriedVersion) {
        Long currentVersion = sharedPref.getLong(context.getString(R.string.preference_version), Long.valueOf(context.getString(R.string.preference_default_version)));
        if (queriedVersion > currentVersion) {
            getUpdateFromFirebase(queriedVersion);
        } else {
            showToast(context, context.getString(R.string.no_new_version), true);
            internetEventCallback.updateCallback();
        }
    }

    private void getVersionFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("examinationVersion");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long value = dataSnapshot.getValue(Long.class);
                compareVersions(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Long defaultValue = Long.valueOf(context.getString(R.string.preference_default_version));
                compareVersions(defaultValue);
            }
        });
    }

    private void getUpdateFromFirebase(final long queriedVersion) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference gsReference = storage.getReferenceFromUrl(context.getString(R.string.update_file_url));
        final File localFile = new File(context.getFilesDir(), "course.json");
        showToast(context, context.getString(R.string.new_version_found), false);
        gsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                sharedPrefEditor.putLong(context.getString(R.string.preference_version), queriedVersion);
                sharedPrefEditor.putString(context.getString(R.string.preference_reviewer_location), localFile.toString());
                sharedPrefEditor.apply();
                showToast(context, context.getString(R.string.update_downloaded), true);
                internetEventCallback.updateCallback();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                showToast(context, context.getString(R.string.update_download_failed), true);
                internetEventCallback.updateCallback();
            }
        });
    }

    private void handleHasInternet() {
        getVersionFromFirebase();
        internetEventCallback.internetCallback(true);
    }

    private void handleNoInternet() {
        showToast(context, context.getString(R.string.device_has_no_internet), false);
        internetEventCallback.internetCallback(false);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Application.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = Objects.requireNonNull(connectivityManager)
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showToast(Context context, CharSequence msg, boolean isLong) {
        Helpers.showToast(context, msg, isLong);
    }

    @Override
    public void internetCallback(boolean haveInternet) {
        if (haveInternet) {
            handleHasInternet();
        } else {
            handleNoInternet();
        }
    }

    @Override
    public void updateCallback() {

    }
}

class CheckInternet extends AsyncTask<String, Void, Boolean> {
    private InternetEventCallback internalListener;

    CheckInternet(InternetEventCallback listener) {
        this.internalListener = listener;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            String urlForChecking = "https://jsonprototyping.firebaseio.com";
            String userAgent = "Mozilla/5.0 (Linux; U; Android 4.1.1; en-gb; Build/KLP) " +
                    "AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Safari/534.30";
            HttpURLConnection urlc = (HttpURLConnection) (
                    new URL(urlForChecking).openConnection());
            urlc.setRequestProperty("User-Agent", userAgent);
            urlc.setConnectTimeout(1500);
            urlc.connect();
            return (urlc.getResponseCode() == 200);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        internalListener.internetCallback(result);
    }
}
