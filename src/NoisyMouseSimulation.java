import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

public class NoisyMouseSimulation {
  public static void main(String[] args) throws AWTException {
    final double NOISE_VALUE = .05;
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

      rob.mouseMove((int) (currentX), (int) (currentY));
      
      System.out.println(prevMouse.toString());
      System.out.println("Noise Added" +((Math.random() - 0.5) * noise) + .5);
    }
  }
}
