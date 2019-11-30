package org.firstinspires.ftc.teamcode.supers;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

public class TeleOp {
    public final DcMotor lf, lb, rf, rb, lsweeper, rsweeper;
    public final Servo lservo, rservo, hook;

    public final BNO055IMU imu;
    public BNO055IMU.Parameters params = new BNO055IMU.Parameters();

    private boolean isInitialized = false;


    public TeleOp(){
        lservo = Globals.hwMap.servo.get("lservo");
        rservo = Globals.hwMap.servo.get("rservo");
        hook = Globals.hwMap.servo.get("hook");

        lf = Globals.hwMap.dcMotor.get("lf");
        lb = Globals.hwMap.dcMotor.get("lb");
        rf = Globals.hwMap.dcMotor.get("rf");
        rb = Globals.hwMap.dcMotor.get("rb");

        lsweeper = Globals.hwMap.dcMotor.get("lsweeper");
        rsweeper = Globals.hwMap.dcMotor.get("rsweeper");

        lf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
        else throw new IllegalStateException("Robot/Gyro not properly initialized");
    }
}
