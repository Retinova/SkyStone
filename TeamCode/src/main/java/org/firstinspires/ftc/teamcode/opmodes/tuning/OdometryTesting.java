package org.firstinspires.ftc.teamcode.opmodes.tuning;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.supers.Mode;
import org.firstinspires.ftc.teamcode.supers.Robot;

@Disabled
@Autonomous(name="odo test", group="odo")
public class OdometryTesting extends LinearOpMode {
    Robot robot;

    @Override
    public void runOpMode(){
        robot = new Robot(this);

        robot.init(Mode.AUTO);

        robot.win();

        robot.odo.initMouse();

        waitForStart();

        robot.odo.drive(12000, 12000);
    }
}
