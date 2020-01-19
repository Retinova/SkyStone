package org.firstinspires.ftc.teamcode.odometryinput;

@Deprecated
public class Odometry {
    private int xPos;
    private int yPos;

    private int deltaX;
    private int deltaY;
    private final int dPI = 1000;

    private double rawAngle;
    private double expectedAngle;
    private double normalizedAngle;

    private PIDController pid = new PIDController(0, 0, 0);

    public Odometry(){}

    public void update(){
        // deltaX = mouse.read8(2);
        // deltaY = mouse.read8(3);
    }

    public void turnBy(double degrees){
        // set target angle while clearing steady state error
        double target = expectedAngle + degrees;

        // set the expected angle at the end of the motion
        if(target >= 360) expectedAngle = target - 360;
        else if (target < -360) expectedAngle = target + 360;
        else expectedAngle = target;

        // determine direction of turn (- or +)
        if(degrees > 0) turnLeftTo(target);

        if(degrees < 0) turnRightTo(target);

    }

    public void turnLeftTo(double target){
        // handle wraparounds
        if(target > 360){

            // determines whether to use 0 - 360 or -180 - 180
            if(rawAngle >= 0){
                // raw angle > 0, use 0 - 360 until 190, then switch to -180 - 180(normalized angle then switch to non-normalized)
                normalizedAngle = normLeft(getRawAngle());

                pid.start();
                while(true){

                    pid.getOutput(normalizedAngle);

                    if(normalizedAngle < 190) normalizedAngle = normLeft(getRawAngle());
                    else normalizedAngle = getRawAngle();

                }

            }
            else{
                // rawAngle < 0, use -180 - 180 straight away(use non-normalized angle) until 10, then switch to 0-360
                normalizedAngle = getRawAngle();

                pid.start();

                while(true){
                    pid.getOutput(normalizedAngle);

                    if(normalizedAngle < 10) normalizedAngle = getRawAngle();
                    else normalizedAngle = normLeft(getRawAngle());
                }
            }
        }

        // the target is positive, check if on negative side
        else if(target >= 0){
            // 0-360
            if(rawAngle >= 0) {
                // normalize for 0 - 360
                normalizedAngle = normLeft(getRawAngle());
                // use pid on normalized angle
                pid.start();

                while(true){
                    pid.getOutput(normalizedAngle);

                    normalizedAngle = normLeft(getRawAngle());
                }

            }
            // -180 - 180 until 10
            else{
                normalizedAngle = getRawAngle();

                pid.start();

                while(true){
                    pid.getOutput(normalizedAngle);

                    if(normalizedAngle < 10) normalizedAngle = getRawAngle();
                    else normalizedAngle = normLeft(getRawAngle());
                }
            }
        }
//
//        // target is negative, but still turning left
//        else{
//            target = normalize(target);
//            normalizedAngle = normalize(rawAngle);
//            // use pid on normalized angle
//        }
    }

    public void turnRightTo(double target){
        // handle wraparounds
        if(target < -360){

            // determines whether to use 0 - -360 or -180 - 180
            if(rawAngle <= 0){
                // use 0 - -360 until -190, then switch to -180 - 180(normalized angle then switch to non-normalized)
                normalizedAngle = normRight(target);
                while(normalizedAngle > -190){

                }
                // use raw angle

            }
            else{
                // rawAngle > 0, use -180 - 180 straight away(use non-normalized angle)
            }
        }

        // just use 0 - 360, when the target is negative
        else if(target <= 0){
            // normalize for 0 - -360
            normalizedAngle = normRight(rawAngle);
            // use pid on normalized angle
        }

        // target is positive, but still turning right
        else{
            target = normRight(target);
            normalizedAngle = normRight(rawAngle);
            // use pid on normalized angle
        }
    }

    public double normRight(double angle){
        if(angle > 0) return angle - 360;
        else return angle;
    }

    public double normLeft(double angle){
        if(angle < 0) return angle + 360;
        else return angle;
    }

    public double getRawAngle() {
        return rawAngle;
    }

    public double getError(double target, double actual){
        return actual - target;
    }
}
