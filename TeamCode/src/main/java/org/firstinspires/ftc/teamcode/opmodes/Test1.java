package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.supers.Robot;

@TeleOp(name="test of robot and also code modularization", group="teleop")
public class Test1 extends LinearOpMode {
    Robot robot;
    double speedSetting = 1.0;
    double pos = 0.0;

    @Override
    public void runOpMode() throws InterruptedException {

        robot = new Robot(this);

        robot.init();

        robot.win();

        waitForStart();

        while(opModeIsActive()) {


            // mecannum math
            double r = Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y);
            double robotAngle = Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) - Math.PI / 4;
            double rightX = -gamepad1.right_stick_x;

            final double lf = r * Math.sin(robotAngle) * speedSetting + rightX * speedSetting;
            final double lb = r * Math.cos(robotAngle) * speedSetting + rightX * speedSetting;
            final double rf = r * Math.cos(robotAngle) * speedSetting - rightX * speedSetting;
            final double rb = r * Math.sin(robotAngle) * speedSetting - rightX * speedSetting;

            robot.tele.lf.setPower(lf);
            robot.tele.lb.setPower(lb);
            robot.tele.rf.setPower(rf);
            robot.tele.rb.setPower(rb);

            // intake servo positions
            if(gamepad1.dpad_up) pos += 0.1;
            if(gamepad1.dpad_down) pos -= 0.1;

            robot.tele.lservo.setPosition(pos);

            // sweeper
            robot.tele.rsweeper.setPower(gamepad1.right_trigger - gamepad1.left_trigger);
            robot.tele.lsweeper.setPower(gamepad1.right_trigger - gamepad1.left_trigger);

            telemetry.addData("Servo pos: ", "%.1f", pos);
            telemetry.update();

        }
    }
}
