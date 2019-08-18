package models;

/**
 * Model class to hold information used when paginating on the front end
 */
public class PaginationHelper {

    private int offset;
    private int previous;
    private int next;
    private int activeTab;
    private boolean nextEnabled ;
    private boolean previousEnabled ;
    private int maxSize;

    public PaginationHelper(int offset, int previous, int next, int activeTab, boolean nextEnabled, boolean previousEnabled, int maxSize) {
        this.offset = offset;
        this.previous = previous;
        this.next = next;
        this.activeTab = activeTab;
        this.nextEnabled = nextEnabled;
        this.previousEnabled = previousEnabled;
        this.maxSize = maxSize;
    }

    /**
     * Constructor for pagination that doesn't need tabs
     * @param offset start position of the returned list
     * @param previous position the previous offset
     * @param next position of the next offset
     * @param nextEnabled boolean true if there is more items in the database to go to
     * @param previousEnabled boolean true if the offset can shift back
     * @param maxSize total size of the number of elements in the database table
     */
    public PaginationHelper(int offset, int previous, int next, Boolean nextEnabled, Boolean previousEnabled, int maxSize){
        this.offset = offset;
        this.previous = previous;
        this.next = next;
        this.nextEnabled = nextEnabled;
        this.previousEnabled = previousEnabled;
        this.maxSize = maxSize;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPrevious() {
        return previous;
    }

    /**
     * Updates the previous value by passed increment if valid
     * To be valid increment must keep previous positive
     * @param amount increment amount
     */
    public void alterPrevious(int amount) {
        this.previous = Math.max(0, previous - amount);
    }

    public int getNext() {
        return next;
    }

    /**
     * Updates the next index if passed increment is valid
     * To be valid the increment must not exceed the max number of entries
     * @param amount increment amount
     */
    public void alterNext(int amount) {
        if (next + amount <= maxSize) {
            next += amount;
        }
    }

    public int getActiveTab() {
        return activeTab;
    }

    public boolean isNextEnabled() {
        return nextEnabled;
    }

    public boolean isPreviousEnabled() {
        return previousEnabled;
    }

    public void setPreviousEnabled(Boolean previousEnabled) {
        this.previousEnabled = previousEnabled;
    }

    /**
     * Forms an info string to show to the user
     * Has info on what entries are being showed and total number of entries
     * @return
     */
    public String getInfoString() {
        return "Showing " + (next - 7) + " to " + next + " of " + maxSize;
    }

    /**
     * Forms an info string to show to the user on the travellers page
     * Has info on what entries are being showed and total number of entries
     * @return
     */
    public String getInfoStringTravellers() {
        return "Showing " + (next - 9) + " - " + next + " of " + maxSize;
    }

    /**
     * Sets the next and previous to be enabled or disabled dependent on the current next and previous values
     */
    public void checkButtonsEnabled() {
        previousEnabled = offset != 0;
        nextEnabled = next != offset && next != maxSize;
    }


}
