package engine;

public class ErrorCreatingNewFileException extends Exception{
    ErrorCreatingNewFileException(String errorMessage) {
        super(errorMessage);
    }
}
