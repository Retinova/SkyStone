package org.firstinspires.ftc.teamcode.vision;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class YellowMask extends OpenCvPipeline {
    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGBA2BGR);
        Mat yellowMask = new Mat();

        Core.inRange(input, new Scalar(0, 130, 220), new Scalar(55, 200, 255), yellowMask);

        return yellowMask;
    }
}
