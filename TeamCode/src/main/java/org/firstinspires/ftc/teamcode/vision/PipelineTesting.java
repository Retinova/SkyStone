package org.firstinspires.ftc.teamcode.vision;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvInternalCamera;

//@Disabled
@TeleOp(name="vision test", group="vision")
public class PipelineTesting extends LinearOpMode {
    OpenCvInternalCamera camera;
//    TranslationPipeline pipeline = new TranslationPipeline();
//    BlackMask pipeline = new BlackMask();
//    YellowMask pipeline = new YellowMask();
//    BlackPipeline pipeline = new BlackPipeline();
    BluePipeline pipeline = new BluePipeline();

    @Override
    public void runOpMode() throws InterruptedException {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        camera.openCameraDevice();

        camera.setPipeline(pipeline);

//        camera.setFlashlightEnabled(true);

        camera.startStreaming(640, 480);

        waitForStart();

        while(opModeIsActive()) {
//            telemetry.addData("Current Layer: ", pipeline.stageToRender);
//            if(pipeline.chosenCenter != null) telemetry.addData("Center: ", pipeline.chosenCenter);
            telemetry.update();
        }

        camera.stopStreaming();
        camera.closeCameraDevice();
    }
}
