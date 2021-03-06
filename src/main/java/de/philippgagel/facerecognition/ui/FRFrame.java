
package de.philippgagel.facerecognition.ui;

import de.philippgagel.facerecognition.camerahandling.FRImageReceiver;
import de.philippgagel.facerecognition.imagemanipulation.FRManipulator;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Philipp Gagel
 */
public class FRFrame extends JFrame{

    private static final Logger LOG = Logger.getLogger(FRFrame.class.getName());
    
    private FRDisplay display;
    private FRImageReceiver receiver;
    private FRManipulator manipulator;
    private Queue<BufferedImage> trippleBuffer;
    private boolean recording;
    private int sensitivity;
    
    public FRFrame(String title, Dimension size, FRImageReceiver receiver){
        this.receiver = receiver;
        this.display = new FRDisplay(size);
        this.manipulator = new FRManipulator(5);
        this.trippleBuffer = new ArrayDeque<>(3);
        this.recording = true;
        this.sensitivity = 50;
        
        JSlider sensitivitySlider = new JSlider(0, 255);
        sensitivitySlider.setPaintTicks(true);
        sensitivitySlider.setPaintLabels(true);
        sensitivitySlider.setMajorTickSpacing(64);
        sensitivitySlider.setMinorTickSpacing(32);
        sensitivitySlider.setExtent(2);
        sensitivitySlider.setComponentPopupMenu(new JPopupMenu("Threshold of the algorithm."));
        sensitivitySlider.setValue(this.sensitivity);
        sensitivitySlider.addChangeListener((ChangeEvent e) -> {
            this.sensitivity = sensitivitySlider.getValue();
        });
        
        JSlider maskSizeSlider = new JSlider(3, 15);
        maskSizeSlider.setPaintTicks(true);
        maskSizeSlider.setPaintLabels(true);
        maskSizeSlider.setSnapToTicks(true);
        maskSizeSlider.setMajorTickSpacing(2);
        maskSizeSlider.setExtent(1);
        maskSizeSlider.setComponentPopupMenu(new JPopupMenu("Size of the mask used to find the edges."));
        maskSizeSlider.setValue(this.manipulator.getMaskSize());
        maskSizeSlider.addChangeListener((ChangeEvent e) -> {
            this.manipulator.setMaskSize(maskSizeSlider.getValue());
        });
        
        super.setTitle(title);
        super.setSize(size);
        super.setLocationRelativeTo(null);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.insets = new Insets(5,5,5,5);
        gbc.weightx = .5;
        gbc.weightx = .5;
        gbc.weighty = .1;
        
        super.getContentPane().add(sensitivitySlider, gbc);

        gbc.gridx = 1; 
        
        super.getContentPane().add(maskSizeSlider, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = .9;
        
        super.getContentPane().add(this.display, gbc);
        
        super.pack();
        super.setVisible(true);
        LOG.info("Frame initilized.");
    }
    
    public final void record(){
        while(this.recording){
            if(this.trippleBuffer.size() < 3){
                this.trippleBuffer.offer(this.manipulator.findEdges(this.receiver.getImage(), this.sensitivity));
            }else{
                this.display.setImage(trippleBuffer.poll());
                this.trippleBuffer.offer(this.manipulator.findEdges(this.receiver.getImage(), this.sensitivity));
                this.display.repaint();
            }
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

