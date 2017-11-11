package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Red Autonomous")
public class CWAutonRed extends CWAuton {

    public void runOpMode(){
        /* INITIALIZATION */
        super.runOpMode();
        waitForStart();
        knockJewel();
    }
}
