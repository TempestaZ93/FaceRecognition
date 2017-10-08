package de.philippgagel.facerecognition.imagemanipulation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 *
 * @author Philipp Gagel
 */
public class FRImageManipulator {
    // sensitivity to operate with
    private int sensitivity;

    // masks to apply to the images
    private int[][][] masks;
    private int weight;
    
    public FRImageManipulator(int maskSize, int sensitivity){
        this.sensitivity = sensitivity;
        
        weight = 4;
        masks = new int[4][3][3];
        
        // Initiate the arrays with the preffered values
        this.masks[0] = new int[][] {
            {0, 0, 0},
            {-weight, 0, weight},
            {0, 0, 0},
        };
        this.masks[1] = new int[][] {
            {0, weight, 0},
            {0, 0, 0},
            {0, -weight, 0},
        };
        this.masks[2] = new int[][] {
            {weight, 0, 0},
            {0, 0, 0},
            {0, 0, -weight},
        };
        this.masks[3] = new int[][] {
            {0, 0, -weight},
            {0, 0, 0},
            {weight, 0, 0},
        };
    }
    
    /**
     * @returns the sensitivity used to determine edges
     */
    public int getSensitivity() {
        return sensitivity / weight;
    }

    /**
     * Sets the sensitivity used to determine edges
     * @param sensitivity 
     */
    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity * weight;
    }
        
    /**
     * This method uses a directional color difference calculation to look for edges
     * 
     * @param src the image to look for edges
     * @return the image containting only the found edges.
     */
    public BufferedImage findEdges(BufferedImage src){
        BufferedImage out;
        
        if(src != null) out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        else return new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        
        for(int y = 0; y < src.getHeight(); y++){
            for(int x = 0; x < src.getWidth(); x++){
                int[] resColor = edgeTest(src, x, y);
                if(resColor!=null) out.setRGB(x, y, (resColor[0]<<16)  | (resColor[1]<<8)  | resColor[2]);
            }
        }
        
        return out;
    }
    
    /**
     * This image uses an adaptive median filter to cancel out noise in the image. 
     * 
     * @param src the image that has to undego noise cancelation 
     * @return the image with less noise
     */
    public BufferedImage cancelNoise(BufferedImage src){
        BufferedImage out;
        
        if(src != null) out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        else return new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        
        for(int y = 0; y < src.getHeight(); y++){
            for(int x = 0; x < src.getWidth(); x++){
                int[] resColor = adaptiveMedian(src, x, y);
                
                if(resColor != null) out.setRGB(x, y, (resColor[0]<<16)  | (resColor[1]<<8)  | resColor[2]);
                else out.setRGB(x, y, Color.white.getRGB());
            }
        }
        
        return out;
    }
    
    // This method applies the adaptive median filter to on pixel at a time
     private int[] adaptiveMedian(BufferedImage img, int x, int y){
        int startX, startY;
        int width, height;
        
        // calculate start coordinates for the 
        startX = x - (3 << 1);
        startY = y - (3 << 1);
        
        // is the start is less than 0 set it to 0
        startX = startX < 0 ? 0 : startX;
        startY = startY < 0 ? 0 : startY;
        
        // calculate width and height
        width = startX + 3 >= img.getWidth() ? img.getWidth() - startX : 3;
        height = startY + 3 >= img.getHeight() ? img.getHeight() - startY : 3;
        
        // if width or height are positive start calculation
        if(width > 0 && height > 0){
            int[] pixels = new int[width * height * 3];
            int[] resColor = new int[3];
        
            img.getRaster().getPixels(startX, startY, width, height, pixels);
        
            // Split the pixels into the different color channels
            int[][] splitPixels = new int[3][width*height];
            int pixelCounter = 0;
        
            for(int i = 0; i< pixels.length; pixelCounter++){
                splitPixels[0][pixelCounter] = pixels[i++];
                splitPixels[1][pixelCounter] = pixels[i++];
                splitPixels[2][pixelCounter] = pixels[i++];
            }
        
            // sort each color channel
            Arrays.sort(splitPixels[0]);
            Arrays.sort(splitPixels[1]);
            Arrays.sort(splitPixels[2]);
            
            // get the median of each color channel
            int medIndex = width*height/2;
            resColor[0] = splitPixels[0][medIndex];
            resColor[1] = splitPixels[1][medIndex];
            resColor[2] = splitPixels[2][medIndex];
            
            return resColor;
        }
        return null;
    }
    
    // This method applies the cirectional color difference algorithm to one pixel at a time.
    private int[] edgeTest(BufferedImage img, int x, int y){
        
        int startX, startY;
        int width, height;
        
        // calculate start coordinates
        startX = x - (3 / 2);
        startY = y - (3 / 2);
        
        // if the coordinates are less than 0 set it to 0
        startX = startX < 0 ? 0 : startX;
        startY = startY < 0 ? 0 : startY;
        
        // calculate the width and heigth
        width = startX + 3 >= img.getWidth() ? img.getWidth() - startX : 3;
        height = startY + 3 >= img.getHeight() ? img.getHeight() - startY : 3;
        
        // If width and height are positive start the calculation
        if(width > 0 && height > 0){
            int[] pixels = new int[width * height * 3];
            int[] resColor = new int[3];
            
            img.getRaster().getPixels(startX, startY, width, height, pixels);
            
            // calculate pixel sums
            int[][] pixelSums = new int[4][3];
            
             for (int[] sum : pixelSums)
                for(int su : sum)
                    su = 0;
        
            int[][] mask;
            int[] sum;
            for (int i = 0; i< masks.length; i++) {
                mask = masks[i];
                sum = pixelSums[i];
                for (int localY = 0; localY < height; localY++) {
                    for (int localX = 0; localX < width; localX++) {
                        int index = localX*3 + localY*width;
                        int maskValue = mask[localX][localY];
                        if(maskValue == 0) continue;
                        sum[0] += pixels[index] * maskValue;
                        sum[1] += pixels[index + 1] * maskValue;
                        sum[2] += pixels[index + 2] * maskValue;
                    }
                }
            }
            
            for(int i = 0; i< 3; i++){
                resColor[i] = Math.max(Math.max(Math.max(pixelSums[0][i], pixelSums[1][i]), pixelSums[2][i]), pixelSums[3][i]);
            }
            
            int max = Math.max(Math.max(resColor[0], resColor[1]), resColor[2]);
            
            if(max > this.sensitivity){
                Arrays.fill(resColor, max);
            }else{
                Arrays.fill(resColor, 0);
            }
                        
            return resColor;
        }
        
        return null;
    }
    
    // This method creates a weighted pixel sum out of all pixels using the given mask.
    private int[][] weightedPixelSum(int [] pixels, int width, int height){
        int[][] sums = new int[4][3];
        
        for (int[] sum : sums)
            for(int su : sum)
                su = 0;
        
        int[][] mask;
        int[] sum;
        for (int i = 0; i< masks.length; i++) {
            mask = masks[i];
            sum = sums[i];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int index = x*3 + y*width;
                    sum[0] += pixels[index] * mask[x][y];
                    sum[1] += pixels[index + 1] * mask[x][y];
                    sum[2] += pixels[index + 2] * mask[x][y];
                }
            }
        }
            
        return sums;
    }
}
