package src.main;

/**
 * This class is used to collect values from database corresponding to search query for key and return values in an object.
 */
public class ObjReturn {

    /**
     * Key for which search query has come.
     */
        String key1;
    /**
     * Value associated with Key
     */
        String value1;
    /**
     * Timer associated with Key
     */
        String time1;
        int totalCopies1;
    /**
     * Copy Number associated with Key
     */
        int copyNum1;
    /**
     * Timer Type associated with key.
     */
        boolean timerType1;
    /**
     * Userid for Key.
     */
        String userId;
    /**
     * Time at which entry was added to database.
     */
        String time;

   // Following lines of code have setters and getters for object.

    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public int getTotalCopies1() {
        return totalCopies1;
    }

    public void setTotalCopies1(int totalCopies1) {
        this.totalCopies1 = totalCopies1;
    }

    public int getCopyNum1() {
        return copyNum1;
    }

    public void setCopyNum1(int copyNum1) {
        this.copyNum1 = copyNum1;
    }

    public boolean getTimerType1() {
        return timerType1;
    }

    public void setTimerType1(boolean timerType1) {
        this.timerType1 = timerType1;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
        
        

   
       
    
}
