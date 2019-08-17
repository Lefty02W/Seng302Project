package models;

public class PaginationHelper {

    private int offset;
    private int previous;
    private int next;
    private int activeTab = 0;
    private Boolean nextEnabled = true;
    private Boolean previousEnabled = false;
    private int maxSize;

    public PaginationHelper(int offset, int previous, int next, int activeTab, Boolean nextEnabled, Boolean previousEnabled, int maxSize) {
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

    public void alterPrevious(int amount) {
        this.previous = Math.max(0, previous - amount);
        previousEnabled = previous != 0;
    }

    public int getNext() {
        return next;
    }

    public void alterNext(int amount) {
        if (next + amount < maxSize) {
            next += amount;
            nextEnabled = true;
        } else {
            nextEnabled = false;
        }
    }

    public int getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(int activeTab) {
        this.activeTab = activeTab;
    }

    public Boolean getNextEnabled() {
        return nextEnabled;
    }

    public void setNextEnabled(Boolean nextEnabled) {
        this.nextEnabled = nextEnabled;
    }

    public Boolean getPreviousEnabled() {
        return previousEnabled;
    }

    public void setPreviousEnabled(Boolean previousEnabled) {
        this.previousEnabled = previousEnabled;
    }
}
