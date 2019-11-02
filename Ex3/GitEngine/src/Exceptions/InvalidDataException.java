package Exceptions;

public class InvalidDataException extends Exception {
    public InvalidDataException(String errorMsg){
        super(errorMsg);
    }
}
