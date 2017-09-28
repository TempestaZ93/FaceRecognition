
package de.philippgagel.facerecognition.imagemanipulation;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Philipp Gagel
 */
public class FRManipulator {
    private int maskSize;
    private int sensitivity;
    
    public FRManipulator(int maskSize, int sensitivity){
        this.maskSize = maskSize;
        this.sensitivity = sensitivity;
    }

    public int getMaskSize() {
        return maskSize;
    }

    public void setMaskSize(int maskSize) {
        this.maskSize = maskSize;
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
    }
        
    public BufferedImage findEdges(BufferedImage src){
        BufferedImage out;
        
        if(src != null) out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        else return new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        
        for(int y = 0; y < src.getHeight(); y++){
            for(int x = 0; x < src.getWidth(); x++){
                int[] resColor = calculatePixel(src, x, y);
                
                if(resColor!=null){      
                    out.setRGB(x, y, new Color(resColor[0], resColor[1], resColor[2]).getRGB());
                }
            }
        }
        
        return out;
    }
    
    private int[] calculatePixel(BufferedImage img, int x, int y){
        
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
            int[] resColor = new int[3];
            
            img.getRaster().getPixels(startX, startY, width, height, pixels);
                        
            // Sum up top row
            int[] tr = pixelSum(pixels, 0, 0, width, 1, width);

            // Sum up bottom row
            int[] br = pixelSum(pixels, 0, height-1, width, 1, width);

            // Sum up left col
            int[] lc = pixelSum(pixels, 0, 0, 1, height, width);

            // Sum up right col
            int[] rc = pixelSum(pixels, width - 1, 0, 1, height, width);

            // Sum up top right corner
            int[] trc = pixelSum(pixels, width - width/2, 0, width/2, height/2, width);

            // Sum up bottom left corner
            int[] blc = pixelSum(pixels, width - width/2, height - height/2, width/2, height/2, width);

            // Sum up top left corner
            int[] tlc = pixelSum(pixels, 0, 0, width/2, height/2, width);

            // Sum up bottom right corner
            int[] brc = pixelSum(pixels, 0, height - 1, width, 1, width);

            int[] diffTB, diffRL, diffD1, diffD2;
            
            diffTB = new int[3];
            diffRL = new int[3];
            diffD1 = new int[3];
            diffD2 = new int[3];

            for(int i = 0; i < 3; i++){
                diffTB[i] = Math.abs(tr[i] - br[i]);
                diffRL[i] = Math.abs(lc[i] - rc[i]);
                diffD1[i] = Math.abs(trc[i] - blc[i]);
                diffD2[i] = Math.abs(tlc[i] - brc[i]);
                
                if(diffTB[i] > this.sensitivity){
                    resColor[i] += 64;
                }
                if(diffRL[i] > this.sensitivity){
                    resColor[i] += 64;
                }
                if(diffD1[i] > this.sensitivity){
                    resColor[i] += 64;
                }
                if(diffD2[i] > this.sensitivity){
                    resColor[i] += 64;
                }
            }
            
            resColor[0] = resColor[0] > 128 ? 255 : 0;
            resColor[1] = resColor[1] > 128 ? 255 : 0;
            resColor[2] = resColor[2] > 128 ? 255 : 0;
            
            return resColor;
        }
        return null;
    }
    
    private int[] pixelSum(int [] pixels, int startX, int startY, int width, int height, int maskW){
        int[] sums = new int[3];
        
        sums[0] = 0;
        sums[1] = 0;
        sums[2] = 0;
        
        for(int y = startY; y < startY + height; y++){
            for(int x = startX; x < startX + width; x++){
                sums[0] += pixels[x*3 + y*maskW];
                sums[1] += pixels[x*3 + y*maskW*3 + 1];
                sums[2] += pixels[x*3 + y*maskW*3 + 2];
            }
        }
        
        return sums;
    }
}
