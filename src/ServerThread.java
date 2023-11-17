import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread{
    private Gui gui;

    public ServerThread(Gui gui){
        this.gui = gui;

    }

    @Override
    public void run() {
        try
        {

            System.out.println("Il Thread partirà");

            gui.sSocket = new ServerSocket(Gui.PORT);
            Socket s = gui.sSocket.accept();

            gui.input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            gui.output = new PrintWriter(s.getOutputStream());

            gui.cSocket = s;

        }
        catch (IOException e)
        {
            //System.out.println("IOException");
        }catch (Exception e){
            //System.out.println("exception generica");
        }

        //System.out.println("Ciao giosuè");


    }
}
