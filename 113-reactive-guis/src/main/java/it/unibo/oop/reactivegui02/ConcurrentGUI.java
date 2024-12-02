package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class ConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.3;
    private static final double HEIGHT_PERC = 0.15;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

     /**
     * Builds a new CGUI.
     */
    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(this.display);
        panel.add(this.up);
        panel.add(this.down);
        panel.add(this.stop);

        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();

        up.addActionListener(event -> agent.startCountingUp());
        down.addActionListener(event -> agent.startCountingDown());
        stop.addActionListener(event -> {
            agent.stopCounting();
            up.setEnabled(false);
            down.setEnabled(false);
            stop.setEnabled(false);
        });

    }

     /*
     * The counter agent is implemented as a nested class. This makes it
     * invisible outside and encapsulated.
     * It manages three buttons:
     * stop, up and down.
     */
    private final class Agent implements Runnable {
        private volatile boolean  stop;
        private boolean up = true;
        private int counter;

        @Override
        public void run() {
                while (!stop) {
                     try {
                        final var nextText = Integer.toString(this.counter);
                        SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                        if (this.up) {
                            this.counter++;
                        } else {
                            this.counter--;
                        }
                        Thread.sleep(100);
                    } catch (InvocationTargetException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }
        /**
         * External command to start counting up.
         */
        public void startCountingUp() {
            this.up = true;
        }

        /**
         * External command to start counting down.
         */ 
        public void startCountingDown() {
            this.up = false;
        }
    }

}
