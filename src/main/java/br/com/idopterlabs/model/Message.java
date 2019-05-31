package br.com.idopterlabs.model;

public class Message {
    private String room;
    private String status;

    @Override
    public String toString() {
        return super.toString();
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
