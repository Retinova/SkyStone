package org.firstinspires.ftc.teamcode.vision.testingpipelines;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class BlackMask extends OpenCvPipeline {
    Mat blackMask = new Mat();
//    Mat sub = new Mat();

//    Rect b = new Rect();

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2HSV);
//        Core.inRange(input, new Scalar(0, 0, 0), new Scalar(30, 30, 30), blackMask);
        Core.inRange(input, new Scalar(0, 0, 0), new Scalar(180, 50, 50), blackMask);
//        b.set(new double[]{input.width()/4, input.height()/4, input.width()/2, input.height()/2});

//        Imgproc.rectangle(blackMask, b, new Scalar(255, 0, 0), 10);

        return blackMask;
    }
}
