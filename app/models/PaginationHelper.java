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
        String info = "Showing ";
        if (!nextEnabled){
            info += (next + 1) + " ";
        } else if (previous == 0) {
                info += "1 ";
        } else {
            info += (next - 7) + " ";
        }
        if (!nextEnabled) {
            info += " to " + maxSize;
        } else {
            info += " to " + next;
        }
        info += " of " + maxSize;
        return info;
    }

    /**
     * Sets the next and previous to be enabled or disabled dependent on the current next and previous values
     */
    public void checkButtonsEnabled() {
        previousEnabled = offset != 0;
        nextEnabled = next != offset && next != maxSize;
    }


}
