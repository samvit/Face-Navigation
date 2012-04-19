import java.awt.AWTException;
import java.awt.Robot;

public class RobotTest {
	public static void main(String[] args) throws AWTException {
		Robot r = new Robot();
		int count = 0;
		while (count < 50000) {
			r.mouseMove(0, 0);
			count++;
		}
	}
}
