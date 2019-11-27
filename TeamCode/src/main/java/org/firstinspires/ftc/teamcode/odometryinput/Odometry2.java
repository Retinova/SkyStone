package org.firstinspires.ftc.teamcode.odometryinput;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.supers.Globals;
import org.firstinspires.ftc.teamcode.supers.Quadrant;

public class Odometry2 {



//    private final int dPI = 1000;

    private final DcMotor lf, lb, rf, rb;
    public final Servo servo;
    public final CRServo crServo;

    private final BNO055IMU imu;
    public BNO055IMU.Parameters params = new BNO055IMU.Parameters();

    private double error;
    // TODO: set threshhold
    private double turnThreshhold;
    private PIDController turnPid = new PIDController(0, 0, 0);

//    private double currentX = 0;
    private double currentY = 0;
    // TODO: set threshhold
    private double coordThreshold;
    private Quadrant angleQuad;
    private PIDController posPid = new PIDController(0, 0, 0);

    private boolean isInitialized = false;



    public Odometry2(){
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



    public void turnTo(double angle){

        // use 0 - 360
        if(angle >= 180){
            turnPid.start();
            error = getError(angle, normLeft(getCurrentAngle()));

            while(Math.abs(error) < turnThreshhold && Globals.opMode.opModeIsActive()){
                turnPid.getOutput(error);

                error = getError(angle, normLeft(getCurrentAngle()));
            }
        }
        // use 0 - -360
        else if(angle <= -180){
            turnPid.start();
            error = getError(angle, normRight(getCurrentAngle()));

            while(Math.abs(error) < turnThreshhold && Globals.opMode.opModeIsActive()){
                turnPid.getOutput(error);

                error = getError(angle, normRight(getCurrentAngle()));
            }

        }
        // use 180 - -180
        else{
            turnPid.start();
            error = getError(angle, getCurrentAngle());

            while(Math.abs(error) < turnThreshhold && Globals.opMode.opModeIsActive()){
                turnPid.getOutput(error);

                error = getError(angle, getCurrentAngle());
            }
        }
    }

    public double getCurrentAngle(){
        if(isInitialized) {
            return imu.getAngularOrientation().firstAngle;
        }
        else throw new IllegalStateException("Robot/gyro not properly initialized");
    }

    public double normRight(double angle){
        if(angle > 0) return angle - 360;
        else return angle;
    }

    public double normLeft(double angle){
        if(angle < 0) return angle + 360;
        else return angle;
    }

    public double getError(double target, double actual){
        return actual - target;
    }

    public void update(){
        double sum = lf.getCurrentPosition() + lb.getCurrentPosition() + rf.getCurrentPosition() + rb.getCurrentPosition();
        double avgDeltaPos = sum / 4.0;
//        double currentAng = getCurrentAngle();
//        double deltaX = avgDeltaPos * Math.cos(Math.toRadians(currentAng + 90));
//        double deltaY = avgDeltaPos * Math.sin(Math.toRadians(currentAng + 90));

//        currentX += deltaX;
        currentY += avgDeltaPos;

        // TODO: reset encoders
    }

    public void drive(double deltaX, double deltaY){
        /*if(deltaX >= 0 && deltaY >= 0) angleQuad = Quadrant.I;
        if(deltaX < 0 && deltaY >= 0) angleQuad = Quadrant.II;
        if(deltaX < 0 && deltaY < 0) angleQuad = Quadrant.III;
        if(deltaX >= 0 && deltaY < 0) angleQuad = Quadrant.IV;*/

        // get the angle to turn for aligning with the hypotenuse from inverse tan, subtract 90 to shift into robot orientation
        double targetAngle = Math.toDegrees(Math.atan2(deltaY, deltaX)) - 90;

        if(targetAngle < 0){
            if(Math.abs(targetAngle - getCurrentAngle()) > Math.abs((targetAngle + 360) - getCurrentAngle())) targetAngle += 360;
        }
        else{
            if(Math.abs(targetAngle - getCurrentAngle()) > Math.abs((targetAngle - 360) - getCurrentAngle())) targetAngle -= 360;
        }

        turnTo(targetAngle);

        // TODO: reset encoders

        double targetDist = currentY + Math.hypot(Math.abs(deltaX), Math.abs(deltaY));

        posPid.start();
        error = getError(targetDist, currentY);

        while(Math.abs(error) < coordThreshold && Globals.opMode.opModeIsActive()){
            posPid.getOutput(error);

            update();

            error = getError(targetDist, currentY);
        }
    }
}
