
package de.philippgagel.facerecognition.imagemanipulation;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Philipp Gagel
 */
public class FRManipulator {
    private int maskSize;
    
    public FRManipulator(int maskSize){
        this.maskSize = maskSize;
    }

    public int getMaskSize() {
        return maskSize;
    }

    public void setMaskSize(int maskSize) {
        this.maskSize = maskSize;
    }
    
    public BufferedImage findEdges(BufferedImage src, int sensitivity){
        BufferedImage out;
        
        if(src != null) out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        else return new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        
        for(int y = 0; y < src.getHeight(); y++){
            for(int x = 0; x < src.getWidth(); x++){
                int value = calculatePixel(src, x, y);
                out.setRGB(x, y, value>sensitivity ? Color.white.getRGB() : 0);
            }
        }
        
        return out;
    }
    
    private int calculatePixel(BufferedImage img, int x, int y){
        
        int startX, startY;
        int width, height;
        
        startX = x - this.maskSize << 1;
        startY = y - this.maskSize << 1;
        
        startX = startX < 0 ? 0 : startX;
        startY = startY < 0 ? 0 : startY;
        
        width = startX + maskSize >= img.getWidth() ? img.getWidth() - startX : maskSize;
        height = startY + maskSize >= img.getHeight() ? img.getHeight() - startY : maskSize;
        
        if(width > 0 && height > 0){
            int[] pixels = new int[width * height * 3];
            
            img.getRaster().getPixels(startX, startY, width, height, pixels);

            // Sum up top row
            int tr = pixelSum(pixels, 0, 0, width, 1, width);

            // Sum up bottom row
            int br = pixelSum(pixels, 0, height-1, width, 1, width);

            // Sum up left col
            int lc = pixelSum(pixels, 0, 0, 1, height, width);

            // Sum up right col
            int rc = pixelSum(pixels, width - 1, 0, 1, height, width);

            // Sum up top right corner
            int trc = pixelSum(pixels, width - width/2, 0, width/2, height/2, width);

            // Sum up bottom left corner
            int blc = pixelSum(pixels, width - width/2, height - height/2, width/2, height/2, width);

            // Sum up top left corner
            int tlc = pixelSum(pixels, 0, 0, width/2, height/2, width);

            // Sum up bottom right corner
            int brc = pixelSum(pixels, 0, height - 1, width, 1, width);

            int diffTB, diffRL, diffD1, diffD2;

            diffTB = Math.abs(tr-br);
            diffRL = Math.abs(rc-lc);
            diffD1 = Math.abs(trc-blc);
            diffD2 = Math.abs(tlc-brc);

            return Math.max(Math.max(Math.max(diffTB, diffRL), diffD1), diffD2);
        }
        return 0;
    }
    
    private int pixelSum(int [] pixels, int startX, int startY, int width, int height, int maskW){
        int res = 0;
        
        for(int y = startY; y < startY + height; y++){
            for(int x = startX; x < startX + width; x++){
                res += pixels[x*3 + y*maskW];
                res += pixels[x*3 + y*maskW*3 + 1];
                res += pixels[x*3 + y*maskW*3 + 2];
            }
        }
        
        return res / (width * height);
    }
}
