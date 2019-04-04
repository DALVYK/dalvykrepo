package com.memoryoverflow.nectar.imgonnapass;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

class ReviewerSanityChecker {
    static boolean sanityCheck(String input) {
        try {
            JSONObject root0 = new JSONObject(input);
            Iterator<String> iterator0 = root0.keys();
            while (iterator0.hasNext()) {
                String key0 = iterator0.next();
                if (StringUtils.isEmpty(key0)) return false;
                JSONObject root1 = root0.getJSONObject(key0);
                if (root1.toString().equals("{}")) return false;
                if (root1.isNull("course_name")) return false;
                else {
                    String course_name = root1.getString("course_name");
                    if (StringUtils.isEmpty(course_name)) return false;
                }
                if (root1.isNull("course_subjects")) return false;
                else {
                    String course_subjects = root1.getString("course_subjects");
                    if (course_subjects.equals("{}")) return false;
                }
                JSONObject root2 = root1.getJSONObject("course_subjects");
                if (root2.toString().equals("{}")) return false;
                Iterator<String> iterator1 = root2.keys();
                while (iterator1.hasNext()) {
                    String key1 = iterator1.next();
                    if (StringUtils.isEmpty(key1)) return false;
                    JSONObject root3 = root2.getJSONObject(key1);
                    if (root3.toString().equals("{}")) return false;
                    if (root3.isNull("subject_name")) return false;
                    else {
                        String subject_name = root3.getString("subject_name");
                        if (StringUtils.isEmpty(subject_name)) return false;
                    }
                    JSONObject root4 = root3.getJSONObject("subject_exam");
                    if (root4.toString().equals("{}")) return false;
                    Iterator<String> iterator2 = root4.keys();
                    while (iterator2.hasNext()) {
                        String key2 = iterator2.next();
                        if (StringUtils.isEmpty(key2)) return false;
                        JSONObject root5 = root4.getJSONObject(key2);
                        if (root5.toString().equals("{}")) return false;
                        if (root5.isNull("question")) return false;
                        String question = root5.getString("question");
                        if (StringUtils.isEmpty(question)) return false;
                        if (root5.isNull("choices")) return false;
                        JSONArray choices = root5.getJSONArray("choices");
                        int arrayLength = choices.length();
                        if (arrayLength == 0) return false;
                        int answerIndex = Integer.valueOf(root5.getString("correct_answer"));
                        if (answerIndex > arrayLength - 1) return false;
                    }
                }
            }
        } catch (JSONException | NumberFormatException e) {
            //e.printStackTrace();
            return false;
        }
        return true;
    }
}
