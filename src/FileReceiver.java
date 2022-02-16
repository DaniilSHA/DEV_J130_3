import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class FileReceiver extends Thread {

    public static final int PORT = 34567;

    private final Socket socket;
    private OutputStream fileWriter;
    private InputStream socketInput;
    private File currentFile;

    public FileReceiver(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            socketInput = socket.getInputStream();
            ObjectInputStream receivingFile = new ObjectInputStream(socketInput);
            String fileName = ((File)receivingFile.readObject()).getName();
            createNewFile(fileName);
            synchronized (socket) {
                byte[] buffer = new byte[1024];
                int lineSize;
                while ((lineSize=socketInput.read(buffer))>0) {
                    fileWriter.write(buffer,0,lineSize);
                }
                System.out.println("Фаил:" + currentFile.getName() + " был успешно загружен в потоке: " + Thread.currentThread().getName());
            }
        } catch (IOException | ClassNotFoundException e) {
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
        fileWriter = new FileOutputStream(currentFile);
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
