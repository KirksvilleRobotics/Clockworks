package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

@Autonomous(name = "Base Autonomous")
@Disabled
public abstract class CWAuton extends LinearOpMode{
    // THIS CLASS SHOULD NOT BE RUN!
    // This is a class for the more specific auton programs to borrow methods from.

    private DcMotor leftDrive, rightDrive, glyphWinch;
    private ColorSensor jewelCol;
    private static final double ROBOT_DIAM = 12.5;
    private static final double DEG_PER_REV = 1440; //technically quarter-degrees
    private static final double GEAR_RATIO = 1.0;
    private static final double WHEEL_DIAM = 4.0; //in inches
    private static final double DEG_PER_INCH = (DEG_PER_REV * GEAR_RATIO) / (WHEEL_DIAM * Math.PI);

    public Servo jewelPitch, jewelYaw, glyphPusher, leftGlyphGrabber, rightGlyphGrabber;
    private VuforiaLocalizer vuforia;
    private VuforiaTrackable relicTemplate;

    public void runOpMode(){
        /* INITIALIZATION */
        //Grab the Vuforia parameters
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters vuParameters = new VuforiaLocalizer.Parameters (cameraMonitorViewId);
        //you will forever be accidentally horizontally scrolling because of this next line
        vuParameters.vuforiaLicenseKey = "AT0wrgH/////AAAAGZ82i6mOOEGnv6hV+FHOAgkR+sNJShoL2nSLxnxgM9dYPkKoFknXp26HuIP0k5wNOjgCsOD7lJwf5SrmxM7mQymx5uAsno1kj7mEwdGDsbwqzjrH6vH1ImHCva/1MS2uWs9H3ADGto3BpIrSr0iglmGQah+eBsPKsoK4qnH5vlNbsg+oU3JE6WehDaOqU7RLU54zT3kfwbRwSsfW1sLoTNIauQZU06V04ObJVjUrorhh2QVQ0blP69upGw0eYXp83P4Fi2IiXhSDlMNbHUTRmG1ZgXxQij/JfSl5tdZRujJcHHs2qnQJh/bZsz4rpmfheglMKPhzJC2/tV0KtO0tCV3Jm23PqG5dcQnWGMxKqWju";
        vuParameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        this.vuforia = ClassFactory.createVuforiaLocalizer(vuParameters);

        //Grab the Vuforia Relic Recovery trackables
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate");

        //Grab motor, servo, and sensors names for the phone config
        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
        jewelPitch = hardwareMap.get(Servo.class, "jewelPitch");
        jewelYaw = hardwareMap.get(Servo.class, "jewelYaw");
        glyphWinch = hardwareMap.get(DcMotor.class, "glyphLift");

        //So directions will be the same for both motors
        rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        // color sensor
        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);
        jewelCol = hardwareMap.get(ColorSensor.class, "jewelCol");
        jewelCol.enableLed(true);

        //Woot
        telemetry.addData("Initialized", "Yay");
    }

    // distances should be in inches
    // speed should usually be a constant
    public void encoderDrive(double leftDis, double rightDis, double speed){

        // the distance the encoders will run in 1/4 degrees
        int leftTar = leftDrive.getCurrentPosition() + (int)(leftDis * DEG_PER_INCH);
        int rightTar = rightDrive.getCurrentPosition() + (int)(rightDis * DEG_PER_INCH);

        // put targets into encoders
        leftDrive.setTargetPosition(leftTar);
        rightDrive.setTargetPosition(rightTar);

        // encoders will run till there target is met
        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // go!
        leftDrive.setPower(speed);
        rightDrive.setPower(speed);

        // loop while robot is moving
        while(opModeIsActive() && (leftDrive.isBusy() || rightDrive.isBusy())) {
            telemetry.addData("Left running to:", leftDis);
            telemetry.addData("Right running to:", rightDis);
            telemetry.update();
        }

        // stop
        leftDrive.setPower(0.0);
        rightDrive.setPower(0.0);

        // switch to turn off RUN_TO_POSITION
        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public static double turnDis(double frac){
        return ROBOT_DIAM * Math.PI * frac;
    }

    //TODO
    public boolean detectJewel(){

        jewelPitch.setPosition(2); //TODO
        jewelYaw.setPosition(2); //TODO
        boolean red = false;
        if(jewelCol.red()>jewelCol.blue()){
            red = true;
        }

        return red;
    }

    public boolean colorIsRed() {
        float[] hsvValues = {0.0f, 0.0f, 0.0f};
        Color.RGBToHSV(jewelCol.red() * 8, jewelCol.green() * 8, jewelCol.blue() * 8, hsvValues);
        if (hsvValues[0] > 0 && hsvValues[0] < 15) {
            return true;
        }
        return false;
    }
}