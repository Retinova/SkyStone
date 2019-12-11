package org.firstinspires.ftc.teamcode.vision;

import com.qualcomm.robotcore.util.RobotLog;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;

public class TranslationPipeline extends OpenCvPipeline {
    enum Stage{INPUT, YELLOWMASK, BLACKMASK, OUTPUT}
    Stage stageToRender = Stage.INPUT;
    Stage[] vals = Stage.values();

    @Override
    public Mat processFrame(Mat input){
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGBA2BGR);
        Mat scrap = input.clone();
        int img_h = input.height(), img_w = input.width(), channels = input.channels();

        Mat yellowMask = new Mat();
        ArrayList<MatOfPoint> yBlockContours = new ArrayList<>();
        Mat hier1 = new Mat();

        ArrayList<Double> contourAreas = new ArrayList<>();
        double largestArea = 0;
        int currentIndex = 0;
        int indexOfYellowArea = 0;
        MatOfPoint yellowArea;
        Rect roiRect;
        Rect offsetROI;
        int roiX, roiY, roiW, roiH;
        int yOffset = 40;
        Mat roi;

        Mat blackMask = new Mat();
        ArrayList<MatOfPoint> bBlockContours = new ArrayList<>();
        Mat hier2 = new Mat();

        ArrayList<MatOfPoint> bestContours = new ArrayList<>();



        Core.inRange(input, new Scalar(0, 160, 220), new Scalar(55, 210, 255), yellowMask);

        Imgproc.findContours(yellowMask, yBlockContours, hier1, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        if(!yBlockContours.isEmpty()) {

            for (MatOfPoint contour : yBlockContours) {
                contourAreas.add(Imgproc.contourArea(contour));
            }

            for (double area : contourAreas) {
                if (area > largestArea) {
                    largestArea = area;
                    indexOfYellowArea = currentIndex;
                }

                currentIndex++;
            }

            yellowArea = yBlockContours.get(indexOfYellowArea);

            roiRect = Imgproc.boundingRect(yellowArea);
            roiX = roiRect.x;
            roiY = roiRect.y;
            roiW = roiRect.width;
            roiH = roiRect.height;
            if(roiY - yOffset >= 0 && roiH + yOffset <= img_h) offsetROI = new Rect(0, roiY - yOffset, img_w, roiH + yOffset);
            else offsetROI = new Rect(0, roiY, img_w, roiH);

            roi = input.submat(offsetROI);

            Imgproc.rectangle(scrap, roiRect, new Scalar(0, 255, 0), 10);
            Imgproc.rectangle(scrap, offsetROI, new Scalar(0, 0, 255), 10);

            Core.inRange(roi, new Scalar(0, 0, 0), new Scalar(30, 30, 30), blackMask);

            Imgproc.findContours(blackMask, bBlockContours, hier2, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            if(!bBlockContours.isEmpty()) {

                for (MatOfPoint contour : bBlockContours) {
                    if (Imgproc.contourArea(contour) > 2000) {
                        bestContours.add(contour);
                    }
                }

                for (MatOfPoint contour : bestContours) {
                    Rect temp = Imgproc.boundingRect(contour);
                    Imgproc.rectangle(scrap, new Point(temp.x, roiY - yOffset + temp.y), new Point(temp.x + temp.width, roiY - yOffset + temp.y + temp.height), new Scalar(255, 0, 200), 30);
                }
            }
        }

        switch(stageToRender){
            case INPUT:
                return input;
            case YELLOWMASK:
                return yellowMask;
            case BLACKMASK:
                return blackMask;
            case OUTPUT:
                return scrap;
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
