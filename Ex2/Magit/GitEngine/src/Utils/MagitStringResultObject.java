package Utils;

import java.util.List;

public class MagitStringResultObject {
    private String data;
    private boolean haveError;
    private String errorMSG;
    private List<String> dataList;
    private boolean isChanged;

    public MagitStringResultObject() {
        this.data = "";
        this.haveError = false;
        this.errorMSG = "";
        this.dataList = null;
        this.isChanged = false;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }

    public boolean getChanged() {
        return this.isChanged;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    public List<String> getDataList() {
        return dataList;
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
