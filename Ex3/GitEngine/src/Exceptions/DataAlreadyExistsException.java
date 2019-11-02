package Exceptions;

public class DataAlreadyExistsException extends Exception {
    public DataAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
