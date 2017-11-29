package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp(name = "Basic TeleOp")
public class CWTeleOpBasic extends OpMode {

    private DcMotor leftDrive, rightDrive, glyphWinch;
    private Servo jewelPitch, jewelYaw;

    private final double THRESHOLD = 0.1;
    private int loop = 0;

    private double leftSpeed = 0.0;
    private double rightSpeed = 0.0;

    @Override
    public void init(){
        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
        jewelPitch = hardwareMap.get(Servo.class, "jewelPitch");
        jewelYaw = hardwareMap.get(Servo.class, "jewelYaw");
        glyphWinch = hardwareMap.get(DcMotor.class, "glyphLift");

        //So directions will be the same for both motors
        rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop(){

        loop++;

        //Controls for LEFT MOTOR (gamepad 1 left stick, A, and B)
        if (gamepad1.left_stick_y > THRESHOLD || gamepad1.left_stick_y < -THRESHOLD) {
            telemetry.addData("StickPos:", gamepad1.left_stick_y);
            leftSpeed = -gamepad1.left_stick_y;
        } else if (gamepad1.a)
            leftSpeed = 0.9;
        else if (gamepad1.b) {
            leftSpeed = -0.9;
        } else {
            leftSpeed = 0.0;
        }

        //Controls for RIGHT MOTOR (gamepad 1 right stick, A, and B)
        if (gamepad1.right_stick_y > THRESHOLD || gamepad1.right_stick_y < -THRESHOLD) {
            telemetry.addData("StickPos:", gamepad1.right_stick_y);
            rightSpeed = -gamepad1.right_stick_y;
        } else if (gamepad1.a) {
            rightSpeed = 0.9;
        } else if (gamepad1.b) {
            rightSpeed = -0.9;
        } else {
            rightSpeed = 0.0;
        }
    }
}
