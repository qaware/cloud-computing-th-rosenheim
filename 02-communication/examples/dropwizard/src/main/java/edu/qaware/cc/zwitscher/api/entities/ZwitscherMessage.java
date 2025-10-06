package edu.qaware.cc.zwitscher.api.entities;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "Eine Nachricht - versendet mit Zwitscher")
public class ZwitscherMessage {
    private Date timestamp;
    private String message;
    
    public ZwitscherMessage(String message){
        this.message = message;
        this.timestamp = new Date();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Schema(description = "Versandzeitpunkt", requiredMode = Schema.RequiredMode.REQUIRED)
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    @Schema(description = "Nachricht", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"Yo!", "yo!", "YO!"})
    public void setMessage(String message) {
        this.message = message;
    }
    
}