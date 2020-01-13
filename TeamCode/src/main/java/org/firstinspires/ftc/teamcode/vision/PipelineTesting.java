package org.firstinspires.ftc.teamcode.vision;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.vision.actualpipelines.BlackPipeline;
import org.firstinspires.ftc.teamcode.vision.actualpipelines.BluePipeline;
import org.firstinspires.ftc.teamcode.vision.actualpipelines.RedPipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

//@Disabled
@Autonomous(name="vision test", group="vision")
public class PipelineTesting extends LinearOpMode {
//    OpenCvInternalCamera camera;
    OpenCvCamera webcam;
//    TranslationPipeline pipeline = new TranslationPipeline();
//    YellowMask pipeline = new YellowMask();
    BlackPipeline pipeline = new BlackPipeline();
//    BluePipeline pipeline = new BluePipeline();
//    RedPipeline pipeline = new RedPipeline();

    @Override
    public void runOpMode() throws InterruptedException {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        webcam.openCameraDevice();

        webcam.setPipeline(pipeline);

        webcam.startStreaming(640, 480);

        webcam.showFpsMeterOnViewport(false);

        waitForStart();

        while(opModeIsActive()) {
//            telemetry.addData("Current Layer: ", pipeline.stageToRender);
//            if(pipeline.chosenCenter != null) telemetry.addData("Center: ", pipeline.chosenCenter);
//            telemetry.update();
            telemetry.addData("FPS: ", webcam.getFps());
            telemetry.addData("Chosen rect x: ", pipeline.chosenRect.y);
            telemetry.addData("Chosen rect area: ", pipeline.chosenRect.area());
            telemetry.update();
        }

//        camera.stopStreaming();
//        camera.closeCameraDevice();

        webcam.stopStreaming();
        webcam.closeCameraDevice();
    }
}
