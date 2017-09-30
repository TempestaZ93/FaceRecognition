
package de.philippgagel.facerecognition.camerahandling;

import com.github.sarxos.webcam.Webcam;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Philipp Gagel
 */
public class FRImageReceiver {
    private static final Logger LOG = Logger.getLogger(FRImageReceiver.class.getName());
    
    private Webcam webcam;
    private BufferedImage currentImage;
    private Dimension imageSize;
    
    public FRImageReceiver(){
        webcam = Webcam.getDefault();
        
        Dimension[] viewSizes = webcam.getViewSizes();
        int viewSizeNum = viewSizes.length;
        LOG.log(Level.INFO, "Found {0} available camera resolutions.", viewSizeNum);
        imageSize = viewSizes[viewSizeNum-1];
        LOG.log(Level.INFO, "{0} is choosen as the resolution to be used.", viewSizes[viewSizeNum-1]);
        
        webcam.setViewSize(imageSize);
        webcam.open();
        LOG.info("Image Receiver initialized");
    }
    
    public BufferedImage getImage(){
        if(webcam.isImageNew()){
            return (currentImage = webcam.getImage());
        }
        return currentImage;
    }

    public Dimension getImageSize() {
        return imageSize;
    }
    
    
}
