package com.vitec.smart.guiding.rule.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by skyel on 2018/9/27.
 */

public class BleScanService extends Service implements BeaconConsumer {

    private static final long DEFAULT_FOREGROUND_SCAN_PERIOD = 3000L;
    private static final long DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD = 3000L;
    private static final String TAG = "BleScanService";
    private BeaconManager beaconManager;
    /** 重新调整格式*/
    public static final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    /** 设置兴趣UUID*/
    public static final String FILTER_UUID = "FDA50693-A4E2-4FB1-AFCF-C6EB07647825";
//    public BeaconLocationData beaconLocationData;
    private boolean inBeaconRange = false;//判断是在beacon区域还是不在beacon区域
    private int goneCount = 0;
    private List<Beacon> beacons;
    private Region region;
    private Beacon lastBeacon;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initBeacon();
        initData();
    }

    private void initData() {
        beacons = new ArrayList<>();
    }

    private void initBeacon() {
//        获取beaconManager实例对象
        beaconManager = BeaconManager.getInstanceForApplication(this);
//        设置搜索的时间间隔和周期
        beaconManager.setForegroundBetweenScanPeriod(DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD);
        beaconManager.setForegroundScanPeriod(DEFAULT_FOREGROUND_SCAN_PERIOD);
//        设置beacon数据包格式
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_FORMAT));
//        将activity与库中的BeaconService绑定到一起,服务准备完成后就会自动回调下面的onBeaconServiceConnect方法
        beaconManager.bind(this);

    }

    /**
     * BeaconConsumer类的回调方法，当BeaconService准备完毕时调用该方法
     */
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                Log.i(TAG, "didRangeBeaconsInRegion: 查看搜索到的beacon总数:"+collection.size());
                beacons.clear();
                if (collection.size() > 0) {
                    //符合要求的beacon集合
                    for (Beacon beacon : collection) {
//                        判断该beacon的UUID是否为我们感兴趣的
                        if (beacon.getId1().toString().equalsIgnoreCase(FILTER_UUID)){
//                            是则添加到集合
                            beacons.add(beacon);
                        }
                    }
                }
                if (beacons.size() > 0) {

                    //                    给收集到的beacons按rssi的信号强弱排序
                    Collections.sort(beacons, new Comparator<Beacon>() {
                        public int compare(Beacon arg0, Beacon arg1) {
                            return arg1.getRssi() - arg0.getRssi();
                        }
                    });


                    Log.e(TAG, "didRangeBeaconsInRegion: 发送数据到主界面" );
                    EventBus.getDefault().post(beacons);
                }
            }

        });
        try {
//            别忘了启动搜索,不然不会调用didRangeBeaconsInRegion方法
            region = new Region(FILTER_UUID, null, null, null);
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (beaconManager != null) {
            try {
                beaconManager.stopRangingBeaconsInRegion(region);
            } catch (RemoteException e) {

            }
//            beaconManager.removeRangeNotifier(this);
            beaconManager.removeAllRangeNotifiers();
            beaconManager.unbind(this);
            beaconManager = null;
        }
        Log.e(TAG, "onDestroy: Service销毁了");
    }

    public static void startScanService(Context context) {
        Intent intent = new Intent(context, BleScanService.class);
        context.startService(intent);
    }

    public static void stopScanService(Context context) {
        context.stopService(new Intent(context, BleScanService.class));
        Log.e(TAG, "stopMyBeaconService: 关闭service" );
    }
}
