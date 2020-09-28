package com.clj.blesample;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.blesample.adapter.DeviceAdapter;
import com.clj.blesample.operation.CharacteristicListFragment;
import com.clj.blesample.operation.OperationActivity;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
//SPINNER
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class graph_fragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final int LONG_DELAY = 3500; // 3.5 seconds
    private static final int SHORT_DELAY = 2000; // 2 seconds

    private LineChart chart1;
    private LineChart chart2;
    private LineChart chart3;
    private LineChart chart4;

    private TextView txt_test;
    private Button btn_record;
    private DeviceAdapter mDeviceAdapter;
    private DeviceAdapter connectedDeviceAdapter;

    private List<BluetoothGattService> serviceList;
    private List<BluetoothGattCharacteristic> characteristicList;
    private BleGattCallback bleGattCallback;

    public final static UUID UUID_SERVICE = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
    public final static UUID UUID_CHARACTERISTIC = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");

    List<BleDevice> connectedDevices;

    private String[] sample_rate = { "1", "10", "100", "1000", "10000"};

    private void displayCount() {
        connectedDeviceAdapter = ((MainActivity)getActivity()).getDeviceAdapter();
        int count2 = connectedDeviceAdapter.getCount();

        BleDevice tempDevice = connectedDeviceAdapter.getItem(0);
        BleDevice tempDevice2 = connectedDeviceAdapter.getItem(1);
        String tempName = Integer.toString(tempDevice.getGraphStatus());
        String tempName2 = Integer.toString(tempDevice2.getGraphStatus());
        //String num2 = Integer.toString(count2);
        showToast(tempName + " " + tempName2,this.getActivity());
    }

    private void getData() {
        connectedDeviceAdapter = ((MainActivity)getActivity()).getDeviceAdapter();
        txt_test.setText("");
        BleDevice tempDevice;

        for (int i = 0; i < connectedDeviceAdapter.getCount();i++) {
            tempDevice = connectedDeviceAdapter.getItem(i);
            if (tempDevice.getGraphStatus() == 1) {
                showToast(tempDevice.getName(), getActivity());
                serviceList = BleManager.getInstance().getBluetoothGattServices(tempDevice);

                for (BluetoothGattService gattService:serviceList) {
                    characteristicList = BleManager.getInstance().getBluetoothGattCharacteristics(gattService);
                    if (gattService.getUuid().toString().equalsIgnoreCase(UUID_SERVICE.toString())) {
                        for (BluetoothGattCharacteristic gattCharacteristic : characteristicList) {
                            if (gattCharacteristic.getUuid().toString().equalsIgnoreCase(UUID_CHARACTERISTIC.toString())) {
                                BleManager.getInstance().read(
                                        tempDevice,
                                        gattService.getUuid().toString(),
                                        gattCharacteristic.getUuid().toString(),
                                        new BleReadCallback() {
                                            @Override
                                            public void onReadSuccess(final byte[] data) {
                                                // DATA TESTING
                                                String str = new String(data, StandardCharsets.UTF_8);
                                                String[] sensorData = unpackageBLEPacket(str);
                                                showToast(str,getActivity());

                                                //showToast("EYY1",getActivity());
                                            }
                                            @Override
                                            public void onReadFailure(final BleException exception) {
                                                showToast("EYY2",getActivity());
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
            }

//                characteristicList = BleManager.getInstance().getBluetoothGattCharacteristics(serviceList.get(0));
//                final byte[] data = characteristicList.get(1).getValue();
//                String str = new String(data, StandardCharsets.UTF_8);
//                if (data.length == 0) {
//                    showToast("hell naw",getActivity());
//                }
                //String str = new String(data, StandardCharsets.UTF_8);
                //txt_test.setText(str);
                //txt_test.setText(txt_test.getText() + " " + tempDevice.getName());
        }
    }



    private void initUI(View v) {
        characteristicList = new ArrayList<>();


        txt_test = (TextView)v.findViewById(R.id.testText);
        txt_test.setOnClickListener(this);

        btn_record = (Button)v.findViewById(R.id.btn_record);
        btn_record.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        View v = inflater.inflate(R.layout.fragment_graph, container, false);
        initUI(v);

        if (savedInstanceState == null) {
            chart1 = initChart(chart1,v,R.id.chart1, 1030f);
            chart2 = initChart(chart2,v,R.id.chart2, 21f);
            chart3 = initChart(chart3,v,R.id.chart3, 60f);
            chart4 = initChart(chart4,v,R.id.chart4, 400f);
            //showToast("onCreate 1");

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

        // SPINNER INITIALIZATION
        Spinner spin = (Spinner) v.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,sample_rate);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        return v;
    }

    // SPINNER OVERRIDES
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

    }
    //SPINNER OVERRIDE
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
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

    private void showToast (String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    // BLE DECODER
    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    private static String getStringRepresentation(ArrayList<Character> list)
    {
        StringBuilder builder = new StringBuilder(list.size());
        for(Character ch: list)
        {
            builder.append(ch);
        }
        return builder.toString();
    }

    private static String[] unpackageBLEPacket(String packetData)
    {
        String[] sensorData = packetData.split("/");
        for (int i = 0;i < sensorData.length;i++)
        {
            String dataID = String.valueOf(sensorData[i].charAt(0));
            switch(dataID) {
                case "A":
                    sensorData[i] = "Time = " + sensorData[i].substring(1) + "ms";
                    break;
                case "B":
                    sensorData[i] = "Temp = " + sensorData[i].substring(1) + "deg C";
                    break;
                case "C":
                    sensorData[i] = "RH = " + sensorData[i].substring(1) + "%";
                    break;
                case "D":
                    sensorData[i] = "R = " + sensorData[i].substring(1) + "ohm";
                    break;
                case "E":
                    sensorData[i] = "dR = " + sensorData[i].substring(1) + "ohm per s";
                    break;
                case "F":
                    sensorData[i] = "Conc = " + sensorData[i].substring(1) + "ppm";
                    break;
            }
        }
        return sensorData;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_record:
                //displayCount();
                getData();
                break;
        }
    }
}