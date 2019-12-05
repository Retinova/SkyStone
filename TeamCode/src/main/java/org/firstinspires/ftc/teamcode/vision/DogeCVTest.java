package org.firstinspires.ftc.teamcode.vision;

import com.disnodeteam.dogecv.DogeCV;
import com.disnodeteam.dogecv.detectors.skystone.SkystoneDetector;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.opencv.core.Rect;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

@TeleOp(name="vision test", group="vision")
public class DogeCVTest extends LinearOpMode {
    OpenCvCamera camera;
    TestPipeline pipeline = new TestPipeline();
//    SkystoneDetector detector;

    @Override
    public void runOpMode() throws InterruptedException {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

//        detector = new SkystoneDetector();
//
//        detector.areaScoringMethod = DogeCV.AreaScoringMethod.PERFECT_AREA;
//
//        detector.useDefaults();

        camera.openCameraDevice();

        camera.setPipeline(pipeline);

        camera.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);

        waitForStart();

        while(opModeIsActive()) {
//            telemetry.addData("Is detected:", detector.isDetected());
//            Rect rect = detector.foundRectangle();
//            if (detector.isDetected())
//                telemetry.addData("Location: ", Integer.toString((int) (rect.x + rect.width * 0.5)) + ", " + Integer.toString((int) (rect.y + 0.5 * rect.height)));
//            telemetry.update();
            telemetry.addData("Current Layer: ", pipeline.layerToRender);
        }


    }
}
