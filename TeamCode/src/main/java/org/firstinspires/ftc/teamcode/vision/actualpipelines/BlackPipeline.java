package org.firstinspires.ftc.teamcode.vision.actualpipelines;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;

public class BlackPipeline extends OpenCvPipeline {
    enum Stage{INPUT, BLACKMASK, OUTPUT}
    Stage stageToRender = Stage.INPUT;
    Stage[] vals = Stage.values();

    private Mat blackMask = new Mat();
    private ArrayList<MatOfPoint> bBlockContours = new ArrayList<>();
    private Mat hier2 = new Mat();

    private ArrayList<MatOfPoint> bestContours = new ArrayList<>();
    private Point currentCenter = new Point();

    private Mat output = new Mat();

    public Rect chosenRect = new Rect();
    public Point chosenCenter = new Point();
    private int highestArea = 0;
    private int lowestX = (int) 1E99;
    private int highestY = 0;

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGBA2RGB);

        input.copyTo(output);

        Core.inRange(input, new Scalar(0, 0, 0), new Scalar(50, 50, 50), blackMask);

        Imgproc.findContours(blackMask, bBlockContours, hier2, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        if(!bBlockContours.isEmpty()){
            for(MatOfPoint contour : bBlockContours){
                if(Imgproc.contourArea(contour) > 5000){
                    bestContours.add(contour);
                }
            }

            for(MatOfPoint contour : bestContours){

                Rect temp = Imgproc.boundingRect(contour);
                currentCenter.set(getCenter(temp));

                if(currentCenter.x < lowestX) {
                    lowestX = (int) currentCenter.x;
                    chosenRect = temp;
                    chosenCenter.set(getCenter(temp));
                }
            }

            if(chosenRect != null){
                Imgproc.rectangle(output, chosenRect, new Scalar(0, 0, 255), 10);
                Imgproc.putText(output, "Chosen", new Point(chosenRect.x, chosenRect.y), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 255, 255));
            }
        }

        lowestX = (int) 1E99;
//        highestY = 0;
        bBlockContours.clear();
        bestContours.clear();

        switch(stageToRender){
            case INPUT:
                return input;
            case BLACKMASK:
                return blackMask;
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

    public double[] getCenter(Rect rect){
        return new double[]{rect.x + rect.width/2, rect.y + rect.height/2};
    }
}
