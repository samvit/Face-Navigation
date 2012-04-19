import com.googlecode.javacv.CanvasFrame;
import static com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

public class MyFirstJavaCVApp {

    public static void main(String[] args) {
        
        // read an image
        final IplImage image = cvLoadImage("face.png");
        
        // create image window named "My Image"
        final CanvasFrame canvas = new CanvasFrame("My Image");
        
        // request closing of the application when the image window is closed
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                
        // show image on window
        canvas.showImage(image);
    }
}