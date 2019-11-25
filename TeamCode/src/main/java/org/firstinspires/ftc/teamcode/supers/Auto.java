package org.firstinspires.ftc.teamcode.supers;

public class Auto {
    public static void drive(double dist){
        Hardware.lf.setPower(1.0);
        Hardware.lb.setPower(1.0);
        Hardware.rf.setPower(1.0);
        Hardware.rb.setPower(1.0);
        // etc
    }
}
