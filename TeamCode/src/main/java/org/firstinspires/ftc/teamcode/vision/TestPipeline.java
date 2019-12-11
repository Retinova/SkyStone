package org.firstinspires.ftc.teamcode.vision;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class TestPipeline extends OpenCvPipeline {
    enum Stage{RAW_IMAGE, IN_RANGE}

    Stage stageToRender = Stage.RAW_IMAGE;
    Stage[] vals = Stage.values();

    @Override
    public Mat processFrame(Mat input) {
        Mat inRange = new Mat();
        Core.inRange(input, new Scalar(0, 0, 0), new Scalar(180, 255, 30), inRange);

        switch(stageToRender) {
            case RAW_IMAGE:
                return input;
            case IN_RANGE:
                return inRange;
            default:
                return input;
        }
    }

    @Override
    public void onViewportTapped(){
        int currentStageNum = stageToRender.ordinal();

        int next = (currentStageNum + 1) % vals.length;

        stageToRender = vals[next];
    }

}
