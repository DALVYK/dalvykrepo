package com.memoryoverflow.nectar.imgonnapass;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

class ReviewerSanityCheckerNewFormat {
    static boolean sanityCheck(String input) {
        try {
            JSONObject root0 = new JSONObject(input);
            Iterator<String> iterator0 = root0.keys();
            Log.d("debug", "I ran");
            while (iterator0.hasNext()) {
                String key0 = iterator0.next();
                if (StringUtils.isEmpty(key0)) {
                    Log.d("debug", "Here1");
                    return false;
                }
                JSONObject root1 = root0.getJSONObject(key0);
                if (root1.toString().equals("{}")) {
                    Log.d("debug", "Here2");
                    return false;
                }
                if (root1.isNull("rev_name")) {
                    Log.d("debug", "Here3");
                    return false;
                } else {
                    String course_name = root1.getString("rev_name");
                    if (StringUtils.isEmpty(course_name)) {
                        Log.d("debug", "Here4");
                        return false;
                    }
                }
                if (root1.isNull("rev_option")) {
                    Log.d("debug", "Here5");
                    return false;
                } else {
                    String course_subjects = root1.getString("rev_option");
                    if (course_subjects.equals("{}")) {
                        Log.d("debug", "Here6");
                        return false;
                    }
                }
                JSONObject root2 = root1.getJSONObject("rev_option");
                if (root2.toString().equals("{}")) {
                    Log.d("debug", "Here7");
                    return false;
                }
                Iterator<String> iterator1 = root2.keys();
                while (iterator1.hasNext()) {
                    String key1 = iterator1.next();
                    if (StringUtils.isEmpty(key1)) {
                        Log.d("debug", "Here8");
                        return false;
                    }
                    JSONObject root3 = root2.getJSONObject(key1);
                    if (root3.toString().equals("{}")) {
                        Log.d("debug", "Here9");
                        return false;
                    }
                    if (root3.isNull("opt_name")) {
                        Log.d("debug", "Here10");
                        return false;
                    } else {
                        String subject_name = root3.getString("opt_name");
                        if (StringUtils.isEmpty(subject_name)) {
                            Log.d("debug", "Here11");
                            return false;
                        }
                    }
                    JSONObject root4 = root3.getJSONObject("opt_question");
                    if (root4.toString().equals("{}")) {
                        Log.d("debug", "Here12");
                        return false;
                    }
                    Iterator<String> iterator2 = root4.keys();
                    while (iterator2.hasNext()) {
                        String key2 = iterator2.next();
                        if (StringUtils.isEmpty(key2)) {
                            Log.d("debug", "Here13");
                            return false;
                        }
                        JSONObject root5 = root4.getJSONObject(key2);
                        if (root5.toString().equals("{}")) {
                            Log.d("debug", "Here14");
                            return false;
                        }
                        if (root5.isNull("question")) {
                            Log.d("debug", "Here15");
                            return false;
                        }
                        String question = root5.getString("question");
                        if (StringUtils.isEmpty(question)) {
                            Log.d("debug", "Here16");
                            return false;
                        }
                        if (root5.isNull("choices")) {
                            Log.d("debug", "Here17");
                            return false;
                        }
                        JSONArray choices = root5.getJSONArray("choices");
                        int arrayLength = choices.length();
                        if (arrayLength == 0) {
                            Log.d("debug", "Here18");
                            return false;
                        }
                        int answerIndex = Integer.valueOf(root5.getString("correct_answer"));
                        if (answerIndex > arrayLength - 1) {
                            Log.d("debug", "Here19");
                            return false;
                        }
                    }
                }
            }
        } catch (JSONException | NumberFormatException e) {
            e.printStackTrace();
            Log.d("debug", "Fuck");
            return false;
        }
        return true;
    }
}
