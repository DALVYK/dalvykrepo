package com.memoryoverflow.nectar.imgonnapass;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class FragmentResult extends Fragment {
    private ReviewerHandler reviewerHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            reviewerHandler = (ReviewerHandler) bundle.getSerializable("ReviewerHandler");
        }
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Result");
    }

    @Override
    public void onStart() {
        initializeScore();
        super.onStart();
    }

    private void initializeScore() {
        View root = Objects.requireNonNull(getView());
        ArrayList<Integer> scores = reviewerHandler.getScore();
        TextView right = root.findViewById(R.id.textViewRight);
        TextView wrong = root.findViewById(R.id.textViewWrong);
        TextView wygw = root.findViewById(R.id.textViewWhereWrong);
        ImageButton backToReviewers = root.findViewById(R.id.imageButtonBackToReviewer);
        ImageButton reTake = root.findViewById(R.id.imageButtonRetake);
        ImageButton goToResult = root.findViewById(R.id.imageButtonGoToResult);
        String rightMsg = "RIGHT: " + String.valueOf(scores.get(0));
        String wrongMsg = "WRONG: " + String.valueOf(scores.get(1));
        right.setText(rightMsg);
        wrong.setText(wrongMsg);
        saveScore(reviewerHandler.getWorkingSubjectName(), scores.get(0), scores.get(1));
        if (scores.get(1) == 0) {
            wygw.setText(R.string.no_wrongs_here);
            ViewGroup viewGroup = root.findViewById(R.id.frameLayoutWrongsContainers);
            View view = getLayoutInflater().inflate(R.layout.nothing_here, viewGroup, false);
            viewGroup.addView(view);
        } else {
            showWhereWronged();
        }
        final ImageButton.OnClickListener reviewerListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helpers.changeFragment(Objects.requireNonNull(getActivity()),
                        Helpers.DEFAULT_HOLDER, new FragmentReviewers(), reviewerHandler,
                        "ReviewerHandler", true);
            }
        };
        ImageButton.OnClickListener retakeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewerHandler.resetReviewer();
                Helpers.changeFragment(Objects.requireNonNull(getActivity()),
                        Helpers.DEFAULT_HOLDER, new FragmentQuestion(), reviewerHandler,
                        "ReviewerHandler", true);
            }
        };
        ImageButton.OnClickListener goToResultListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final JSONObject root = new JSONObject(Helpers.readFromFile(
                            Objects.requireNonNull(getContext()), getString(R.string.result_file)));
                    GraphDataHandler graphDataHandler = new GraphDataHandler();
                    graphDataHandler.setWorkingSubject(reviewerHandler.getWorkingSubjectName());
                    graphDataHandler.setWorkingSubjectArrayList(Helpers.getPlottable(reviewerHandler.getWorkingSubjectName(), root));
                    Helpers.changeFragment(Objects.requireNonNull(getActivity()), Helpers.DEFAULT_HOLDER, new FragmentGraph(),
                            graphDataHandler, "GraphDataHandler", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        backToReviewers.setOnClickListener(reviewerListener);
        reTake.setOnClickListener(retakeListener);
        goToResult.setOnClickListener(goToResultListener);
    }

    private void showWhereWronged() {
        Helpers.changeFragment(Objects.requireNonNull(getActivity()),
                R.id.frameLayoutWrongsContainers, new FragmentWrongs(), reviewerHandler,
                "ReviewerHandler", false);
    }

    private void saveScore(String subject, int score, int wrong) {
        File result = new File(Objects.requireNonNull(getActivity()).getFilesDir(), getString(R.string.result_file));
        if (!result.exists()) {
            JSONObject resultRoot = new JSONObject();
            try {
                JSONObject scoresRoot = new JSONObject();
                scoresRoot.put(String.valueOf(System.currentTimeMillis()), getJsonScore(score, wrong));
                resultRoot.put(subject, scoresRoot);
                Helpers.writeToFile(Objects.requireNonNull(getContext()),
                        getString(R.string.result_file), resultRoot.toString(3));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject root = new JSONObject(Helpers.readFromFile(
                        Objects.requireNonNull(getContext()), getString(R.string.result_file)));
                if (root.isNull(subject)) {
                    JSONObject subjectRoot = new JSONObject();
                    subjectRoot.put(String.valueOf(System.currentTimeMillis()), getJsonScore(score, wrong));
                    root.put(subject, subjectRoot);
                } else {
                    JSONObject subjectRoot = root.getJSONObject(subject);
                    subjectRoot.put(String.valueOf(System.currentTimeMillis()), getJsonScore(score, wrong));
                }
                Helpers.writeToFile(Objects.requireNonNull(getContext()),
                        getString(R.string.result_file), root.toString(3));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject getJsonScore(int score, int wrong) {
        JSONObject subjectScore = new JSONObject();
        try {
            subjectScore.put("score", score);
            subjectScore.put("wrong", wrong);
            return subjectScore;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return subjectScore;
    }

}
