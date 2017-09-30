
package de.philippgagel.facerecognition;

import de.philippgagel.facerecognition.imagemanipulation.FRImageRenderer;
import de.philippgagel.facerecognition.ui.FRFrame;
import de.philippgagel.facerecognition.ui.listeners.FRInputListener;
import java.awt.Dimension;

/**
 *
 * @author Philipp Gagel
 */
public class FRMain {
    public static void main(String[] args){
        Dimension size = new Dimension(640, 480);
        String title = "Webcam-Test";
        
        FRImageRenderer renderer = new FRImageRenderer();
        FRFrame frame = new FRFrame(title, size, renderer);
        FRInputListener controller = new FRInputListener(frame);
        
        frame.addKeyListener(controller);
        frame.record();
    }
}
