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
import org.firstinspires.ftc.teamcode.supers.Sides;
import org.firstinspires.ftc.teamcode.vision.actualpipelines.BlackPipeline;

import java.util.HashMap;
import java.util.Iterator;

public class Odometry2 {
    // hardware
    private final DcMotor lf, lb, rf, rb, lsweeper, rsweeper;
    private final Servo lservo, rservo, hook;
    public final BNO055IMU imu;
    public BNO055IMU.Parameters params = new BNO055IMU.Parameters();
    private boolean isGyroInitialized = false;

    // backend mouse stuff + constants
    private final int dpi = 1000;
    private final int fieldLength = 144000;
    private int[] lastTotals = {0, 0}; // used to find deltas at every call of update()

    // turning vars
    public double angleError;
    // TODO: set threshold + tuning
    private double turnThreshhold = 1.0;
    private PIDController turnPid = new PIDController(0.015, 0.0, 0.0);

    // odo vars
    private double currentX = 0;
    private double currentY = 0;
    private double coordError;
    // TODO: set threshold + tuning
    private double coordThreshold = 0b110010000; // 0.4 in
    private PIDController posPid = new PIDController(0, 0, 0);

    // mouse hardware
    private UsbManager usbManager;
    private UsbDevice device;
    private HashMap<String, UsbDevice> deviceList = new HashMap<>();
    private MouseThread mouseThread;
    private boolean isMouseInitialized = false;

    // telemetry
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
            isMouseInitialized = true;
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
        isGyroInitialized = true;
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
        if(isMouseInitialized) mouseThread.start();
    }

    public void turnTo(double angle){

        // use 0 - 360
        if(angle >= 180 || angle <= -180) {
            angle = normalize(angle); // normalize the angle if its negative

            int counter = 0; // counter to keep track of consecutive times robot was within threshold

            turnPid.start();
            angleError = getError(angle, normalize(getCurrentAngle()));

            while (Globals.opMode.opModeIsActive()) {
                if(Math.abs(angleError) > turnThreshhold) counter++; // threshold check
                else counter = 0; // reset threshold counter (i.e. overshoot)

                if(counter >= 2) break;

                setVelocity(0, turnPid.getOutput(angleError)); // set motor velocities with controller output

                telem.addData("Current error: ", angleError);
                telem.addData("Current angle: ", getCurrentAngle());
                telem.addData("Target: ", angle);
                telem.update();

                angleError = getError(angle, normalize(getCurrentAngle())); // update error using normalized angle
            }

            setVelocity(0, 0); // stop motion

            telem.addData("Current error: ", angleError);
            telem.addData("Current angle: ", getCurrentAngle());
            telem.addData("Target: ", angle);
            telem.update();
        }

        // use 180 - -180
        // all components are the same as above case but adjusted for not using 0 - 360
        else{
            int counter = 0;

            turnPid.start();
            angleError = getError(angle, getCurrentAngle());

            while(Globals.opMode.opModeIsActive()) {
                if(Math.abs(angleError) > turnThreshhold) counter++;
                else counter = 0;

                if(counter >= 2) break;

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
        if(isGyroInitialized) {
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
        double currentAng = Math.toRadians(getCurrentAngle()); // get angle in radians
        double shifted = currentAng + (Math.PI / 2.0); // get angle shifted 90 degrees for y inputs

        int[] totals = mouseThread.getCoords(); // 0 = mouse x, 1 = mouse y

        // shift the angle for the y-input, but dont for the x-input because perpendicular to y movement(already shifted)
        // y
        double deltaX1 = (totals[1] - lastTotals[1]) * Math.cos(shifted);
        double deltaY1 = (totals[1] - lastTotals[1]) * Math.sin(shifted);

        // x
        double deltaX2 = (totals[0] - lastTotals[0]) * Math.cos(currentAng);
        double deltaY2 = (totals[0] - lastTotals[0]) * Math.sin(currentAng);

        // add the calculated deltas
        currentX += deltaX1 + deltaX2;
        currentY += deltaY1 + deltaY2;

        // record last update
        lastTotals = totals;
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
//        double targetDist = currentY + Math.hypot(Math.abs(deltaX), Math.abs(deltaY)); // irrelevant now?

        double targetX = currentX + deltaX;
        double targetY = currentY + deltaY;

        double lf;
        double lb;
        double rf;
        double rb;
        double coordOut;
        double curAng;

        int counter = 0;

        // TODO: eventually add maintaining of angle (see PushbotAutoDriveByGyro)

        posPid.start();

        coordError = Math.hypot(getError(targetX, currentX), getError(targetY, currentY));
        double angleToTarget = Math.atan2(getError(targetY, currentY), getError(targetX, currentX)) - Math.PI / 4 - Math.toRadians(getCurrentAngle());

        while(Globals.opMode.opModeIsActive()){
//            setVelocity(posPid.getOutput(coordError), 0);
            if(Math.abs(coordError) < coordThreshold) counter++;
            else counter = 0;

            if(counter >= 2) break;

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

    public void alignWithSkystone(BlackPipeline pipeline, Sides side){
        int current = pipeline.chosenRect.y;

        // red
        // right y  ~374
        // middle y ~179
        // left y ~0
        if(side == Sides.RED) {
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