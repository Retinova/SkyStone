package org.firstinspires.ftc.teamcode.vision;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class BlackMask extends OpenCvPipeline {
    @Override
    public Mat processFrame(Mat input) {
        Mat blackMask = new Mat();

        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGBA2BGR);
        Core.inRange(input, new Scalar(0, 0, 0), new Scalar(30, 30, 30), blackMask);

        return blackMask;
    }
}
