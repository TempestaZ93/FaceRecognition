package de.philippgagel.facerecognition.camerahandling;

import com.github.sarxos.webcam.Webcam;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * This class uses the default webcam of the host pc and tries to receive images from it.
 * 
 * @author Philipp Gagel
 */
public class FRImageReceiver {
    private static final Logger LOG = Logger.getLogger(FRImageReceiver.class.getName());
    
    // Webcam object to receive the iamges
    private Webcam activeWebcam;
    // Image to save the current image until a new one is ready
    private BufferedImage currentImage;
    // Size of the images that can be requested.
    private Dimension imageSize;
    
    private static FRImageReceiver instance;
    
    /**
     * Creates an ImageReceiver with the highest possible resolution for the default webcam 
     * of the system
     */
    private FRImageReceiver(){
        activeWebcam = Webcam.getDefault();
        
        Dimension[] viewSizes = activeWebcam.getViewSizes();
        int viewSizeNum = viewSizes.length;
        LOG.log(Level.INFO, "Found {0} available camera resolutions.", viewSizeNum);
        imageSize = viewSizes[viewSizeNum-1];
        LOG.log(Level.INFO, "{0} is choosen as the resolution to be used.", viewSizes[viewSizeNum-1]);
        
        activeWebcam.setViewSize(imageSize);
        activeWebcam.open();
        LOG.info("Image Receiver initialized");
    }
    
    /**
     * This method returns the current Image of the webcam
     * 
     * @return 
     */
    public BufferedImage getImage(){
        if(activeWebcam.isImageNew()){
            return (currentImage = activeWebcam.getImage());
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
        return activeWebcam.isImageNew();
    }
    
    public List<String> getWebcams(){
        return Webcam.getWebcams().stream().map(Webcam::getName).collect(Collectors.toList());
    }
    
    public void setWebcam(String name){
        this.activeWebcam = Webcam.getWebcamByName(name);
    }
    
    public String getCurrentWebcam(){
        return this.activeWebcam.getName();
    }
    
    public static FRImageReceiver getInstance(){
        if(instance == null){
            instance = new FRImageReceiver();
        }
        return instance;
    }
            
}
