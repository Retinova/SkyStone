package org.firstinspires.ftc.teamcode.RobotLib.TestClasses;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RobotLib.Robot;
import org.firstinspires.ftc.teamcode.RobotLib.DriveModes;

public class TestRobot extends Robot {
    private DriveModes driver = new DriveModes();

    public DcMotor leftFront, leftBack, rightFront, rightBack = null;

    public TestRobot(){}

    @Override
    public void init(HardwareMap hwMap) {
        leftFront = hwMap.get(DcMotor.class, "left_front");
        leftBack = hwMap.get(DcMotor.class, "left_back");
        rightFront = hwMap.get(DcMotor.class, "right_front");
        rightBack = hwMap.get(DcMotor.class, "right_back");

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void drive() {
        driver.mecannum(leftFront, leftBack, rightBack, rightFront, gamepad1);
    }

    @Override
    public void runOpMode() throws InterruptedException {

    }
}
