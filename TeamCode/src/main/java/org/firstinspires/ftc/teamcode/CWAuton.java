package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

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

    private DcMotor leftDrive, rightDrive, glyphLift;
    private TouchSensor liftAlert;
    private static final double DEG_PER_REV = 1440; //technically quarter-degrees
    private static final double GEAR_RATIO = 1.0;
    private static final double WHEEL_DIAM = 4.0; //in inches
    private static final double DEG_PER_INCH = (DEG_PER_REV * GEAR_RATIO) / (WHEEL_DIAM * Math.PI);
    private static final double DRIVE_SPEED = 0.75;
    private static final double TURN_SPEED = 0.5;

    private Servo jewelPitch, jewelYaw, glyphPusher, leftGlyphGrabber, rightGlyphGrabber;
    private VuforiaLocalizer vuforia;
    private VuforiaTrackable relicTemplate;

    public void runOpMode(){
        /* INITIALIZATION */
        //Grab the Vuforia parameters
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters vuParameters = new VuforiaLocalizer.Parameters (cameraMonitorViewId);
        //you will forever be accidentaly horizontally scrolling because of this next line
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
        glyphPusher = hardwareMap.get(Servo.class, "glyphPusher");
        glyphLift = hardwareMap.get(DcMotor.class, "glyphLift");
        leftGlyphGrabber = hardwareMap.get(Servo.class, "grabberLeft");
        rightGlyphGrabber = hardwareMap.get(Servo.class, "grabberRight");
        liftAlert = hardwareMap.get(TouchSensor.class, "liftAlert");

        //So directions will be the same for both motors
        rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        //Woot
        telemetry.addData("Initialized", "Yay");
    }

    // distances should be in inches
    // speed should usually be a constant
    public void encoderDrive(double rightDis, double leftDis, double speed){

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

    public void knockJewel(){

    }

}
