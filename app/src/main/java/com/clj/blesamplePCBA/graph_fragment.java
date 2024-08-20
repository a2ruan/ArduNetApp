package com.clj.blesamplePCBA;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.blesamplePCBA.adapter.DeviceAdapter;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
//SPINNER
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

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

    private LineChart chart1; // Temp
    private LineChart chart2; // Humidity
    private LineChart chart3; // PPM
    private LineChart chart4; // R1
    private LineChart chart5; // dR1
    private LineChart chart6; // R2
    private LineChart chart7; // dR2
    private LineChart chart8; // R3
    private LineChart chart9; // dR3
    private LineChart chart10; // R4
    private LineChart chart11; // dR4

    // Store all data into 3D arraylist
    ArrayList<ArrayList<ArrayList<String>>> csvData;

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
    Map<Integer, String> mymapName;

    public void exportCSV(View v, String csvDefaultName) {
        for (int sheetID = 0; sheetID < csvData.size();sheetID++) {
            //showToast(mymapName.get(0));
            String csvName = mymapName.get(sheetID+1) + "_" + csvDefaultName;
            StringBuilder data = new StringBuilder();
            for (int rowID = 0; rowID < csvData.get(sheetID).size();rowID++) {
                for (int columnID = 0; columnID < csvData.get(sheetID).get(rowID).size();columnID++) {
                    if (columnID == 0) {
                        data.append(csvData.get(sheetID).get(rowID).get(columnID));
                    }
                    else {
                        data.append(","+csvData.get(sheetID).get(rowID).get(columnID));
                    }
                }
                data.append("\n");
            }
            try{
                //saving the file into device
                Context context = getActivity().getApplicationContext();
                FileOutputStream out = context.openFileOutput(csvName+".csv", Context.MODE_PRIVATE);
                out.write((data.toString()).getBytes());
                out.close();

                //exporting
                //context = getActivity().getApplicationContext();
                File filelocation = new File(context.getFilesDir(), csvName+".csv");
                Uri path = FileProvider.getUriForFile(context, "com.example.exportcsv.fileprovider", filelocation);
                Intent fileIntent = new Intent(Intent.ACTION_SEND);
                fileIntent.setType("text/csv");
                fileIntent.putExtra(Intent.EXTRA_SUBJECT, csvName); // File Name
                fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                startActivity(Intent.createChooser(fileIntent, "Send mail"));
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void getData() {
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
                                                addDevicesReminder.setVisibility(View.GONE);
                                                if (mymap.get(tempDevice.getMac()) == null) {
                                                    //showToast(finalTempDevice.getMac());
                                                    int randomColor = getRandomColor();
                                                    //txt_test.setText(txt_test.getText() + tempDevice.getName() + " ");
                                                    ILineDataSet tempDataSet = initializeLineDataSet(tempDevice.getName(),randomColor);
                                                    chart1.getData().addDataSet(tempDataSet);
                                                    chart2.getData().addDataSet(initializeLineDataSet(tempDevice.getName(),randomColor));
                                                    chart3.getData().addDataSet(initializeLineDataSet(tempDevice.getName(),randomColor));
                                                    chart4.getData().addDataSet(initializeLineDataSet(tempDevice.getName(),randomColor));
                                                    chart5.getData().addDataSet(initializeLineDataSet(tempDevice.getName(),randomColor));
                                                    chart6.getData().addDataSet(initializeLineDataSet(tempDevice.getName(),randomColor));
                                                    chart7.getData().addDataSet(initializeLineDataSet(tempDevice.getName(),randomColor));
                                                    chart8.getData().addDataSet(initializeLineDataSet(tempDevice.getName(),randomColor));
                                                    chart9.getData().addDataSet(initializeLineDataSet(tempDevice.getName(),randomColor));
                                                    chart10.getData().addDataSet(initializeLineDataSet(tempDevice.getName(),randomColor));
                                                    chart11.getData().addDataSet(initializeLineDataSet(tempDevice.getName(),randomColor));
                                                    dataSetIndex = chart1.getData().getDataSetCount()-1;
                                                    //showToast(Integer.toString(dataSetIndex));
                                                    //showToast(Integer.toString(dataSetIndex));
                                                    mymap.put(tempDevice.getMac(),dataSetIndex);
                                                    mymapName.put(dataSetIndex,tempDevice.getName());
                                                    //showToast(Integer.toString(dataSetIndex) + ":" + mymapName.get(dataSetIndex));

                                                    ArrayList<ArrayList<String>> newSheet = new ArrayList<ArrayList<String>>();
                                                    ArrayList<String> headers = new ArrayList<String>();
                                                    headers.add("Date");
                                                    headers.add("Time");
                                                    headers.add("Temperature(°C)");
                                                    headers.add("RelativeHumidity(%)");
                                                    headers.add("Concentration(ppm)");
                                                    headers.add("R1(Ω)");
                                                    headers.add("dR1(Ω/s)");
                                                    headers.add("R2(Ω)");
                                                    headers.add("dR2(Ω/s)");
                                                    headers.add("R3(Ω)");
                                                    headers.add("dR3(Ω/s)");
                                                    headers.add("R4(Ω)");
                                                    headers.add("dR4(Ω/s)");
                                                    newSheet.add(headers);
                                                    csvData.add(dataSetIndex-1,newSheet);
                                                }
                                                else {
                                                    dataSetIndex = mymap.get(tempDevice.getMac());
                                                    //showToast(Integer.toString(dataSetIndex));
                                                }
                                                String str = new String(data, StandardCharsets.UTF_8);
                                                String[] sensorData = unpackageBLEPacket(str);
                                                //showToast(Float.toString(xValueTemp) + "s @ " + sensorData[3] + "ohm");
//                                                    dataPacket = dataPacket + "A" + (String)deviceName + "/";
//                                                    dataPacket = dataPacket + "B" + (String)relativeTime + "/";
//                                                    dataPacket = dataPacket + "C" + (String)temperature + "/";
//                                                    dataPacket = dataPacket + "D" + (String)humidity + "/";
//                                                    dataPacket = dataPacket + "E" + (String)ppm + "/";
//                                                    dataPacket = dataPacket + "F" + (String)resistance[0] + "/";
//                                                    dataPacket = dataPacket + "G" + (String)deltaResistance[0] + "/";
//                                                    dataPacket = dataPacket + "H" + (String)resistance[1] + "/";
//                                                    dataPacket = dataPacket + "I" + (String)deltaResistance[1] + "/";
//                                                    dataPacket = dataPacket + "J" + (String)resistance[2] + "/";
//                                                    dataPacket = dataPacket + "K" + (String)deltaResistance[2] + "/";
//                                                    dataPacket = dataPacket + "L" + (String)resistance[3] + "/";
//                                                    dataPacket = dataPacket + "M" + (String)deltaResistance[3];
                                                int dataChecker = 0;
                                                try { // Check to see that inputs are valid numbers, otherwise skip the entire packet or else will crash
                                                    Float.valueOf(sensorData[2]);
                                                    Float.valueOf(sensorData[3]);
                                                    Float.valueOf(sensorData[4]);
                                                    Float.valueOf(sensorData[5]);
                                                    Float.valueOf(sensorData[6]);
                                                    Float.valueOf(sensorData[7]);
                                                    Float.valueOf(sensorData[8]);
                                                    Float.valueOf(sensorData[9]);
                                                    Float.valueOf(sensorData[10]);
                                                    Float.valueOf(sensorData[11]);
                                                    Float.valueOf(sensorData[12]);
                                                    dataChecker = 1;
                                                }
                                                catch (Exception e) {
                                                }
                                                if (dataChecker == 1) { // If floats all converted properly and exist, then add to chart display
                                                    addEntry(chart1, mymap.get(tempDevice.getMac()), (float) xValueTemp,(float) Float.valueOf(sensorData[2])); // Temp
                                                    addEntry(chart2, mymap.get(tempDevice.getMac()) , (float) xValueTemp,(float) Float.valueOf(sensorData[3])); // Humidity
                                                    addEntry(chart3, mymap.get(tempDevice.getMac()), (float) xValueTemp,(float) Float.valueOf(sensorData[4])); // Concentration
                                                    addEntry(chart4, mymap.get(tempDevice.getMac()), (float) xValueTemp,(float) Float.valueOf(sensorData[5])); // R1
                                                    addEntry(chart5, mymap.get(tempDevice.getMac()), (float) xValueTemp,(float) Float.valueOf(sensorData[6])); // dR1
                                                    addEntry(chart6, mymap.get(tempDevice.getMac()) , (float) xValueTemp,(float) Float.valueOf(sensorData[7])); // R2
                                                    addEntry(chart7, mymap.get(tempDevice.getMac()), (float) xValueTemp,(float) Float.valueOf(sensorData[8])); // dR2
                                                    addEntry(chart8, mymap.get(tempDevice.getMac()), (float) xValueTemp,(float) Float.valueOf(sensorData[9])); // R3
                                                    addEntry(chart9, mymap.get(tempDevice.getMac()), (float) xValueTemp,(float) Float.valueOf(sensorData[10])); // dR3
                                                    addEntry(chart10, mymap.get(tempDevice.getMac()) , (float) xValueTemp,(float) Float.valueOf(sensorData[11])); // R4
                                                    addEntry(chart11, mymap.get(tempDevice.getMac()), (float) xValueTemp,(float) Float.valueOf(sensorData[12])); // dR4

                                                    // Add to dataset for eventual excel export
                                                    SimpleDateFormat date1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                                    Date now1 = new Date();
                                                    String currentDate = date1.format(now1);

                                                    SimpleDateFormat time1 = new SimpleDateFormat("H:mm:ss", Locale.US);
                                                    Date now2 = new Date();
                                                    String currentTime = time1.format(now2);
                                                    //showToast(currentDate + ":" + currentTime);

                                                    // Initialize new row;
                                                    ArrayList<String> newRow = new ArrayList<String>();
                                                    newRow.add(currentDate);
                                                    newRow.add(currentTime);
                                                    newRow.add(sensorData[2]);
                                                    newRow.add(sensorData[3]);
                                                    newRow.add(sensorData[4]);
                                                    newRow.add(sensorData[5]);
                                                    newRow.add(sensorData[6]);
                                                    newRow.add(sensorData[7]);
                                                    newRow.add(sensorData[8]);
                                                    newRow.add(sensorData[9]);
                                                    newRow.add(sensorData[10]);
                                                    newRow.add(sensorData[11]);
                                                    newRow.add(sensorData[12]);
                                                    csvData.get(dataSetIndex-1).add(newRow);
                                                    //showToast(Integer.toString(csvData.get(dataSetIndex-1).size()));
                                                    //showToast(csvData.get(0).get(1).get(1));
                                                }
                                            }
                                            @Override
                                            public void onReadFailure(final BleException exception) {
                                                showToast("Bluetooth unstable.  Move close to device");
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
        csvData = new ArrayList<>(10);
        mymap = new HashMap<String, Integer>();
        mymapName = new HashMap<Integer, String>();
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
            chart1 = initChart(chart1,v,R.id.chart1, 21f);
            chart2 = initChart(chart2,v,R.id.chart2, 60f);
            chart3 = initChart(chart3,v,R.id.chart3, 400f);
            chart4 = initChart(chart4,v,R.id.chart4, 1000f);
            chart5 = initChart(chart5,v,R.id.chart5, 1f);
            chart6 = initChart(chart6,v,R.id.chart6, 1000f);
            chart7 = initChart(chart7,v,R.id.chart7, 1f);
            chart8 = initChart(chart8,v,R.id.chart8, 1000f);
            chart9 = initChart(chart9,v,R.id.chart9, 1f);
            chart10 = initChart(chart10,v,R.id.chart10, 1000f);
            chart11 = initChart(chart11,v,R.id.chart11, 1f);

            rescaleAllCharts();
        }
        return v;
    }

    public void resetCharts(View v) {
        chart1 = initChart(chart1,v,R.id.chart1, 21f);
        chart2 = initChart(chart2,v,R.id.chart2, 60f);
        chart3 = initChart(chart3,v,R.id.chart3, 400f);
        chart4 = initChart(chart4,v,R.id.chart4, 1000f);
        chart5 = initChart(chart5,v,R.id.chart5, 1f);
        chart6 = initChart(chart6,v,R.id.chart6, 1000f);
        chart7 = initChart(chart7,v,R.id.chart7, 1f);
        chart8 = initChart(chart8,v,R.id.chart8, 1000f);
        chart9 = initChart(chart9,v,R.id.chart9, 1f);
        chart10 = initChart(chart10,v,R.id.chart10, 1000f);
        chart11 = initChart(chart11,v,R.id.chart11, 1f);

        rescaleAllCharts();

        xValueTemp = 0;
        graphStatus = false;
        displayGraphStatus = false;
        initUI(getView());
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

    public void rescaleAllCharts() {
        //rescaleYAxis(chart1);
        rescaleYAxis(chart2);
        rescaleYAxis(chart3);
        rescaleYAxis(chart4);
        //rescaleYAxis(chart5);
        rescaleYAxis(chart6);
        //rescaleYAxis(chart7);
        rescaleYAxis(chart8);
        //rescaleYAxis(chart9);
        rescaleYAxis(chart10);
        //rescaleYAxis(chart11);
    }

    private void addEntry(LineChart chartTemp, int dataSetIndex, float xVal, float yVal) {
        LineData data = chartTemp.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(dataSetIndex);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            //data.addEntry(new Entry(set.getEntryCount(), yVal),dataSetIndex);
            data.addEntry(new Entry(xValueTemp, yVal),dataSetIndex);
            data.notifyDataChanged();

            chartTemp.setVisibleXRangeMaximum(30);
            chartTemp.notifyDataSetChanged();
            chartTemp.moveViewToX(data.getEntryCount());
            chartTemp.moveViewTo(data.getXMax()-7, 55f, YAxis.AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setHighLightColor(Color.rgb(0,0,0));
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(30);
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


        final LineChart finalChartTemp = chartTemp;
        chartTemp.setOnChartValueSelectedListener(new OnChartValueSelectedListener()
        {
            @Override
            public void onValueSelected(Entry e, Highlight h)
            {
                float x=e.getX();
                float y=e.getY();
                finalChartTemp.getDescription().setText("x="+Float.toString(x) + ",y=" + Float.toString(y));
            }

            @Override
            public void onNothingSelected()
            {
                finalChartTemp.getDescription().setText("");
            }
        });

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
        set1.setHighLightColor(Color.rgb(0,0,0));
        set1.setCircleRadius(1);
        set1.setFillColor(Color.rgb(250,250,250));
        set1.setCircleColor(Color.rgb(250,250,250));
        set1.setColor(Color.rgb(250,250,250));
        set1.setDrawValues(false);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        chartTemp.setData(data);
        return chartTemp;
    }

    private LineDataSet initializeLineDataSet(String setName, int randomColor) {
        ArrayList<Entry> yValues = new ArrayList<>();
        LineDataSet setTemp = new LineDataSet(yValues,setName);
        setTemp.setHighLightColor(Color.rgb(0,0,0));
        setTemp.setCircleRadius(1);
        setTemp.setFillColor(randomColor);
        setTemp.setCircleColor(randomColor);
        setTemp.setColor(randomColor);
        setTemp.setLineWidth(1);
        setTemp.setFillAlpha(30);
        setTemp.setDrawFilled(true);
        setTemp.setDrawValues(false);
        return setTemp;
    }

    final Random mRandom = new Random(System.currentTimeMillis());

    public int getRandomColor() {
        Random rand = new Random();
        int r = rand.nextInt(10);

        int[] randomColor = new int[10];
        randomColor[0] = Color.rgb(244, 67, 54);
        randomColor[1] = Color.rgb(236, 64, 122);
        randomColor[2] = Color.rgb(213, 0, 249);
        randomColor[3] = Color.rgb(124, 77, 255);
        randomColor[4] = Color.rgb(48, 79, 254);
        randomColor[5] = Color.rgb(38, 198, 218);
        randomColor[6] = Color.rgb(100, 255, 218);
        randomColor[7] = Color.rgb(0, 230, 118);
        randomColor[8] = Color.rgb(255, 214, 0);
        randomColor[9] = Color.rgb(255, 160, 0);
        r = r - 1;
        if (r < 0) {r = 0;}
        if (r > 8) {r = 9;}
        int randomColorHolder = randomColor[r];
        return randomColorHolder;
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
        String[] sensorData;
        try {
            sensorData = packetData.split("/");
            for (int i = 0; i < sensorData.length; i++) {
                sensorData[i] = sensorData[i].substring(1);
            }
        }
        catch (Exception ee) {
            String[] errorHolder = new String[2];
            errorHolder[0] = "Array";
            errorHolder[1] = "Failed";
            sensorData = errorHolder;
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
                if (graphStatus) {
                    getData();
                }
                if (counter < 10 || counter%100 == 0) {
                    rescaleAllCharts();
                }
                counter=counter + 1;
            }
        };
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (graphStatus) {
                    getActivity().runOnUiThread(runnable);
                    try {
                        Thread.sleep(1000);
                        xValueTemp = xValueTemp + 1;
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
                if (graphStatus) {
                    // DO THIS WHEN RECORDING
                    btn_record.setBackgroundResource(R.drawable.ic_baseline_fiber_manual_record_24);
                    graphStatus = false;
                }
                else {
                    // EXIT RECORD
                    graphStatus = true;
                    startGraphing();
                    btn_record.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    btn_stop.setVisibility(getView().VISIBLE);
                }
                break;
            case R.id.btn_save_record:
                btn_record.setBackgroundResource(R.drawable.ic_baseline_fiber_manual_record_24);
                btn_stop.setVisibility(getView().GONE);
                //exportData(getView());
                saveDataToCSV();
                graphStatus = false;
                addDevicesReminder.setVisibility(View.VISIBLE);
                addDevicesReminder.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void saveDataToCSV() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("File Name");
        alert.setMessage("Save file as:");
        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd(H_mm_ss)", Locale.US);
        Date now = new Date();
        final String fileName = formatter.format(now);

        input.setText(fileName);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable value = input.getText();
                //showToast(value.toString());
                // ADD CODE AFTER CONFIRMATION OF FILE SAVE
                exportCSV(getView(), fileName);
                resetCharts(getView());
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                AlertDialog.Builder cancelAlert = new AlertDialog.Builder(getActivity());
                cancelAlert.setTitle("Confirm data deletion");
                cancelAlert.setMessage("Are you sure you don't want to save?");
                cancelAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        resetCharts(getView());
                    }
                });
                cancelAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        saveDataToCSV();
                    }
                });
                cancelAlert.show();
            }
        });
        alert.show();
    }
}