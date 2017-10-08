package de.philippgagel.facerecognition.imagemanipulation;

import de.philippgagel.facerecognition.camerahandling.FRImageReceiver;
import de.philippgagel.facerecognition.camerahandling.WebcamNotFoundException;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Philipp Gagel
 */
public class FRImageRenderer extends Observable{
    private final FRImageManipulator manipulator;
    private final FRImageReceiver receiver;
    
    private Thread imageReceiverThread;
    private Thread noiseCancelationThread;
    private Thread edgeDetectionThread;
    
    private List<BufferedImage> inputList;
    
    private Queue<BufferedImage> inputQueue;
    private Queue<BufferedImage> noiseQueue;
    private Queue<BufferedImage> outputQueue;
    
    private ReentrantLock inputLock;
    private ReentrantLock noiseLock;
    private ReentrantLock outputLock;
    
    private volatile boolean running;
    
    private volatile int queueSize = 5;
    
    public FRImageRenderer() throws WebcamNotFoundException {
        receiver = FRImageReceiver.getInstance();
        manipulator = new FRImageManipulator(5, 20);
        
        prepareQueues();
        prepareLocks();
        prepareThreads();
    }
    
    private void prepareQueues(){
        
        inputQueue = new ArrayDeque<>(queueSize);
        noiseQueue = new ArrayDeque<>(queueSize);
        outputQueue = new ArrayDeque<>(queueSize);
    }
    
    private void prepareLocks(){
        inputLock = new ReentrantLock(true);
        outputLock = new ReentrantLock(true);
        noiseLock = new ReentrantLock(true);
    }
    
    private void prepareThreads(){
        running = true;
        
        imageReceiverThread = new Thread(() -> receiveImages());
        noiseCancelationThread = new Thread(() -> cancelNoise());
        edgeDetectionThread = new Thread(() -> findEdges());
        
        imageReceiverThread.start();
        noiseCancelationThread.start();
        edgeDetectionThread.start();
    }
    
    private void receiveImages(){
        while(running){
            if(receiver.isNewImageAvailable()){
                inputLock.lock();
                try {
                    if(inputQueue.size() < queueSize)
                        inputQueue.offer(receiver.getImage());        
                }
                finally {
                    inputLock.unlock();
                }
            }
        }
    }
    
    private void cancelNoise(){
        BufferedImage image;
        
        while (running){
           
            inputLock.lock();

            if((image = inputQueue.poll())!=null){
                inputLock.unlock();

                image = manipulator.cancelNoise(image);
                
                noiseLock.lock();
                try {
                    if(noiseQueue.size()<queueSize)
                        noiseQueue.offer(image);
                }
                finally {
                    noiseLock.unlock();
                }
            }else{
                inputLock.unlock();
            }
        }
    }
    
    public void findEdges(){
        BufferedImage image;
        while(running){
          
            noiseLock.lock();
            if((image = noiseQueue.poll())!= null){
                noiseLock.unlock();

                image = manipulator.findEdges(image);
                
                outputLock.lock();
                try {
                    if(outputQueue.size()<queueSize)
                        outputQueue.offer(image);
                }
                finally {
                    outputLock.unlock();
                }
            }else{
                noiseLock.unlock();
            }
        }
    }
    
    public BufferedImage getImage(){
        BufferedImage image = null;
        outputLock.lock();
        try {
            image = outputQueue.poll();
        }
        finally {
            outputLock.unlock();
        }
        
        return image;
    }
    
    public boolean isNewImageAvailable(){
        boolean available = false;
        outputLock.lock();
        try {
            available = !this.outputQueue.isEmpty();
        }
        finally {
            outputLock.unlock();
        }
        return available;
    }
    
    public void setSensitivity(int sensitivity){
        this.manipulator.setSensitivity(sensitivity);
    }
    
    public int getSensitivity(){
        return this.manipulator.getSensitivity();
    }
    
    public void stop(){
        this.running = false;
    }
}
