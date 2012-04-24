import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.CV_SEQ_CHAIN_CONTOUR;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_32F;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvClone;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvMinMaxLoc;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRect;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvScalarAll;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_core.cvSub;
import static com.googlecode.javacv.cpp.opencv_core.cvZero;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MOP_OPEN;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_CCOMP;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_TM_SQDIFF_NORMED;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvBoundingRect;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMatchTemplate;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMorphologyEx;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.MouseInfo;
import java.awt.Point;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvArr;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvFont;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

public class Webcam {
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
  static final int STAGE_INIT = 1;
  static final int STAGE_TRACKING	= 2;

  static Robot robot;
  static Dimension screenSize, canvasSize;

  private static void captureFrame() throws AWTException {
    // 0-default camera, 1 - next...so on
    final OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
    //        CvScalar min = cvScalar(80, 100, 100, 0);
    //        CvScalar max= cvScalar(100, 255, 255, 0);

    CvSeq comp = new CvSeq(null);
    CvRect	window = new CvRect(), eye = new CvRect();
    int		key, nc, found; 
    int		text_delay, stage = STAGE_INIT;
    int frameNumber = 0;

    robot = new Robot();
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    try {
      grabber.start();
      IplImage img = grabber.grab();
      // create image window named "My Image"
      final CanvasFrame canvas = new CanvasFrame("My Image");
      storage = CvMemStorage.create();

      /* kernel = cvCreateStructuringElementEx(3, 3, 1, 1, CV_SHAPE_CROSS, null);
         gray   = cvCreateImage(cvGetSize(img), 8, 1);
         prev   = cvCreateImage(cvGetSize(img), 8, 1);
         diff   = cvCreateImage(cvGetSize(img), 8, 1);
         tpl	   = cvCreateImage(cvSize(TPL_WIDTH, TPL_HEIGHT), 8, 1);

         gray.origin(img.origin());
         prev.origin(img.origin());
         diff.origin(img.origin()); */

      double gamma = .80;
      Point currentMouse = MouseInfo.getPointerInfo().getLocation();
      Point prevMouse;
      while (img != null) {                                
        // request closing of the application when the image window is closed
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        cvFlip(img, img, 1);
        //                detectFaces(img);
        //                IplImage imgThreshold = cvCreateImage(cvGetSize(img), 8, 1);
        //                //apply thresholding
        //                cvInRangeS(img, min, max, imgThreshold);
        //                //smooth filter- median
        //                cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 13);
        // show image on window
        /* img.origin(0);

           if (stage == STAGE_INIT) {
           window = cvRect(0, 0, img.width(), img.height());
           }

           cvCvtColor(img, gray, CV_BGR2GRAY);
           nc = getConnectedComponents(gray, prev, window, comp);

           System.out.println("nc: " + nc);
           System.out.println(stage);

           if (stage == STAGE_INIT && isEyePair(comp, nc, eye) != 0)
           {
           System.out.println("isEyePair == 1!");
           cvSetImageROI(gray, eye);
        //        			cvCopy(gray, tpl, null);
        cvResetImageROI(gray);

        stage = STAGE_TRACKING;
        text_delay = 10;
           }

           if (stage == STAGE_TRACKING)
           {
           found = locateEye(gray, tpl, window, eye);

           if (found == 0)
           stage = STAGE_INIT;

           if (isBlink(comp, nc, window, eye) != 0) {
           text_delay = 10;
           }

           drawRect(img, diff, window, eye);
           }

        //              cvShowImage(wnd_name, frame);
        //        		cvShowImage(wnd_debug, diff);
        prev = new IplImage(cvClone(gray));
        */
        canvasSize = canvas.getCanvasSize();

        //                if (frameNumber % 10 == 0) {

       prevMouse = currentMouse;
        detectNose(img , prevMouse, .8);
       currentMouse = MouseInfo.getPointerInfo().getLocation();
        //                }
        //                detectEyes(img);

        canvas.showImage(img);
        //                canvas.setBounds(0, 0, 500, 500);
        img = grabber.grab();
        frameNumber++;
        //              cvSaveImage("capture.jpg", img);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Initialize images, memory, and windows
   * @throws AWTException 
   */
  //    public static void init() {
  //    	String[] msg = { "Blink Detection 1.0", 
  //    					"Copyright (c) 2009", 
  //    					"http://nashruddin.com", 
  //    					"Press 'q' to quit...",
  //    					"Press 'r' to restart...",
  //    					"Have fun!" };
  //    	int delay, i;
  //
  //    	
  //    	capture = new OpenCVFrameGrabber(0);
  //    	capture.start();
  //    	frame = capture.grab();
  //
  //    	cvInitFont(font, CV_FONT_HERSHEY_SIMPLEX, 0.4, 0.4, 0, 1, 8);
  //    	cvNamedWindow("Name", 1);
  //
  //    	for (delay = 20, i = 0; i < 6; i++, delay = 20)
  //    		while (delay)
  //    		{
  //    			frame = capture.grab();
  ////    			DRAW_TEXT(frame, msg[i], delay, 0);
  //    			canvas.sh
  //    			cvShowImage("Name", frame);
  //    			cvWaitKey(30);
  //    		}
  //
  //    	storage = cvCreateMemStorage(0);
  //
  //    	kernel = cvCreateStructuringElementEx(3, 3, 1, 1, CV_SHAPE_CROSS, null);
  //    	gray   = cvCreateImage(cvGetSize(frame), 8, 1);
  //    	prev   = cvCreateImage(cvGetSize(frame), 8, 1);
  //    	diff   = cvCreateImage(cvGetSize(frame), 8, 1);
  //    	tpl	   = cvCreateImage(cvSize(TPL_WIDTH, TPL_HEIGHT), 8, 1);
  //
  //    	gray.origin(frame.origin());
  //    	prev.origin(frame.origin());
  //    	diff.origin(frame.origin());
  //
  //    	cvNamedWindow("Name", 1);
  //    }


  public static void main(String[] args) throws AWTException {
    captureFrame();
  }

  public static void drawRect(IplImage f, IplImage d, CvRect rw, CvRect ro) {
    System.out.println("drawing rectangle");
    cvRectangle(f, POINT_TL(rw), POINT_BR(rw), CV_RGB(255, 0, 0), 1, 8, 0);
    cvRectangle(f, POINT_TL(ro), POINT_BR(ro), CV_RGB(0, 255, 0), 1, 8, 0);
    cvRectangle(d, POINT_TL(rw), POINT_BR(rw), cvScalarAll(255),  1, 8, 0);
    cvRectangle(d, POINT_TL(ro), POINT_BR(ro), cvScalarAll(255),  1, 8, 0);
  }

  public static CvPoint POINT_TL(CvRect r) {
    return cvPoint(r.x(), r.y());
  }

  public static CvPoint POINT_BR(CvRect r) {
    return cvPoint(r.x() + r.width(), r.y() + r.height());
  }

  public static int detectEyes(IplImage img) {
    CvHaarClassifierCascade cascade = null; // new CvHaarClassifierCascade();
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

    CvSeq faces = cvHaarDetectObjects(gray, cascade, storage, 2.5, 4, CV_HAAR_DO_CANNY_PRUNING, cvSize(15,15), cvSize(25,25));

    for (int i = 0; i < ((faces != null) ? faces.total() : 0); i++) {
      CvRect r = new CvRect(cvGetSeqElem(faces, i));
      cvRectangle(img, cvPoint(r.x(), r.y()), cvPoint(r.x() + r.width(), r.y() + r.height()), CV_RGB(255, 0, 0), 1, 8, 0);
      if (i == 0) {
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        //    			System.out.println("canvas Width: " + canvasSize.width);
        //    			System.out.println("screen Width: " + screenSize.width);
        //    			System.out.println(((r.x() + r.width()/2 + 0.0) / canvasSize.width) * screenSize.width);
        //    			System.out.println(((r.y() + r.height()/2 + 0.0) / canvasSize.height) * screenSize.height);
        //    			robot.mouseMove((int) (((r.x() + r.width()/2 + 0.0) / canvasSize.width) * screenSize.width), (int) (((r.y() + r.height()/2 + 0.0) / canvasSize.height) * screenSize.height) );
      }
    }

    return faces.total();
  }

  public static int detectNose(IplImage img , Point prevMouse, double gamma) {
    CvHaarClassifierCascade cascade = null; // new CvHaarClassifierCascade();
    storage = null; // new CvMemStorage();

    if (cascade == null) {
      String file = "/usr/local/Cellar/opencv/2.3.1a/share/OpenCV/haarcascades/haarcascade_mcs_nose.xml";
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

    CvSeq faces = cvHaarDetectObjects(gray, cascade, storage, 1.5, 10, CV_HAAR_DO_CANNY_PRUNING+5, cvSize(10,10), cvSize(10,10));
    for (int i = 0; i < ((faces != null) ? faces.total() : 0); i++) {
      CvRect r = new CvRect(cvGetSeqElem(faces, i));
      cvRectangle(img, cvPoint(r.x(), r.y()), cvPoint(r.x() + r.width(), r.y() + r.height()), CV_RGB(255, 0, 0), 1, 8, 0);
      if (i == 0) {
        //    			System.out.println("canvas Width: " + canvasSize.width);
        //    			System.out.println("screen Width: " + screenSize.width);
        //    			System.out.println( (int) (((r.x() + r.width()/2 + 0.0) / canvasSize.width) * screenSize.width) );
        //    			System.out.println( (int) (((r.y() + r.height()/2 + 0.0) / canvasSize.height) * screenSize.height) );
        double bound = 0.4;
        if (r.x() + r.width()/2 < bound * canvasSize.width) {
          xPos = 0;
        } else if (r.x() + r.width()/2 > (1-bound) * canvasSize.width) {
          xPos = screenSize.width;
        } else {
          xPos = (int) ((r.x() + r.width()/2 + 0.0 - bound * canvasSize.width) / ((1-2*bound) * canvasSize.width) * screenSize.width);
        }

        if (r.y() + r.height()/2 < bound * canvasSize.height) {
          yPos = 0;
        } else if (r.y() + r.height()/2 > (1-bound) * canvasSize.height) {
          yPos = screenSize.height;
        } else {
          yPos = (int) ((r.y() + r.height()/2 + 0.0 - bound * canvasSize.height) / ((1-2*bound) * canvasSize.height) * screenSize.height);
        }

        /* smoothing */
      double prevX = prevMouse.x;
      double prevY = prevMouse.y;
      double currentX = xPos;
      double currentY = yPos;
      // NOW APPLY THE LOW PASS FILTER
      currentX = (int) (gamma * currentX + (1 - gamma) * prevX + (1 - gamma) * (1 - gamma));
      currentY = (int) (gamma * currentY + (1 - gamma) * prevY + (1 - gamma) * (1 - gamma));
      /* end smoothing */
      robot.mouseMove((int) (currentX), (int) (currentY));
      }
    }

    return faces.total();
  }

  public static int getConnectedComponents(IplImage img, IplImage prev, CvRect window, CvSeq comp) {
    IplImage _diff;
    cvZero(diff);

    /* apply search window to images */
    //    	System.out.println(img);
    //    	System.out.println(window);
    cvSetImageROI(img, window);
    cvSetImageROI(prev, window);
    cvSetImageROI(diff, window);

    /* motion analysis */
    cvSub(img, prev, diff, null);
    cvThreshold(diff, diff, 5, 255, CV_THRESH_BINARY);
    cvMorphologyEx(diff, diff, null, kernel, CV_MOP_OPEN, 1);

    /* reset search window */
    cvResetImageROI(img);
    cvResetImageROI(prev);
    cvResetImageROI(diff);

    _diff = new IplImage(cvClone(diff));

    System.out.println("sizeof: " + Loader.sizeof(CvContour.class));
    System.out.println("CV_SEQ_CHAIN_CONTOUR: " + CV_SEQ_CHAIN_CONTOUR);

    int nc = cvFindContours(_diff, storage, comp, CV_SEQ_CHAIN_CONTOUR, 
        CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));

    //    	System.out.println("comp in 2: " + comp);

    cvClearMemStorage(storage);
    cvReleaseImage(_diff);

    return nc;
  }


  /**
   * Experimentally-derived heuristics to determine whether
   * the connected components are eye pair or not.
   *
   * @param	CvSeq*  comp the connected components
   * @param	int     num  the number of connected components
   * @param   CvRect* eye  output parameter, will contain the location of the 
   *                       first component
   * @return	int          '1' if eye pair, '0' otherwise
   */
  public static int isEyePair(CvSeq comp, int num, CvRect eye) {
    System.out.println("comp: " + comp);
    System.out.println("num: " + num);

    if (comp == null) {
      System.out.println("terminate 1");
      return 0;
    }

    CvRect r1 = cvBoundingRect(comp, 1);
    comp = comp.h_next();

    if (comp == null) {
      System.out.println("terminate 2");
      return 0;
    }

    CvRect r2 = cvBoundingRect(comp, 1);

    /* the width of the components are about the same */
    if (Math.abs(r1.width() - r2.width()) >= 5) {
      System.out.println("terminate 3");
      return 0;
    }

    /* the height f the components are about the same */
    if (Math.abs(r1.height() - r2.height()) >= 5) {
      System.out.println("terminate 4");
      return 0;
    }

    /* vertical distance is small */
    if (Math.abs(r1.y() - r2.y()) >= 5) {
      System.out.println("terminate 5");
      return 0;
    }

    /* reasonable horizontal distance, based on the components' width */
    int dist_ratio = Math.abs(r1.x() - r2.x()) / r1.width();
    if (dist_ratio < 2 || dist_ratio > 5) {
      System.out.println("terminate 6");
      return 0;
    }

    /* get the centroid of the 1st component */
    CvPoint point = cvPoint(r1.x() + (r1.width() / 2), r1.y() + (r1.height() / 2));

    /* return eye boundaries */
    eye = cvRect(point.x() - (TPL_WIDTH / 2), point.y() - (TPL_HEIGHT / 2), TPL_WIDTH, TPL_HEIGHT);

    return 1;
  }

  /**
   * Locate the user's eye with template matching
   *
   * @param	IplImage* img     the source image
   * @param	IplImage* tpl     the eye template
   * @param	CvRect*   window  search within this window,
   *                            will be updated with the recent search window
   * @param	CvRect*   eye     output parameter, will contain the current
   *                            location of user's eye
   * @return	int               '1' if found, '0' otherwise
   */
  public static int locateEye(IplImage img, IplImage tpl, CvRect window, CvRect eye) {
    IplImage	tm;
    CvRect		win;
    CvPoint		minloc = new CvPoint(0,0), maxloc = new CvPoint(500,500), point;
    double[] minval = null, maxval = null;
    int			w, h;

    /* get the centroid of eye */
    point = cvPoint(
        eye.x() + eye.width() / 2, 
        eye.y() + eye.height() / 2
        );

    /* setup search window 
       replace the predefined WIN_WIDTH and WIN_HEIGHT above 
       for your convenient */
    win = cvRect(point.x() - WIN_WIDTH / 2, point.y() - WIN_HEIGHT / 2, WIN_WIDTH, WIN_HEIGHT);

    /* make sure that the search window is still within the frame */
    if (win.x() < 0)
      win.x(0);
    if (win.y() < 0)
      win.y(0);
    if (win.x() + win.width() > img.width())
      win.x(img.width() - win.width());
    if (win.y() + win.height() > img.height())
      win.y(img.height() - win.height());

    /* create new image for template matching result where: 
       width  = W - w + 1, and
       height = H - h + 1 */
    w  = win.width()  - tpl.width()  + 1;
    h  = win.height() - tpl.height() + 1;
    tm = cvCreateImage(cvSize(w, h), IPL_DEPTH_32F, 1);

    /* apply the search window */
    cvSetImageROI(img, win);

    /* template matching */
    cvMatchTemplate(img, tpl, tm, CV_TM_SQDIFF_NORMED);
    cvMinMaxLoc(new CvArr(tm), minval, maxval, minloc, maxloc, null);

    /* release things */
    cvResetImageROI(img);
    cvReleaseImage(tm);

    /* only good matches */
    //    	if (minval[0] > TM_THRESHOLD) {
    //    		return 0;
    //    	}

    /* return the search window */
    window = win;

    /* return eye location */
    eye = cvRect(win.x() + minloc.x(), win.y() + minloc.y(), TPL_WIDTH, TPL_HEIGHT);

    return 1;
  }

  public static int isBlink(CvSeq comp, int num, CvRect window, CvRect eye) {
    if (comp == null || num != 1) {
      return 0;
    }

    CvRect r1 = cvBoundingRect(comp, 1);

    /* component is within the search window */
    if (r1.x() < window.x())
      return 0;
    if (r1.y() < window.y())
      return 0;
    if (r1.x() + r1.width() > window.x() + window.width())
      return 0;
    if (r1.y() + r1.height() > window.y() + window.height())
      return 0;

    /* get the centroid of eye */
    CvPoint pt = cvPoint(
        eye.x() + eye.width() / 2,
        eye.y() + eye.height() / 2
        );

    /* component is located at the eye's centroid */
    if (pt.x() <= r1.x() || pt.x() >= r1.x() + r1.width())
      return 0;
    if (pt.y() <= r1.y() || pt.y() >= r1.y() + r1.height())
      return 0;

    return 1;
  }

}
