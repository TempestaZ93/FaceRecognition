package de.philippgagel.facerecognition.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Date;
import javax.swing.JPanel;

/**
 *
 * @author Philipp Gagel
 */
public class FRDisplay extends JPanel{

    private BufferedImage image;
    
    private int FPS;
    private int fpsCounter;
    private long fpsTimeMark;
    
    public FRDisplay(Dimension size){
        fpsTimeMark = new Date().getTime();
        super.setPreferredSize(size);
    }    
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if(image != null) g.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), null);
        
        char[] fps = Integer.toString(calcFPS()).toCharArray();
        g.setColor(Color.red);
        g.setFont(new Font("Times New Roman", Font.BOLD, 25));
        g.drawChars(fps, 0, fps.length, 30, 30);
        
    }
    
    private int calcFPS(){
        long now = new Date().getTime();
        
        if(now > fpsTimeMark + 1000){
            FPS = fpsCounter;
            fpsCounter = 0;
            fpsTimeMark = now;
        }else{
            fpsCounter++;
        }
        
        return FPS;
    }
    
    public void setImage(BufferedImage image){
        this.image = image;
    }
    
    
}
