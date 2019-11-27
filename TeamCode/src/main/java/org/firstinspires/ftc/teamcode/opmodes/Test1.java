package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.supers.Auto;
import org.firstinspires.ftc.teamcode.supers.Globals;
import org.firstinspires.ftc.teamcode.supers.Robot;

@TeleOp(name="test of robot and also code modularization", group="teleop")
public class Test1 extends LinearOpMode {
    Robot robot;
//    DcMotor lf, lb, rf, rb = null;


    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(this);

        robot.init();

        robot.win();

        waitForStart();

        while(opModeIsActive()) {
            if (gamepad1.a) {
                robot.tele.lf.setPower(1.0);
                robot.tele.lb.setPower(1.0);
                robot.tele.rf.setPower(1.0);
                robot.tele.rb.setPower(1.0);
            }
            if (gamepad1.b) {
                robot.tele.lf.setPower(0.0);
                robot.tele.lb.setPower(0.0);
                robot.tele.rf.setPower(0.0);
                robot.tele.rb.setPower(0.0);
            }

            if (gamepad1.right_bumper) robot.tele.servo.setPosition(0.2);
            if (gamepad1.left_bumper) robot.tele.servo.setPosition(0.8);

            if (gamepad1.y) robot.tele.crServo.setPower(gamepad1.left_stick_y);
            else robot.tele.crServo.setPower(0);

        }
    }
}
