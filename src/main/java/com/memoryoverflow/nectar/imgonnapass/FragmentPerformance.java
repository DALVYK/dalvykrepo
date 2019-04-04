package com.memoryoverflow.nectar.imgonnapass;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.Objects;

public class FragmentPerformance extends Fragment {
    static final String INSTANCE_ID = "INSTANCE";
    boolean isSet = false;
    GraphDataHandler graphDataHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        graphDataHandler = new GraphDataHandler();
        return inflater.inflate(R.layout.fragment_performance, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            isSet = savedInstanceState.getBoolean(INSTANCE_ID);
        }
    }

    @Override
    public void onStart() {
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Performance");
        if (!isSet) {
            initializePerformanceOptions();
            isSet = true;
        }
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(INSTANCE_ID, isSet);
        super.onSaveInstanceState(outState);
    }

    void initializePerformanceOptions() {
        File result = new File(Objects.requireNonNull(getActivity()).getFilesDir(), getString(R.string.result_file));
        if (result.exists()) {
            try {
                final JSONObject root = new JSONObject(Helpers.readFromFile(
                        Objects.requireNonNull(getContext()), getString(R.string.result_file)));
                if (!root.toString().equals("{}") || root.length() != 0) {
                    Iterator<String> iterator = root.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        Button.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                String subject = ((Button) view).getText().toString();
                                root.remove(subject);
                                try {
                                    Helpers.writeToFile(getContext(),
                                            getString(R.string.result_file), root.toString(3));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Helpers.showToast(getContext(), getString(R.string.performance_result_deleted), true);
                                Helpers.changeFragment(getActivity(), Helpers.DEFAULT_HOLDER, new FragmentPerformance(),
                                        graphDataHandler, "GraphDataHandler", true);
                                return true;
                            }
                        };
                        Button.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String subject = ((Button) view).getText().toString();
                                graphDataHandler.setWorkingSubject(subject);
                                graphDataHandler.setWorkingSubjectArrayList(Helpers.getPlottable(subject, root));
                                Helpers.changeFragment(getActivity(), Helpers.DEFAULT_HOLDER, new FragmentGraph(),
                                        graphDataHandler, "GraphDataHandler", false);
                            }
                        };
                        Helpers.inflateTemplateButton(getContext(),
                                R.id.linearLayoutPerformanceSubject, R.layout.template_button_light
                                , R.id.template_button_light, key, listener, onLongClickListener);
                    }
                    Button.OnClickListener listenerBack = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DrawerLayout mDrawerLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.drawer_layout);
                            mDrawerLayout.openDrawer(Gravity.START);
                        }
                    };
                    Helpers.inflateTemplateButton(getContext(), R.id.linearLayoutPerformanceSubject,
                            R.layout.template_button_dark, R.id.template_button_dark, "BACK", listenerBack,
                            null);
                } else replaceViewIfNoResult();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else replaceViewIfNoResult();

    }

    void replaceViewIfNoResult() {
        Helpers.showToast(getContext(), getString(R.string.no_performance_result_message), true);
        ViewGroup viewGroup = Objects.requireNonNull(getView()).findViewById(R.id.relativeLayoutPerformanceRoot);
        viewGroup.removeAllViews();
        getLayoutInflater().inflate(R.layout.nothing_here, viewGroup, true);
    }


}
