import javax.swing.*;
import java.io.IOException;

public class Main
{
    public static void main(String[] args)
    {


        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    new Gui().setVisible(true);
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        /*
        Enigma e = new Enigma();
        String s = "ciaociao";
        s = s.toUpperCase();
        s = e.writeMsg(s);
        System.out.println("criptaggio:" + s);

        e = new Enigma();
        s = e.writeMsg(s);
        System.out.println("decriptaggio:" + s);
        */

    }
}