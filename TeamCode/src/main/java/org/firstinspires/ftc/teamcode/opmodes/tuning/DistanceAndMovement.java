package org.firstinspires.ftc.teamcode.opmodes.tuning;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.supers.Direction;
import org.firstinspires.ftc.teamcode.supers.Mode;
import org.firstinspires.ftc.teamcode.supers.Robot;

@Disabled
@Autonomous(name="Distance testing", group="testing")
public class DistanceAndMovement extends LinearOpMode {
    Robot robot;

    @Override
    public void runOpMode() throws InterruptedException{
        robot = new Robot(this);

        robot.init(Mode.AUTO);

        robot.win();

        waitForStart();

        robot.auto.drive(Direction.RIGHT, 24, 1.0);
        sleep(3000);
        robot.auto.drive(Direction.LEFT, 24, 1.0);
    }
}
