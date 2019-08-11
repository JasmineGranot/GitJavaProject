package engine;

public class MagitStringResultObject {
    private String data;
    private boolean haveError;
    private String errorMSG;

    MagitStringResultObject() {
        this.data = "";
        this.haveError = false;
        this.errorMSG = "";
    }

    public void setData(String newData) {
        data = newData;
    }

    public void setErrorMSG(String msg) {
        errorMSG = msg;
    }

    public void setIsHasError(boolean haveError) {
        this.haveError = haveError;
    }

    public String getData() {
        return data;
    }

    public String getErrorMSG() {
        return errorMSG;
    }

    public boolean getIsHasError() {
        return haveError;
    }
}
