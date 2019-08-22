package Utils;

import GitObjects.Branch;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;

import java.util.LinkedList;
import java.util.List;

public class ResultList<T> {
    private List<T> res;
    private boolean hasError;
    private String errorMsg;

    public void setHasError(boolean hasError){
        this.hasError = hasError;
    }

    public void setRes(List<T> res){
        this.res = res;
    }

    public void setErrorMsg(String errorMsg){
        this.errorMsg = errorMsg;
    }

    public List<T> getRes(){
        return res;
    }

    public boolean getHasError(){
        return hasError;
    }

    public String getErrorMsg(){
        return errorMsg;
    }
}
