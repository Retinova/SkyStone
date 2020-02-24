package org.firstinspires.ftc.teamcode.demobot;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

//@Disabled
@TeleOp(name="demobot", group="demobot")
public class DriveMode extends LinearOpMode {
    DcMotor lf, lb, rf, rb = null;

    @Override
    public void runOpMode() throws InterruptedException {
        lf = hardwareMap.dcMotor.get("lf");
        lb = hardwareMap.dcMotor.get("lb");
        rf = hardwareMap.dcMotor.get("rf");
        rb = hardwareMap.dcMotor.get("rb");

        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lb.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        while(opModeIsActive()){

            double r = Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y);
            double robotAngle = Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) - Math.PI / 4;
            double rightX = gamepad1.right_stick_x;

            final double lfp = r * Math.sin(robotAngle) + rightX;
            final double lbp = r * Math.cos(robotAngle) + rightX;
            final double rfp = r * Math.cos(robotAngle) - rightX;
            final double rbp = r * Math.sin(robotAngle) - rightX;

            lf.setPower(-lfp);
            rb.setPower(-rbp);
            lb.setPower(-lbp);
            rf.setPower(-rfp);

            telemetry.addData("lf power:", lf.getPower());
            telemetry.addData("lb power:", lb.getPower());
            telemetry.addData("rf power:", rf.getPower());
            telemetry.addData("rb power:", rb.getPower());
            telemetry.update();
        }
    }
}
