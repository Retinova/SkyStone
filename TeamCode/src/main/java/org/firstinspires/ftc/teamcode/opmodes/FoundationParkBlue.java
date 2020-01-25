package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.supers.Direction;
import org.firstinspires.ftc.teamcode.supers.Mode;
import org.firstinspires.ftc.teamcode.supers.Robot;

@Autonomous(name="Foundation Park Blue", group="foundation")
public class FoundationParkBlue extends LinearOpMode {
    Robot robot;

    @Override
    public void runOpMode() throws InterruptedException{
        robot = new Robot(this);

        robot.init(Mode.AUTO);

        robot.win();

        robot.auto.hook.setPosition(1);

        waitForStart();

        robot.auto.drive(Direction.BACK, 32, 0.3);
        sleep(200);
        robot.auto.hook.setPosition(0.45);
        sleep(500);
        robot.auto.drive(Direction.FORWARD, 26, 0.4);
        robot.odo.turnTo(90);
        robot.auto.hook.setPosition(1);
        robot.auto.drive(Direction.FORWARD, 32, 0.5);

    }
}
