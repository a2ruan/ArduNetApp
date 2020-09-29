package com.clj.blesample;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
//SPINNER
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.RequiresApi;
import java.util.Calendar;

public class graph_fragment extends Fragment implements View.OnClickListener {

    private static final int LONG_DELAY = 3500; // 3.5 seconds
    private static final int SHORT_DELAY = 2000; // 2 seconds
    public final static UUID UUID_SERVICE = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
    public final static UUID UUID_CHARACTERISTIC = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");
    private boolean graphStatus;
    private boolean displayGraphStatus;

    private float xValueTemp;
    private float previousTime;
    private float currentTime;
    private int dataSetIndex;

    private LineChart chart1;
    private LineChart chart2;
    private LineChart chart3;
    private LineChart chart4;

    private TextView txt_test;
    private Button btn_record;
    private RelativeLayout addDevicesReminder;
    private Button btn_stop;

    private DeviceAdapter mDeviceAdapter;
    private DeviceAdapter connectedDeviceAdapter;
    private List<BluetoothGattService> serviceList;
    private List<BluetoothGattCharacteristic> characteristicList;

    List<BleDevice> connectedDevices;
    Map<String, Integer> mymap;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void getData() {
        //txt_test.setText(" ");
        connectedDeviceAdapter = ((MainActivity)getActivity()).getDeviceAdapter();
        for (int ii = 0; ii < connectedDeviceAdapter.getCount();ii++) {
            final BleDevice tempDevice = connectedDeviceAdapter.getItem(ii); // Get BLE devices from connected list
            if (tempDevice.getGraphStatus() == 1) { // Filter by graph status
                serviceList = BleManager.getInstance().getBluetoothGattServices(tempDevice);
                for (BluetoothGattService gattService:serviceList) { // Parse all services
                    characteristicList = BleManager.getInstance().getBluetoothGattCharacteristics(gattService);
                    if (gattService.getUuid().toString().equalsIgnoreCase(UUID_SERVICE.toString())) { // Filter by UUID
                        for (BluetoothGattCharacteristic gattCharacteristic : characteristicList) { // Parse all characteristics
                            if (gattCharacteristic.getUuid().toString().equalsIgnoreCase(UUID_CHARACTERISTIC.toString())) { // Filter by UUID
                                BleManager.getInstance().read(
                                        tempDevice,
                                        gattService.getUuid().toString(),
                                        gattCharacteristic.getUuid().toString(),
                                        new BleReadCallback() { // Get broadcast
                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                            @Override
                                            public void onReadSuccess(final byte[] data) {
                                                //txt_test.setText(txt_test.getText() + tempDevice.getMac());
                                                if (mymap.get(tempDevice.getMac()) == null) {
                                                    //showToast(finalTempDevice.getMac());
                                                    int randomColor = getRandomColor();
                                                    //txt_test.setText(txt_test.getText() + tempDevice.getName() + " ");

                                                    ILineDataSet tempDataSet = initializeLineDataSet(tempDevice.getName(),randomColor);
                                                    chart1.getData().addDataSet(tempDataSet);
                                                    chart2.getData().addDataSet(initializeLineDataSet(tempDevice.getName(),randomColor));
                                                    chart3.getData().addDataSet(initializeLineDataSet(tempDevice.getName(),randomColor));
                                                    chart4.getData().addDataSet(initializeLineDataSet(tempDevice.getName(),randomColor));
                                                    dataSetIndex = chart1.getData().getDataSetCount()-1;

                                                    //showToast(Integer.toString(dataSetIndex));
                                                    mymap.put(tempDevice.getMac(),dataSetIndex);
                                                }
                                                else {
                                                    dataSetIndex = mymap.get(tempDevice.getMac());
                                                }
                                                //showToast(Integer.toString(dataSetIndex));
                                                //showToast(Integer.toString(mymap.size()));
                                                //txt_test.setText(txt_test.getText() + Integer.toString(mymap.get(tempDevice.getMac())) + " ");
                                                String str = new String(data, StandardCharsets.UTF_8);
                                                String[] sensorData = unpackageBLEPacket(str);
                                                //showToast(Float.toString(xValueTemp) + "s @ " + sensorData[3] + "ohm");
                                                // 0 = time, 1 = temp, 2 = RH, 3 = resistance, 4 = dR, 5 = ppm
                                                addEntry(chart1, mymap.get(tempDevice.getMac()), (float) xValueTemp,(float) Float.valueOf(sensorData[3])); // Resistance
                                                addEntry(chart2, mymap.get(tempDevice.getMac()) , (float) xValueTemp,(float) Float.valueOf(sensorData[1])); // Temperature
                                                addEntry(chart3, mymap.get(tempDevice.getMac()), (float) xValueTemp,(float) Float.valueOf(sensorData[2])); // Relative Humidity
                                                addEntry(chart4, mymap.get(tempDevice.getMac()), (float) xValueTemp,(float) Float.valueOf(sensorData[5])); // Gas Concentration
                                                xValueTemp = xValueTemp + 1;
                                            }
                                            @Override
                                            public void onReadFailure(final BleException exception) {

                                            }
                                        }
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    private void initUI(View v) {
        characteristicList = new ArrayList<>();
        mymap = new HashMap<String, Integer>();
        dataSetIndex = 0;

        addDevicesReminder = (RelativeLayout) v.findViewById(R.id.addDevicesReminder);
        addDevicesReminder.bringToFront();

        txt_test = (TextView)v.findViewById(R.id.testText);
        txt_test.setOnClickListener(this);

        btn_record = (Button)v.findViewById(R.id.btn_record);
        btn_record.setOnClickListener(this);

        btn_stop = (Button)v.findViewById(R.id.btn_save_record);
        btn_stop.setOnClickListener(this);
        btn_stop.setVisibility(v.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xValueTemp = 0;
        graphStatus = false;
        displayGraphStatus = false;
        View v = inflater.inflate(R.layout.fragment_graph, container, false);
        initUI(v);

        if (savedInstanceState == null) {
            chart1 = initChart(chart1,v,R.id.chart1, 1030f);
            chart2 = initChart(chart2,v,R.id.chart2, 21f);
            chart3 = initChart(chart3,v,R.id.chart3, 60f);
            chart4 = initChart(chart4,v,R.id.chart4, 400f);
            rescaleYAxis(chart1);
            rescaleYAxis(chart2);
            rescaleYAxis(chart3);
            rescaleYAxis(chart4);
        }
        return v;
    }

    public void rescaleYAxis(LineChart lc) {
        lc.getAxisLeft().setAxisMaximum(lc.getYMax()+lc.getYMax()-lc.getYMin());

        if (lc.getYMin() > 0) {
            lc.getAxisLeft().setAxisMinimum(lc.getYMin()-(lc.getYMax()-lc.getYMin()));
        }
        else {
            lc.getAxisLeft().setAxisMinimum(0);
        }


    }

    private void addEntry(LineChart chartTemp, int dataSetIndex, float xVal, float yVal) {
        LineData data = chartTemp.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(dataSetIndex);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), yVal),dataSetIndex);
            data.notifyDataChanged();
            chartTemp.notifyDataSetChanged();
            chartTemp.setVisibleXRangeMaximum(30);
            chartTemp.moveViewToX(data.getEntryCount());
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

        chartTemp.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chartTemp.getAxisLeft().setTextColor(R.color.colorTextPrimary);
        chartTemp.getAxisRight().setTextColor(R.color.colorTextPrimary);
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

        // First Data Set
        int randColor = getRandomColor();
        ArrayList<Entry> yValues = new ArrayList<>();
        yValues.add(new Entry(0, 0));
        LineDataSet set1 = new LineDataSet(yValues,"");
        set1.setCircleRadius(1);
        set1.setFillColor(Color.rgb(255,255,255));
        set1.setCircleColor(Color.rgb(255,255,255));
        set1.setColor(Color.rgb(255,255,255));
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        data.setDrawValues(false);
        chartTemp.setData(data);
        return chartTemp;
    }

    private LineDataSet initializeLineDataSet(String setName, int randomColor) {
        ArrayList<Entry> yValues = new ArrayList<>();
        LineDataSet setTemp = new LineDataSet(yValues,setName);
        setTemp.setCircleRadius(1);
        setTemp.setFillColor(randomColor);
        setTemp.setCircleColor(randomColor);
        setTemp.setColor(randomColor);
        setTemp.setFillAlpha(80);
        setTemp.setDrawFilled(true);
        return setTemp;
    }

    private int getRandomColor() {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        int randomColor = Color.rgb(r,g,b);
        return randomColor;
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
                // 0 = time, 1 = temp, 2 = RH, 3 = resistance, 4 = dR, 5 = ppm
                case "A":
                    sensorData[i] = sensorData[i].substring(1);
                    break;
                case "B":
                    sensorData[i] = sensorData[i].substring(1);
                    break;
                case "C":
                    sensorData[i] = sensorData[i].substring(1);
                    break;
                case "D":
                    sensorData[i] = sensorData[i].substring(1);
                    break;
                case "E":
                    sensorData[i] = sensorData[i].substring(1);
                    break;
                case "F":
                    sensorData[i] = sensorData[i].substring(1);
                    break;
            }
        }
        return sensorData;
    }

    private Thread thread;

    public void startGraphing() {
        if (thread != null) {thread.interrupt();}
        final Runnable runnable = new Runnable() {
            int counter = 0;

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void run() {
                //addEntry();
                if (graphStatus) {
                    addDevicesReminder.setVisibility(View.GONE);
                    getData();
                }
                if (counter%5 == 0) {
                    rescaleYAxis(chart1);
                    rescaleYAxis(chart2);
                    rescaleYAxis(chart3);
                    rescaleYAxis(chart4);
                }
                counter=counter + 1;
            }
        };
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (graphStatus) {
                    // Don't generate garbage runnables inside the loop.
                    getActivity().runOnUiThread(runnable);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_record:
                //showToast("1",getActivity());
                if (graphStatus) {
                    // DO THIS WHEN RECORDING
                    btn_record.setBackgroundResource(R.drawable.ic_baseline_fiber_manual_record_24);
                    graphStatus = false;
                    //showToast("2",getActivity());
                }
                else {
                    // EXIT RECORD
                    graphStatus = true;
                    startGraphing();
                    //showToast("3",getActivity());
                    btn_record.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    btn_stop.setVisibility(getView().VISIBLE);
                }
                break;
            case R.id.btn_save_record:
                btn_record.setBackgroundResource(R.drawable.ic_baseline_fiber_manual_record_24);
                btn_stop.setVisibility(getView().GONE);
                graphStatus = false;
                break;
        }
    }
}