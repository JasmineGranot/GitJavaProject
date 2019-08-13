package XMLHandler;

public class XMLValidationResult {

    private boolean isValid = true;
    private String validationMsg = null;

    public String getMessage(){return validationMsg;}
    public boolean isValid(){return isValid;}

    public void setIsValid(boolean isValid){this.isValid = isValid;}
    public void setMessage(String validationMsg){this.validationMsg = validationMsg;}



}
