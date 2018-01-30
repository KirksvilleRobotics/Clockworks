package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Tester")
public class CWTester extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        CustomColor cc = hardwareMap.get(CustomColor.class, "jewelCol");
        print("Begin");
        for(int i = 0; i < 6; i++) {
            print("Passive...");
            cc.passiveMode();
            sleep(1000);
            print("Active...");
            cc.activeMode();
            sleep(1000);
        }

        while(true) {

            cc.read();
            String o = String.format("R:%4d  G:%4d  B:%4d  W:%4d", cc.red(), cc.green(), cc.blue(), cc.white());
            print(o);
            sleep(250);
        }

    }

    private void print(String o)
    {
        telemetry.addLine(o);
        telemetry.update();
    }
}
