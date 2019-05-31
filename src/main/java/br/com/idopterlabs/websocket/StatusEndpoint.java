package br.com.idopterlabs.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import br.com.idopterlabs.model.Message;

@ServerEndpoint(value = "/status/{room}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class StatusEndpoint {
    private Session session;
    private static final Set<StatusEndpoint> statusEndpoints = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("room") String room) throws IOException, EncodeException, InterruptedException {

        this.session = session;
        statusEndpoints.add(this);

        Message message = new Message();
        message.setRoom(room);
        message.setStatus("Wait");
        broadcast(message);
        
        // trecho pra exemplificar o servidor enviando mensagens ao client web em js
        for(int i = 0; i < 10; i++) {
        	TimeUnit.SECONDS.sleep(3);
        	message.setStatus("Check #" + i);
            broadcast(message);
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        statusEndpoints.remove(this);
        Message message = new Message();
        message.setRoom("#");
        message.setStatus("Closed");
        broadcast(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private static void broadcast(Message message) throws IOException, EncodeException {
        statusEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote()
                        .sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
