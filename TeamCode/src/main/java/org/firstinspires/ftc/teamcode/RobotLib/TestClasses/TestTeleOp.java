package org.firstinspires.ftc.teamcode.RobotLib.TestClasses;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="TestTeleOp", group="teleop")
public class TestTeleOp extends LinearOpMode {
    private TestRobot robot = new TestRobot();


    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);

        waitForStart();

        robot.drive();
    }
}
