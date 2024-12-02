package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class AnotherConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.3;
    private static final double HEIGHT_PERC = 0.15;
    private long time = TimeUnit.SECONDS.toMillis(10);

    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

    final CountAgent agent = new CountAgent();
    final InterruptAgent interAgent = new InterruptAgent();

     /**
     * Builds a new CGUI.
     */
    public AnotherConcurrentGUI() {
        super();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        

        final JPanel panel = new JPanel();
        panel.add(this.display);
        panel.add(this.up);
        panel.add(this.down);
        panel.add(this.stop);

        this.getContentPane().add(panel);
        this.setVisible(true);

        
        new Thread(this.agent).start();
        new Thread(this.interAgent).start();

        up.addActionListener(event -> agent.startCountingUp());
        down.addActionListener(event -> agent.startCountingDown());
        stop.addActionListener(event -> {
            agent.stopCounting();
            up.setEnabled(false);
            down.setEnabled(false);
            stop.setEnabled(false);
        });

    }

    private final class InterruptAgent implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(time);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            AnotherConcurrentGUI.this.agent.stopCounting();
            AnotherConcurrentGUI.this.up.setEnabled(false);
            AnotherConcurrentGUI.this.down.setEnabled(false);
            AnotherConcurrentGUI.this.stop.setEnabled(false);
        }  
    }

     /*
     * The counter agent is implemented as a nested class. This makes it
     * invisible outside and encapsulated.
     * It manages three buttons:
     * stop, up and down.
     */
    private final class CountAgent implements Runnable {
        private volatile boolean  stop;
        private boolean up = true;
        private int counter;

        @Override
        public void run() {
                while (!stop) {
                     try {
                        final var nextText = Integer.toString(this.counter);
                        SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
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
