package interfaces;

import java.util.concurrent.CompletionStage;

/**
 * Purpose of the interface is to allow different
 * object types to implement their own undo method
 * Examples of 'types' include Profile, Destinations etc.
 */
public interface TypesInterface {
    
    /**
     * Method to perform an undo action.
     * The method is expected to operate asynchronously
     * @param objectID - ID of the object to have the undo operation performed against
     * @return null
     */
    CompletionStage<Void> undo(int objectID);
}
