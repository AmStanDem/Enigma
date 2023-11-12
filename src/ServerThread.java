import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread{
    Gui gui;

    public ServerThread(Gui gui){
        this.gui = gui;

    }

    @Override
    public void run() {
        try {
            Socket s = new ServerSocket(Gui.PORT).accept();

            gui.input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            gui.output = new PrintWriter(s.getOutputStream());

            gui.cSocket = s;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
