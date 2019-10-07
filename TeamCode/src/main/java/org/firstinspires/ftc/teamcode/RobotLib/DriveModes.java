package org.firstinspires.ftc.teamcode.RobotLib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

public class DriveModes {

    public DriveModes(){

    }

    public void mecannum(DcMotor leftFront, DcMotor leftBack, DcMotor rightBack, DcMotor rightFront, Gamepad gamepad1){

        double r = Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y);
        double robotAngle = Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) - Math.PI / 4;
        double rightX = gamepad1.right_stick_x;

        double v1 = r * Math.cos(robotAngle) + rightX;
        double v3 = r * Math.sin(robotAngle) + rightX;
        double v2 = r * Math.sin(robotAngle) - rightX;
        double v4 = r * Math.cos(robotAngle) - rightX;

        leftFront.setPower(v1);
        leftBack.setPower(v3);
        rightFront.setPower(v2);
        rightBack.setPower(v4);
    }
}
