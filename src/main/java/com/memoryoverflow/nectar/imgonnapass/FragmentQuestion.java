package com.memoryoverflow.nectar.imgonnapass;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class FragmentQuestion extends Fragment {
    private ReviewerHandler reviewerHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            reviewerHandler = (ReviewerHandler) bundle.getSerializable("ReviewerHandler");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeQuestion();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(reviewerHandler.getWorkingSubjectName());
    }

    void initializeQuestion() {
        Objects.requireNonNull(reviewerHandler).setWorkingQuestionData();
        setQuestionTitle();
        spawnQuestion();
        spawnChoices();
    }

    void setQuestionTitle() {
        ViewGroup viewGroup = Objects.requireNonNull(getView()).findViewById(R.id.linearLayoutQuestionContainer);
        TextView questionTitle = viewGroup.findViewById(R.id.textViewQuestionTitle);
        TextView takeCount = viewGroup.findViewById(R.id.textViewTakeCount);
        String title = getString(R.string.question_question_start) + " " +
                reviewerHandler.getWorkingQuestionNumber() + "/" + reviewerHandler.getQuestionCount();
        questionTitle.setText(title);


        File result = new File(Objects.requireNonNull(getActivity()).getFilesDir(), getString(R.string.result_file));
        if (result.exists()) {
            try {
                JSONObject root = new JSONObject(Helpers.readFromFile(
                        Objects.requireNonNull(getContext()), getString(R.string.result_file)));
                if (!root.isNull(reviewerHandler.getWorkingSubjectName())) {
                    int count = ((JSONObject) root.get(reviewerHandler.getWorkingSubjectName())).length();
                    if (count == 0) {
                        takeCount.setText(R.string.take_count_default);
                    } else {
                        String temp = "Take: " + String.valueOf(count + 1);
                        takeCount.setText(temp);
                    }
                } else {
                    takeCount.setText(R.string.take_count_default);
                }
            } catch (JSONException e) {
                takeCount.setText(R.string.take_count_default);
                e.printStackTrace();
            }
        } else {
            takeCount.setText(R.string.take_count_default);
        }

    }

    void spawnQuestion() {
        ViewGroup viewGroup = Objects.requireNonNull(getView()).findViewById(R.id.linearLayoutQuestionContainer);
        TextView questionTitle = viewGroup.findViewById(R.id.textViewQuestion);
        questionTitle.setText(reviewerHandler.getQuestionQuestion());
    }

    void spawnChoices() {
        Button.OnClickListener listenerCorrect = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewerHandler.tallyScore(true);
                nextQuestion();
            }
        };

        Button.OnClickListener listenerWrong = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = String.valueOf(((Button) view).getText());
                reviewerHandler.rememberWrongs(answer);
                reviewerHandler.tallyScore(false);
                nextQuestion();
            }
        };

        for (Map<String, String> map : reviewerHandler.getQuestionChoices()) {
            String isCorrect = String.valueOf(map.get("isCorrect"));
            String choice = String.valueOf(map.get("choice"));
            if (isCorrect.equals("true")) Helpers.inflateTemplateButton(getContext(),
                    R.id.linearLayoutChoicesContainer, R.layout.template_button_light_choice,
                    R.id.template_button_light_choice, choice, listenerCorrect, null);
            else Helpers.inflateTemplateButton(getContext(),
                    R.id.linearLayoutChoicesContainer, R.layout.template_button_light_choice,
                    R.id.template_button_light_choice, choice, listenerWrong, null);
        }
    }

    void nextQuestion() {
        if (reviewerHandler.nextQuestion()) {
            Helpers.changeFragment(Objects.requireNonNull(getActivity()), Helpers.DEFAULT_HOLDER
                    , new FragmentQuestion(), reviewerHandler,
                    "ReviewerHandler", false);
        } else {
            Helpers.changeFragment(Objects.requireNonNull(getActivity()), Helpers.DEFAULT_HOLDER
                    , new FragmentResult(), reviewerHandler,
                    "ReviewerHandler", false);
        }
    }
}
