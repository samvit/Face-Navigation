import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.cvClone;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvFont;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

public class Tracking implements KeyListener {
	static OpenCVFrameGrabber capture;
	static IplImage frame, gray, prev, diff, tpl;
	static CvMemStorage storage;
	static IplConvKernel kernel;
	static CvFont font;

	static final int TPL_WIDTH = 16;
	static final int TPL_HEIGHT = 12;
	static final int WIN_WIDTH = TPL_WIDTH * 2;
	static final int WIN_HEIGHT = TPL_HEIGHT * 2;
	static final double TM_THRESHOLD = 0.4;
	static final double SMOOTHING_FACTOR = 0.2;
	static final int STAGE_INIT = 1;
	static final int STAGE_TRACKING = 2;

	static Robot robot;
	static Dimension screenSize, canvasSize;
	
	static long lastKeyPress = System.currentTimeMillis();

	private static void startTracking() throws AWTException {        
		// 0 = default camera, 1 = next, etc.
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		robot = new Robot();
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		try {
			grabber.start();
			IplImage img = grabber.grab();
			final CanvasFrame canvas = new CanvasFrame("Tracking Feed");
			storage = CvMemStorage.create();

			Point currentMouse = MouseInfo.getPointerInfo().getLocation();
			
			while (img != null) {
				canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
				canvasSize = canvas.getCanvasSize();
				
				cvFlip(img, img, 1);
				detectEyes(img, currentMouse);
				detectProfile(img);
				currentMouse = MouseInfo.getPointerInfo().getLocation();
				canvas.showImage(img);
				img = grabber.grab();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void detectProfile(IplImage img) {
		CvHaarClassifierCascade cascade = null;
		storage = null; // new CvMemStorage();
		
		if (cascade == null) {
			String file = "/usr/local/Cellar/opencv/2.3.1a/share/OpenCV/haarcascades/haarcascade_profileface.xml";
			cascade = new CvHaarClassifierCascade(cvLoad(file));
			storage = cvCreateMemStorage(0);
		}
		
		if (img.nChannels() == 1) {
			gray = (IplImage) cvClone(img);
		} else {
			gray = cvCreateImage(cvGetSize(img), img.depth(), 1);
			cvCvtColor(img, gray, CV_RGB2GRAY);
		}

		CvSeq faces = cvHaarDetectObjects(gray, cascade, storage, 2.5, 4, CV_HAAR_DO_CANNY_PRUNING, cvSize(15, 15), cvSize(25, 25));

		for (int i = 0; i < ((faces != null) ? faces.total() : 0); i++) {
			CvRect r = new CvRect(cvGetSeqElem(faces, i));
			cvRectangle(img, cvPoint(r.x(), r.y()), cvPoint(r.x() + r.width(), r.y() + r.height()), CV_RGB(255, 0, 0), 1, 8, 0);
			
			if (i == 0 && System.currentTimeMillis() - lastKeyPress > 1000) {
				lastKeyPress = System.currentTimeMillis();
				robot.keyPress(KeyEvent.VK_BACK_QUOTE);
				robot.keyRelease(KeyEvent.VK_BACK_QUOTE);
			}
		}
	}

	public static void detectEyes(IplImage img) {
		CvHaarClassifierCascade cascade = null;
		storage = null; // new CvMemStorage();
		
		if (cascade == null) {
			String file = "/usr/local/Cellar/opencv/2.3.1a/share/OpenCV/haarcascades/haarcascade_profileface.xml";
			cascade = new CvHaarClassifierCascade(cvLoad(file));
			storage = cvCreateMemStorage(0);
		}
		
		if (img.nChannels() == 1) {
			gray = (IplImage) cvClone(img);
		} else {
			gray = cvCreateImage(cvGetSize(img), img.depth(), 1);
			cvCvtColor(img, gray, CV_RGB2GRAY);
		}

		CvSeq faces = cvHaarDetectObjects(gray, cascade, storage, 2.5, 4, CV_HAAR_DO_CANNY_PRUNING, cvSize(15, 15), cvSize(25, 25));

		for (int i = 0; i < ((faces != null) ? faces.total() : 0); i++) {
			CvRect r = new CvRect(cvGetSeqElem(faces, i));
			cvRectangle(img, cvPoint(r.x(), r.y()), cvPoint(r.x() + r.width(), r.y() + r.height()), CV_RGB(255, 0, 0), 1, 8, 0);
			
			if (i == 0) {
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			}
		}
	}

	public static void detectEyes(IplImage img, Point prevMouse) {
		CvHaarClassifierCascade cascade = null;
		storage = null; // new CvMemStorage();

		if (cascade == null) {
			String file = "/usr/local/Cellar/opencv/2.3.1a/share/OpenCV/haarcascades/haarcascade_mcs_eyepair_big.xml";
			cascade = new CvHaarClassifierCascade(cvLoad(file));
			storage = cvCreateMemStorage(0);
		}

		if (img.nChannels() == 1) {
			gray = (IplImage) cvClone(img);
		} else {
			gray = cvCreateImage(cvGetSize(img), img.depth(), 1);
			cvCvtColor(img, gray, CV_RGB2GRAY);
		}

		int xPos = 0, yPos = 0;

		CvSeq faces = cvHaarDetectObjects(gray, cascade, storage, 1.5, 5, CV_HAAR_DO_CANNY_PRUNING + 5, cvSize(20, 20), cvSize(20, 20));
		
		for (int i = 0; i < ((faces != null) ? faces.total() : 0); i++) {
			CvRect r = new CvRect(cvGetSeqElem(faces, i));
			cvRectangle(img, cvPoint(r.x(), r.y()), cvPoint(r.x() + r.width(), r.y() + r.height()), CV_RGB(255, 0, 0), 1, 8, 0);
			
			if (i == 0) {
				double bound = 0.4;
				
				if (r.x() + r.width() / 2 < bound * canvasSize.width) {
					xPos = 0;
				} else if (r.x() + r.width() / 2 > (1 - bound) * canvasSize.width) {
					xPos = screenSize.width;
				} else {
					xPos = (int) ((r.x() + r.width() / 2 + 0.0 - bound * canvasSize.width) / ((1 - 2 * bound) * canvasSize.width) * screenSize.width);
				}

				if (r.y() + r.height() / 2 < bound * canvasSize.height) {
					yPos = 0;
				} else if (r.y() + r.height() / 2 > (1 - bound) * canvasSize.height) {
					yPos = screenSize.height;
				} else {
					yPos = (int) ((r.y() + r.height() / 2 + 0.0 - bound * canvasSize.height) / ((1 - 2 * bound) * canvasSize.height) * screenSize.height);
				}

				// Smooth mouse movement using a Low Pass Filter (Expected Weighted Moving Average)
				xPos = (int) (SMOOTHING_FACTOR * xPos + (1 - SMOOTHING_FACTOR) * prevMouse.x + (1 - SMOOTHING_FACTOR) * (1 - SMOOTHING_FACTOR));
				yPos = (int) (SMOOTHING_FACTOR * yPos + (1 - SMOOTHING_FACTOR) * prevMouse.y + (1 - SMOOTHING_FACTOR) * (1 - SMOOTHING_FACTOR));
				
				robot.mouseMove(xPos, yPos);
			}
		}
	}

	public static void main(String[] args) throws AWTException {
		startTracking();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println(e.getKeyCode());
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
