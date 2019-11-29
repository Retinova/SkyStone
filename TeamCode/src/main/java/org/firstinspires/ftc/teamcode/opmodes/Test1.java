package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.supers.Auto;
import org.firstinspires.ftc.teamcode.supers.Globals;
import org.firstinspires.ftc.teamcode.supers.Hardware;

public class Test1 extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Globals.test1 = this;

        Hardware.init(hardwareMap);

        waitForStart();

        Auto.drive(5);
    }
}
