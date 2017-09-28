
package de.philippgagel.facerecognition.picture;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Philipp Gagel
 */
public class FRImageReceiver {
    private static final Logger LOG = Logger.getLogger(FRImageReceiver.class.getName());
    
    private String path;
    private List<File> images;
    
    public FRImageReceiver(String path){
        this.path = path;
        this.images = new LinkedList<>();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<File> getImages() {
        return images;
    }
}
