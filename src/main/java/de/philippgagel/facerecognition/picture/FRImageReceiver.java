
package de.philippgagel.facerecognition.picture;

import com.github.sarxos.webcam.Webcam;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

/**
 *
 * @author Philipp Gagel
 */
public class FRImageReceiver {
    private static final Logger LOG = Logger.getLogger(FRImageReceiver.class.getName());
    
    private Webcam webcam;
    
    public FRImageReceiver(Dimension size){
        webcam = Webcam.getDefault();
        webcam.open();
        webcam.setCustomViewSizes(new Dimension[] {size});
        LOG.info("Image Receiver initialized");
    }
    
    public BufferedImage getImage(){
        return webcam.getImage();
    }
}
