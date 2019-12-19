package org.firstinspires.ftc.teamcode.vision.actualpipelines;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;

public class BluePipeline extends OpenCvPipeline {
    enum Stage{INPUT, BLURRED, BLUEMASK, OUTPUT}
    Stage stageToRender = Stage.INPUT;
    Stage[] vals = Stage.values();

    private Mat blurred = new Mat();

    private Mat blueMask = new Mat();
    private ArrayList<MatOfPoint> foundationContours = new ArrayList<>();
    private Mat hier = new Mat();

    private ArrayList<Double> contourAreas = new ArrayList<>();
    private double largestArea = 0;
    private int currentIndex = 0;
    private int indexOfFoundationArea = 0;
    private MatOfPoint foundationArea;

    private Rect roiRect;

    private Mat output = new Mat();

    @Override
    public Mat processFrame(Mat input){
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2HSV);

        input.copyTo(output);

        // TODO: blur fix
        Imgproc.blur(input, blurred, new Size(7, 7));

        largestArea = 0;
        currentIndex = 0;
        indexOfFoundationArea = 0;

        Core.inRange(blurred, new Scalar(100, 20, 110), new Scalar(120, 80, 155), blueMask);

        Imgproc.findContours(blueMask, foundationContours, hier, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        if(!foundationContours.isEmpty()){
            for(MatOfPoint contour : foundationContours){
                contourAreas.add(Imgproc.contourArea(contour));
            }
            for(double area : contourAreas){
                if (area > largestArea) {
                    largestArea = area;
                    indexOfFoundationArea = currentIndex;
                }

                currentIndex++;
            }

            foundationArea = foundationContours.get(indexOfFoundationArea);

            roiRect = Imgproc.boundingRect(foundationArea);

            Imgproc.rectangle(output, roiRect, new Scalar(0, 255, 0), 10);
        }

        foundationContours.clear();
        contourAreas.clear();

        switch(stageToRender){
            case INPUT:
                return input;
            case BLURRED:
                return blurred;
            case BLUEMASK:
                return blueMask;
            case OUTPUT:
                return output;
            default:
                return input;
        }
    }

    @Override
    public void onViewportTapped() {
        int currentStageNum = stageToRender.ordinal();

        int next = (currentStageNum + 1) % vals.length;

        stageToRender = vals[next];
    }
}
