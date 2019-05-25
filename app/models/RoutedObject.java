package models;

/**
 * This class is used a s wrapper to send objects to views
 * @param <T> The type of the object being sent
 */
public class RoutedObject<T> {

    private T object;
    private boolean isEdit;
    private boolean isView;

    public RoutedObject(T object, boolean isEdit, boolean isView) {
        this.object = object;
        this.isEdit = isEdit;
        this.isView = isView;
    }

    public T getObject() {
        return object;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public boolean isView() {
        return isView;
    }
}
