package org.firstinspires.ftc.teamcode.odometryinput;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.telecom.Call;

import org.firstinspires.ftc.teamcode.supers.Globals;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class MouseThread extends Thread {
    byte[] data;
    UsbManager manager;
    UsbDevice device;
    UsbInterface intf;
    UsbEndpoint ep;
    UsbDeviceConnection connection;

//    public static ArrayList<byte[]> allData = new ArrayList<>();

    public MouseThread(UsbManager manager, UsbDevice device){
        this.manager = manager;
        this.device = device;

        intf = device.getInterface(0);
        ep = intf.getEndpoint(0);

        data = new byte[ep.getMaxPacketSize()];

        connection = manager.openDevice(device);
        connection.claimInterface(intf, true);
    }

   /* @Override
    public byte[] call(){
        connection.bulkTransfer(ep, data, data.length, 2);
        return data;
    }*/

    @Override
    public void run(){
//        connection.bulkTransfer(ep, data, data.length, 2);
//        MouseTest2.bytes = data;

        while(Globals.opMode.opModeIsActive()){
            connection.bulkTransfer(ep, data, data.length, 2);
            MouseTest2.allData.add(data);
        }
    }
}
