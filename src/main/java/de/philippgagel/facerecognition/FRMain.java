
package de.philippgagel.facerecognition;

import de.philippgagel.facerecognition.picture.FRImageReceiver;
import de.philippgagel.facerecognition.ui.FRFrame;
import de.philippgagel.facerecognition.ui.listeners.FRInputListener;
import java.awt.Dimension;

/**
 *
 * @author Philipp Gagel
 */
public class FRMain {
    public static void main(String[] args){
        Dimension size = new Dimension(800, 600);
        String title = "Webcam-Test";
        
        FRImageReceiver receiver = new FRImageReceiver(size);
        FRFrame frame = new FRFrame(title, size, receiver);
        FRInputListener controller = new FRInputListener(frame);
        
        frame.addKeyListener(controller);
        frame.record();
    }
}
