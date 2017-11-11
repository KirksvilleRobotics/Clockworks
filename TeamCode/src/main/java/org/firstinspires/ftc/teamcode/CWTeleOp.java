package org.firstinspires.ftc.teamcode;

import android.text.method.Touch;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import static com.sun.tools.javac.util.Constants.format;

@TeleOp(name = "Main TeleOp")
public class CWTeleOp extends OpMode {

    private DcMotor leftDrive, rightDrive, glyphLift;
    private Servo jewelPitch, jewelYaw, glyphPusher, leftGlyphGrabber, rightGlyphGrabber;
    private VuforiaLocalizer vuforia;
    private TouchSensor liftAlert;
    private boolean x1Available = true;
    private boolean y1Available = true;
    private boolean leftBumper1Available = true;
    private boolean rightBumper1Available = true;
    private boolean guide1Available = true;

    //Experimental modes; currently set to toggle with GUIDE key
    private boolean vuMode = false;
    private boolean pidMode = false;

    private VuforiaTrackable relicTemplate;

    private int pidTweakMode = 0;
    private int pidMotorMode = 0;

    private int loop = 0;

    private final double THRESHOLD = 0.1;

    private long pastTimeMillis = System.currentTimeMillis();

    //I'm really sorry, but the following is going to be a little disgusting
    //Each motor needs its own PID info associated with it, which means we need an inner class

    private class PIDMotor {
        double pastErr = 0;
        double pastVel = 0;
        double pastPos = 0;
        double pastIntegral = 0;
        double pastTime = 0;
        private double kp = 0.1; //TODO Calculate values
        private double ki = 0.1; // FILLER
        private double kd = 0.1; // FILLER
        DcMotor motor;

        PIDMotor(DcMotor source) {
            motor = source;
        }
    }

    PIDMotor leftDrivePID;
    PIDMotor rightDrivePID;
    PIDMotor glyphLiftPID;

    private double leftSpeed = 0.0;
    private double rightSpeed = 0.0;
    private double liftSpeed = 0.0;

    @Override
    public void init() {

        //Grab the Vuforia parameters
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters vuParameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        //you will forever be accidentaly horizontally scrolling because of this next line
        vuParameters.vuforiaLicenseKey = "AT0wrgH/////AAAAGZ82i6mOOEGnv6hV+FHOAgkR+sNJShoL2nSLxnxgM9dYPkKoFknXp26HuIP0k5wNOjgCsOD7lJwf5SrmxM7mQymx5uAsno1kj7mEwdGDsbwqzjrH6vH1ImHCva/1MS2uWs9H3ADGto3BpIrSr0iglmGQah+eBsPKsoK4qnH5vlNbsg+oU3JE6WehDaOqU7RLU54zT3kfwbRwSsfW1sLoTNIauQZU06V04ObJVjUrorhh2QVQ0blP69upGw0eYXp83P4Fi2IiXhSDlMNbHUTRmG1ZgXxQij/JfSl5tdZRujJcHHs2qnQJh/bZsz4rpmfheglMKPhzJC2/tV0KtO0tCV3Jm23PqG5dcQnWGMxKqWju";
        vuParameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        this.vuforia = ClassFactory.createVuforiaLocalizer(vuParameters);

        //Grab the Vuforia Relic Recovery trackables
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate");

        //Grab motor, servo, and sensors names from the phone config
        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
        jewelPitch = hardwareMap.get(Servo.class, "jewelPitch");
        jewelYaw = hardwareMap.get(Servo.class, "jewelYaw");
        glyphPusher = hardwareMap.get(Servo.class, "glyphPusher");
        glyphLift = hardwareMap.get(DcMotor.class, "glyphLift");
        leftGlyphGrabber = hardwareMap.get(Servo.class, "grabberLeft");
        rightGlyphGrabber = hardwareMap.get(Servo.class, "grabberRight");
        liftAlert = hardwareMap.get(TouchSensor.class, "liftAlert");

        //So directions will be the same for both motors
        rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        leftDrivePID = new PIDMotor(leftDrive);
        rightDrivePID = new PIDMotor(rightDrive);
        glyphLiftPID = new PIDMotor(glyphLift);

        //Woot
        telemetry.addData("Initialized", "Yay");
    }

    @Override
    public void loop() {

        loop++; //Just to keep track

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
         * Gamepad values range from -1.0 at the top position to 1.0 at the bottom position.
         *
         */
        /* GAMEPAD1 - DRIVING */


        //Controls for LEFT MOTOR (gamepad 1 left stick, A, and B)
        if (gamepad1.left_stick_y > THRESHOLD || gamepad1.left_stick_y < -THRESHOLD) {
            telemetry.addData("StickPos:", gamepad1.left_stick_y);
            leftSpeed = -gamepad1.left_stick_y;
        } else if (gamepad1.a)
            leftSpeed = 0.75;
        else if (gamepad1.b) {
            leftSpeed = -0.75;
        } else {
            leftSpeed = 0.0;
        }

        //Controls for RIGHT MOTOR (gamepad 1 right stick, A, and B)
        if (gamepad1.right_stick_y > THRESHOLD || gamepad1.right_stick_y < -THRESHOLD) {
            telemetry.addData("StickPos:", gamepad1.right_stick_y);
            rightSpeed = -gamepad1.right_stick_y;
        } else if (gamepad1.a) {
            rightSpeed = 0.75;
        } else if (gamepad1.b) {
            rightSpeed = -0.75;
        } else {
            rightSpeed = 0.0;
        }

        //Controls to put the program in EXPERIMENTAL MODES (guide button)
        if (gamepad1.guide && pidMode && guide1Available) {
            pidMode = false;
            vuMode = true;
            guide1Available = false;
        } else if (gamepad1.guide && vuMode && guide1Available) {
            pidMode = false;
            vuMode = false;
            guide1Available = false;
        } else if (gamepad1.guide && !vuMode && !pidMode && guide1Available) {
            pidMode = true;
            vuMode = false;
            guide1Available = false;
        } else if (!gamepad1.guide) {
            guide1Available = true;
        }

        //Controls to mess with the PID CONSTANTS (bumpers)
        if (gamepad1.left_bumper && leftBumper1Available) {
            switch (pidTweakMode) {
                case 0: {
                    if (pidMotorMode == 0) leftDrivePID.kp += 0.01;
                    if (pidMotorMode == 1) rightDrivePID.kp += 0.01;
                    if (pidMotorMode == 2) glyphLiftPID.kp += 0.01;
                    break; //Java tip: Without 'break' switches "fall through" and execute all subsequent clauses.
                }
                case 1: {
                    if (pidMotorMode == 0) leftDrivePID.ki += 0.01;
                    if (pidMotorMode == 1) rightDrivePID.ki += 0.01;
                    if (pidMotorMode == 2) glyphLiftPID.ki += 0.01;
                    break;
                }
                case 2: {
                    if (pidMotorMode == 0) leftDrivePID.kd += 0.01;
                    if (pidMotorMode == 1) rightDrivePID.kd += 0.01;
                    if (pidMotorMode == 2) glyphLiftPID.kd += 0.01;
                    break;
                }
            }
            leftBumper1Available = false;
        } else if (!gamepad1.left_bumper) {
            leftBumper1Available = true;
        }

        //Controls to CHANGE which PID CONSTANTS we are messing with (X and Y)
        if (gamepad1.y && y1Available) {
            pidTweakMode++;
            pidTweakMode %= 3;
            y1Available = false;
        } else if (!gamepad1.y) {
            y1Available = true;
        }

        if (gamepad1.x && x1Available) {
            pidMotorMode++;
            pidMotorMode %= 3;
            y1Available = false;
        } else if (!gamepad1.x) {
            x1Available = true;
        }


        /* GAMEPAD2 - ACCESSORIES */

        //Controls for GLYPH PUSHER (gamepad 2 DPad)
        if (gamepad2.dpad_up) {
            glyphPusher.setPosition(1.00);
        } else if (gamepad2.dpad_down) {
            glyphPusher.setPosition(0.00);
        }

        //For grabber servos, assuming the moving end is on the outside:
        //On left, 91deg is grabbing, 0deg is in.
        //On right, 91deg is grabbing, 180deg is in.
        //It turns out that it's nice to have a little extra in both positions, for various reasons.

        //Controls for GLYPH GRABBERS, LEFT AND RIGHT (gamepad 2 A & B)
        if (gamepad2.a) { //Grab
            leftGlyphGrabber.setPosition(0.6);
            rightGlyphGrabber.setPosition(0.4);
        } else if (gamepad2.b) { //Release
            leftGlyphGrabber.setPosition(0.02);
            rightGlyphGrabber.setPosition(0.98);
        }


        //Controls for GLYPH LIFT MOTOR (gamepad 2 X & Y)
        if (gamepad2.x) {
            liftSpeed = 1.0;
        } else if (gamepad2.y) {
            liftSpeed = -1.0;
        } else liftSpeed = 0.0;

        //OTHER STUFF


        //Mess around with Vuforia in experimental mode

        if (vuMode) {
            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);

            if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
                telemetry.addData("VuMark", "%s visible", vuMark);

                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) relicTemplate.getListener()).getPose();
                telemetry.addData("VuMark pose", format(pose));
            }
        }

        //Add telemetry data
        String pidInfo = " ";
        switch (pidTweakMode) {
            case 0:
                pidInfo += "Modifying KP ";
                break;
            case 1:
                pidInfo += "Modifying KI ";
                break;
            case 2:
                pidInfo += "Modifying KD ";
                break;
        }

        switch (pidMotorMode) {
            case 0:
                pidInfo += "on the left motor";
            case 1:
                pidInfo += "on the right motor";
            case 2:
                pidInfo += "on the lift motor";
        }

        String experimentalInfo = " ";
        if (pidMode) experimentalInfo += "PID testing mode";
        else if (vuMode) experimentalInfo += "Vuforia testing mode";
        else experimentalInfo += "None";


        if (pidMode) {
            telemetry.addData("PID tweaking", gamepad1.guide ? pidInfo : " Not modifying constants");
            telemetry.addData("PID constants (right)", " P=" + rightDrivePID.kp + ", I=" + rightDrivePID.ki + ", D=" + rightDrivePID.kd);
            telemetry.addData("Right motor speed", rightDrivePID.pastVel + " QD/ms");
            telemetry.addData("Left motor speed", leftDrivePID.pastVel + " QD/ms");
            telemetry.addData("Lift motor speed", glyphLiftPID.pastVel + " QD/ms");
        }
        telemetry.addData("Experimental", experimentalInfo);
        telemetry.addData("Loop", " " + loop);


        //Don't forget to actually run the motors.
        if (pidMode) {
            motorPID(leftDrivePID, leftSpeed);
            motorPID(rightDrivePID, rightSpeed);
            motorPID(glyphLiftPID, liftSpeed);
            telemetry.addData("PID Mode", " enabled");
        } else {
            leftDrive.setPower(leftSpeed);
            rightDrive.setPower(rightSpeed);
            glyphLift.setPower(liftSpeed);
            telemetry.addData("PID Mode", " disabled");
        }
    }

    // hier Drachen sein
    private void motorPID(PIDMotor m, double tarVel) {
        /*
        //The fastest velocity we can get is about 3.6 QD/ms.
        tarVel *= 3.6;

        //Just squint your eyes and accept that PIDMotor is a wrapper class of sorts.
        //Sorry.

        //Get the current velocity based on current and past motor positions and elapsed time.
        //... and it's in quarter-degrees per millisecond.
        double vel = (m.motor.getCurrentPosition() - m.pastPos) / (System.currentTimeMillis() - m.pastTime);

        //Get the difference between current velocity and the velocity we want to get to
        double error = tarVel - vel;

        //Proportional component: Essentially, undo KP fraction of the error.
        double p = -error;
        //Integral component: Undo the error based on KI fraction of how long and how hard it has existed.
        double i = (m.pastIntegral + (error * (System.currentTimeMillis() - m.pastTime)));
        //Derivative component: Undo the error KD fraction of how fast it is changing (stabilize it).
        double d = (m.pastErr - error) / (System.currentTimeMillis() - m.pastTime);

        m.motor.setPower(crunch((m.kp*p + m.ki*i + m.kd*d), 1.0, 0.0));

        m.pastPos = m.motor.getCurrentPosition();
        m.pastErr = error;
        m.pastVel = vel;
        m.pastIntegral = i;
        m.pastTime = System.currentTimeMillis();

        */
    }

    private double crunch(double power, double max, double min) {
        if (power > max) {
            return max;
        } else if (power < min) {
            return min;
        } else {
            return power;
        }
    }

}