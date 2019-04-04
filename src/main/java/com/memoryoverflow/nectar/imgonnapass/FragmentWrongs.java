package com.memoryoverflow.nectar.imgonnapass;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Map;
import java.util.Objects;

public class FragmentWrongs extends Fragment {
    private ReviewerHandler reviewerHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            reviewerHandler = (ReviewerHandler) bundle.getSerializable("ReviewerHandler");
        }
        return inflater.inflate(R.layout.fragment_wrongs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView tvQuestion = view.findViewById(R.id.textViewWrongedQuestion);
        TextView tvRightAnswer = view.findViewById(R.id.textViewWrongedRightAnswer);
        TextView tvWrongAnswer = view.findViewById(R.id.textViewWrongedWrongAnswer);
        TextView tvPosition = view.findViewById(R.id.textViewWrongedPosition);
        String position = String.valueOf(reviewerHandler.getWrongedIndex() + 1) + " of " + reviewerHandler.getTotalWronged();
        tvPosition.setText(position);
        Map<String, String> rememberedWrong = reviewerHandler.getRememberedWrongs();
        String question = "Question: " + rememberedWrong.get("question");
        String right = "Right Answer: " + rememberedWrong.get("right_answer");
        String wrong = "Your Answer: " + rememberedWrong.get("wrong_answer");
        tvQuestion.setText(question);
        tvRightAnswer.setText(right);
        tvWrongAnswer.setText(wrong);
        initializeButtons();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    void initializeButtons() {
        final ImageButton back = Objects.requireNonNull(getView()).findViewById(R.id.imageButtonWrongedPrevious);
        final ImageButton next = getView().findViewById(R.id.imageButtonWrongedNext);
        if (reviewerHandler.getWrongedIndex() == 0) back.setEnabled(false);
        else back.setEnabled(true);
        if (reviewerHandler.getWrongedIndex() + 1 == reviewerHandler.getTotalWronged())
            next.setEnabled(false);
        else next.setEnabled(true);

        Button.OnClickListener backListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reviewerHandler.prevWronged()) {
                    Helpers.changeFragment(Objects.requireNonNull(getActivity()),
                            R.id.frameLayoutWrongsContainers, new FragmentWrongs(), reviewerHandler,
                            "ReviewerHandler", true);
                }
            }
        };

        Button.OnClickListener nextListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reviewerHandler.nextWronged()) {
                    Helpers.changeFragment(Objects.requireNonNull(getActivity()),
                            R.id.frameLayoutWrongsContainers, new FragmentWrongs(), reviewerHandler,
                            "ReviewerHandler", false);
                }
            }
        };
        back.setOnClickListener(backListener);
        next.setOnClickListener(nextListener);
    }

}
