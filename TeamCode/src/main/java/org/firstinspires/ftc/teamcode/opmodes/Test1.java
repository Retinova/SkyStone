package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.supers.Mode;
import org.firstinspires.ftc.teamcode.supers.Robot;

@TeleOp(name="teleop", group="teleop")
public class Test1 extends LinearOpMode {
    Robot robot;
    double speedSetting = 1.0;
    double pos = 0.0;
    boolean toggle, lastA, lastRight, lastLeft = false;

    @Override
    public void runOpMode() throws InterruptedException {

        robot = new Robot(this);

        robot.init(Mode.TELEOP);

        robot.win();

        // reset servos
        robot.tele.lservo.setPosition(0.9);
        robot.tele.rservo.setPosition(0.0);
        robot.tele.hook.setPosition(0.65);

        waitForStart();

        while (opModeIsActive()) {


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
            // lservo {0.2, 1.0}
            // rservo {0.8, 0.0}
            // hook {0.65, 0.25}
//            robot.tele.hook.setPosition(pos);

            if (gamepad1.right_bumper) {
                robot.tele.lservo.setPosition(0.9);
                robot.tele.rservo.setPosition(0.0);
//                if(pos != 1.0) pos += 0.1;

            }
            if (gamepad1.left_bumper) {
                robot.tele.lservo.setPosition(0.15);
                robot.tele.rservo.setPosition(0.85);
//                if(pos != 0.0) pos -= 0.1;
            }

            lastRight = gamepad1.right_bumper;
            lastLeft = gamepad1.left_bumper;

//            robot.tele.lservo.setPosition(1 - pos);
//            robot.tele.rservo.setPosition(pos);

            if (gamepad1.a) {
                robot.tele.hook.setPosition(0.65);
//                toggle = !toggle;
            }
            if (gamepad1.y) {
                robot.tele.hook.setPosition(0.25);
//                toggle = !toggle;
            }

//            lastA = gamepad1.a;

            // sweeper
            robot.tele.rsweeper.setPower(gamepad1.right_trigger - gamepad1.left_trigger);
            robot.tele.lsweeper.setPower(gamepad1.right_trigger - gamepad1.left_trigger);

            telemetry.addData("Servo pos: ", "%.1f", pos);
            telemetry.update();

        }
    }
}