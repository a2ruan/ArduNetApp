package com.clj.blesample;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.blesample.adapter.DeviceAdapter;
import com.clj.blesample.comm.ObserverManager;
import com.clj.blesample.operation.OperationActivity;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleMtuChangedCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);
        initView();

        // Menu Initialization
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        navigationView = (BottomNavigationView) findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_device:
                Intent intent1 = new Intent(this,MainActivity.class);
                startActivity(intent1);
                return true;
            case R.id.navigation_graph:
                return true;
            case R.id.navigation_data:
                Intent intent2 = new Intent(this,ThirdActivity.class);
                startActivity(intent2);
                return true;
            default:
                return true;
        }
    }

    //toast message function
    private void showToast (String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}