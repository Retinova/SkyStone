package org.firstinspires.ftc.teamcode.vision;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class YellowMask extends OpenCvPipeline {
    Mat yellowMask = new Mat();

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2HSV);
//        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGBA2RGB);

//        hsv
//        needs tuning/fixing but on right track
        Core.inRange(input, new Scalar(15, 210, 170), new Scalar(20, 255, 205), yellowMask);
//        rgb

        return yellowMask;
    }
}
