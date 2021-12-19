package si.fri.rso.uniborrow.chat.lib;

import java.sql.Timestamp;

public class Chat {

    private Integer chatId;
    private Integer userFromId;
    private Integer userToId;
    private String message;
    private Timestamp msgTimestamp;

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public Integer getUserFromId() {
        return userFromId;
    }

    public void setUserFromId(Integer userFromId) {
        this.userFromId = userFromId;
    }

    public Integer getUserToId() {
        return userToId;
    }

    public void setUserToId(Integer userToId) {
        this.userToId = userToId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getMsgTimestamp() {
        return msgTimestamp;
    }

    public void setMsgTimestamp(Timestamp msgTimestamp) {
        this.msgTimestamp = msgTimestamp;
    }
}
