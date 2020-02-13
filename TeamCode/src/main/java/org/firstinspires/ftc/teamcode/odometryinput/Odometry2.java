package org.firstinspires.ftc.teamcode.odometryinput;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.supers.Direction;
import org.firstinspires.ftc.teamcode.supers.Globals;
import org.firstinspires.ftc.teamcode.supers.Quadrant;
import org.firstinspires.ftc.teamcode.vision.actualpipelines.BlackPipeline;

import java.util.HashMap;
import java.util.Iterator;

public class Odometry2 {

    private final DcMotor lf, lb, rf, rb, lsweeper, rsweeper;
    private final Servo lservo, rservo, hook;

    private final int dpi = 1000;
    private final int fieldLength = 144000;
    private int[] coords = {0, 0};

    public final BNO055IMU imu;
    public BNO055IMU.Parameters params = new BNO055IMU.Parameters();

    public double angleError;
    // TODO: set threshhold + tuning
    private double turnThreshhold = 1.0;
    private PIDController turnPid = new PIDController(0.015, 0.0, 0.0);

    private double currentX = 0;
    private double currentY = 0;
    private double coordError;
    // TODO: set threshhold + tuning
    private double coordThreshold = 0b110010000; // 0.4 in
    private Quadrant angleQuad;
    private PIDController posPid = new PIDController(0, 0, 0);

    private UsbManager usbManager;
    private UsbDevice device;
    private HashMap<String, UsbDevice> deviceList = new HashMap<>();
    private MouseThread mouseThread;
    private boolean mouseInitialized = false;

    private boolean isInitialized = false;

    private Telemetry telem;
    
    public Odometry2(){
        telem = Globals.telem;

        lservo = Globals.hwMap.servo.get("lservo");
        rservo = Globals.hwMap.servo.get("rservo");
        hook = Globals.hwMap.servo.get("back");

        lf = Globals.hwMap.dcMotor.get("lf");
        lb = Globals.hwMap.dcMotor.get("lb");
        rf = Globals.hwMap.dcMotor.get("rf");
        rb = Globals.hwMap.dcMotor.get("rb");

        lsweeper = Globals.hwMap.dcMotor.get("lsweep");
        rsweeper = Globals.hwMap.dcMotor.get("rsweep");

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
//        rsweeper.setDirection(DcMotorSimple.Direction.REVERSE);
        lservo.setDirection(Servo.Direction.REVERSE);

        // mouse
        try {
            usbManager = (UsbManager) AppUtil.getDefContext().getSystemService(Context.USB_SERVICE);
            deviceList = usbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            while(deviceIterator.hasNext()){
                device = deviceIterator.next();
                if(device.getProductId() == 0x4d0f) break;
            }

            mouseThread = new MouseThread(usbManager, device);
            mouseInitialized = true;
        } catch (Exception e){
            telem.addData("Failed to setup mouse: ", e);
            telem.update();
        }
    }

    public void initGyro(){
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        while(imu.getSystemStatus() != BNO055IMU.SystemStatus.RUNNING_FUSION && !Globals.opMode.isStopRequested()) {
            imu.initialize(params);
            telem.addData("Status: ", imu.getSystemStatus());
            telem.update();
        }
        isInitialized = true;
        telem.addData("status; ", imu.getSystemStatus());
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

    public void initMouse(){
        if(mouseInitialized) mouseThread.start();
    }

    public void turnTo(double angle){

        // use 0 - 360
        if(angle >= 180 || angle <= -180) {
            if (angle <= -180) angle = normalize(angle);

            turnPid.start();
            angleError = getError(angle, normalize(getCurrentAngle()));

            while (Math.abs(angleError) > turnThreshhold && Globals.opMode.opModeIsActive()) {
                setVelocity(0, turnPid.getOutput(angleError));
                telem.addData("Current error: ", angleError);
                telem.addData("Current angle: ", getCurrentAngle());
                telem.addData("Target: ", angle);
                telem.update();

                angleError = getError(angle, normalize(getCurrentAngle()));
            }

            setVelocity(0, 0);

            telem.addData("Current error: ", angleError);
            telem.addData("Current angle: ", getCurrentAngle());
            telem.addData("Target: ", angle);
            telem.update();
        }

        // use 180 - -180
        else{
            turnPid.start();
            angleError = getError(angle, getCurrentAngle());

            while(Math.abs(angleError) > turnThreshhold && Globals.opMode.opModeIsActive()) {
                setVelocity(0, turnPid.getOutput(angleError));
                telem.addData("Current error: ", angleError);
                telem.addData("Current angle: ", getCurrentAngle());
                telem.addData("Target: ", angle);
                telem.update();
                angleError = getError(angle, getCurrentAngle());
            }

            setVelocity(0, 0);

            telem.addData("Current error: ", angleError);
            telem.addData("Current angle: ", getCurrentAngle());
            telem.addData("Target: ", angle);
            telem.update();
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
        return actual - target;
    }

    public void update() {
        double currentAng = Math.toRadians(getCurrentAngle());
        double shifted = currentAng + (Math.PI / 2.0);
        // 0 = mouse x, 1 = mouse y
        int[] totals = mouseThread.getCoords();
        // shift the angle for the y-input, but dont for the x-input because perpendicular to y movement
        double newX1 = totals[1] * Math.cos(shifted);
        double newY1 = totals[1] * Math.sin(shifted);

        double newX2 = totals[0] * Math.cos(currentAng);
        double newY2 = totals[0] * Math.sin(currentAng);

        currentX = newX1 + newX2;
        currentY = newY1 + newY2;
    }

    public void drive(double deltaX, double deltaY){

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


        // get the target distance as the current "y" value plus the hypotenuse of the desired change in coordinates
        double targetDist = currentY + Math.hypot(Math.abs(deltaX), Math.abs(deltaY)); // irrelevant now?

        double targetX = currentX + deltaX;
        double targetY = currentY + deltaY;

        double lf;
        double lb;
        double rf;
        double rb;
        double coordOut;
        double curAng;

        // TODO: eventually add maintaining of angle (see PushbotAutoDriveByGyro)

        posPid.start();

        coordError = Math.hypot(getError(targetX, currentX), getError(targetY, currentY));
        double angleToTarget = Math.atan2(getError(targetY, currentY), getError(targetX, currentX)) - Math.PI / 4 - Math.toRadians(getCurrentAngle());

        while(Math.abs(coordError) < coordThreshold && Globals.opMode.opModeIsActive()){
//            setVelocity(posPid.getOutput(coordError), 0);
            coordOut = posPid.getOutput(coordError);

            lf = coordOut * Math.sin(angleToTarget);
            lb = coordOut * Math.cos(angleToTarget);
            rf = coordOut * Math.cos(angleToTarget);
            rb = coordOut * Math.sin(angleToTarget);

            this.lf.setPower(lf);
            this.lb.setPower(lb);
            this.rf.setPower(rf);
            this.rb.setPower(rb);

            // update coordinates
            update();
            curAng = Math.toRadians(getCurrentAngle());

            coordError = Math.hypot(getError(targetX, currentX), getError(targetY, currentY));
            // subtracts the current angle for field-centric
            angleToTarget = Math.atan2(getError(targetY, currentY), getError(targetX, currentX)) - Math.PI / 4 - curAng;
        }

        setVelocity(0, 0);
    }

    // TODO: series of coords
    public void drive(double[][] coords){}

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


    /**
     * 1 = red, 0 = blue
     */
    public void alignWithSkystone(BlackPipeline pipeline, int side){
        int current = pipeline.chosenRect.y;

        // red
        // right y  ~374
        // middle y ~179
        // left y ~0
        if(side == 1) {
            // right
            if (current > 250) {
                drive(Direction.FORWARD, 4, 0.5);
                turnTo(-6);
            }
            // middle
            else if (current > 100) {
                drive(Direction.FORWARD, 4, 0.5);
                turnTo(5);
            }
            // left
            else {
                drive(Direction.FORWARD, 4, 0.5);
                turnTo(18);
            }
        }

        // blue
        // right y ~56
        // middle y ~255
        // left y ~460
        else{
            // right
            if (current > 350) {
                drive(Direction.FORWARD, 4, 0.5);
                turnTo(-5);
            }
            // middle
            else if (current > 150) {
                drive(Direction.FORWARD, 4, 0.5);
                turnTo(5);
            }
            // left
            else {
                drive(Direction.FORWARD, 4, 0.5);
                turnTo(18);
            }
        }
    }

    public void drive(Direction direction, double distance, double speed){

        int newLeftFrontTarget = 0;
        int newLeftBackTarget = 0;
        int newRightFrontTarget = 0;
        int newRightBackTarget = 0;

        double wheelDiam = 4.0;
        double ticksPerRev = 537.6;
        double inchesPerRev = wheelDiam * Math.PI;
        double ticksPerInch = ticksPerRev/inchesPerRev;

        if (direction == Direction.FORWARD) {
            distance *= ticksPerInch;
            newLeftFrontTarget = lf.getCurrentPosition() + (int) distance;
            newLeftBackTarget = lb.getCurrentPosition() + (int) distance;
            newRightFrontTarget = rf.getCurrentPosition() + (int) distance;
            newRightBackTarget = rb.getCurrentPosition() + (int) distance;
        }
        if (direction == Direction.BACK) {
            distance *= ticksPerInch;
            newLeftFrontTarget = lf.getCurrentPosition() - (int) distance;
            newLeftBackTarget = lb.getCurrentPosition() - (int) distance;
            newRightFrontTarget = rf.getCurrentPosition() - (int) distance;
            newRightBackTarget = rb.getCurrentPosition() - (int) distance;
        }
        if (direction == Direction.LEFT) {
            distance *= ticksPerInch;
            newLeftFrontTarget = lf.getCurrentPosition() - (int) distance;
            newLeftBackTarget = lb.getCurrentPosition() + (int) distance;
            newRightFrontTarget = rf.getCurrentPosition() + (int) distance;
            newRightBackTarget = rb.getCurrentPosition() - (int) distance;
        }
        if (direction == Direction.RIGHT) {
            distance *= ticksPerInch;
            newLeftFrontTarget = lf.getCurrentPosition() + (int) distance;
            newLeftBackTarget = lb.getCurrentPosition() - (int) distance;
            newRightFrontTarget = rf.getCurrentPosition() - (int) distance;
            newRightBackTarget = rb.getCurrentPosition() + (int) distance;
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
            if(direction == Direction.LEFT || direction == Direction.RIGHT){
                lf.setPower(Math.abs(speed));
                lb.setPower(Math.abs(speed));
                rf.setPower(Math.abs(speed));
                rb.setPower(Math.abs(speed));
            }
            else {
                lf.setPower(Math.abs(speed));
                rf.setPower(Math.abs(speed));
                lb.setPower(Math.abs(speed));
                rb.setPower(Math.abs(speed));
            }

            // Keep looping until the motor is at the desired position that was inputted
            while (Globals.opMode.opModeIsActive() &&
                    (lf.isBusy() && lb.isBusy() && rf.isBusy() && rb.isBusy())) {

                // Display current status of motor paths
                telem.addData("Path1", "Running to %7d :%7d :%7d :%7d", newLeftFrontTarget, newLeftBackTarget, newRightFrontTarget, newRightBackTarget);
                telem.addData("Path2", "Running at %7d :%7d :%7d :%7d", lf.getCurrentPosition(), lb.getCurrentPosition(), rf.getCurrentPosition(), rb.getCurrentPosition());
                telem.addData("right back", rb.getPower());
                telem.addData("right front", rf.getPower());
                telem.addData("left back", lb.getPower());
                telem.addData("left front", lf.getPower());
                telem.update();
            }

            // Stop all motion
            if(direction == Direction.LEFT || direction == Direction.RIGHT) {
                lf.setPower(0);
                lb.setPower(0);
                rf.setPower(0);
                rb.setPower(0);
            }
            else {
                lf.setPower(0);
                rf.setPower(0);
                lb.setPower(0);
                rb.setPower(0);
            }

            resetEncoders();

        }
    }
}