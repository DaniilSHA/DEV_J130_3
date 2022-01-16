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
             Scanner scanner = new Scanner(new FileInputStream(fileName));
             PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true) ) {
            socketWriter.println(fileName.getName());
            while (scanner.hasNext()) {
                socketWriter.println(scanner.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new FileSender("SendingFiles" + File.separator + "test1.txt").start();
        new FileSender("SendingFiles" + File.separator + "test2.txt").start();
        new FileSender("SendingFiles" + File.separator + "test3.txt").start();
    }
}
