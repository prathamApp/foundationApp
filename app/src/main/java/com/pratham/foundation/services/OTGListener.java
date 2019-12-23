package com.pratham.foundation.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;

import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.StorageInfo;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.pratham.foundation.BaseActivity.myUsbDevice;

public class OTGListener extends BroadcastReceiver {
    private static final String TAG = OTGListener.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //Initilizing globel class to access USB ATTACH and DETACH state
        if (action.equalsIgnoreCase("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            myUsbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (myUsbDevice != null) {
//                int vendorID = device.getVendorId();
//                int productID = device.getProductId();
//                Toast.makeText(context, "onReceive: usb attached: vendorid= " + vendorID + "..productid=" + productID, Toast.LENGTH_SHORT).show();
                List<StorageInfo> info = FC_Utility.getStorageList();
//                for (StorageInfo in : info) {
//                    Toast.makeText(context, in.getDisplayName(), Toast.LENGTH_SHORT).show();
//                }
                EventMessage message = new EventMessage();
                message.setMessage(FC_Constants.OTG_INSERTED);
                EventBus.getDefault().post(message);
            }
        } else if (action.equalsIgnoreCase("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
            //When ever device Detach set your global variable to "false"
//            Toast.makeText(context, "onReceive: usb detached", Toast.LENGTH_SHORT).show();
        }
    }
}
