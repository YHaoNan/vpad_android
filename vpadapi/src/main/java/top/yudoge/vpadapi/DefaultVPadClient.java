package top.yudoge.vpadapi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class DefaultVPadClient implements VPadClient {
    public static final int CONNECT_TIMEOUT = 10000;
    @Override
    public VPadConnection connect(VPadServer vPadServer) {
        try {
            Socket client = new Socket();
            client.setTcpNoDelay(true);
            client.connect(new InetSocketAddress(vPadServer.getIp(), 1236), CONNECT_TIMEOUT);
            return new DefaultVPadConnection(client);
        } catch (IOException e) {
            throw new ClientException("IOException", e);
        }
    }
}
