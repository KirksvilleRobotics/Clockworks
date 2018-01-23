package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Red Far")
public class CWAutonRedFar extends CWAuton {

    @Override
    public void runOpMode(){
        /* INITIALIZATION */
        super.runOpMode();

        waitForStart();

        // 30 SEC AUTONOMOUS PERIOD ----------------------------------------------------------------

        //1 Do all the crap with the jewel
        jewelRoutine(RED);

        // 2 Drive backwards off the platform
        encoderDrive(27.0, 27.0, 0.50);

        // 3 Rotate towards glyph boxes
        encoderDrive(-turnDis(0.45), turnDis(0.45), 0.75);

        // 4 Drive into the glyph zone
        encoderDrive(-41, -42, 0.75);
    }
}