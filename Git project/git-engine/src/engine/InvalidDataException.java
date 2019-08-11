package engine;

public class InvalidDataException extends Exception {
    InvalidDataException(String errorMsg){
        super(errorMsg);
    }
}
