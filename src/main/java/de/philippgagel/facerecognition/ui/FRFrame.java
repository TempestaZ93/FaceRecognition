
package de.philippgagel.facerecognition.ui;

import de.philippgagel.facerecognition.picture.FRImageReceiver;
import java.awt.Dimension;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Philipp Gagel
 */
public class FRFrame extends JFrame{

    private static final Logger LOG = Logger.getLogger(FRFrame.class.getName());
    
    private FRDisplay display;
    private FRImageReceiver receiver;
    private boolean recording;
    
    public FRFrame(String title, Dimension size, FRImageReceiver receiver){
        this.receiver = receiver;
        this.display = new FRDisplay();
        this.recording = true;
        
        super.setTitle(title);
        super.setSize(size);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.getContentPane().add(this.display);
        super.pack();
        super.setVisible(true);
        LOG.info("Frame initilized.");
    }
    
    public final void record(){
        while(this.recording){
            this.display.setImage(receiver.getImage());
            this.display.repaint();
        }
    }
    
    public void setRecording(boolean recording){
        if(recording && !this.recording){
            this.recording = recording;
            record();
        }
        this.recording = recording;
    }

    public boolean isRecording() {
        return recording;
    }
    
    
}

