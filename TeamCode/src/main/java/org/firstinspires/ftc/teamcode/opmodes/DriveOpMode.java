package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.supers.Mode;
import org.firstinspires.ftc.teamcode.supers.Robot;

@TeleOp(name="TeleOp", group="driving")
public class DriveOpMode extends LinearOpMode {
    Robot robot;

    double speedSetting = 1.0;
    boolean lastRight = false, lastLeft = false, currentRight, currentLeft, toggle = true, lastA = false, currentA;
    double[] positions = new double[]{0.0, 0.1, 0.2, 0.3, 0.4, 0.5};
    int currentIndex = 5;
    double[] hookPos = new double[]{0.0, 1.0};
    int hookIndex = 1;

    @Override
    public void runOpMode() throws InterruptedException{
        robot = new Robot(this);

        robot.init(Mode.TELEOP);

        robot.win();

        waitForStart();

        while(isStarted() && !isStopRequested()){

            // mecannum math
            double r = Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y);
            double robotAngle = Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) - Math.PI / 4;
            double rightX = -gamepad1.right_stick_x;

            double lf = r * Math.sin(robotAngle) * speedSetting + rightX * speedSetting;
            double lb = r * Math.cos(robotAngle) * speedSetting + rightX * speedSetting;
            double rf = r * Math.cos(robotAngle) * speedSetting - rightX * speedSetting;
            double rb = r * Math.sin(robotAngle) * speedSetting - rightX * speedSetting;

            if(lf > 0.7) lf = 1;
            if(lf < -0.7) lf = -1;
            if(lb > 0.7) lb = 1;
            if(lb < -0.7) lb = -1;
            if(rf > 0.7) rf = 1;
            if(rf < -0.7) rf = -1;
            if(rb > 0.7) rb = 1;
            if(rb < -0.7) rb = -1;

            robot.tele.lf.setPower(-lf);
            robot.tele.lb.setPower(-lb);
            robot.tele.rf.setPower(-rf);
            robot.tele.rb.setPower(-rb);

            // intake servos
            currentLeft = gamepad1.left_bumper;
            currentRight = gamepad1.right_bumper;

            if(currentLeft && !lastLeft){
                // decrement position
                int nextPos = (currentIndex + 5) % 6;
                currentIndex = nextPos;
            }
            if(currentRight && !lastRight){
                // increment position
                int nextPos = (currentIndex + 1) % 6;
                currentIndex = nextPos;
            }

            robot.tele.rservo.setPosition(positions[currentIndex]);
            robot.tele.lservo.setPosition(positions[currentIndex]);

            lastLeft = currentLeft;
            lastRight = currentRight;

            // hook
            currentA = gamepad1.a;

            if(currentA && !lastA){
                int nextPos = (hookIndex + 1) % 2;
                hookIndex = nextPos;
            }

            robot.tele.hook.setPosition(hookPos[hookIndex]);

            lastA = currentA;

            // sweeper
            robot.tele.lsweeper.setPower(gamepad1.right_trigger - gamepad1.left_trigger);
            robot.tele.rsweeper.setPower(gamepad1.right_trigger - gamepad1.left_trigger);

            // telemetry
            telemetry.addData("Servo pos: ", positions[currentIndex]);
            telemetry.addData("Touched: ", robot.tele.intake.isPressed());
            telemetry.addData("Hook pos: ", hookPos[hookIndex]);
            telemetry.update();
        }
    }
}
