package org.firstinspires.ftc.teamcode.odometryinput;

public class Odometry2 {

//    private final int dPI = 1000;

    private double rawAngle;
    private double error;

    PIDController turnPid = new PIDController(0, 0, 0);

    private double currentX = 0;
    private double currentY = 0;

    PIDController posPid = new PIDController(0, 0, 0);


    public void turnTo(double angle){

        // use 0 - 360
        if(angle >= 180){
            turnPid.start();
            error = getError(angle, normLeft(getCurrentAngle()));

            while(true){
                turnPid.getOutput(error);

                error = getError(angle, normLeft(getCurrentAngle()));
            }
        }
        // use 0 - -360
        else if(angle <= -180){
            turnPid.start();
            error = getError(angle, normRight(getCurrentAngle()));

            while(true){
                turnPid.getOutput(error);

                error = getError(angle, normRight(getCurrentAngle()));
            }

        }
        // use 180 - -180
        else{
            turnPid.start();
            error = getError(angle, getCurrentAngle());

            while(true){
                turnPid.getOutput(error);

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

    /*public void update(){
        double sum = motor1.getCurrentPosition() + motor2.getCurrentPosition() + motor3.getCurrentPosition() + motor4.getCurrentPosition();
        double avgDeltaPos = sum / 4.0;
        double currentAng = getCurrentAngle();
        double deltaX = avgDeltaPos * Math.cos(Math.toRadians(currentAng + 90));
        double deltaY = avgDeltaPos * Math.sin(Math.toRadians(currentAng + 90));

        currentX += deltaX;
        currentY += deltaY;
    }*/
}
