import javafx.stage.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class Gui extends JFrame
{
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    /*
    * getGraphics will return null in the constructor as the component
    * will not be visible at the time of creation. For custom painting in Swing override the
    * paintComponent(g) method instead. There the Graphics handle will always be properly initialized.
    */
    public static final String GUI_NAME = "Enigma machine";




    private JPanel jPanelChat;

    private TextArea textArea;

    private JScrollPane jScrollPane;

    private RollersSelectionDialog rollersSelectionWindow;

    private Container c;

    private Button button;

    private Button [] btnsKeyboard;

    private Button [] btnsLights;



    //selezione rolli tramite jdialog
    public Gui()
    {
        super(GUI_NAME);

        setSize(screenSize.width, screenSize.height); // set the screen size
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Set full screen.
        // (do not actually update the frame size until after the constructor)
        //System.out.println("! " + getHeight() + "   " + getHeight());

        c = getContentPane();


        //this.setLayout(null);
        c.setLayout(null);
        c = getContentPane();

        jPanelChat = new JPanel();
        jPanelChat.setLayout(null);
        jPanelChat.setBounds(0 , 0 , getWidth() /2 , getHeight() * 2 / 3);
        jPanelChat.setBackground(Color.BLUE);
        jPanelChat.setOpaque(true);
        jPanelChat.setVisible(true);

        textArea = new TextArea();
        textArea.setBounds(0,0,100,100);
        textArea.enableInputMethods(false);

        jScrollPane = new JScrollPane (textArea);
        jScrollPane.setBounds(0, 50, 70, 80);
        jPanelChat.add(jScrollPane);
        c.add(jPanelChat);



        rollersSelectionWindow = new RollersSelectionDialog(this);


        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //this.setResizable(false); // do not let you re put it full screen
        this.setVisible(true);
    }


    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        int centerX = getWidth() / 2; // get the center of the window.

        g2d.drawLine(centerX, 0, centerX, getHeight()); // draw the lines.

        g2d.drawLine(centerX,getHeight() * 2 / 3,  0, getHeight() * 2 / 3);

        //System.out.println(getHeight() + "   " + getHeight());
    }

}
