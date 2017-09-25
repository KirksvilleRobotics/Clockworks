//created by The Clockworks

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="Basic: Iterative OpMode", group="Iterative Opmode")
public class TeleOp extends OpMode {

    public DcMotor leftDrive, rightDrive;
    public Servo jewelPitch, jewelYaw, glyphPusher;
    
    public float threshold = 10;
    public boolean aAvailable = true;

    @Override
    public void init() {

        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
        jewelPitch = hardwareMap.get(Servo.class, "jewelPitch");
        jewelYaw = hardwareMap.get(Servo.class, "jewelYaw");
        glyphPusher = hardwareMap.get(Servo.class, "glyphPusher");

        leftDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Initialized...", "Woot");
    }

    @Override
    public void loop() {

        /* GAMEPAD1 - DRIVING */

        //Left Stick - power controlled by stick pos
        //Threshold is because the joysticks are never perfect
        if(gamepad1.left_stick_y > threshold || gamepad1.left_stick_y < -threshold){
            leftDrive.setPower(gamepad1.left_stick_y);
        }

        //Right Stick - power controlled by stick pos
        //Threshold is because the joysticks are never perfect
        if(gamepad1.right_stick_y > threshold || gamepad1.right_stick_y < -threshold){
            rightDrive.setPower(gamepad1.right_stick_y); //TODO convert
        }

        // A Button - gradual acceleration
        // only accelerates again after A has been pressed OR released.
        if(gamepad1.a && aAvailable){ ///0.0 - 1.0
            for(int i = 1; i <= 10; i++){
                leftDrive.setPower((float)(i*0.1));
                rightDrive.setPower((float)(i*0.1));
                try {
                    wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            aAvailable = false;
        }

        if(!gamepad1.a && !(aAvailable)){
            for(int i = 10; i >= 1; i-=2){
                leftDrive.setPower((float)(i*0.1));
                rightDrive.setPower((float)(i*0.1));
                try {
                    wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            aAvailable = true;
        }

        /* GAMEPAD2 - ACCESSORIES */

        //TopHat - Glyph Pusher
        if(gamepad2.dpad_up){

        }
    }
}
