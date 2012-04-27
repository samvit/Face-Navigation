import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class MouseClicker {
	
	public static void main(String[] args) throws AWTException {
		Robot robot = new Robot();
		
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	public static void mouseClick() throws AWTException {
		Robot robot = new Robot();
		
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

}
