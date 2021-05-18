package src.main;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 *This class is used to create Input and Output Buffer for IndexingManager.
 */
public class IndexingManagerBuffer {

    /**
     * List is created for Input Buffer.
     */
    private static final List<File> inputIMBuffer = new LinkedList<>();
    /**
     * List is created for Output Buffer.
     */
    private static final List<File> outputIMBuffer = new LinkedList<>();
    /**
     * Input Buffer Lock has been created.
     */
    private static final ReentrantLock inputIMBufferLock = new ReentrantLock();
    /**
     * Output Buffer Lock has been created
     */
    private static final ReentrantLock outputIMBufferLock = new ReentrantLock();
    /**
     * Private object of class is created.
     */
    private static IndexingManagerBuffer indexingManagerBuffer;

    /**
     * Default constructor of class has been made private so that cannot be accessed from outside the class.
     */
 private IndexingManagerBuffer(){

 }

    /** Making Singleton object.
     * @return Object of class.
     */
 public static IndexingManagerBuffer getInstance(){
     if (indexingManagerBuffer == null) {
         indexingManagerBuffer= new IndexingManagerBuffer();
     }
     return indexingManagerBuffer ;

 }

// Returning object of Input IndexingManager Buffer.

    private List<File> getInputIMBuffer() {
        return inputIMBuffer;
    }

    /** Adding file to Input buffer.
     * @param file File which is to be added to Buffer.
     * @return True if file is added to buffer.
     */
    boolean addToIMInputBuffer(File file) {
        inputIMBufferLock.lock();
        inputIMBuffer.add(file);
        System.out.println("File added to Input buffer");
        inputIMBufferLock.unlock();
        return true;
    }

    /** This method is used to fetch file from input buffer one by one.
     * @return File which is fetched.
     */
    File fetchFromIMInputBuffer() {
        inputIMBufferLock.lock();
        File file = null;
        try{
            file = inputIMBuffer.get(0);
            inputIMBuffer.remove(0);
        } catch(Exception e) {

        }
        inputIMBufferLock.unlock();
        return file;
    }

    /** Adding file to Output buffer.
     * @param file File to be added to Output Buffer.
     * @return Returns true if added successfully.

     */
    boolean addToIMOutputBuffer(File file) {
        outputIMBufferLock.lock();
        outputIMBuffer.add(file);
        System.out.println("File added to Output buffer");
        outputIMBufferLock.unlock();
        return true;
    }


    /** This method is used to fetch file from output buffer one by one
     * @return File which is fetched.
     */
    File fetchFromIMOutputBuffer() {
        outputIMBufferLock.lock();
        File file = null;
        try{
            file = outputIMBuffer.get(0);
            outputIMBuffer.remove(0);
        } catch(Exception e) {

        }
        outputIMBufferLock.unlock();
        return file;
    }

}
