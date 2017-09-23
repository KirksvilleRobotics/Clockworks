package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by karl ramberg on 9/20/17.
 */

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="Basic: Iterative OpMode", group="Iterative Opmode")
public class TeleOp extends OpMode {

    public DcMotor leftDrive, rightDrive;
    public Servo jewelPitch, jewelYaw, glyphPusher;
    
    public float threshold;
    public boolean aReleased = true;
    public boolean aPressed = false;
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

        /* GAMEPAD1 - Driving */

        //Left Stick - power controlled by stick pos
        if(gamepad1.left_stick_y > threshold || gamepad1.left_stick_y < -threshold){
            leftDrive.setPower(gamepad1.left_stick_y);
        }

        //Right Stick - power controlled by stick pos
        if(gamepad1.right_stick_y > threshold || gamepad1.right_stick_y < -threshold){
            rightDrive.setPower(gamepad1.right_stick_y);
        }

        // A Button - gradual acceleration
        // only accelerates again after A has been pressed OR released.
        if(gamepad1.a && aReleased){
            for(int i = 1; i <= 10; i++){
                leftDrive.setPower((float)(i*0.1));
                rightDrive.setPower((float)(i*0.1));
            }
            aReleased = false;
            aPressed = true;
        }

        if(!gamepad1.a && aPressed){
            for(int i = 10; i >= 1; i--){
                leftDrive.setPower((float)(i*0.1));
                rightDrive.setPower((float)(i*0.1));
            }
            aReleased = true;
            aPressed = false;
        }

        /* GAMEPAD2 - Accesories */

    }
}
