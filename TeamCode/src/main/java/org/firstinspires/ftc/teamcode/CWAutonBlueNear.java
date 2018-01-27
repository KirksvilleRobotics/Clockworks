package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * Created by yearbook on 1/24/18.
 */

@Autonomous(name="Blue Near")
public class CWAutonBlueNear extends CWAuton {

    @Override
    public void runOpMode(){
        /* INITIALIZATION */
        super.runOpMode();

        waitForStart();

        // 30 SEC AUTONOMOUS PERIOD ----------------------------------------------------------------

        // 1 Do all the crap with the jewel
        jewelRoutine(BLUE);

        // 2 Turn towards the parking space
        encoderDrive(turnDis(0.31), -turnDis(0.31), 0.75);

        // 3 Drive into the parking space
        encoderDrive(-36, -36, 0.75);
    }
}
