package org.firstinspires.ftc.teamcode.odometryinput;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.supers.Globals;
import org.firstinspires.ftc.teamcode.supers.Quadrant;

public class Odometry2 {

    private final DcMotor lf, lb, rf, rb, lsweeper, rsweeper;
    public final Servo lservo, rservo, hook;

    private final BNO055IMU imu;
    public BNO055IMU.Parameters params = new BNO055IMU.Parameters();

    private double angleError;
    // TODO: set threshhold + tuning
    private double turnThreshhold;
    private PIDController turnPid = new PIDController(0.005, 0, 0);

//    private double currentX = 0;
    private double currentY = 0;
    private double coordError;
    // TODO: set threshhold + tuning
    private double coordThreshold;
    private Quadrant angleQuad;
    private PIDController posPid = new PIDController(0, 0, 0);

    private boolean isInitialized = false;

    private final double countsPerInch = 0;



    public Odometry2(){
        lservo = Globals.hwMap.servo.get("lservo");
        rservo = Globals.hwMap.servo.get("rservo");
        hook = Globals.hwMap.servo.get("hook");

        lf = Globals.hwMap.dcMotor.get("lf");
        lb = Globals.hwMap.dcMotor.get("lb");
        rf = Globals.hwMap.dcMotor.get("rf");
        rb = Globals.hwMap.dcMotor.get("rb");

        lsweeper = Globals.hwMap.dcMotor.get("lsweeper");
        rsweeper = Globals.hwMap.dcMotor.get("rsweeper");

        imu = Globals.hwMap.get(BNO055IMU.class, "imu");
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        params.calibrationDataFile = "BNO055IMUCalibration.json";

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
        lsweeper.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void initGyro(){
        ElapsedTime timer = new ElapsedTime();
        while(timer.seconds() < 5 && imu.getSystemStatus() != BNO055IMU.SystemStatus.RUNNING_FUSION) {
            imu.initialize(params);
        }
        isInitialized = true;
    }

    public void setVelocity(double forward, double clockwise){
        // TODO: make sure directions are correct
        double right = forward - clockwise;
        double left = forward + clockwise;
        double max = Math.max(Math.max(Math.abs(left), Math.abs(right)), 1.0);

        lf.setPower(left / max);
        lb.setPower(left / max);
        rf.setPower(right / max);
        rb.setPower(right / max);
    }

    public void turnTo(double angle){

        // use 0 - 360
        if(angle >= 180 || angle <= -180){
            if(angle <= -180) angle = normalize(angle);

            turnPid.start();
            angleError = getError(angle, normalize(getCurrentAngle()));

            while(Math.abs(angleError) < turnThreshhold && Globals.opMode.opModeIsActive()){
                setVelocity(0, turnPid.getOutput(angleError));

                angleError = getError(angle, normalize(getCurrentAngle()));
            }
        }
        // use 0 - -360
        /*else if(angle <= -180){
            turnPid.start();

            angleError = getError(angle, normRight(getCurrentAngle()));

            while(Math.abs(angleError) < turnThreshhold && Globals.opMode.opModeIsActive()){
                setVelocity(0, turnPid.getOutput(angleError));

                angleError = getError(angle, normRight(getCurrentAngle()));
            }

        }*/
        // use 180 - -180
        else{
            turnPid.start();
            angleError = getError(angle, getCurrentAngle());

            while(Math.abs(angleError) < turnThreshhold && Globals.opMode.opModeIsActive()){
                setVelocity(0, turnPid.getOutput(angleError));

                angleError = getError(angle, getCurrentAngle());
            }
        }
    }

    public double getCurrentAngle(){
        if(isInitialized) {
            return imu.getAngularOrientation().firstAngle;
        }
        else throw new IllegalStateException("Robot/gyro not properly initialized");
    }

    @Deprecated
    public double normRight(double angle){
        if(angle > 0) return angle - 360;
        else return angle;
    }

    public double normalize(double angle){
        angle = angle % 360;
        if(angle < 0) return angle + 360;
        else return angle;
    }

    public double getError(double target, double actual){
        return normalize(actual - target);
    }

    public void update(){
        // get a working average of the current encoder count
        double sum = lf.getCurrentPosition() + lb.getCurrentPosition() + rf.getCurrentPosition() + rb.getCurrentPosition();
        double avgDeltaPos = sum / 4.0;
//        this is commented out temporarily, until actual odometry is ready (currently utilizes only forward/backward tracking)
//        double currentAng = getCurrentAngle();
//        double deltaX = avgDeltaPos * Math.cos(Math.toRadians(currentAng + 90));
//        double deltaY = avgDeltaPos * Math.sin(Math.toRadians(currentAng + 90));

//        currentX += deltaX;
        currentY += avgDeltaPos;

        resetEncoders();
    }

    public void drive(double deltaX, double deltaY){
        /*if(deltaX >= 0 && deltaY >= 0) angleQuad = Quadrant.I;
        if(deltaX < 0 && deltaY >= 0) angleQuad = Quadrant.II;
        if(deltaX < 0 && deltaY < 0) angleQuad = Quadrant.III;
        if(deltaX >= 0 && deltaY < 0) angleQuad = Quadrant.IV;*/

        double wheelDiam = 4.0;
        double ticksPerRev = 280.0;
        double gearReduction = (2.0/3.0);
        double ticksPerInch = (ticksPerRev * gearReduction) / (wheelDiam * Math.PI);

        deltaX *= ticksPerInch;
        deltaY *= ticksPerInch;

        // get the angle to turn for aligning with the hypotenuse from inverse tan, subtract 90 to shift into proper robot orientation
        double targetAngle = Math.toDegrees(Math.atan2(deltaY, deltaX)) - 90;

        // optimize the angle being passed into turnTo() (positive angle vs. negative counterpart, finds whichever is closest)
        if(targetAngle < 0){
            if(Math.abs(targetAngle - getCurrentAngle()) > Math.abs((targetAngle + 360) - getCurrentAngle())) targetAngle += 360;
        }
        else{
            if(Math.abs(targetAngle - getCurrentAngle()) > Math.abs((targetAngle - 360) - getCurrentAngle())) targetAngle -= 360;
        }

        // align with hypotenuse
        turnTo(targetAngle);

        resetEncoders();

        // get the target distance as the current "y" value plus the hypotenuse of the desired change in coordinates
        double targetDist = currentY + Math.hypot(Math.abs(deltaX), Math.abs(deltaY));

        // TODO: eventually add maintaining of angle (see pushbotuatodrivebygyro)

        posPid.start();
//        turnPid.start();

//        angleError = getError(targetAngle, getCurrentAngle());
        coordError = getError(targetDist, currentY);

        while(Math.abs(coordError) < coordThreshold && Globals.opMode.opModeIsActive()){
//            setVelocity(posPid.getOutput(coordError), turnPid.getOutput(angleError));
            setVelocity(posPid.getOutput(coordError), 0);

            // update "y" values
            update();

            coordError = getError(targetDist, currentY);
//            angleError = getError(targetAngle, getCurrentAngle());
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
