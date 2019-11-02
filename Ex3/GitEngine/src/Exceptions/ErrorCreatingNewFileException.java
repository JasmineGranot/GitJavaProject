package Exceptions;

public class ErrorCreatingNewFileException extends Exception{
    public ErrorCreatingNewFileException(String errorMessage) {
        super(errorMessage);
    }
}
