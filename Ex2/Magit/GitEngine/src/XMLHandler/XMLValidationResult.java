package XMLHandler;

public class XMLValidationResult {

    private boolean isValid = true;
    private String validationMsg = null;

    public String getMessage() {return validationMsg;}
    public boolean isValid() {return isValid;}

    void setIsValid(boolean isValid) {this.isValid = isValid;}
    void setMessage(String validationMsg) {this.validationMsg = validationMsg;}
}
