package backend.Chat.websocket;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Data;

/**
 * @author Soma Germano
 * this class handles construction of messages
 */
@Entity
@Table(name = "messages")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userName;

    @Lob
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent")
    private Date sent = new Date();

    /**
     * default constructor
     */
    public Message() {};

    /**
     * constructor for message
     * @param userName
     * @param content
     */
    public Message(String userName, String content) {
        this.userName = userName;
        this.content = content;
    }

    /**
     * retrieves message id
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * updates message id
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * retrieves username of message sender
     * @return
     */
    public String getUserName() {
        return userName;
    }

    /**
     * updates username of message sender
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * retrieves content of message
     * @return
     */
    public String getContent() {
        return content;
    }

    /**
     * updates content of message
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * retrieves message sent date
     * @return
     */
    public Date getSent() {
        return sent;
    }

    /**
     * update message sent date
     * @param sent
     */
    public void setSent(Date sent) {
        this.sent = sent;
    }


}

