package com.memoryoverflow.nectar.imgonnapass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

class GraphDataHandler implements Serializable {
    private String workingSubject;
    private ArrayList<Map<String, Integer>> workingSubjectArrayList;

    ArrayList<Map<String, Integer>> getWorkingSubjectArrayList() {
        return workingSubjectArrayList;
    }

    void setWorkingSubjectArrayList(ArrayList<Map<String, Integer>> arrayList) {
        workingSubjectArrayList = arrayList;
    }

    String getWorkingSubject() {
        return workingSubject;
    }

    void setWorkingSubject(String subject) {
        workingSubject = subject;
    }

}
