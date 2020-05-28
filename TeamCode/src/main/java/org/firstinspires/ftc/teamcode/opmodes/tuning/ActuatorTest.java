package org.firstinspires.ftc.teamcode.opmodes.tuning;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.teamcode.supers.Globals;

@Disabled
@TeleOp(name="actuator test", group="testing")
public class ActuatorTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException{
        Globals.opMode = this;

        CRServo actuator;

        actuator = hardwareMap.crservo.get("actuator");

        telemetry.addData(">", "Initialized");
        telemetry.update();

        waitForStart();

        while(opModeIsActive()){
            actuator.setPower(gamepad1.right_trigger - gamepad1.left_trigger);

            telemetry.addData("Actuator power:", actuator.getPower());
            telemetry.update();
        }
    }
}
