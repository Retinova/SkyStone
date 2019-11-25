package org.firstinspires.ftc.teamcode.supers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Hardware {
    public static DcMotor lf, lb, rf, rb = null;

    public static HardwareMap hwMap;

    public static void init(HardwareMap hwMap){
        lf = hwMap.dcMotor.get("lf");
        lb = hwMap.dcMotor.get("lb");
        rf = hwMap.dcMotor.get("rf");
        rb = hwMap.dcMotor.get("rb");
    }
}
