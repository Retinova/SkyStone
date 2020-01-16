package org.firstinspires.ftc.teamcode.odometryinput;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import org.firstinspires.ftc.teamcode.supers.Globals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MouseThread extends Thread {
    private byte[]               data;
    private final List<byte[]>   allData = Collections.synchronizedList(new ArrayList<byte[]>());
    private int[]                coords = {0, 0};


    private UsbInterface         intf;
    private UsbEndpoint          ep;
    private UsbDeviceConnection  connection;

    public MouseThread(UsbManager manager, UsbDevice device){
        intf = device.getInterface(0);
        ep = intf.getEndpoint(0);

        data = new byte[ep.getMaxPacketSize()];
        Arrays.fill(data, (byte) 0);

        connection = manager.openDevice(device);
        connection.claimInterface(intf, true);
    }

    @Override
    public void run(){
        while(Globals.opMode.opModeIsActive()){
            connection.bulkTransfer(ep, data, data.length, 0);
            allData.add(data);
        }

        connection.releaseInterface(intf);
        connection.close();
    }

    public int[] getCoords(){
        synchronized (allData) {
            if (!allData.isEmpty()) {
                for (byte[] dataArr : allData) {
                    coords[0] += (int) dataArr[1];
                    coords[1] += (int) dataArr[2];
                }
                allData.clear();
            }
        }
        return coords;
    }
}
