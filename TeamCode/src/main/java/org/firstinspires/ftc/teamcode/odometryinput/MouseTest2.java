package org.firstinspires.ftc.teamcode.odometryinput;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.supers.Globals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Disabled
@TeleOp(name="mouse test 2", group="mouse")
public class MouseTest2 extends LinearOpMode {
    UsbManager usbManager;
    HashMap<String, UsbDevice> deviceList;
    UsbDevice device;
    UsbInterface intf;
    UsbEndpoint ep;

    public static List<byte[]> allData = Collections.synchronizedList(new ArrayList<byte[]>());
    byte[] bytes;

    UsbDeviceConnection connection;
    boolean initialized = false;


    @Override
    public void runOpMode() throws InterruptedException {
        Globals.opMode = this;

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newSingleThreadExecutor();
        Future<byte[]> response;
        // response.get();
        // executor.shutdown();
        ElapsedTime timer = new ElapsedTime();

        try {
            usbManager = (UsbManager) AppUtil.getDefContext().getSystemService(Context.USB_SERVICE);
            deviceList = usbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            while(deviceIterator.hasNext()){
                device = deviceIterator.next();
                if(device.getProductId() == 0x4d0f) break;
            }
//            device = deviceList.get("/dev/bus/usb/001/002");

            intf = device.getInterface(0);
            ep = intf.getEndpoint(0);

            connection = usbManager.openDevice(device);
            connection.claimInterface(intf, true);

            initialized = true;
        } catch (Exception e){
            telemetry.addData("Failed to setup mouse: ", e);
            telemetry.update();
        }

        bytes = new byte[ep.getMaxPacketSize()];
        Arrays.fill(bytes, 0, bytes.length-1, (byte) 0);
        int timeout = 2; // ms

        telemetry.update();

        waitForStart();

        timer.reset();
        new MouseThread(usbManager, device).start();

        while(opModeIsActive()) {
            // TODO: thread transfer and make it not crash
            if (initialized) {
//                response = executor.submit(new MouseThread(usbManager, device));
//                data.add(response);
//                new MouseThread(usbManager, device).start();
            }

            synchronized (allData) {
                for (byte[] data : allData) {
                    bytes[1] += data[1];
                    bytes[2] += data[2];
                }

                allData.clear();
            }

            telemetry.addData("x?", (int) bytes[1]);
            telemetry.addData("y?", (int) bytes[2]);
            telemetry.addData("loop time", timer.milliseconds());
            telemetry.update();
            timer.reset();
        }

        connection.releaseInterface(intf);
        connection.close();


    }
}