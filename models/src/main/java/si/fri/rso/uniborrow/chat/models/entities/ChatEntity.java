package si.fri.rso.uniborrow.chat.models.entities;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Table(name = "chats")
@NamedQueries(value = {
        @NamedQuery(
                name = "ChatEntity.getAllChats",
                query = "SELECT c FROM ChatEntity c"
        ),
        @NamedQuery(
                name = "ChatEntity.getChatOfUsers",
                query = "SELECT c FROM ChatEntity c " +
                        "WHERE (c.userFrom = :userOne OR c.userTo = :userOne) AND " +
                        "(c.userFrom = :userTwo OR c.userTo = :userTwo) " +
                        "ORDER BY c.msgTimestamp"
        ),
        @NamedQuery(
                name = "ChatEntity.getUsersFromForUser",
                query = "SELECT DISTINCT c.userFrom FROM ChatEntity c WHERE c.userTo = :userId"
        ),
        @NamedQuery(
                name = "ChatEntity.getUsersToForUser",
                query = "SELECT DISTINCT c.userTo FROM ChatEntity c WHERE c.userFrom = :userId"
        )
})
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_from")
    private Integer userFrom;

    @Column(name = "user_to")
    private Integer userTo;

    @Column(name = "message")
    private String message;

    @Column(name = "msg_timestamp", columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private Timestamp msgTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(Integer userFrom) {
        this.userFrom = userFrom;
    }

    public Integer getUserTo() {
        return userTo;
    }

    public void setUserTo(Integer userTo) {
        this.userTo = userTo;
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
