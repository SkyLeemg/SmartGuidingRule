package com.vitec.smart.guiding.rule.listener;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.vitec.smart.guiding.rule.interfaces.MainActivityGettable;
import com.vitec.smart.guiding.rule.R;

/**
 * Created by admin on 2018/10/10.
 */
public class DeviceItemClickListener implements AdapterView.OnItemClickListener ,View.OnClickListener{

    private Context context;
    private Dialog bottomDialog;
    private View contentView;
    private Button btnConnectDevice;
//    private List<Beacon> devices;
    private String macAddress;
    private MainActivityGettable getter;

    public DeviceItemClickListener(Context context,MainActivityGettable getter) {
        this.context = context;
        this.getter = getter;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        macAddress =getter.getDevices().get(i).getBluetoothAddress();
        showBottomDialog();


    }


    private void showBottomDialog() {
        bottomDialog = new Dialog(context, R.style.BottomDialog);
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_content_normal, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        initView();
    }

    private void initView() {
        btnConnectDevice = (Button) contentView.findViewById(R.id.btn_connect_device);
        btnConnectDevice.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect_device:
                Toast.makeText(context,"正在连接",Toast.LENGTH_SHORT).show();
                bottomDialog.dismiss();
                break;
        }
    }
}
