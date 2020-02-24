package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.supers.Direction;
import org.firstinspires.ftc.teamcode.supers.Mode;
import org.firstinspires.ftc.teamcode.supers.Robot;
import org.firstinspires.ftc.teamcode.supers.Sides;
import org.firstinspires.ftc.teamcode.vision.actualpipelines.BlackPipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;

//@Disabled
@Autonomous(name="Skystone Deliver Park Blue", group="stones")
public class SkystoneDeliverParkBlue extends LinearOpMode {
    Robot robot;
    OpenCvCamera webcam;
    BlackPipeline pipeline = new BlackPipeline();

    @Override
    public void runOpMode() throws InterruptedException{
        robot = new Robot(this);

        robot.init(Mode.AUTO);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.openCameraDevice();
        webcam.setPipeline(pipeline);
        webcam.showFpsMeterOnViewport(false);
        pipeline.onViewportTapped();
        pipeline.onViewportTapped();
        webcam.startStreaming(640, 480);


        robot.win();

        waitForStart();

        robot.auto.lservo.setPosition(0.0);
        robot.auto.rservo.setPosition(0.0);

        sleep(1000);
//        robot.odo.drive(Direction.FORWARD, 4, 0.5);
//        robot.odo.turnTo(6);

        robot.odo.alignWithSkystone(pipeline, Sides.BLUE);
        robot.odo.drive(Direction.FORWARD, 18, 0.5);
        robot.auto.autoIntake();
        robot.odo.turnTo(90);
        robot.odo.drive(Direction.FORWARD, 56, 0.5);
        robot.auto.output();
        robot.odo.drive(Direction.BACK, 10, 0.5);

        robot.auto.lservo.setPosition(0.6);
        robot.auto.rservo.setPosition(0.6);
    }
}
