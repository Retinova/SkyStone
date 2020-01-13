package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.supers.Direction;
import org.firstinspires.ftc.teamcode.supers.Mode;
import org.firstinspires.ftc.teamcode.supers.Robot;

@Autonomous(name="park to left", group="auto")
public class ParkToLeft extends LinearOpMode {
    Robot robot;

    @Override
    public void runOpMode() throws InterruptedException{
        robot = new Robot(this);

        robot.init(Mode.AUTO);


        robot.win();

        waitForStart();

        robot.auto.lservo.setPosition(0.0);
        robot.auto.rservo.setPosition(0.0);

        robot.auto.drive(Direction.FORWARD, 6, 0.5);
        robot.odo.turnTo(90);
        robot.auto.drive(Direction.FORWARD, 26, 0.5);

        robot.auto.lservo.setPosition(0.6);
        robot.auto.rservo.setPosition(0.6);
    }
}
