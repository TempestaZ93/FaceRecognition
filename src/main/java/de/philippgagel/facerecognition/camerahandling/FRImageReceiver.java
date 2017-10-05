package de.philippgagel.facerecognition.camerahandling;

import com.github.sarxos.webcam.Webcam;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * This class uses the default webcam of the host pc and tries to receive images from it.
 * 
 * @author Philipp Gagel
 */
public class FRImageReceiver {
    private static final Logger LOG = Logger.getLogger(FRImageReceiver.class.getName());
    
    // Webcam object to receive the iamges
    private Webcam webcam;
    // Image to save the current image until a new one is ready
    private BufferedImage currentImage;
    // Size of the images that can be requested.
    private Dimension imageSize;
    
    /**
     * Creates an ImageReceiver with the highest possible resolution for the default webcam 
     * of the system
     */
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
    
    /**
     * This method returns the current Image of the webcam
     * 
     * @return 
     */
    public BufferedImage getImage(){
        if(webcam.isImageNew()){
            return (currentImage = webcam.getImage());
        }
        return currentImage;
    }

    /**
     * This method returns the dimensions of the videostream sent by the camera
     * 
     * @return 
     */
    public Dimension getImageSize() {
        return imageSize;
    }
    
    public boolean isNewImageAvailable(){
        return webcam.isImageNew();
    }
    
    
}
