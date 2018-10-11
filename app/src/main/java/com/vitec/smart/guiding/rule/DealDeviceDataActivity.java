package com.vitec.smart.guiding.rule;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.table.TableData;
import com.vitec.smart.guiding.rule.bean.TestInfo;
import com.vitec.smart.guiding.rule.service.ConnectDeviceService;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by skyel on 2018/10/11.
 */
//@SmartTable(name = "devicedata" )
public class DealDeviceDataActivity extends Activity {

    private static final String TAG = "DealDeviceDataActivity";
    private TextView tvMsg;
    private SmartTable table;

    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;
    //    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;


    private BluetoothAdapter mBluetooth;
    private ConnectDeviceService mService = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;
    private int mState = UART_PROFILE_DISCONNECTED;
    private StringBuffer stringBuffer=new StringBuffer();
    private int positon = 0;
    private List<TestInfo> lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_device_data);
        initView();
        service_init();
        initData();
    }

    private void initData() {
//        Column<String> column1 = new Column<String>("垂直度");
//        Column<String> column2 = new Column<String>("水平度");
//        Column<String> column3 = new Column<String>("误差");
//        List<String> datas = new ArrayList<>();
//        datas.add("11.1");
//        datas.add("121.1");
//        datas.add("992");
//        TableData<String> tableData = new TableData<String>("data", datas, column1);
        lists = new ArrayList<>();
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        lists.add(new TestInfo("", "", ""));
        table.setData(lists);

        table.getConfig().setMinTableWidth(getScreenWidth());

    }



    private void initView() {
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        table = (SmartTable) findViewById(R.id.table);
    }

    public int getScreenWidth() {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        return manager.getDefaultDisplay().getWidth();
    }

    private void service_init() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Intent bindIntent = new Intent(this, ConnectDeviceService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver,makeGattUpdateIntentFilter());
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectDeviceService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(ConnectDeviceService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ConnectDeviceService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ConnectDeviceService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(ConnectDeviceService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }


    private ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ((ConnectDeviceService.LocalBinder) iBinder).getService();
            Log.e(TAG, "onServiceConnected: mService=" + mService );
            if (!mService.initialize()) {
                Log.e(TAG, "onServiceConnected: 不能初始化蓝牙" );
                finish();
            }
            Log.e(TAG, "onServiceConnected: 服务绑定完成");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        mService.disconnect();
        unbindService(mServiceConnection);
    }

    private final BroadcastReceiver UARTStatusChangeReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            final Intent mIntent = intent;
            //*********************//
            if (action.equals(ConnectDeviceService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.e(TAG, "广播收到了UART_CONNECT_MSG");
                        stringBuffer.append("[" + currentDateTimeString + "]Connected to: " + mDevice.getName());
                        stringBuffer.append("\n");
                        tvMsg.setText(stringBuffer.toString());
                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }

            //*********************//
            if (action.equals(ConnectDeviceService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.e(TAG, "广播收到了UART_DISCONNECT_MSG");
                        stringBuffer.append("[" + currentDateTimeString + "]" + "连接失败");
                        stringBuffer.append("\n");
                        tvMsg.setText(stringBuffer.toString());
                        mState = UART_PROFILE_DISCONNECTED;
                        mService.close();
                        //setUiState();

                    }
                });
            }


            //*********************//
            if (action.equals(ConnectDeviceService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }
            //*********************//
            if (action.equals(ConnectDeviceService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(ConnectDeviceService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String text = new String(txValue, "UTF-8");
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            stringBuffer.append("[" + currentDateTimeString + "]收到的数据长度：" + text.length() + "，数据内容：" + text);
                            stringBuffer.append("\n");
//                            tvMsg.setText(stringBuffer.toString());
                            if (positon < lists.size()) {
                                lists.get(positon).setColumn(text);
                                positon++;
                                table.setData(lists);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
            //*********************//
            if (action.equals(ConnectDeviceService.DEVICE_DOES_NOT_SUPPORT_UART)){
//                showMessage("Device doesn't support UART. Disconnecting");
                Log.e(TAG, "onReceive: 设备不支持UART,");
//                mService.disconnect();
            }
        }
    };


}
