package com.vitec.smart.guiding.rule;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.vitec.smart.guiding.rule.adapter.BleDeviceAdapter;
import com.vitec.smart.guiding.rule.interfaces.MainActivityGettable;
import com.vitec.smart.guiding.rule.listener.DeviceItemClickListener;
import com.vitec.smart.guiding.rule.service.BleScanService;

import org.altbeacon.beacon.Beacon;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class DeviceActivity extends AppCompatActivity implements MainActivityGettable {

    private static final String TAG = "DeviceActivity";
    private ListView lvBleDevice;
    private BleDeviceAdapter mBleDeviceAdapter;
    private List<Beacon> devices;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        initView();
//        EventBus.getDefault().register(this);
        checkBleEnable();
        requestLocationPermissions();
        initData();
        initService();
    }

    /**
     * 动态申请蓝牙定位权限
     */
    private void requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });

                builder.show();

            }

        }
    }

    /**
     * 动态权限回调方法
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    /**
     * 用于检查手机蓝牙设备是否可以，以及蓝牙设备是否有打开
     */
    private void checkBleEnable() {
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter == null) {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("错误").setMessage("你的设备不具备蓝牙功能!").create();
            dialog.show();
            return;
        }

        if(!adapter.isEnabled()) {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("蓝牙设备未打开,请开启此功能后重试!")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(mIntent, 1);
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this,"正在搜索设备",Toast.LENGTH_SHORT).show();
        EventBus.getDefault().register(this);
        initService();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        BleScanService.stopScanService(this);
        Log.e(TAG, "onDestroy: " );
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        BleScanService.stopScanService(this);
    }

    private void initService() {
        BleScanService.startScanService(this);
    }

    private void initData() {
        devices = new ArrayList<>();
        mBleDeviceAdapter = new BleDeviceAdapter(devices, this);
        lvBleDevice.setAdapter(mBleDeviceAdapter);
        lvBleDevice.setOnItemClickListener(new DeviceItemClickListener(this,this));
    }

    private void initView() {
        lvBleDevice = (ListView) findViewById(R.id.lv_ble_device);
    }

    /**
     * 接收从搜索服务里发送过来的数据
     * @param beacons
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void beaconBussCallBack(List<Beacon> beacons) {
        Log.e(TAG, "beaconBussCallBack: 查看回调的beacon:"+beacons.get(0).toString() );
        displayBleDevice(beacons);
    }


    /**
     * 显示搜索到的设备信息
     * @param beacons
     */
    private void displayBleDevice(List<Beacon> beacons) {
        if ( mBleDeviceAdapter== null) {
            mBleDeviceAdapter = new BleDeviceAdapter(beacons, this);
            lvBleDevice.setAdapter(mBleDeviceAdapter);

        } else {
            devices = beacons;
            mBleDeviceAdapter.setBeacons(devices);
            mBleDeviceAdapter.notifyDataSetChanged();
        }
    }

    public  List<Beacon> getDevices() {
        return devices;
    }

    public void setDevices(List<Beacon> devices) {
        this.devices = devices;
    }
}
