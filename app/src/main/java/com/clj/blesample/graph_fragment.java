package com.clj.blesample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clj.fastble.BleManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class graph_fragment extends Fragment {

    private LineChart chart1;
    private LineChart chart2;
    private LineChart chart3;
    private LineChart chart4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_graph, container, false);
        chart1 = initChart(chart1,v,R.id.chart1);
        chart2 = initChart(chart2,v,R.id.chart2);
        chart3 = initChart(chart3,v,R.id.chart3);
        chart4 = initChart(chart4,v,R.id.chart4);

        BleManager.getInstance().getAllConnectedDevice();

        return v;
    }

    private LineChart initChart(LineChart chartTemp, View viewTemp,int viewID) {
        // Find Chart by ID
        chartTemp = (LineChart) viewTemp.findViewById(viewID);

        // Formatting
        chartTemp.getDescription().setEnabled(true);
        chartTemp.getDescription().setText("");
        chartTemp.setDragEnabled(true);
        chartTemp.setScaleEnabled(true);
        chartTemp.setPinchZoom(true);
        //chartTemp.setBorderColor(R.color.chartBorder);
        chartTemp.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chartTemp.getAxisLeft().setTextColor(R.color.colorTextPrimary); // left y-axis
        chartTemp.getAxisRight().setTextColor(R.color.colorTextPrimary); // right y-axis
        chartTemp.getXAxis().setTextColor(R.color.colorTextPrimary);

        chartTemp.getXAxis().setTextSize(10);
        chartTemp.getAxisLeft().setTextSize(10);

        chartTemp.getAxisRight().setEnabled(false);
        chartTemp.getLegend().setTextColor(R.color.colorTextPrimary);
        chartTemp.getLegend().setForm(Legend.LegendForm.CIRCLE);


        Legend legend = chartTemp.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);


        // Temporary Data
        ArrayList<Entry> yValues = new ArrayList<>();
        yValues.add(new Entry(0, 1035f));
        yValues.add(new Entry(1, 1060f));
        yValues.add(new Entry(2, 1070f));
        yValues.add(new Entry(3, 1076f));
        yValues.add(new Entry(4, 1080f));
        yValues.add(new Entry(5, 1030f));

        LineDataSet set1 = new LineDataSet(yValues,"Gas Sensor 1");
        set1.setCircleRadius(1);
        set1.setFillAlpha(110);
        set1.setDrawFilled(true);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        //LineData data = new LineData(dataSets);
        //chartTemp.setData(data);

        // Temp Data 2
        ArrayList<Entry> yValues2 = new ArrayList<>();
        yValues2.add(new Entry(0, 1000f));
        yValues2.add(new Entry(1, 1020f));
        yValues2.add(new Entry(2, 1040f));
        yValues2.add(new Entry(3, 1026f));
        yValues2.add(new Entry(4, 1010f));
        yValues2.add(new Entry(5, 1090f));

        LineDataSet set2 = new LineDataSet(yValues2,"Gas Sensor 2");
        set2.setCircleRadius(1);
        set2.setFillAlpha(110);
        set2.setDrawFilled(true);
        dataSets.add(set2);

        LineData data = new LineData(dataSets);
        chartTemp.setData(data);

        return chartTemp;


    }

}