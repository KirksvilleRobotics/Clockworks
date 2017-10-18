//created by The Clockworks

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Clockworks TeleOp")
public class ClockworksTeleOp extends OpMode {

    private DcMotor leftDrive, rightDrive;
    private double leftSpeed, rightSpeed  = 0.0;

    private Servo jewelPitch, jewelYaw, glyphPusher;
    
    private final double THRESHOLD = 5;
    private boolean aAvailable = true;

    private final double KP = 0.1; //TODO Calculate values
    private final double KI = 0.1; // FILLER
    private final double KD = 0.1; // FILLER

    private long pastTimeMillis = System.currentTimeMillis();
    private double pastErr = 0;
    private double pastPos = 0;
    private double integral = 0;


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
            telemetry.addData("StickPos:", gamepad1.left_stick_y);
            //leftSpeed = (double)(gamepad1.left_stick_y+128)/256;
            leftDrive.setPower((double)(gamepad1.left_stick_y+128)/256); //temp
        } else {
            //leftSpeed = 0.0;
            leftDrive.setPower(0.0);//temp till PID works
        }

        //Right Stick - power controlled by stick pos
        //THRESHOLD is because the joysticks are never perfect
        if(gamepad1.right_stick_y > THRESHOLD || gamepad1.right_stick_y < -THRESHOLD){
            telemetry.addData("StickPos:", gamepad1.right_stick_y);
            //rightSpeed = (double)(gamepad1.right_stick_y+128)/256;
            rightDrive.setPower((double)(gamepad1.right_stick_y+128)/256); //temp

        } else {
            //rightSpeed = (double)(gamepad1.right_stick_y+128)/256;
            rightDrive.setPower(0.0); //temp till PID works
        }

        // A Button - gradual acceleration
        // only accelerates again after A has been pressed OR released.
        if(gamepad1.a && aAvailable){ ///0.0 - 1.0
            leftSpeed = 0.75;
            rightSpeed = 0.75;
            aAvailable = false;
        }

        if(!gamepad1.a && !(aAvailable)){
            leftSpeed = 0.0;
            rightSpeed = 0.0;
            aAvailable = true;
        }

        /* GAMEPAD2 - ACCESSORIES */

        //TopHat - Glyph Pusher
        if(gamepad2.dpad_up){

        }

        //A - Glyph Grabber
        if(gamepad1.a){

        }
        //B - Glyph Release
        if(gamepad2.b){

        }

        //motorPID(leftDrive, toQDPS(leftSpeed));
        //motorPID(rightDrive, toQDPS(rightSpeed));
    }

    private void motorPID(DcMotor m, double tarVel){

        //Get the current velocity based on current and past motor positions and elapsed time
        double vel = (m.getCurrentPosition() - pastPos) / (System.currentTimeMillis() - pastTimeMillis);

        //Get the difference between current velocity and the velocity we want to get to
        double error = tarVel - vel;

        double p = vel - tarVel;
        integral = (integral + (error * (System.currentTimeMillis() - pastTimeMillis)));
        double derivative = (pastErr - error) / (System.currentTimeMillis() - pastTimeMillis);

        m.setPower(crunch((KP*p) + (KI*integral) + (KD*derivative), 1.0, 0.0));

        pastPos = m.getCurrentPosition();
        pastErr = error;

        pastTimeMillis = System.currentTimeMillis();
    }

    private double crunch(double power, double max, double min){
        if(power >= max) {
            return max;
        }else if(power <= min){
            return min;
        }else {
            return power;
        }
    }

    private double toQDPS(double percentage){
        //152rpm is 100% motor power
        return (152*percentage*360*4)/60;
    }
}
