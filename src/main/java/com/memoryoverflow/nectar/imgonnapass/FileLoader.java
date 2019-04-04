package com.memoryoverflow.nectar.imgonnapass;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

class FileLoader {
    private ArrayList<Map<String, String>> loadedReviewer;

    FileLoader(Context ctx, SharedPreferences sharedPreferences, boolean loadDefault) {
        if (!loadDefault) {
            String reviewer = ctx.getString(R.string.preference_reviewer_location);
            String path = sharedPreferences.getString(reviewer, "");
            try {
                InputStream inputStream = new FileInputStream(new File(path));
                loadedReviewer = jsonToArrayList(Objects.requireNonNull(readReviewer(inputStream)));
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                InputStream inputStream = ctx.getAssets().open(ctx.getString(R.string.def_reviewer_file));
                loadedReviewer = jsonToArrayList(Objects.requireNonNull(readReviewer(inputStream)));
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private JSONObject readReviewer(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            bufferedReader.close();
            if (ReviewerSanityChecker.sanityCheck(stringBuilder.toString())) {
                return new JSONObject(stringBuilder.toString());
            } else {
                return new JSONObject();
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private ArrayList<Map<String, String>> jsonToArrayList(JSONObject obj) {
        if (obj.length() != 0 || obj.toString().equals("{}")) {
            ArrayList<Map<String, String>> loaded = new ArrayList<>();
            Iterator objIterator = obj.keys();
            try {
                while (objIterator.hasNext()) {
                    Map<String, String> reviewerMap = new HashMap<>();
                    String reviewerKey = (String) objIterator.next();
                    JSONObject reviewer = obj.getJSONObject(reviewerKey);
                    reviewerMap.put("course_key", reviewerKey);
                    reviewerMap.put("course_name", reviewer.getString("course_name"));
                    reviewerMap.put("course_subjects", reviewer.getString("course_subjects"));
                    loaded.add(reviewerMap);
                }
                return loaded;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    ArrayList<Map<String, String>> getLoadedJson() {
        return loadedReviewer;
    }
}


