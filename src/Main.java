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
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        /*
        Enigma e = new Enigma();
        String s = "ciaoilsleesplendenteequantosorridiloeunpomenoperchefaischifostupidociaoilsleesplendenteequantosorridiloeunpomenoperchefaischifostupidociaoilsleesplendenteequantosorridiloeunpomenoperchefaischifostupido";
        //s = "KVIJQSLROJAUEGUMVCERZJXKQDDHVLIUWLMFZYMNLOWSJXDXRXNQUSVMCSGIYXRRGXKUKBUNTYBTSLXQIUHLRYYVOXKCNQOZEYEQXZHVFLOCVDWEWPKPDDVITCMZKWSBMRFNBWMHLJOSEDFZRXOHTCTFHSTULGQHHHRIFHXQCQNOWXMSHBEQJPIYREKVDJBZMIJZAAVEI";
        s = s.toUpperCase();
        System.out.println(e.writeMsg(s));

    }
}