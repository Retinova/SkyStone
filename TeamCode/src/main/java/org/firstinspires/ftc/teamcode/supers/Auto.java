package org.firstinspires.ftc.teamcode.supers;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

public class Auto {
    private final DcMotor lf, lb, rf, rb, lsweeper, rsweeper;
    public final Servo lservo, rservo, hook;

    private final BNO055IMU imu;
    public BNO055IMU.Parameters params = new BNO055IMU.Parameters();

    private boolean isInitialized = false;


    public Auto(){
        lservo = Globals.hwMap.servo.get("lservo");
        rservo = Globals.hwMap.servo.get("rservo");
        hook = Globals.hwMap.servo.get("hook");

        lf = Globals.hwMap.dcMotor.get("lf");
        lb = Globals.hwMap.dcMotor.get("lb");
        rf = Globals.hwMap.dcMotor.get("rf");
        rb = Globals.hwMap.dcMotor.get("rb");

        lsweeper = Globals.hwMap.dcMotor.get("lsweeper");
        rsweeper = Globals.hwMap.dcMotor.get("rsweeper");

        lf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lb.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rb.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lsweeper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rsweeper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lsweeper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rsweeper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lb.setDirection(DcMotorSimple.Direction.REVERSE);

        imu = Globals.hwMap.get(BNO055IMU.class, "imu");
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        params.calibrationDataFile = "BNO055IMUCalibration.json";
    }
    public void initGyro(){
        imu.initialize(params);
        isInitialized = true;
    }

    public double getCurrentAngle(){
        if(isInitialized) {
            return imu.getAngularOrientation().firstAngle;
        }
        else throw new IllegalStateException("Robot/gyro not properly initialized");
    }


    public void drive(Direction direction, double distance, double speed) {

        int newLeftFrontTarget = 0;
        int newLeftBackTarget = 0;
        int newRightFrontTarget = 0;
        int newRightBackTarget = 0;

        double wheelDiam = 4.0;
        double ticksPerRev = 280.0;
        double gearReduction = (2.0/3.0);
        double ticksPerInch = (ticksPerRev * gearReduction) / (wheelDiam * Math.PI);

        if (direction == Direction.FORWARD) {
            distance = distance * ticksPerInch;
            newLeftFrontTarget = lf.getCurrentPosition() + (int) distance;
            newLeftBackTarget = lb.getCurrentPosition() + (int) distance;
            newRightFrontTarget = rf.getCurrentPosition() + (int) distance;
            newRightBackTarget = rb.getCurrentPosition() + (int) distance;
        }
        if (direction == Direction.BACK) {
            distance = distance * ticksPerInch;
            newLeftFrontTarget = lf.getCurrentPosition() + (int) distance;
            newLeftBackTarget = lb.getCurrentPosition() + (int) distance;
            newRightFrontTarget = rf.getCurrentPosition() + (int) distance;
            newRightBackTarget = rb.getCurrentPosition() + (int) distance;
        }
        if (direction == Direction.LEFT) {
            distance = distance * ticksPerInch;
            newLeftFrontTarget = lf.getCurrentPosition() - (int) distance;
            newLeftBackTarget = lb.getCurrentPosition() + (int) distance;
            newRightFrontTarget = rf.getCurrentPosition() + (int) distance;
            newRightBackTarget = rb.getCurrentPosition() - (int) distance;
        }
        if (direction == Direction.RIGHT) {
            distance = distance * ticksPerInch;
            newLeftFrontTarget = lf.getCurrentPosition() - (int) distance;
            newLeftBackTarget = lb.getCurrentPosition() + (int) distance;
            newRightFrontTarget = rf.getCurrentPosition() + (int) distance;
            newRightBackTarget = rb.getCurrentPosition() - (int) distance;
        }

        // Ensure that the OpMode is still active
        if (Globals.opMode.opModeIsActive()) {
            lf.setTargetPosition(newLeftFrontTarget);
            lb.setTargetPosition(newLeftBackTarget);
            rf.setTargetPosition(newRightFrontTarget);
            rb.setTargetPosition(newRightBackTarget);

            // Turn On RUN_TO_POSITION
            lf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            lb.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rb.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Reset timer and begin to run the motors
            lf.setPower(Math.abs(speed));
            lb.setPower(Math.abs(speed));
            rf.setPower(Math.abs(speed));
            rb.setPower(Math.abs(speed));

            // Keep looping until the motor is at the desired position that was inputted
            while (Globals.opMode.opModeIsActive() &&
                    (lf.isBusy() && lb.isBusy() && rf.isBusy() && rb.isBusy())) {

                // Display current status of motor paths
                Globals.opMode.telemetry.addData("Path1", "Running to %7d :%7d :%7d :%7d", newLeftFrontTarget, newLeftBackTarget, newRightFrontTarget, newRightBackTarget);
                Globals.opMode.telemetry.addData("Path2", "Running at %7d :%7d :%7d :%7d", lf.getCurrentPosition(), lb.getCurrentPosition(), rf.getCurrentPosition(), rb.getCurrentPosition());
                Globals.opMode.telemetry.addData("right back", rb.getPower());
                Globals.opMode.telemetry.addData("right front", rf.getPower());
                Globals.opMode.telemetry.addData("left back", lb.getPower());
                Globals.opMode.telemetry.addData("left front", lf.getPower());
                Globals.opMode.telemetry.update();
            }

            // Stop all motion
            lf.setPower(0);
            rf.setPower(0);
            lb.setPower(0);
            rb.setPower(0);

            resetEncoders();

        }
    }

    public void resetEncoders(){
        lf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        lf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lb.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rb.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
