package com.vitec.smart.guiding.rule.listener;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by admin on 2018/10/10.
 */
public class DeviceItemClickListener implements AdapterView.OnItemClickListener {

    private Context context;
    public DeviceItemClickListener(Context context) {
        this.context = context;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
