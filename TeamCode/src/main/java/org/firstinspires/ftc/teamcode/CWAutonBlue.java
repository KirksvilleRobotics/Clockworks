package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Blue Autonomous")
public class CWAutonBlue extends CWAuton {

    @Override
    public void runOpMode(){
        // INITIALIZATION --------------------------------------------------------------------------
        super.runOpMode();

        waitForStart();

        // 30 SEC AUTONOMOUS PERIOD ----------------------------------------------------------------
        knockJewel();

        // driving
    }
}
