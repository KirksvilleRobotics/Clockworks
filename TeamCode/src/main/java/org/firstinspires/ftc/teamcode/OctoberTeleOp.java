package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "October TeleOp")
public class OctoberTeleOp extends OpMode {

    private DcMotor leftDrive, rightDrive, glyphLift;
    private Servo jewelPitch, jewelYaw, glyphPusher, leftGlyphGrabber, rightGlyphGrabber;

    private boolean x1Available = true;
    boolean pidMode = false;

    private final double THRESHOLD = 0.1;

    private final double KP = 1.0; //TODO Calculate values
    private final double KI = 0.1; // FILLER
    private final double KD = 0.1; // FILLER

    private long pastTimeMillis = System.currentTimeMillis();

    //I'm really sorry, but the following is going to be a little disgusting
    //Each motor needs its own PID info associated with it, which means we need an inner class

    private class PIDMotor {
        double pastErr = 0;
        double pastPos = 0;
        double pastIntegral = 0;
        double pastTime = 0;
        DcMotor motor;
        PIDMotor(DcMotor source) {motor = source;}
    }

    PIDMotor leftDrivePID;
    PIDMotor rightDrivePID;
    PIDMotor glyphLiftPID;

    private double leftSpeed  = 0.0;
    private double rightSpeed = 0.0;
    private double liftSpeed = 0.0;

    @Override
    public void init() {

        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
        jewelPitch = hardwareMap.get(Servo.class, "jewelPitch");
        jewelYaw = hardwareMap.get(Servo.class, "jewelYaw");
        glyphPusher = hardwareMap.get(Servo.class, "glyphPusher");
        glyphLift = hardwareMap.get(DcMotor.class, "glyphLift");
        leftGlyphGrabber = hardwareMap.get(Servo.class, "grabberLeft");
        rightGlyphGrabber = hardwareMap.get(Servo.class, "grabberRight");

        rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        leftDrivePID = new PIDMotor(leftDrive);
        rightDrivePID = new PIDMotor(rightDrive);
        glyphLiftPID = new PIDMotor(glyphLift);

        telemetry.addData("Initialized...", "Yayw");
    }

    @Override
    public void loop() {

        /* Notes for control orientation:
         *
         *                 180 Servo:
         *               +------------+                                 :   Gamepad sticks:
         *  180 degrees _|________/--\|____________v 0 degrees          :   +--.--+   +-----+
         *  (1.0)        |        \--/|           /      (0.0)          :   | =O= |   |  _  |
         *               +------------+         //                      :   |  *  |   | =O= |
         *                                   <-/ turns this way         :   +-----+   +--`--+
         *                                                              :    -1.0       1.0
         *
         * Gamepad values range from -1.0 at the top position to 1.0 at the bottom position .
         *
        */
        /* GAMEPAD1 - DRIVING */


        //Controls for LEFT MOTOR (gamepad 1 left stick, A, and B)
        if(gamepad1.left_stick_y > THRESHOLD || gamepad1.left_stick_y < -THRESHOLD){
            telemetry.addData("StickPos:", gamepad1.left_stick_y);
            leftSpeed = -gamepad1.left_stick_y;
        } else if(gamepad1.a)
            leftSpeed = 0.75;
        else if(gamepad1.b) {
            leftSpeed = -0.75;
        } else {
            leftSpeed = 0.0;
        }

        //Controls for RIGHT MOTOR (gamepad 1 right stick, A, and B)
        if(gamepad1.right_stick_y > THRESHOLD || gamepad1.right_stick_y < -THRESHOLD){
            telemetry.addData("StickPos:", gamepad1.right_stick_y);
            rightSpeed = -gamepad1.right_stick_y;
        } else if(gamepad1.a) {
            rightSpeed = 0.75;
        } else if(gamepad1.b) {
            rightSpeed = -0.75;
        } else {
            rightSpeed = 0.0;
        }

        //Controls to put the program in PID MODE (X button)
        if(gamepad1.x && pidMode && x1Available) {
            pidMode = false;
            x1Available = false;
        } else if(gamepad1.x && ! pidMode && x1Available) {
            pidMode = true;
            x1Available = false;
        } else if(! gamepad1.x) {
            x1Available = true;
        }

        /* GAMEPAD2 - ACCESSORIES */

        //Controls for GLYPH PUSHER (gamepad 2 DPad)
        if(gamepad2.dpad_up){
            glyphPusher.setPosition(1.00);
        } else if(gamepad2.dpad_down) {
            glyphPusher.setPosition(0.00);
        }

        //For grabber servos, assuming the moving end is on the outside:
        //On left, 91 is grabbing, 0 is in.
        //On right, 91 is grabbing, 180 is in.

        //Controls for GLYPH GRABBERS, LEFT AND RIGHT (gamepad 2 A & B)
        if(gamepad2.a) { //Grab
            leftGlyphGrabber.setPosition(0.6);
            rightGlyphGrabber.setPosition(0.4);
        } else if(gamepad2.b){ //Release
            leftGlyphGrabber.setPosition(0.00);
            rightGlyphGrabber.setPosition(1.00);
        }

        //Controls for GLYPH LIFT MOTOR (gamepad 2 X & Y)
        if(gamepad2.x) {
            liftSpeed = 1.0;
        } else if(gamepad2.y) {
            liftSpeed = -1.0;
        } else liftSpeed = 0.0;



        //OTHER STUFF


        telemetry.addData("Left motor PID", (leftDrivePID == null)? "Nope" : "Found");
        telemetry.addData("Right motor PID", (rightDrivePID == null)? "Nope" : "Found");
        telemetry.addData("Glyph motor PID", (glyphLiftPID == null)? "Nope" : "Found");

        //Don't forget to actually run the motors. If X has been toggled, we're in PID mode.
        if(pidMode) {
            motorPID(leftDrivePID, toQDPM(leftSpeed));
            motorPID(rightDrivePID, toQDPM(rightSpeed));
            motorPID(glyphLiftPID, toQDPM(liftSpeed));
            telemetry.addData("PID Mode", " enabled");
        } else {
            leftDrive.setPower(leftSpeed);
            rightDrive.setPower(rightSpeed);
            glyphLift.setPower(liftSpeed);
            telemetry.addData("PID Mode", " disabled");
        }


    }

    private void motorPID(PIDMotor m, double tarVel){


        //Just squint your eyes and accept that PIDMotor is a wrapper class of sorts.
        //Sorry.

        //Get the current velocity based on current and past motor positions and elapsed time.
        //... and it's in quarter-degrees per millisecond.
        double vel = (m.motor.getCurrentPosition() - m.pastPos) / (System.currentTimeMillis() - m.pastTime);

        //Get the difference between current velocity and the velocity we want to get to
        double error = tarVel - vel;

        double p = (vel - tarVel);
        m.pastIntegral = (m.pastIntegral + (error * (System.currentTimeMillis() - m.pastTime)));
        double derivative = (m.pastErr - error) / (System.currentTimeMillis() - m.pastTime);

        m.motor.setPower(crunch((KP*p) + (KI*m.pastIntegral) + (KD*derivative), 1.0, 0.0));

        m.pastPos = m.motor.getCurrentPosition();
        m.pastErr = error;

        m.pastTime = System.currentTimeMillis();
    }

    private double crunch(double power, double max, double min){
        if(power > max) {
            return max;
        }else if(power < min){
            return min;
        }else {
            return power;
        }
    }

    private double toQDPM(double percentage){
        //152rpm is 100% speed, give or take.
        return (152*percentage)*1440  /  (60.0 * 1000.0);
        //In case you're interested, the fastest speed this can return is around 3.64 QD/ms,
        //or about 910 degrees per second.
    }
}