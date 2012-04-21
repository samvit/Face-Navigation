//imports
import static com.googlecode.javacv.cpp.opencv_core.cvCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetCentralMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetSpatialMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMoments;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.InputEvent;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

public class ObjectPositionDetect {
    static int hueLowerR = 160;
    static int hueUpperR = 180;

    public static void main(String[] args) throws Exception, AWTException {
    	final OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
    	Robot robot = new Robot();
    	grabber.start();
        IplImage img = grabber.grab();
        CanvasFrame canvas = new CanvasFrame("My Image");
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        while (img != null) {
	        img = grabber.grab();
        	cvFlip(img, img, 1);

//	        cvSaveImage("hsvthreshold.jpg", thresholdImage);
        	
	        IplImage thresholdImage = hsvThreshold(img);
	        Dimension r = getCoordinates(thresholdImage);
	        	        
	        CvPoint center = new CvPoint(r.width, r.height);
	        int radius = 10;
	        cvCircle(thresholdImage, center, 3, CvScalar.RED, -1, 8, 0);
	        cvCircle(thresholdImage, center, radius, CvScalar.BLUE, 3, 8, 0);
	        
	        canvas.showImage(thresholdImage);
	        
	        if (center.y() != 0 || center.x() != 0) {
    			robot.mousePress(InputEvent.BUTTON1_MASK);
	        } else {
    			robot.mouseRelease(InputEvent.BUTTON1_MASK);
	        }
	        
//	        if (center.y() <= 240 && center.y() > 0) {
//	        	robot.mouseWheel(-5);
//	        } else if (center.y() > 240) {
//	        	robot.mouseWheel(5);
//	        }

//	    	CvRect r = new CvRect(cvGetSeqElem(faces, i));
//	    	cvRectangle(img, cvPoint(r.x(), r.y()), cvPoint(r.x() + r.width(), r.y() + r.height()), CV_RGB(255, 0, 0), 1, 8, 0);
//	        System.out.println("Dimension of original Image : " + thresholdImage.width() + " , " + thresholdImage.height());
//	        System.out.println("Position of red spot    : x : " + position.width + " , y : " + position.height);
        }
    }

    static Dimension getCoordinates(IplImage thresholdImage) {
        int posX = 0;
        int posY = 0;
        CvMoments moments = new CvMoments();
        cvMoments(thresholdImage, moments, 1);
        // cv Spatial moment : Mji=sumx,y(I(x,y)¥xj¥yi)
        // where I(x,y) is the intensity of the pixel (x, y).
        double momX10 = cvGetSpatialMoment(moments, 1, 0); // (x,y)
        double momY01 = cvGetSpatialMoment(moments, 0, 1);// (x,y)
        double area = cvGetCentralMoment(moments, 0, 0);
        posX = (int) (momX10 / area);
        posY = (int) (momY01 / area);
        return new Dimension(posX, posY);
    }

    static IplImage hsvThreshold(IplImage orgImg) {
        // 8-bit, 3- color =(RGB)
    	System.out.println(cvGetSize(orgImg));
        IplImage imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);
        System.out.println(cvGetSize(orgImg));
        cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
        // 8-bit 1- color = monochrome
        IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        // cvScalar : ( H , S , V, A)
        cvInRangeS(imgHSV, cvScalar(hueLowerR, 100, 100, 0), cvScalar(hueUpperR, 120, 120, 0), imgThreshold);
        cvReleaseImage(imgHSV);
        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 13);
        // save
        return imgThreshold;
    }
}
