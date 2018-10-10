package com.vitec.smart.guiding.rule.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vitec.beacontest.R;

import org.altbeacon.beacon.Beacon;

import java.util.List;


public class BleAdapter extends BaseAdapter {

    private List<Beacon> beacons;
    private Context mContext;

    public BleAdapter(List<Beacon> beacons, Context context) {
        this.beacons = beacons;
        mContext = context;
    }

    @Override
    public int getCount() {
        return beacons.size();
    }

    @Override
    public Object getItem(int i) {
        return beacons.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (view == null) {
            view = inflater.inflate(R.layout.item_beacon_adapter, null);
            holder = new ViewHolder();
            holder.tvbeaconName = (TextView) view.findViewById(R.id.tv_beacon_name);
            holder.tvbeaconUUID = (TextView) view.findViewById(R.id.tv_beacon_uuid);
            holder.tvbeaconMajor = (TextView) view.findViewById(R.id.tv_beacon_major);
            holder.tvbeaconMinor = (TextView) view.findViewById(R.id.tv_beacon_minor);
            holder.tvbeaconRssi = (TextView) view.findViewById(R.id.tv_beacon_rssi);
            holder.tvbeaconPw = (TextView) view.findViewById(R.id.tv_beacon_pw);
            holder.tvbeaconMac = (TextView) view.findViewById(R.id.tv_beacon_mac);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tvbeaconName.setText("Name："+beacons.get(i).getBluetoothName());
        holder.tvbeaconUUID.setText("UUID："+beacons.get(i).getId1());
        holder.tvbeaconMajor.setText("Major："+beacons.get(i).getId2());
        holder.tvbeaconMinor.setText("Minor：" + beacons.get(i).getId3());
        holder.tvbeaconRssi.setText("Rssi：" + beacons.get(i).getRssi());
        holder.tvbeaconPw.setText("Power：" + beacons.get(i).getTxPower());
        holder.tvbeaconMac.setText("Mac："+beacons.get(i).getBluetoothAddress());
        return view;
    }

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<Beacon> beacons) {
        this.beacons = beacons;
    }

    class ViewHolder {
        TextView tvbeaconName;
        TextView tvbeaconUUID;
        TextView tvbeaconMajor;
        TextView tvbeaconMinor;
        TextView tvbeaconRssi;
        TextView tvbeaconPw;
        TextView tvbeaconMac;
    }
}
