
package de.philippgagel.facerecognition.imagemanipulation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 *
 * @author Philipp Gagel
 */
public class FRImageManipulator {
    private int maskSize;
    private int sensitivity;

    private int[][] maskHorizontal;
    private int[][] maskVertical;
    private int[][] maskDiagonal45;
    private int[][] maskDiagonal135;
    
    public FRImageManipulator(int maskSize, int sensitivity){
        this.maskSize = maskSize;
        this.sensitivity = sensitivity;
        
        this.maskHorizontal = new int[][] {
            {0, 0, 0},
            {-4, 0, 4},
            {0, 0, 0},
        };
        this.maskVertical = new int[][] {
            {0, 4, 0},
            {0, 0, 0},
            {0, -4, 0},
        };
        this.maskDiagonal45 = new int[][] {
            {4, 0, 0},
            {0, 0, 0},
            {0, 0, -4},
        };
        this.maskDiagonal135 = new int[][] {
            {0, 0, -4},
            {0, 0, 0},
            {4, 0, 0},
        };
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
                if(resColor!=null) out.setRGB(x, y, new Color(resColor[0], resColor[1], resColor[2]).getRGB());
            }
        }
        
        return out;
    }
    
    public BufferedImage cancelNoise(BufferedImage src){
        BufferedImage out;
        
        if(src != null) out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        else return new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        
        for(int y = 0; y < src.getHeight(); y++){
            for(int x = 0; x < src.getWidth(); x++){
                int[] resColor = adaptiveMedian(src, x, y);
                
                if(resColor != null) out.setRGB(x, y, new Color(resColor[0], resColor[1], resColor[2]).getRGB());
                else out.setRGB(x, y, Color.white.getRGB());
            }
        }
        
        return out;
    }
  
      private int[] adaptiveMedian(BufferedImage img, int x, int y){
        int startX, startY;
        int width, height;
        
        startX = x - (3 << 1);
        startY = y - (3 << 1);
        
        startX = startX < 0 ? 0 : startX;
        startY = startY < 0 ? 0 : startY;
        
        width = startX + 3 >= img.getWidth() ? img.getWidth() - startX : 3;
        height = startY + 3 >= img.getHeight() ? img.getHeight() - startY : 3;
        
        if(width > 0 && height > 0){
            int[] pixels = new int[width * height * 3];
            int[] resColor = new int[3];
        
            img.getRaster().getPixels(startX, startY, width, height, pixels);
        
            int[][] splitPixels = splitColors(pixels, width*height);
            
            Arrays.sort(splitPixels[0]);
            Arrays.sort(splitPixels[1]);
            Arrays.sort(splitPixels[2]);
            
            resColor[0] = splitPixels[0][width*height/2];
            resColor[1] = splitPixels[1][width*height/2];
            resColor[2] = splitPixels[2][width*height/2];
            
            return resColor;
        }
        return null;
    }
    
    private int[][] splitColors(int[] pixels, int pixelNum){
        int[][] splitColors = new int[3][pixelNum];
        int pixelCounter = 0;
        
        for(int i = 0; i< pixels.length; pixelCounter++){
            splitColors[0][pixelCounter] = pixels[i++];
            splitColors[1][pixelCounter] = pixels[i++];
            splitColors[2][pixelCounter] = pixels[i++];
        }
        
        return splitColors;
    }
    
    
    private int[] calculatePixel(BufferedImage img, int x, int y){
        
        int startX, startY;
        int width, height;
        
        startX = x - (3 << 1);
        startY = y - (3 << 1);
        
        startX = startX < 0 ? 0 : startX;
        startY = startY < 0 ? 0 : startY;
        
        width = startX + 3 >= img.getWidth() ? img.getWidth() - startX : 3;
        height = startY + 3 >= img.getHeight() ? img.getHeight() - startY : 3;
        
        
        if(width > 0 && height > 0){
            int[] pixels = new int[width * height * 3];
            int[] resColor = new int[3];
            
            img.getRaster().getPixels(startX, startY, width, height, pixels);
            
            int[] horizontal = weightedPixelSum(pixels, width, height, this.maskHorizontal);
            int[] vertical = weightedPixelSum(pixels, width, height, this.maskVertical);
            int[] diagonal45 = weightedPixelSum(pixels, width, height, this.maskDiagonal45);
            int[] diagonal135 = weightedPixelSum(pixels, width, height, this.maskDiagonal135);
            
            for(int i = 0; i< 3; i++){
                resColor[i] = Math.max(Math.max(Math.max(horizontal[i], vertical[i]), diagonal45[i]), diagonal135[i]) / 4;
            }
            
            return resColor;
        }
        
        return null;
    }
    
    private int[] weightedPixelSum(int [] pixels, int width, int height, int[][] mask){
        int[] sums = new int[3];
        
        sums[0] = 0;
        sums[1] = 0;
        sums[2] = 0;
        
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                sums[0] += pixels[x*3 + y*width] * mask[x][y];
                sums[1] += pixels[x*3 + y*width*3 + 1] * mask[x][y];
                sums[2] += pixels[x*3 + y*width*3 + 2] * mask[x][y];
            }
        }
                
        sums[0] = Math.abs(sums[0]);
        sums[1] = Math.abs(sums[1]);
        sums[2] = Math.abs(sums[2]);
        
        return sums;
    }
}
