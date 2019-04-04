package com.memoryoverflow.nectar.imgonnapass;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

public class FragmentReviewers extends Fragment implements FileLoadedCallback {
    SharedPreferences sharedPreferences;
    ReviewerHandler reviewerHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reviewers, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPreferences = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Reviewers");
        checkReviewerExists();
    }

    void checkReviewerExists() {
        String reviewer = getString(R.string.preference_reviewer_location);
        String path = sharedPreferences.getString(reviewer, "");
        if (!path.isEmpty()) {
            FileLoader loader = new FileLoader(getContext(), sharedPreferences, false);
            new LoadAndSanityCheck(this, loader).execute();
        } else {
            FileLoader loader = new FileLoader(getContext(), sharedPreferences, true);
            new LoadAndSanityCheck(this, loader).execute();
        }
    }

    void spawnReviewerOptions() {
        Button.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                reviewerHandler.setWorkingReviewer(button.getText().toString());
                Helpers.changeFragment(Objects.requireNonNull(getActivity()),
                        Helpers.DEFAULT_HOLDER, new FragmentReviewersSubjects(),
                        reviewerHandler, "ReviewerHandler", false);
            }
        };

        Button.OnClickListener listenerBack = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               DrawerLayout mDrawerLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.drawer_layout);
               mDrawerLayout.openDrawer(Gravity.START);
            }
        };

        for (String str : reviewerHandler.getReviewerOptions()) {
            Helpers.inflateTemplateButton(getContext(), R.id.linearLayoutReviewerContainer,
                    R.layout.template_button_light, R.id.template_button_light, str, listener,
                    null);
        }

        Helpers.inflateTemplateButton(getContext(), R.id.linearLayoutReviewerContainer,
                R.layout.template_button_dark, R.id.template_button_dark, "BACK", listenerBack,
                null);

    }

    @Override
    public void reviewerhandler(ReviewerHandler reviewerHandler) {
        this.reviewerHandler = reviewerHandler;
        if (this.reviewerHandler.isReviewerOk()) {
            String doShuffleId = getString(R.string.preference_doShuffle);
            Boolean doShuffle = sharedPreferences.getBoolean(doShuffleId, false);
            reviewerHandler.setDoShuffle(doShuffle);
            spawnReviewerOptions();
        } else {
            Helpers.showToast(getContext(), getString(R.string.reviewer_corrupted), true);
            ViewGroup rootView = Objects.requireNonNull(getView()).findViewById(R.id.relativeLayoutReviewerRoot);
            rootView.removeAllViews();
            getLayoutInflater().inflate(R.layout.corrupted_file, rootView, true);
        }
    }
}

interface FileLoadedCallback {
    void reviewerhandler(ReviewerHandler reviewerHandler);
}

class LoadAndSanityCheck extends AsyncTask<String, Void, ReviewerHandler> {
    private FileLoader fileLoader;
    private FileLoadedCallback fileLoadedCallback;

    LoadAndSanityCheck(FileLoadedCallback fileLoadedCallback, FileLoader fileLoader) {
        this.fileLoadedCallback = fileLoadedCallback;
        this.fileLoader = fileLoader;
    }

    @Override
    protected ReviewerHandler doInBackground(String... strings) {
        return new ReviewerHandler(fileLoader.getLoadedJson());
    }

    @Override
    protected void onPostExecute(ReviewerHandler reviewerHandler) {
        fileLoadedCallback.reviewerhandler(reviewerHandler);
        super.onPostExecute(reviewerHandler);
    }
}