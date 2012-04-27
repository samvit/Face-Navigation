//imports
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
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
import java.util.ArrayList;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

public class TongueTracker {
    static int hueLowerR = 160;
    static int hueUpperR = 170;
    static CanvasFrame canvas = new CanvasFrame("My Image");
    static ArrayList<String> list = new ArrayList<String>();

    public static void main(String[] args) throws Exception, AWTException, InterruptedException {
    	final OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
    	Robot robot = new Robot();
    	grabber.start();
        IplImage img = grabber.grab(), noTongue = null, tongue = null;
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        
//        long startTime = System.currentTimeMillis();
//        
//        while (System.currentTimeMillis() - startTime < 3000) {
//        	System.out.println("Getting no tongue out pic now!");
//        	noTongue = grabber.grab();
//        }
//        
//        startTime = System.currentTimeMillis();
//        
//        while (System.currentTimeMillis() - startTime < 3000) {
//        	System.out.println("Getting tongue out pic now!");
//        	tongue = grabber.grab();
//        }
//        
//        findOptimalValues(noTongue, tongue);
//        System.out.println(list.toString());
//        Thread.sleep(10000);
        
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
    
    static void findOptimalValues(IplImage noTongue, IplImage tongue) throws InterruptedException {
    	for (int x = 0; x <= 256; x += 20) {
    		for (int y = 0; y <= 256; y += 20) {
    			for (int z = 0; z <= 256; z += 20) {
    				System.out.println("Starting x: " + x + ", y: " + y + ", z: " + z);
//    		        IplImage imgNoTongueHSV = cvCreateImage(cvGetSize(noTongue), 8, 3);
//    		        cvCvtColor(noTongue, imgNoTongueHSV, CV_BGR2HSV);
//    		        IplImage imgNoTongueThreshold = cvCreateImage(cvGetSize(noTongue), 8, 1);
//    		        // cvScalar : ( H , S , V, A)
//    		        cvInRangeS(imgNoTongueHSV, cvScalar(x, y, z, 1.0), cvScalar(x+20, y+20, z+20, 1.0), imgNoTongueThreshold);
//    		        cvReleaseImage(imgNoTongueHSV);
//    		        cvSmooth(imgNoTongueThreshold,imgNoTongueThreshold, CV_MEDIAN, 13);
//    		        Dimension noTongueDimension = getCoordinates(imgNoTongueThreshold);
//    		        CvPoint noTongueCenter = new CvPoint(noTongueDimension.width, noTongueDimension.height);
    		        
//    		        if (noTongueCenter.x() == 0 && noTongueCenter.y() == 0) {
    		        	System.out.println("Inside first if!");
    		        	IplImage imgTongueHSV = cvCreateImage(cvGetSize(tongue), 8, 3);
        		        cvCvtColor(noTongue, imgTongueHSV, CV_BGR2HSV);
        		        IplImage imgTongueThreshold = cvCreateImage(cvGetSize(tongue), 8, 1);
        		        // cvScalar : ( H , S , V, A)
        		        cvInRangeS(imgTongueHSV, cvScalar(x, y, z, 0), cvScalar(x+30, y+30, z+30, 0), imgTongueThreshold);
        		        cvReleaseImage(imgTongueHSV);
        		        cvSmooth(imgTongueThreshold,imgTongueThreshold, CV_MEDIAN, 13);
        		        Dimension tongueDimension = getCoordinates(imgTongueThreshold);
        		        CvPoint tongueCenter = new CvPoint(tongueDimension.width, tongueDimension.height);
        		        
        		        if (tongueCenter.x() != 0 && tongueCenter.y() != 0) {
        		        	System.out.println("Found it at x: " + tongueCenter.x() + ", y: " + tongueCenter.y());
        		        	System.out.println("x: " + x + ", y: " + y + ", z: " + z);
        		        	list.add("(" + x + ", " + y + ", " + z + ")");
        		        	canvas.showImage(imgTongueThreshold);
//        		        	Thread.sleep(2000);
        		        }
//    		        }
    			}
    		}
    	}
    	
//    	return new int[6];
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
    	CvSize size = cvGetSize(orgImg);
        IplImage imgHSV = cvCreateImage(size, IPL_DEPTH_8U, 3);
//        System.out.println(cvGetSize(orgImg));
        cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
        // 8-bit 1- color = monochrome
        IplImage imgThreshold = cvCreateImage(size, IPL_DEPTH_8U, 1);
        IplImage imgThreshold2 = cvCreateImage(size, IPL_DEPTH_8U, 1);
        
        IplImage hsvMask = cvCreateImage(size, IPL_DEPTH_8U, 1);
    	CvScalar  hsv_min = cvScalar(0, 30, 80, 0);
    	CvScalar  hsv_max = cvScalar(20, 150, 255, 0);
    	
//    	cvInRangeS(imgHSV, hsv_min, hsv_max, imgThreshold);
        
        // cvScalar : ( H , S , V, A)
//        cvInRangeS(imgHSV, cvScalar(hueLowerR, 60, 100, 0), cvScalar(hueUpperR, 90, 120, 30), imgThreshold);
        cvInRangeS(imgHSV, cvScalar(hueLowerR, 100, 100, 0), cvScalar(hueUpperR, 255, 255, 0), imgThreshold);
//        cvInRangeS(imgHSV, cvScalar(120, 60, 100, 0), cvScalar(150, 90, 130, 0), imgThreshold);
//        cvInRangeS(orgImg, cvScalar(170, 50, 170, 0), cvScalar(256, 180, 256, 0), imgThreshold2);
//        cvOr(imgThreshold, imgThreshold2, imgThreshold, null);
//        cvReleaseImage(imgHSV);
        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 13);
        // save
        return imgThreshold;
    }
}

class Points {
	int x, y, z;
	
	public Points(int x, int y, int z) {
		x = this.x;
		y = this.y;
		z = this.z;
	}
	
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
