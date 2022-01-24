import sun.net.util.IPAddressUtil;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class FileSender extends Thread {

    private File fileName;

    public FileSender(String fileName) {
        this.fileName = new File(fileName);
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(InetAddress.getLocalHost(), FileReceiver.PORT);
             FileInputStream input = new FileInputStream(fileName);
             OutputStream output = socket.getOutputStream();
             ObjectOutputStream sendingFile = new ObjectOutputStream(output)) {
            sendingFile.writeObject(fileName);
            byte[] buffer = new byte[1024];
            int lineSize;
            while ((lineSize=input.read(buffer))>0) {
                output.write(buffer,0,lineSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new FileSender("SendingFiles" + File.separator + "test1.txt").start();
        new FileSender("SendingFiles" + File.separator + "test2.txt").start();
        new FileSender("SendingFiles" + File.separator + "test3.txt").start();
        new FileSender("SendingFiles" + File.separator + "table-of-irregular-verbs.pdf").start();
    }
}
