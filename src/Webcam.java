import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_features2d.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Webcam {
    private static void captureFrame() {
        // 0-default camera, 1 - next...so on
        final OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        CvScalar min = cvScalar(0, 0, 130, 0);
        CvScalar max= cvScalar(140, 110, 255, 0);

        try {
            grabber.start();
            IplImage img = grabber.grab();
            // create image window named "My Image"
            final CanvasFrame canvas = new CanvasFrame("My Image");

            while (img != null) {                                
                // request closing of the application when the image window is closed
                canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                cvFlip(img, img, 1);
                IplImage imgThreshold = cvCreateImage(cvGetSize(img), 8, 1);
                //apply thresholding
                cvInRangeS(img, min, max, imgThreshold);
                //smooth filter- median
                cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 13);
                // show image on window
                canvas.showImage(imgThreshold);
                img = grabber.grab();
//              cvSaveImage("capture.jpg", img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        captureFrame();
    }
    
}