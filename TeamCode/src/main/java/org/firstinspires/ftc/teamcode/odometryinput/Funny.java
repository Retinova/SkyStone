package org.firstinspires.ftc.teamcode.odometryinput;

import android.media.MediaPlayer;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.android.AndroidOrientation;
import org.firstinspires.ftc.robotcore.external.android.AndroidTextToSpeech;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.R;

@Disabled
@TeleOp(name="just a totally normal opmode nothing to see here folks", group="opmode")
public class Funny extends LinearOpMode {
    AndroidTextToSpeech tts = new AndroidTextToSpeech();
    boolean lasta, lastb, lastx, lasty = false;
    AndroidOrientation gyro = new AndroidOrientation();
    MediaPlayer mediaPlayer;
    //double[] notes = new double[]{1.125, 1.125, 2.125};
    //int index = 0;



    @Override
    public void runOpMode() throws InterruptedException {
        /*tts.initialize();

        tts.setLanguageAndCountry("en", "US");
*/
//        mediaPlayer = MediaPlayer.create(hardwareMap.appContext, R.raw.funnysound);

        if(gyro.isAvailable()){
            gyro.startListening();
            gyro.setAngleUnit(AngleUnit.DEGREES);
        }
        else{
            telemetry.addData("Gyro ", "unavailable");
            telemetry.update();
        }

        waitForStart();

        while(opModeIsActive()){
            if(gamepad1.a && !lasta){
//                tts.speak("sand under table");
//                mediaPlayer.start();
            }

            lasta = gamepad1.a;

            if(gamepad1.b && !lastb){
//                tts.speak("Evening Gromit");
            }

            lastb = gamepad1.b;

            if(gamepad1.x && !lastx){
//                tts.speak("loser");
            }

            lastx = gamepad1.x;

            if(gamepad1.y && !lasty){
                //tts.setPitch((float) notes[index]);
//                tts.speak("OK Boomer");
                //index++;
            }

            lasty = gamepad1.y;

            telemetry.addData("Heading: ", gyro.getAzimuth());
            telemetry.addData("Pitch: ", gyro.getPitch());
            telemetry.addData("Roll: ", gyro.getRoll());
            telemetry.addData("Magnitude: ", gyro.getMagnitude());
            telemetry.update();

//            tts.setPitch(1);

//            if (index >= 2){
//                index = 0;
//            }
        }

        mediaPlayer.stop();

//        tts.close();
    }
}
