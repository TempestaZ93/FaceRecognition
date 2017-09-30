/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.philippgagel.facerecognition.imagemanipulation;

import de.philippgagel.facerecognition.camerahandling.FRImageReceiver;

/**
 *
 * @author Philipp Gagel
 */
public class FRImageRenderer {
    private FRImageManipulator manipulator;
    private FRImageReceiver receiver;
    
    private Thread noiseCancelationThread;
    private Thread edgeDetectionThread;
    
    public FRImageRenderer(){
        receiver = new FRImageReceiver();
        manipulator = new FRImageManipulator(5, 50);
    }
}
