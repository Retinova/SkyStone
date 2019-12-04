package org.firstinspires.ftc.teamcode.supers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.odometryinput.Odometry2;

public class Robot {
    public TeleOp tele;
    public Auto auto;
    public Odometry2 odo;

    private boolean isInitialized = false;

    public Robot(LinearOpMode opMode){
        Globals.opMode = opMode;
        Globals.hwMap = opMode.hardwareMap;

        isInitialized = true;
    }

    public void init(){
        if(!isInitialized) throw new IllegalStateException("Robot class uninitialized. Construct Robot instance in runOpMode()");

        tele = new TeleOp();
        auto = new Auto();
        odo = new Odometry2();

        initGyro();
    }

    public void initGyro(){
//        auto.initGyro();
        odo.initGyro();
    }

    public void win(){
        Globals.opMode.telemetry.addData(">", " Won successfully");
        Globals.opMode.telemetry.update();
    }
}
