import java.io.*;
import java.net.*;
import java.util.*;

public class FileServer {
    public static void main(String[] args) throws IOException {
        String filename = "video.mp4"; // nombre del archivo a enviar
        int numChunks = 10; // número de fragmentos en que se divide el archivo
        int chunkSize = 50 * 1024 * 1024; // tamaño de cada fragmento en bytes (en este ejemplo, 5 MB)
        int port = 8080; // puerto del servidor

        // dividir el archivo en fragmentos y guardarlos en una lista
        List<byte[]> chunks = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(filename);
            byte[] buffer = new byte[chunkSize];
            int bytesRead;
            int chunkIndex = 1;
            while ((bytesRead = fis.read(buffer)) != -1) {
                chunks.add(Arrays.copyOfRange(buffer, 0, bytesRead));
                chunkIndex++;
                if (chunkIndex > numChunks) {
                    break;
                }
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // aleatorizar el orden de los fragmentos
        Collections.shuffle(chunks);

        // iniciar el servidor en el puerto especificado
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado en el puerto " + port);

        // aceptar conexiones entrantes de los clientes
        for (int i = 1; i <= 5; i++) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente " + i + " conectado desde " + clientSocket.getInetAddress().getHostAddress());

            // enviar cada fragmento al cliente en orden aleatorio
            for (int j = 0; j < numChunks; j++) {
                byte[] chunk = chunks.get(j);
                OutputStream os = clientSocket.getOutputStream();
                os.write(chunk);
                os.flush();

                // mostrar mensaje en consola indicando el fragmento enviado y el porcentaje restante
                double progress = (double) (j + 1) / numChunks * 100;
                System.out.println("Cliente " + i + " recibió el fragmento " + (j + 1) + " del archivo " + filename + ", " + String.format("%.2f", progress) + "% completo");

                // esperar a que el cliente confirme la recepción del fragmento
                InputStream is = clientSocket.getInputStream();
                byte[] ack = new byte[1];
                is.read(ack);
            }

            // mostrar mensaje en consola indicando que se ha recibido el archivo completo
            System.out.println("Cliente " + i + " recibió el archivo completo " + filename);
            clientSocket.close();
        }

        // cerrar el servidor
        serverSocket.close();
        System.out.println("Servidor cerrado");
    }
}
