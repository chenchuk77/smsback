package net.devopskb.smsback.core;

/**
 * Created by chenchuk on 1/19/2016.
 */
public class SimpleSms {


    private Long id;
    private Long timestamp;
    private int type;
    private String senderAddress;
    private String recipientAddress;
    private String content;


    public SimpleSms(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public SimpleSms setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
        return this;
    }
//    public void setRecipientAddress(String recipientAddress) {
//        this.recipientAddress = recipientAddress;
//    }

    public String getContent() {
        return content;
    }

    public SimpleSms setContent(String content) {
        this.content = content;
        return this;
    }


    @Override
    public String toString() {
        return "SmsMessage{" +
                "id:" + id +
                ", timestamp:" + timestamp +
                ", type:" + type +
                ", senderAddress:'" + senderAddress + '\'' +
                ", recipientAddress:'" + recipientAddress + '\'' +
                ", content:'" + content + '\'' +
                '}';
        //return ""+id+","+timestamp+","+type+","+senderAddress+","+recipientAddress+","+content;
        //return "id,timestamp,type,sender,recipient,content";

    }
}
