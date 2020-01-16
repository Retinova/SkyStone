package org.firstinspires.ftc.teamcode.odometryinput;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.supers.Globals;

import java.util.HashMap;
import java.util.Iterator;

//@Disabled
@TeleOp(name="mouse test 2", group="mouse")
public class MouseTest2 extends LinearOpMode {
    private UsbManager usbManager;
    private HashMap<String, UsbDevice> deviceList;
    private UsbDevice device;

    private int[] coords = {0, 0};

    private MouseThread mouseThread;
    private boolean initialized = false;

    @Override
    public void runOpMode() throws InterruptedException {
        Globals.opMode = this;
        ElapsedTime timer = new ElapsedTime();

        try {
            usbManager = (UsbManager) AppUtil.getDefContext().getSystemService(Context.USB_SERVICE);
            deviceList = usbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            while(deviceIterator.hasNext()){
                device = deviceIterator.next();
                if(device.getProductId() == 0x4d0f) break;
            }

            mouseThread = new MouseThread(usbManager, device);
            initialized = true;
        } catch (Exception e){
            telemetry.addData("Failed to setup mouse: ", e);
            telemetry.update();
        }

        telemetry.addData("Set up mouse", initialized);
        telemetry.update();

        waitForStart();

        mouseThread.start();
        timer.reset();
        while(opModeIsActive()) {
            if(initialized) {
                coords = mouseThread.getCoords();
            }

            telemetry.addData("x", coords[0]);
            telemetry.addData("y", coords[1]);
            telemetry.addData("loop time", timer.milliseconds());
            telemetry.update();
            timer.reset();
        }
    }
}