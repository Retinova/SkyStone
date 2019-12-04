package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.supers.Direction;
import org.firstinspires.ftc.teamcode.supers.Robot;

@Autonomous(name="foundation park", group="auto")
public class Auto1 extends LinearOpMode {
    Robot robot;

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(this);

        robot.init();

        robot.win();

        robot.auto.lservo.setPosition(0.2);
        robot.auto.rservo.setPosition(0.8);
        robot.auto.hook.setPosition(0.65);

        waitForStart();

        robot.auto.drive(Direction.FORWARD, 6, 1.0);
//        robot.odo.turnTo(180);
//        robot.auto.drive(Direction.BACK, 3, 1.0);
//        robot.auto.hook.setPosition(0.25);
//        robot.auto.drive(Direction.FORWARD, 6, 1.0);
//        robot.auto.hook.setPosition(0.65);
//        robot.auto.drive(Direction.RIGHT, 12, 1.0);
    }
}
