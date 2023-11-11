import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class Gui extends JFrame
{

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

        //this.setLayout(null);
        this.setLayout(new GridBagLayout());
        c = getContentPane();

        JButton debug = new JButton("debug");
        debug.setBounds(0, 300, 500, 400);

        c.add(debug);

        //mainPanel = new JPanel();
        //mainPanel.setLayout(null);

        jPanelChat = new JPanel();
        //jPanelChat.setLayout(null);
        jPanelChat.setBounds(0 , getHeight() * 2 / 3 , getWidth() * 2 /3 , getHeight() * 2 / 3);

        this.add(jPanelChat);

        textArea = new TextArea();
        textArea.setBounds(0,0,100,100);
        textArea.enableInputMethods(false);

        jScrollPane = new JScrollPane (textArea);
        jPanelChat.add(jScrollPane);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.fill = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.gridx = 30;
        gridBagConstraints.gridy = 30;

        this.add(jPanelChat, gridBagConstraints);

        JButton debugMeglio = new JButton("debugMeglio");
        debugMeglio.setBounds(0,0, 20, 30);

        c.add(debugMeglio);



        rollersSelectionWindow = new RollersSelectionDialog(this);

        this.setResizable(false);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Set full screen.
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        /*
      c.weightx = 0.5;
}
c.fill = GridBagConstraints.HORIZONTAL;
c.gridx = 0;
c.gridy = 0;
pane.add(button, c);
        */

        this.setVisible(true);
    }


    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;

        int centerX = getWidth() / 2; // get the center of the window.

        g2d.drawLine(centerX, 0, centerX, getHeight()); // draw the lines.

        g2d.drawLine(centerX,getHeight() * 2 / 3,  0, getHeight() * 2 / 3);



    }

}
