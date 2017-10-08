package de.philippgagel.facerecognition;

import de.philippgagel.facerecognition.camerahandling.FRImageReceiver;
import de.philippgagel.facerecognition.camerahandling.WebcamNotFoundException;
import de.philippgagel.facerecognition.imagemanipulation.FRImageRenderer;
import de.philippgagel.facerecognition.ui.FRFrame;
import de.philippgagel.facerecognition.ui.listeners.FRInputListener;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Philipp Gagel
 */
public class FRMain {
    public static void main(String[] args) {
        Dimension size = new Dimension(640, 480);
        String title = "Webcam-Test";
        
        try {
            FRImageReceiver.getInstance();
            FRImageRenderer renderer = new FRImageRenderer();
            FRFrame frame = new FRFrame(title, size, renderer);
            FRInputListener controller = new FRInputListener(frame);

            frame.addKeyListener(controller);
            frame.record();
        } catch (WebcamNotFoundException ex) {
            JOptionPane.showMessageDialog(new JFrame(), "No Camera found", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }
}
