import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        String serverName = "localhost";
        int port = 8080;
        String fileName = "video.mp4";
        int numFragments = 10;

        try {
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            // create input stream to read from socket
            DataInputStream dis = new DataInputStream(new BufferedInputStream(client.getInputStream()));

            // create file output stream to write received file
            FileOutputStream fos = new FileOutputStream(fileName);

            // create buffer to hold fragment data
            byte[] buffer = new byte[dis.readInt()];

            // loop to receive and write each fragment
            for (int i = 0; i < numFragments; i++) {
                // receive fragment data
                int numBytesRead = dis.read(buffer);

                // write fragment data to file
                fos.write(buffer, 0, numBytesRead);

                // calculate and print percentage of file received
                double percentComplete = ((double) (i + 1) / numFragments) * 100;
                System.out.printf("Received fragment %d of %d (%.2f%%)\n", i + 1, numFragments, percentComplete);
            }

            // close streams and socket
            fos.close();
            dis.close();
            client.close();

            System.out.println("File received successfully");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}