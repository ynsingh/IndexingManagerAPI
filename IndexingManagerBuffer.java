import java.io.File;
import java.util.LinkedList;
import java.util.List;


//This class is used to create Input and Output Buffer for IndexingManager.


public class IndexingManagerBuffer {

    private static final List<File> inputIMBuffer = new LinkedList<>();
    private static final List<File> outputIMBuffer = new LinkedList<>();
    private static IndexingManagerBuffer indexingManagerBuffer;

// Default constructor of class has been made private so that cannot be accessed from outside the class.

 private IndexingManagerBuffer(){

 }

 //Making Singleton object

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


 // Adding file to Input buffer. Returning true if added successfully.
    boolean addToIMInputBuffer(File file) {

        inputIMBuffer.add(file);
        System.out.println("File added to Input buffer");

        return true;
    }

  //  This method is used to fetch file from input buffer one by one.

    File fetchFromIMInputBuffer() {

        File file = null;
        try{
            file = inputIMBuffer.get(0);
            inputIMBuffer.remove(0);
        } catch(Exception e) {

        }

        return file;
    }
// Adding file to Output buffer. Returning true if added successfully.

    boolean addToIMOutputBuffer(File file) {

        outputIMBuffer.add(file);
        System.out.println("File added to Output buffer");

        return true;
    }

// This method is used to fetch file from input buffer one by one.


    File fetchFromIMOutputBuffer() {

        File file = null;
        try{
            file = outputIMBuffer.get(0);
            outputIMBuffer.remove(0);
        } catch(Exception e) {

        }

        return file;
    }





















}
