package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Red")
public class CWAutonRed extends CWAuton {

    @Override
    public void runOpMode(){
        /* INITIALIZATION */
        super.runOpMode();

        waitForStart();

        // 30 SEC AUTONOMOUS PERIOD ----------------------------------------------------------------

        //encoderDrive(-5.0, -5.0, 0.75);

        // 1 Knock off the correct jewel
        /*if(detectJewel()){ // if ball is red
            jewelYaw.setPosition(2); // TODO move left
        }else{
            jewelYaw.setPosition(2); // TODO move right
        }*/

        // 2 Drive backwards off the platform
        encoderDrive(24.5, 24.5, 0.75);

        // 3 Rotate towards glyph boxes
        encoderDrive(-turnDis(0.45), turnDis(0.45), 0.75);

        // 4 Drive into the glyph zone
        encoderDrive(-41, -42, 0.75);
    }
}
