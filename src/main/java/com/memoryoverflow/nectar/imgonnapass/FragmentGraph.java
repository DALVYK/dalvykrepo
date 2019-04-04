package com.memoryoverflow.nectar.imgonnapass;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class FragmentGraph extends Fragment {
    GraphDataHandler graphDataHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            graphDataHandler = (GraphDataHandler) bundle.getSerializable("GraphDataHandler");
        }
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }


    @Override
    public void onStart() {
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(graphDataHandler.getWorkingSubject());
        super.onStart();
        initializeGraph();
    }

    void initializeGraph() {
        ArrayList<Map<String, Integer>> arrayList = graphDataHandler.getWorkingSubjectArrayList();
        DataPoint[] dataPoint = new DataPoint[arrayList.size() + 1];
        dataPoint[0] = new DataPoint(0, 0);
        int yMax = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            Map<String, Integer> map = arrayList.get(i);
            yMax = Objects.requireNonNull(map.get("score")) + Objects.requireNonNull(map.get("wrong"));
            DataPoint tempDataPoint = new DataPoint((i + 1), Objects.requireNonNull(map.get("score")));
            dataPoint[i + 1] = tempDataPoint;
        }
        GraphView graph = Objects.requireNonNull(getView()).findViewById(R.id.graph);
        graph.getGridLabelRenderer().setNumHorizontalLabels(arrayList.size() + 1);
        graph.getGridLabelRenderer().setNumVerticalLabels(yMax + 1);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoint);
        series.setColor(Objects.requireNonNull(getContext()).getColor(R.color.colorAccent));
        GridLabelRenderer gridLabelRenderer = graph.getGridLabelRenderer();
        gridLabelRenderer.setHorizontalAxisTitle(getString(R.string.graph_xaxis_title));
        gridLabelRenderer.setHorizontalAxisTitleTextSize(32);
        gridLabelRenderer.setVerticalAxisTitle(getString(R.string.graph_yaxis_title));
        gridLabelRenderer.setVerticalAxisTitleTextSize(32);
        graph.addSeries(series);
    }
}
