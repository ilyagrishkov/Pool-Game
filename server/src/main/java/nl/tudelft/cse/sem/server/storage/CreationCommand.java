package nl.tudelft.cse.sem.server.storage;

/**
 * Command to create objects of type T.
 *
 * @param <T> Type of object of this creation command
 */
public interface CreationCommand<T> {
    /**
     * Creates the object of type T.
     *
     * @return Instance of type T
     */
    T execute();
}
