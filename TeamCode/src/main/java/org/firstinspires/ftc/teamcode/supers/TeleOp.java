package org.firstinspires.ftc.teamcode.supers;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class TeleOp {
    public final DcMotor lf, lb, rf, rb;
    public final Servo servo;
    public final CRServo crServo;

    public final BNO055IMU imu;
    public BNO055IMU.Parameters params = new BNO055IMU.Parameters();

    private boolean isInitialized = false;


    public TeleOp(){
        servo = Globals.hwMap.servo.get("servo");
        crServo = Globals.hwMap.crservo.get("crservo");

        lf = Globals.hwMap.dcMotor.get("lf");
        lb = Globals.hwMap.dcMotor.get("lb");
        rf = Globals.hwMap.dcMotor.get("rf");
        rb = Globals.hwMap.dcMotor.get("rb");


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
