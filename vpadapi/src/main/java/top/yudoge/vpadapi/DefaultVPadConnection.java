package top.yudoge.vpadapi;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import top.yudoge.vpadapi.structure.Message;


public class DefaultVPadConnection implements VPadConnection {

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    public DefaultVPadConnection(Socket socket) {
        this.socket = socket;
        try {
            outputStream = this.socket.getOutputStream();
            inputStream = this.socket.getInputStream();
        } catch (IOException e) {
            throw new ClientException("IOException in the constructor of `DefaultConnection`", e);
        }
    }

    @Override
    public void disconnect() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(Message msg) {
        try {
            outputStream.write(msg.toBytes());
        } catch (IOException e) {
            throw new ClientException("IOException when call `sendMessage` in `DefaultConnection`", e);
        }
    }

    @Override
    public void readMessage(Message msg) {
        byte bytes[] = new byte[1024];
        try {
            inputStream.read(bytes);
            msg.fromBytes(bytes, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isClosed() {
        return this.socket.isClosed();
    }
}
