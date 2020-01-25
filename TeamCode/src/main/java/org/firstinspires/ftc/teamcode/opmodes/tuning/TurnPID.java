package org.firstinspires.ftc.teamcode.opmodes.tuning;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.supers.Mode;
import org.firstinspires.ftc.teamcode.supers.Robot;

//@Disabled
@Autonomous(name="turn pid tuning", group="testing")
public class TurnPID extends LinearOpMode {
    Robot robot;

    @Override
    public void runOpMode() throws InterruptedException{
        robot = new Robot(this);

        robot.init(Mode.AUTO);

        robot.win();

        waitForStart();

//        telemetry.addData("idiot", robot.odo.angleError);
//        telemetry.update();

        robot.odo.turnTo(90);

        sleep(2000);
        robot.odo.turnTo(-90);

        sleep(2000);
        robot.odo.turnTo(0);

        robot.odo.imu.close();

        sleep(2000);
    }
}
