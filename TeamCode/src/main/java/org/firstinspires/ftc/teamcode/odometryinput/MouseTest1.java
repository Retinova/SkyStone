package org.firstinspires.ftc.teamcode.odometryinput;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;

public class MouseTest1 extends LinearOpMode {

    I2cDeviceSynch mouse;

    @Override
    public void runOpMode() throws InterruptedException {
        mouse = hardwareMap.i2cDeviceSynch.get("mouse");

        I2cDeviceSynch.ReadWindow readWindow = new I2cDeviceSynch.ReadWindow(1, 6, I2cDeviceSynch.ReadMode.REPEAT);

        mouse.setReadWindow(readWindow);

        mouse.engage();

        waitForStart();

        while (opModeIsActive()){
            telemetry.addData("Byte 0: ", mouse.read8(0));
            telemetry.addData("Byte 1: ", mouse.read8(1));
            telemetry.addData("Byte 2: ", mouse.read8(2));
            telemetry.addData("Byte 3: ", mouse.read8(3));
        }
    }
}
