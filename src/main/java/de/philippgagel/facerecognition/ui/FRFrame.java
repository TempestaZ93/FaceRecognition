package de.philippgagel.facerecognition.ui;

import de.philippgagel.facerecognition.camerahandling.FRImageReceiver;
import de.philippgagel.facerecognition.camerahandling.WebcamNotFoundException;
import de.philippgagel.facerecognition.imagemanipulation.FRImageRenderer;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Philipp Gagel
 */
public class FRFrame extends JFrame{

    private static final Logger LOG = Logger.getLogger(FRFrame.class.getName());
    
    private FRDisplay display;
    private FRImageRenderer renderer;
    private boolean recording;
    private int fps;
    
    private final int sliderStart = 5;
    private final int sliderEnd = 50;
    private final int sliderTicks = 5;
    
    public FRFrame(String title, Dimension size, int fps,FRImageRenderer renderer) throws WebcamNotFoundException{
        this.renderer = renderer;
        this.display = new FRDisplay(size);
        this.recording = true;
        this.fps = fps;
        
        super.addWindowStateListener((WindowEvent e) -> {
            if(e.getID() == WindowEvent.WINDOW_CLOSED){
                renderer.stop();
            }
        });
        
        FRImageReceiver receiver;
        receiver = FRImageReceiver.getInstance();
        
        JSlider sensitivitySlider = new JSlider(sliderStart, sliderEnd);
        sensitivitySlider.setPaintTicks(true);
        sensitivitySlider.setPaintLabels(true);
        sensitivitySlider.setMajorTickSpacing(sliderTicks);
        sensitivitySlider.setExtent(2);
        sensitivitySlider.setComponentPopupMenu(new JPopupMenu("Threshold of the algorithm."));
        sensitivitySlider.setValue(this.renderer.getSensitivity());
        sensitivitySlider.addChangeListener((ChangeEvent e) -> {
            this.renderer.setSensitivity(sensitivitySlider.getValue());
        });
        
        
        String[] cams = new String[receiver.getWebcams().size()];
        receiver.getWebcams().toArray(cams);
        javax.swing.JList<String> camSelector = new javax.swing.JList<>(cams);
        camSelector.setDragEnabled(false);
        camSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        camSelector.setSelectedIndex(cams.length-1);
        camSelector.addListSelectionListener(e ->{
            receiver.setWebcam(cams[e.getFirstIndex()]);
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
        gbc.weightx = .8;
        gbc.weighty = .1;
        
        super.getContentPane().add(sensitivitySlider, gbc);

        gbc.gridx = 1; 
        gbc.weightx = .2;
        
        super.getContentPane().add(camSelector, gbc);
        
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
        long lastAskedTime = System.currentTimeMillis();
        int timebetweenFrames = (int)(1f/fps * 1000);
        
        while(this.recording){
            if(this.renderer.isNewImageAvailable()){
                if(System.currentTimeMillis() > lastAskedTime + timebetweenFrames){
                    this.display.setImage(this.renderer.getImage());
                    this.display.repaint();
                    lastAskedTime = System.currentTimeMillis();
                }
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

