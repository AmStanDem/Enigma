import javax.swing.*;
import java.io.IOException;

public class Main
{
    public static void main(String[] args)
    {
        Enigma e = new Enigma();
        /*
        System.out.println(e.pushKey('E'));
        System.out.println(e.pushKey('E'));
        */

        System.out.println(e.pushKey('F'));


        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    new Gui().setVisible(true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}