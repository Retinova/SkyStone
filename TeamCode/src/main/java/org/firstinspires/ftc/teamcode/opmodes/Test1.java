package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.supers.Robot;

@TeleOp(name="test of robot and also code modularization", group="teleop")
public class Test1 extends LinearOpMode {
    Robot robot;

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

        }
    }
}
