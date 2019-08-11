package engine;

public class MagitStringResultObject {
    public String data;
    public boolean haveError;
    public String errorMSG;

    MagitStringResultObject(){
        this.data = "";
        this.haveError = false;
        this.errorMSG = "";
    }
}
