package com.memoryoverflow.nectar.imgonnapass;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class Helpers {
    static final int DEFAULT_HOLDER = -1;

    static void changeFragment(FragmentActivity activity, int holderId, Fragment fragment, @Nullable Serializable serializable, @Nullable String key, boolean isBack) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        if (serializable != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(key, serializable);
            fragment.setArguments(bundle);
        }
        if (isBack)
            ft.setCustomAnimations(R.anim.slide_left_to_center, R.anim.slide_center_to_right);
        else ft.setCustomAnimations(R.anim.slide_right_to_center, R.anim.slide_center_to_left);
        if (holderId == DEFAULT_HOLDER) ft.replace(R.id.fragment_holder, fragment);
        else ft.replace(holderId, fragment);
        ft.commit();
    }

    static void inflateTemplateButton(Context context, int container, int layoutId, int buttonId, String text, Button.OnClickListener listener, Button.OnLongClickListener onLongClickListener) {
        LinearLayout linearLayout = ((Activity) context).findViewById(container);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(layoutId, linearLayout, true);
        Button button = view.findViewById(buttonId);
        button.setText(text);
        if (listener != null) button.setOnClickListener(listener);
        if (onLongClickListener != null) button.setOnLongClickListener(onLongClickListener);
        button.setId(View.generateViewId());
    }

    static void writeToFile(Context context, String filename, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    static String readFromFile(Context context, String filename) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    static void showToast(Context context, CharSequence msg, boolean isLong) {
        Toast toast = Toast.makeText(context, msg, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_template,
                (ViewGroup) ((Activity) context).getWindow().getDecorView().getRootView().findViewById(R.id.toast_container));
        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);
        toast.setView(layout);
        toast.show();
    }

    static ArrayList<Map<String, Integer>> getPlottable(String subject, JSONObject workingSubjectJson) {
        ArrayList<Map<String, Integer>> tempArrayList = new ArrayList<>();
        Iterator<String> iterator = workingSubjectJson.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.equals(subject)) {
                try {
                    JSONObject object = workingSubjectJson.getJSONObject(key);
                    Iterator<String> internalIterator = object.keys();
                    while (internalIterator.hasNext()) {
                        Map<String, Integer> internalMap = new HashMap<>();
                        String internalKey = internalIterator.next();
                        JSONObject internalObject = object.getJSONObject(internalKey);
                        internalMap.put("score", internalObject.getInt("score"));
                        internalMap.put("wrong", internalObject.getInt("wrong"));
                        tempArrayList.add(internalMap);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return tempArrayList;
    }

}
