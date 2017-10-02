//created by The Clockworks

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="Basic: Iterative OpMode", group="Iterative Opmode")
public class TeleOp extends OpMode {

    private DcMotor leftDrive, rightDrive;
    private Servo jewelPitch, jewelYaw, glyphPusher;
    
    private final double THRESHOLD = 10;
    private boolean aAvailable = true;

    private final double KP = 0.1; //TODO Calculate values
    private final double KI = 0.1; // FILLER
    private final double KD = 0.1; // FILLER

    private long pastTimeMillis = System.currentTimeMillis();
    private double pastErr = 0;
    private double pastPos = 0;
    private double pastIng = 0;
    private double tarSpeed  = 0;

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
        //THRESHOLD is because the joysticks are never perfect
        if(gamepad1.left_stick_y > THRESHOLD || gamepad1.left_stick_y < -THRESHOLD){
            leftDrive.setPower(gamepad1.left_stick_y); //TODO convert
        }

        //Right Stick - power controlled by stick pos
        //THRESHOLD is because the joysticks are never perfect
        if(gamepad1.right_stick_y > THRESHOLD || gamepad1.right_stick_y < -THRESHOLD){
            rightDrive.setPower(gamepad1.right_stick_y); //TODO convert
        }

        // A Button - gradual acceleration
        // only accelerates again after A has been pressed OR released.
        if(gamepad1.a && aAvailable){ ///0.0 - 1.0
            /*for(int i = 1; i <= 10; i++){
                leftDrive.setPower((double)(i*0.1));
                rightDrive.setPower((double)(i*0.1));
                try {
                    wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
            tarSpeed = 0.0; //TODO quarter degrees per millisecond
            aAvailable = false;
        }

        if(!gamepad1.a && !(aAvailable)){
            /*for(int i = 10; i >= 1; i-=2){
                leftDrive.setPower((double)(i*0.1));
                rightDrive.setPower((double)(i*0.1));
                try {
                    wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }*/
            tarSpeed = 0.0; //TODO quarter degrees per millisecond
            aAvailable = true;
        }

        /* GAMEPAD2 - ACCESSORIES */

        //TopHat - Glyph Pusher
        if(gamepad2.dpad_up){

        }

        //A - Glyph Grabber
        //B - Glyph Release

        motorPID(leftDrive, tarSpeed);
        motorPID(rightDrive, tarSpeed);
    }

    public void motorPID(DcMotor m, double tarSpeed){
        double currentVel = (m.getCurrentPosition() - pastPos) / (System.currentTimeMillis() - pastTimeMillis);

        double error = currentVel - tarSpeed;

        double p = (currentVel - tarSpeed);
        double i = (pastIng + ( error * (System.currentTimeMillis() - pastTimeMillis)));
        double d = (pastErr - error) / (System.currentTimeMillis() - pastTimeMillis);

        m.setPower(crunch((KP*p) + (KI*i) + (KD*d)));

        pastPos = m.getCurrentPosition();
        pastErr = error;
        pastIng = i;
        pastTimeMillis = System.currentTimeMillis();
    }

    public double crunch(double power){
        if(power > 1.0) {
            return 1.0;
        }else if(power < 0.0){
            return 0.0;
        }else {
            return power;
        }
    }
}
