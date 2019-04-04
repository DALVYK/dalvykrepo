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

import java.util.Objects;

public class FragmentReviewersSubjects extends Fragment {
    private ReviewerHandler reviewerHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reviewers_option, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            reviewerHandler = (ReviewerHandler) bundle.getSerializable("ReviewerHandler");
        }
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Subjects");
        spawnOptionItems();
        super.onViewCreated(view, savedInstanceState);
    }

    private void spawnOptionItems() {
        Button.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                reviewerHandler.setWorkingQuestion(String.valueOf(button.getText()));
                Helpers.changeFragment(Objects.requireNonNull(getActivity()),
                        Helpers.DEFAULT_HOLDER, new FragmentQuestion(), reviewerHandler,
                        "ReviewerHandler", false);
            }
        };
        for (String str : reviewerHandler.getWorkingReviewerSubjects()) {
            Helpers.inflateTemplateButton(getContext(), R.id.linearLayoutReviewerOptionItems,
                    R.layout.template_button_light, R.id.template_button_light, str, listener,
                    null);
        }

        Button.OnClickListener backListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helpers.changeFragment(Objects.requireNonNull(getActivity()),
                        Helpers.DEFAULT_HOLDER, new FragmentReviewers(), reviewerHandler,
                        "ReviewerHandler", true);
            }
        };
        Helpers.inflateTemplateButton(getContext(), R.id.linearLayoutReviewerOptionItems,
                R.layout.template_button_dark, R.id.template_button_dark,
                getString(R.string.reviewer_options_back), backListener,
                null);
    }
}
