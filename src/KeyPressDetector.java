import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class KeyPressDetector {
  public static void main(String args[]) {
    JFrame frame = new JFrame();
    Container contentPane = frame.getContentPane();
    ActionListener listener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("Command: " + e.getActionCommand());
        int modifiers = e.getModifiers();
        System.out.println("\tALT : "
            + checkMod(modifiers, ActionEvent.ALT_MASK));
        System.out.println("\tCTRL : "
            + checkMod(modifiers, ActionEvent.CTRL_MASK));
        System.out.println("\tMETA : "
            + checkMod(modifiers, ActionEvent.META_MASK));
        System.out.println("\tSHIFT: "
            + checkMod(modifiers, ActionEvent.SHIFT_MASK));
        Object source = e.getSource();
        if (source instanceof JComboBox) {
          JComboBox jb = (JComboBox) source;
          System.out.println("Combo: " + jb.getSelectedItem());
        }
      }

      private boolean checkMod(int modifiers, int mask) {
        return ((modifiers & mask) == mask);
      }
    };

    String flavors[] = { "Item 1", "Item 2", "Item 3"};
    JComboBox jc = new JComboBox(flavors);
    jc.setMaximumRowCount(4);
    jc.setEditable(true);
    jc.addActionListener(listener);
    contentPane.add(jc, BorderLayout.NORTH);

    JButton b = new JButton("Button!");
    b.addActionListener(listener);
    contentPane.add(b, BorderLayout.CENTER);

    JPanel panel = new JPanel();
    JLabel label = new JLabel("Label 1: ");
    JTextField text = new JTextField("Type your text", 15);
    text.addActionListener(listener);
    label.setDisplayedMnemonic(KeyEvent.VK_1);
    label.setLabelFor(text);
    panel.add(label);
    panel.add(text);
    contentPane.add(panel, BorderLayout.SOUTH);

    frame.pack();
    frame.setVisible(true);
  }
}