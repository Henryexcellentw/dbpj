package Entity;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * @Description：
 * @Author Huangzisu
 * @date 2023-12-02
 **/
public class Message {
    private Integer mId;
    private Integer uId;
    private String content;
    private Timestamp time;
    public Message(Integer mId, Integer uId, String content, Timestamp time) {
        this.mId = mId;
        this.uId = uId;
        this.content = content;
        this.time = time;
    }
    public Message() {
    }

    public Integer getmId() {
        return mId;
    }

    public void setmId(Integer mId) {
        this.mId = mId;
    }

    public Integer getuId() {
        return uId;
    }

    public void setuId(Integer uId) {
        this.uId = uId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
