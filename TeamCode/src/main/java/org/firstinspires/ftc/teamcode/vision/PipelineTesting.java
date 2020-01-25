package org.firstinspires.ftc.teamcode.vision;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.supers.Globals;
import org.firstinspires.ftc.teamcode.vision.actualpipelines.BlackPipeline;
import org.firstinspires.ftc.teamcode.vision.actualpipelines.BluePipeline;
import org.firstinspires.ftc.teamcode.vision.actualpipelines.RedPipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

//@Disabled
@Autonomous(name="vision test", group="vision")
public class PipelineTesting extends LinearOpMode {
//    OpenCvInternalCamera camera;
    OpenCvCamera webcam;
//    TranslationPipeline pipeline = new TranslationPipeline();
//    YellowMask pipeline = new YellowMask();
    BlackPipeline pipeline = new BlackPipeline();
    BluePipeline pipeline2 = new BluePipeline();
    RedPipeline pipeline1 = new RedPipeline();

    @Override
    public void runOpMode() throws InterruptedException {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//        camera = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

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
            if(gamepad1.b) webcam.setPipeline(pipeline1);
            if(gamepad1.y) webcam.setPipeline(pipeline2);
            if(gamepad1.a) webcam.setPipeline(pipeline);

            telemetry.addData("FPS: ", webcam.getFps());
            telemetry.addData("Chosen rect x: ", pipeline.chosenRect.y);
            telemetry.addData("Chosen rect area: ", pipeline.chosenRect.area());
            telemetry.update();
        }

        webcam.stopStreaming();
        webcam.closeCameraDevice();

//        camera.stopStreaming();
//        camera.closeCameraDevice();
    }
}
