import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

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

    private JPanel jPanelButtonskeyBoard;

    private JPanel jPanelButtonsLights;

    private Vector<TextArea> textAreaVector;

    private TextArea textArea;

    private JScrollPane jScrollPane;

    private RollersSelectionDialog rollersSelectionWindow;

    private Container c;

    private Button [] btnsKeyboard;

    private Button [] btnsLights;



    //selezione rolli tramite jdialog
    public Gui() throws IOException {
        super(GUI_NAME);

        this.setIconImage(ImageIO.read(IMAGE_ICON));

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
        jScrollPane.setBounds(0, 50, getWidth() / 2, 400);
        jPanelChat.add(jScrollPane);
        c.add(jPanelChat);

        jPanelButtonsLights = new JPanel();
        jPanelButtonsLights.setBounds(800, 290, 700,200);
        jPanelButtonsLights.setLayout(null);
        //jPanelButtonsLights.setBackground(Color.RED);

        jPanelButtonskeyBoard = new JPanel();
        jPanelButtonskeyBoard.setBounds(800, 550, 700,200);
        jPanelButtonskeyBoard.setLayout(null);
        //jPanelButtonskeyBoard.setBackground(Color.BLUE);



        btnsKeyboard = new RoundButton[ALPHABET_LENGTH];
        labelsLights = new RoundLabel[ALPHABET_LENGTH];



        int startX = (jPanelButtonskeyBoard.getWidth() / 2) -
                ((9 * KEYBOARD_BUTTONS_SIZE + 8 * KEYBOARD_BUTTONS_HORIZONTAL_SPACE) / 2);
        int keyboardStartY = 20;
        int lightsStartY = 20;
        for (int i = 0; i < btnsKeyboard.length; i++)
        {
            btnsKeyboard[i] = new RoundButton(String.valueOf(ENIGMA_ALPHABET.charAt(i)));
            btnsKeyboard[i].setFocusable(false);
            btnsKeyboard[i].setBackground(KEYBOARD_NON_PRESSED_BUTTON);
            btnsKeyboard[i].serBorderColor(KEYBOARD_BORDER);
            btnsKeyboard[i].setActionCommand(ACTION_COMMAND_KEYBOARD_BUTTONS);
            btnsKeyboard[i].addActionListener(this);
            btnsKeyboard[i].setSize(KEYBOARD_BUTTONS_SIZE, KEYBOARD_BUTTONS_SIZE);

            labelsLights[i] = new RoundLabel(String.valueOf(ENIGMA_ALPHABET.charAt(i)));
            labelsLights[i].setFocusable(false);
            labelsLights[i].setEnabled(false);
            labelsLights[i].setHorizontalAlignment(SwingConstants.CENTER);
            labelsLights[i].setBackground(LIGHTS_ON);
            labelsLights[i].serBorderColor(LIGHTS_BORDER);
            labelsLights[i].setSize(KEYBOARD_BUTTONS_SIZE, KEYBOARD_BUTTONS_SIZE);


            if(i < 9){
                btnsKeyboard[i].setLocation(startX + i * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_HORIZONTAL_SPACE),
                        keyboardStartY);
                labelsLights[i].setLocation(startX + i * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_HORIZONTAL_SPACE),
                        lightsStartY);
            }
            if(i >= 9 && i < 17){
                btnsKeyboard[i].setLocation(startX + KEYBOARD_BUTTONS_SIZE/2 + (i - 9) * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_HORIZONTAL_SPACE),
                        keyboardStartY + KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_VERTICAL_SPACE);
                labelsLights[i].setLocation(startX + KEYBOARD_BUTTONS_SIZE/2 + (i - 9) * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_HORIZONTAL_SPACE),
                        lightsStartY + KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_VERTICAL_SPACE);
            }
            if(i >= 17){
                btnsKeyboard[i].setLocation(startX  + (i - 17) * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_HORIZONTAL_SPACE),
                        keyboardStartY + 2 * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_VERTICAL_SPACE));
                labelsLights[i].setLocation(startX  + (i - 17) * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_HORIZONTAL_SPACE),
                        lightsStartY + 2 * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_VERTICAL_SPACE));
            }

            jPanelButtonskeyBoard.add(btnsKeyboard[i]);
            jPanelButtonsLights.add(labelsLights[i]);

        }

        c.add(jPanelButtonskeyBoard);
        c.add(jPanelButtonsLights);

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

    public void addTextArea(TextArea textArea)
    {
        textAreaVector.add(textArea);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();

        if(e.getActionCommand().equals(ACTION_COMMAND_KEYBOARD_BUTTONS))
        {
            RoundButton buttonPressed = (RoundButton) source;
            char letter = buttonPressed.getText().charAt(0);
            plainText = plainText + letter;

            //System.out.println((int)letter + "!");// debug

            char encryptedLetter = enigma.pushKey(letter);

            plainText = plainText + letter;
            encryptedText = encryptedText + encryptedLetter;

            System.out.println(letter + " -> " + encryptedLetter);// debug


        }

        updateGraphic();
    }
}
