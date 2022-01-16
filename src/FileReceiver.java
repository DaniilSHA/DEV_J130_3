import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class FileReceiver extends Thread {

    public static final int PORT = 34567;

    private final Socket socket;
    private PrintWriter fileWriter;
    private Scanner socketInput;
    private File currentFile;

    public FileReceiver(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            socketInput = new Scanner(socket.getInputStream());
            String fileName = socketInput.nextLine();
            createNewFile(fileName);
            synchronized (socket) {
                while (socketInput.hasNext()) {
                    fileWriter.println(socketInput.nextLine());
                }
                System.out.println("Фаил:" + currentFile.getName() + " был успешно загружен в потоке: " + Thread.currentThread().getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
                socketInput.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createNewFile(String fileName) throws IOException {
        fileName = "ReceivingFiles" + File.separator + fileName;
        currentFile = new File(fileName);
        if (!currentFile.exists()) currentFile.createNewFile();
        fileWriter = new PrintWriter(new FileWriter(currentFile), true);
    }

    public static void main(String[] args) {
        System.out.println("Start server...");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new FileReceiver(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
