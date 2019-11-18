package org.firstinspires.ftc.teamcode.odometryinput;

public class Odometry2 {

    private final int dPI = 1000;

    private double rawAngle;
    private double error;

    PIDController pid = new PIDController(0, 0, 0);


    public void turnTo(double angle){

        // use 0 - 360
        if(angle >= 180){
            pid.start();
            error = getError(angle, normLeft(getCurrentAngle()));

            while(true){
                pid.getOutput(error);

                error = getError(angle, normLeft(getCurrentAngle()));
            }
        }
        // use 0 - -360
        else if(angle <= -180){
            pid.start();
            error = getError(angle, normRight(getCurrentAngle()));

            while(true){
                pid.getOutput(error);

                error = getError(angle, normRight(getCurrentAngle()));
            }

        }
        // use 180 - -180
        else{
            pid.start();
            error = getError(angle, getCurrentAngle());

            while(true){
                pid.getOutput(error);

                error = getError(angle, getCurrentAngle());
            }
        }
    }

    public double getCurrentAngle(){
        return rawAngle;
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
}
