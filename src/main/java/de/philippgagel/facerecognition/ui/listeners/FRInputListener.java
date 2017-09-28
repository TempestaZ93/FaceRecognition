
package de.philippgagel.facerecognition.ui.listeners;

import de.philippgagel.facerecognition.ui.FRFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.logging.Logger;

/**
 *
 * @author Philipp Gagel
 */
public class FRInputListener implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener{

    FRFrame frame;
    
    private static final Logger LOG = Logger.getLogger(FRInputListener.class.getName());

    public FRInputListener(FRFrame frame){
        this.frame = frame;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyChar()){
            case 'r':
                frame.setRecording(!frame.isRecording());
                break;
            default:
                break;
        }
        
        
    }
    @Override
    public void keyReleased(KeyEvent e) {
        
    }

}
