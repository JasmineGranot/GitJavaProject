package GitObjects;
import Utils.MagitUtils;

public class NotificationObject {
    private String msg;
    private String creationDate;

    public NotificationObject(String msg) {
        setMsg(msg);
        creationDate = MagitUtils.getTodayAsStr();
    }

    private void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
    public String getDate() {
        return creationDate;
    }

}
