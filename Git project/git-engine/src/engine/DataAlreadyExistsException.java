package engine;

public class DataAlreadyExistsException extends Exception {
    DataAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
