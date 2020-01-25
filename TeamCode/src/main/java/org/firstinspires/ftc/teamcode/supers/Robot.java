package org.firstinspires.ftc.teamcode.supers;

import com.acmerobotics.dashboard.FtcDashboard;
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
        Globals.dash = FtcDashboard.getInstance();

        isInitialized = true;
    }

    public void init(Mode mode){
        if(!isInitialized) throw new IllegalStateException("Robot class uninitialized. Construct Robot instance in runOpMode()");

        if(mode == Mode.AUTO) {
            auto = new Auto();
            odo = new Odometry2();
            initGyro();
        }

        else tele = new TeleOp();
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
