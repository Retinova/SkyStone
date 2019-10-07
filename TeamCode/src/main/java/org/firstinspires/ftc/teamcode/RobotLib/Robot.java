package org.firstinspires.ftc.teamcode.RobotLib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

public abstract class Robot extends LinearOpMode implements Hardware{
    public abstract void init(HardwareMap hwMap);
    public abstract void drive();
}
