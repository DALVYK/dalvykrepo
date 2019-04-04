package com.memoryoverflow.nectar.imgonnapass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

class ReviewerHandler implements Serializable {
    private ArrayList<Map<String, String>> loadedReviewer;
    private ArrayList<Map<String, String>> workingReviewerSubjects;
    private ArrayList<Map<String, String>> workingReviewerSubjectQuestionMap;
    private ArrayList<Map<String, String>> rememberedWrongs; //reset
    private String workingReviewer;
    private String workingReviewerSubject;
    private String tempCorrectAnswer; //reset
    private String tempQuestion; //reset
    private int workingReviewerQuestionIndex = 0; //reset
    private int workingReviewerSubjectQuestionCount = 0; //reset
    private int scoreCorrect = 0; //reset
    private int scoreWrong = 0; //reset
    private int wrongedIndex = 0; //reset
    private boolean doShuffle = true;
    private boolean isInitialized = false;

    ReviewerHandler(ArrayList<Map<String, String>> loadedReviewer) {
        this.loadedReviewer = loadedReviewer;
    }

    boolean isReviewerOk() {
        return loadedReviewer.size() != 0;
    }

    ArrayList<String> getReviewerOptions() {
        ArrayList<String> reviewerOptions = new ArrayList<>();
        for (Map<String, String> map : loadedReviewer) {
            reviewerOptions.add(map.get("course_name"));
        }
        return reviewerOptions;
    }

    void setWorkingReviewer(String workingReviewer) {
        this.workingReviewer = workingReviewer;
    }

    ArrayList<String> getWorkingReviewerSubjects() {
        ArrayList<Map<String, String>> optionsMap = new ArrayList<>();
        ArrayList<String> options = new ArrayList<>();
        for (Map<String, String> map : loadedReviewer) {
            String x = map.get("course_name");
            if (workingReviewer.equals(x)) {
                try {
                    JSONObject object = new JSONObject(map.get("course_subjects"));
                    Iterator<String> iterator = object.keys();
                    while (iterator.hasNext()) {
                        Map<String, String> innerMap = new HashMap<>();
                        String key = iterator.next();
                        JSONObject inner = object.getJSONObject(key);
                        options.add(inner.getString("subject_name"));
                        innerMap.put("subject_key", key);
                        innerMap.put("subject_name", inner.getString("subject_name"));
                        innerMap.put("subject_exam", inner.getString("subject_exam"));
                        optionsMap.add(innerMap);
                    }
                    workingReviewerSubjects = optionsMap;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return options;
    }

    void setDoShuffle(boolean b) {
        doShuffle = b;
    }

    void setWorkingQuestion(String s) {
        workingReviewerSubject = s;
    }

    String getWorkingSubjectName() {
        return workingReviewerSubject;
    }

    String getQuestionCount() {
        for (Map<String, String> map : workingReviewerSubjects) {
            if (Objects.equals(map.get("subject_name"), workingReviewerSubject)) {
                try {
                    JSONObject questions = new JSONObject(map.get("subject_exam"));
                    workingReviewerSubjectQuestionCount = questions.length();
                    return String.valueOf(questions.length());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return "0";
    }

    void setWorkingQuestionData() {
        if (isInitialized) return;
        isInitialized = true;
        ArrayList<Map<String, String>> questionMap = new ArrayList<>();
        for (Map<String, String> map : workingReviewerSubjects) {
            if (Objects.equals(map.get("subject_name"), workingReviewerSubject)) {
                try {
                    JSONObject questions = new JSONObject(map.get("subject_exam"));
                    Iterator<String> iterator = questions.keys();
                    while (iterator.hasNext()) {
                        Map<String, String> tempQuestionMap = new HashMap<>();
                        String key = iterator.next();
                        JSONObject question = questions.getJSONObject(key);
                        tempQuestionMap.put("question_key", key);
                        tempQuestionMap.put("question_question", question.getString("question"));
                        tempQuestionMap.put("question_correct_answer", question.getString("correct_answer"));
                        tempQuestionMap.put("question_choices", question.getString("choices"));
                        questionMap.add(tempQuestionMap);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (doShuffle) Collections.shuffle(questionMap);
        workingReviewerSubjectQuestionMap = questionMap;
    }

    String getWorkingQuestionNumber() {
        return String.valueOf((workingReviewerQuestionIndex + 1));
    }

    boolean nextQuestion() {
        if (workingReviewerQuestionIndex < workingReviewerSubjectQuestionCount - 1) {
            workingReviewerQuestionIndex++;
            return true;
        } else return false;
    }

    String getQuestionQuestion() {
        Map<String, String> test = workingReviewerSubjectQuestionMap.get(workingReviewerQuestionIndex);
        tempQuestion = String.valueOf(test.get("question_question"));
        return String.valueOf(test.get("question_question"));
    }

    ArrayList<Map<String, String>> getQuestionChoices() {
        Map<String, String> test = workingReviewerSubjectQuestionMap.get(workingReviewerQuestionIndex);
        ArrayList<Map<String, String>> tempArrayList = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(test.get("question_choices"));
            for (int i = 0; i < array.length(); i++) {
                Map<String, String> mapTemp = new HashMap<>();
                mapTemp.put("choice", String.valueOf(array.get(i)));
                if (i == Integer.valueOf(Objects.requireNonNull(test.get("question_correct_answer")))) {
                    tempCorrectAnswer = array.getString(i);
                    mapTemp.put("isCorrect", "true");
                } else {
                    mapTemp.put("isCorrect", "false");
                }
                tempArrayList.add(mapTemp);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (doShuffle) Collections.shuffle(tempArrayList);
        return tempArrayList;
    }

    void rememberWrongs(String answer) {
        Map<String, String> tempMap = new HashMap<>();
        if (rememberedWrongs == null) rememberedWrongs = new ArrayList<>();
        tempMap.put("question", tempQuestion);
        tempMap.put("right_answer", tempCorrectAnswer);
        tempMap.put("wrong_answer", answer);
        rememberedWrongs.add(tempMap);
    }

    Map<String, String> getRememberedWrongs() {
        return rememberedWrongs.get(wrongedIndex);
    }

    boolean nextWronged() {
        if (wrongedIndex < rememberedWrongs.size() - 1) {
            wrongedIndex++;
            return true;
        } else return false;
    }

    boolean prevWronged() {
        if (wrongedIndex >= 0) {
            wrongedIndex--;
            return true;
        } else return false;
    }

    int getWrongedIndex() {
        return wrongedIndex;
    }

    int getTotalWronged() {
        return rememberedWrongs.size();
    }

    void tallyScore(boolean isCorrect) {
        if (isCorrect) scoreCorrect++;
        else scoreWrong++;
    }

    ArrayList<Integer> getScore() {
        ArrayList<Integer> score = new ArrayList<>();
        int scoreTotal = scoreCorrect + scoreWrong;
        score.add(scoreCorrect);
        score.add(scoreWrong);
        score.add(scoreTotal);
        return score;
    }

    void resetReviewer() {
        if (rememberedWrongs != null) rememberedWrongs.clear();
        tempCorrectAnswer = "";
        tempQuestion = "";
        workingReviewerQuestionIndex = 0;
        workingReviewerSubjectQuestionCount = 0;
        scoreCorrect = 0;
        scoreWrong = 0;
        wrongedIndex = 0;
    }

}
