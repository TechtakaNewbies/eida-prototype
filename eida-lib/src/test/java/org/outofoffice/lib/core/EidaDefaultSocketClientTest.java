package org.outofoffice.lib.core;

import org.junit.jupiter.api.Test;
import org.outofoffice.lib.core.socket.EidaDefaultSocketClient;
import org.outofoffice.lib.core.socket.EidaSocketClient;
import org.outofoffice.lib.core.socket.EidaSocketClientLoggingProxy;
import org.outofoffice.lib.exception.EidaException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;


class EidaDefaultSocketClientTest {

    EidaSocketClient eidaClient = new EidaSocketClientLoggingProxy(new EidaDefaultSocketClient());


    @Test
    void request() {
        String host = "localhost";
        int port = 9999;
        String address = host + ":" + port;

        instantServer(port, msg -> String.format("OK\nReceived: %s", msg)).start();

        String response = eidaClient.request(address, "hello");

        assertThat(response).isEqualTo("OK\nReceived: hello");
    }

    private Thread instantServer(int port, Function<String, String> responseGenerator) {
        return new Thread(() -> {
            try (
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String request = reader.readLine();
                String response = responseGenerator.apply(request);
                writer.println(response);
            } catch (Exception e) {
                throw new EidaException(e);
            }
        });
    }

}
