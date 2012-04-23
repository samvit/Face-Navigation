import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

public class MouseSmoother {
  public static void main(String[] args) throws AWTException {
    // THIS IS THE SAME AS NoisyMouse, BUT ITS SMOOTHED WITH A LOW PASS FILTER
    final double NOISE_VALUE = .05;
    double gamma = .80;
    Point currentMouse = MouseInfo.getPointerInfo().getLocation();
    Robot rob = new Robot();
    rob.mouseMove((int) currentMouse.getX(), (int) currentMouse.getY());
    Point prevMouse;
    double currentX, prevX, currentY, prevY;
    // dont manipulate this line, use the const defined above ^
    double noise = 1 + NOISE_VALUE; 
    while (true) {
      prevMouse = currentMouse;
      prevX = prevMouse.x;
      prevY = prevMouse.y;
      currentMouse = MouseInfo.getPointerInfo().getLocation();
      currentX = currentMouse.x + ((Math.random() - 0.5) * noise) + .5;
      currentY = currentMouse.y + ((Math.random() - 0.5) * noise) + .5;

      // NOW APPLY THE LOW PASS FILTER
      currentX = (int) (gamma * currentX + (1 - gamma) * prevX + (1 - gamma)
          * (1 - gamma));
      currentY = (int) (gamma * currentY + (1 - gamma) * prevY + (1 - gamma)
          * (1 - gamma));

      rob.mouseMove((int) (currentX), (int) (currentY));
      System.out.println(prevMouse.toString());
      System.out.println("Noise Added" +((Math.random() - 0.5) * noise) + .5);
    }
  }
}
