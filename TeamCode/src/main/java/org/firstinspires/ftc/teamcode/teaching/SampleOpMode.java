package org.firstinspires.ftc.teamcode.teaching;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Sample Op", group="teleop")
public class SampleOpMode extends LinearOpMode {
    private DcMotor left_front, left_back, right_front, right_back = null;

    private double leftPower;
    private double rightPower;

    @Override
    public void runOpMode() throws InterruptedException {


        left_front = hardwareMap.get(DcMotor.class, "left_front");
        left_back = hardwareMap.get(DcMotor.class, "left_back");
        right_front = hardwareMap.get(DcMotor.class, "right_front");
        right_back = hardwareMap.get(DcMotor.class, "right_back");

        left_front.setDirection(DcMotorSimple.Direction.REVERSE);
        left_back.setDirection(DcMotorSimple.Direction.REVERSE);



        waitForStart();


        while(opModeIsActive()){

            leftPower = Range.clip(gamepad1.left_stick_y + gamepad1.right_stick_x, -1.0, 1.0);
            rightPower = Range.clip(gamepad1.left_stick_y - gamepad1.right_stick_x, -1.0, 1.0);


            left_front.setPower(leftPower);
            left_back.setPower(leftPower);
            right_front.setPower(rightPower);
            right_back.setPower(rightPower);

        }
    }
}
