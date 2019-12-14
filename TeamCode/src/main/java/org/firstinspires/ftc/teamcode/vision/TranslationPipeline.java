package org.firstinspires.ftc.teamcode.vision;

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
    private Stage[] vals = Stage.values();

    private Mat output = new Mat();
    private int img_h, img_w, channels;

    private Mat yellowMask = new Mat();
    private ArrayList<MatOfPoint> yBlockContours = new ArrayList<>();
    private Mat hier1 = new Mat();

    private ArrayList<Double> contourAreas = new ArrayList<>();
    private double largestArea = 0;
    private int currentIndex = 0;
    private int indexOfYellowArea = 0;
    private MatOfPoint yellowArea;
    private Rect roiRect;
    private Rect offsetROI = new Rect();
    private int rectX, rectY, rectW, rectH;
    private final int yOffset = 40;
    private Mat roi = new Mat();

    private Mat blackMask = new Mat();
    private Mat blackMaskResize = new Mat();
    private ArrayList<MatOfPoint> bBlockContours = new ArrayList<>();
    private Mat hier2 = new Mat();

    private ArrayList<MatOfPoint> bestContours = new ArrayList<>();

    @Override
    public Mat processFrame(Mat input){
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGBA2BGR);
        img_h = input.height();
        img_w = input.width();
        channels = input.channels();
        input.copyTo(output);

        input.copyTo(blackMaskResize);

        largestArea = 0;
        currentIndex = 0;
        indexOfYellowArea = 0;

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
            rectX = roiRect.x;
            rectY = roiRect.y;
            rectW = roiRect.width;
            rectH = roiRect.height;

            if(rectY - yOffset >= 0 && rectH + yOffset <= img_h) {
                offsetROI.set(new double[]{0, rectY - yOffset, img_w, rectH + yOffset});
            }
            else {
                offsetROI.set(new double[]{0, rectY, img_w, rectH});
            }


            if(roi != null) roi = input.submat(offsetROI);

            Imgproc.rectangle(output, roiRect, new Scalar(0, 255, 0), 10);
            Imgproc.rectangle(output, offsetROI, new Scalar(0, 0, 255), 10);

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
                    Imgproc.rectangle(output, new Point(temp.x, rectY - yOffset + temp.y), new Point(temp.x + temp.width, rectY - yOffset + temp.y + temp.height), new Scalar(255, 0, 200), 30);
                }
            }
        }

        yBlockContours.clear();
        contourAreas.clear();
        bBlockContours.clear();
        bestContours.clear();

        switch(stageToRender){
            case INPUT:
                return input;
            case YELLOWMASK:
                return yellowMask;
            case BLACKMASK:
                return blackMaskResize;
            case OUTPUT:
                return output;
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
