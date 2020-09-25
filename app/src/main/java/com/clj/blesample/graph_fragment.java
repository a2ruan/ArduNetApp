package com.clj.blesample;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class graph_fragment extends Fragment {

    private LineChart chart1;
    private LineChart chart2;
    private LineChart chart3;
    private LineChart chart4;

    private BleDevice bleDevice;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic characteristic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_graph, container, false);
        if (savedInstanceState == null) {
            chart1 = initChart(chart1,v,R.id.chart1, 1030f);
            chart2 = initChart(chart2,v,R.id.chart2, 21f);
            chart3 = initChart(chart3,v,R.id.chart3, 60f);
            chart4 = initChart(chart4,v,R.id.chart4, 400f);
            showToast("onCreate 1");

            for (int i = 0;i < 50; i++) {
                addEntry(chart1, 0, (float) i,(float) (Math.random() * 2) + 1030);
                addEntry(chart1, 1, (float) i,(float) (Math.random() * 2) + 1040);

                addEntry(chart2, 0, (float) i,(float) (Math.random() * 2) + 20);
                addEntry(chart2, 1, (float) i,(float) (Math.random() * 3) + 22);

                addEntry(chart3, 0, (float) i,(float) (Math.random() * 2) + 60);
                addEntry(chart3, 1, (float) i,(float) (Math.random() * 2) + 61);

                addEntry(chart4, 0, (float) i,(float) (Math.random() * 2) + 100);
                addEntry(chart4, 1, (float) i,(float) (Math.random() * 2) + 101);
            }
            chart1.getAxisLeft().setAxisMaximum(chart1.getYMax()+chart1.getYMax()-chart1.getYMin());
            chart1.getAxisLeft().setAxisMinimum(chart1.getYMin()-(chart1.getYMax()-chart1.getYMin()));

            chart2.getAxisLeft().setAxisMaximum(chart2.getYMax()+chart2.getYMax()-chart2.getYMin());
            chart2.getAxisLeft().setAxisMinimum(chart2.getYMin()-(chart2.getYMax()-chart2.getYMin()));

            chart3.getAxisLeft().setAxisMaximum(chart3.getYMax()+chart3.getYMax()-chart3.getYMin());
            chart3.getAxisLeft().setAxisMinimum(chart3.getYMin()-(chart3.getYMax()-chart3.getYMin()));

            chart4.getAxisLeft().setAxisMaximum(chart4.getYMax()+chart4.getYMax()-chart4.getYMin());
            chart4.getAxisLeft().setAxisMinimum(chart4.getYMin()-(chart4.getYMax()-chart4.getYMin()));

        }
        else
        {
            showToast("onCreate 2");
        }



        return v;
    }

    private void addEntry(LineChart chartTemp, int dataSetIndex, float xVal, float yVal) {

        LineData data = chartTemp.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(dataSetIndex);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 5) + 1030f), 0);
            data.addEntry(new Entry(set.getEntryCount(), yVal),dataSetIndex);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            chartTemp.notifyDataSetChanged();

            // limit the number of visible entries
            chartTemp.setVisibleXRangeMaximum(120);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chartTemp.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            chartTemp.moveViewTo(data.getXMax()-7, 55f, YAxis.AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    private void updateChart(LineChart chartTemp, View viewTemp, int viewID) {
        // Find Chart by ID
        chartTemp = (LineChart) viewTemp.findViewById(viewID);
        LineData data = chartTemp.getData();
    }

    private LineChart initChart(LineChart chartTemp, View viewTemp,int viewID, float rangeApprox) {
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
        yValues.add(new Entry(0, (float) (Math.random() * 5) + rangeApprox));
//        yValues.add(new Entry(1, (float) (Math.random() * 5) + rangeApprox));
//        yValues.add(new Entry(2, (float) (Math.random() * 5) + rangeApprox));
//        yValues.add(new Entry(3, (float) (Math.random() * 5) + rangeApprox));
//        yValues.add(new Entry(4, (float) (Math.random() * 5) + rangeApprox));
//        yValues.add(new Entry(5, (float) (Math.random() * 5) + rangeApprox));

        LineDataSet set1 = new LineDataSet(yValues,"Gas Sensor 1");
        set1.setCircleRadius(1);
        set1.setFillColor(Color.rgb(255,179,0));
        set1.setCircleColor(Color.rgb(255,179,0));
        set1.setColor(Color.rgb(255,179,0));
        set1.setFillAlpha(80);
        set1.setDrawFilled(true);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        //LineData data = new LineData(dataSets);
        //chartTemp.setData(data);

        // Temp Data 2
        ArrayList<Entry> yValues2 = new ArrayList<>();
        yValues2.add(new Entry(0, (float) (Math.random() * 5) + rangeApprox));
//        yValues2.add(new Entry(1, (float) (Math.random() * 5) + rangeApprox));
//        yValues2.add(new Entry(2, (float) (Math.random() * 5) + rangeApprox));
//        yValues2.add(new Entry(3, (float) (Math.random() * 5) + rangeApprox));
//        yValues2.add(new Entry(4, (float) (Math.random() * 5) + rangeApprox));
//        yValues2.add(new Entry(5, (float) (Math.random() * 5) + rangeApprox));

        LineDataSet set2 = new LineDataSet(yValues2,"Gas Sensor 2");
        set2.setCircleRadius(1);
        set2.setFillAlpha(80);
        set2.setDrawFilled(true);
        dataSets.add(set2);

        LineData data = new LineData(dataSets);
        chartTemp.setData(data);

        return chartTemp;
    }

    private void showToast (String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

}