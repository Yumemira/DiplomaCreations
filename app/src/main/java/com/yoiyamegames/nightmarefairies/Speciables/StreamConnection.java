package com.yoiyamegames.nightmarefairies.Speciables;


import com.yoiyamegames.nightmarefairies.Bases.EthernetStatics;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public final class StreamConnection {
    public static final Socket socket;

    static {
        try {
            socket = IO.socket(EthernetStatics.ipaddr);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public StreamConnection() throws URISyntaxException {

    }
    public void shutdownConnect(){
        socket.disconnect();
        socket.off();
    }
}
