import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class Gui extends JFrame implements ChangeListener {
    public static int PORT = 60000;
    public static final String GUI_NAME = "Enigma machine";

    private static final String ENIGMA_ALPHABET = "QWERTZUIOASDFGHJKPYXCVBNML";

    private static final int ALPHABET_LENGTH = ENIGMA_ALPHABET.length();

    private static final File IMAGE_ICON = new File("assets/images/Enigma_Icon.png");

    private static final int KEYBOARD_BUTTONS_SIZE = 45;
    private static final int KEYBOARD_BUTTONS_HORIZONTAL_SPACE = 12;
    private static final int KEYBOARD_BUTTONS_VERTICAL_SPACE = 18;

    private static final String ACTION_COMMAND_KEYBOARD_BUTTONS = "KEYBOARD_BUTTON";

    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    public static final Color KEYBOARD_NON_PRESSED_BUTTON = new Color(121, 121, 121);
    public static final Color KEYBOARD_PRESSED_BUTTON = new Color(58, 58, 58);
    public static final Color KEYBOARD_BORDER = new Color(35, 35, 35);
    public static final Color LIGHTS_OFF = new Color(250, 250, 221);
    public static final Color LIGHTS_ON = new Color(255, 246, 65);
    public static final Color LIGHTS_BORDER = new Color(255, 247, 31);
    public static final Color TEXTES_COLOR = new Color(255, 255, 255);
    public static final Color IO_PANEL_COLOR = new Color(192, 192, 192);
    public static final Color MSG_SEND_BUTTON = new Color(18, 243, 8);


    private ServerThread t;
    public Socket cSocket;
    public BufferedReader input;
    public PrintWriter output;


    private JPanel jPanelChat;

    private JPanel jPanelButtonskeyBoard;

    private JPanel jPanelButtonsLights;

    private Vector<TextArea> textAreaVector;

    private JTextArea textArea;

    private JScrollPane jScrollPane;

    private RollersSelectionDialog rollersSelectionWindow;

    private Container c;

    private RoundButton[] btnsKeyboard;
    private RoundButton lastPressedKeyboardButton;

    private RoundLabel[] labelsLights;

    private JPanel panelIO;

    private JButton buttonSendMessage;

    private JTextArea textFieldMsgEncrypted, textFieldMsgOriginal;

    private JTextField textFieldIP;

    private JLabel labelConnectionStatus;

    Enigma enigma;
    String plainText, encryptedText;


    //selezione rolli tramite jdialog
    public Gui() throws IOException {
        super(GUI_NAME);

        this.setIconImage(ImageIO.read(IMAGE_ICON));

        enigma = new Enigma();
        plainText = "";
        encryptedText = "";


        setSize(SCREEN_SIZE.width, SCREEN_SIZE.height); // set the screen size
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Set full screen.
        // (do not actually update the frame size until after the constructor)
        //System.out.println("! " + getHeight() + "   " + getHeight());

        c = getContentPane();

        //this.setLayout(null);
        c.setLayout(null);
        c = getContentPane();

        jPanelChat = new JPanel();
        jPanelChat.setLayout(null);
        jPanelChat.setBounds(0, 0, getWidth() / 2, getHeight() * 2 / 3);
        jPanelChat.setBackground(Color.BLUE);
        jPanelChat.setOpaque(true);
        jPanelChat.setVisible(true);

        textArea = new JTextArea();
        textArea.setBounds(0, 0, 100, 100);
        textArea.enableInputMethods(false);

        jScrollPane = new JScrollPane(textArea);
        jScrollPane.setBounds(0, 50, getWidth() / 2, 400);
        jPanelChat.add(jScrollPane);
        c.add(jPanelChat);

        jPanelButtonsLights = new JPanel();
        jPanelButtonsLights.setBounds(800, 290, 700, 200);
        jPanelButtonsLights.setLayout(null);
        //jPanelButtonsLights.setBackground(Color.RED);

        jPanelButtonskeyBoard = new JPanel();
        jPanelButtonskeyBoard.setBounds(800, 550, 700, 200);
        jPanelButtonskeyBoard.setLayout(null);
        //jPanelButtonskeyBoard.setBackground(Color.BLUE);


        btnsKeyboard = new RoundButton[ALPHABET_LENGTH];
        labelsLights = new RoundLabel[ALPHABET_LENGTH];


        lastPressedKeyboardButton = null;
        int startX = (jPanelButtonskeyBoard.getWidth() / 2) -
                ((9 * KEYBOARD_BUTTONS_SIZE + 8 * KEYBOARD_BUTTONS_HORIZONTAL_SPACE) / 2);
        int keyboardStartY = 20;
        int lightsStartY = 20;
        for (int i = 0; i < btnsKeyboard.length; i++) {
            btnsKeyboard[i] = new RoundButton(String.valueOf(ENIGMA_ALPHABET.charAt(i)));
            btnsKeyboard[i].setFocusable(false);
            btnsKeyboard[i].setBackground(KEYBOARD_NON_PRESSED_BUTTON);
            btnsKeyboard[i].serBorderColor(KEYBOARD_BORDER);
            btnsKeyboard[i].setActionCommand(ACTION_COMMAND_KEYBOARD_BUTTONS);
            btnsKeyboard[i].addChangeListener(this);
            btnsKeyboard[i].setSize(KEYBOARD_BUTTONS_SIZE, KEYBOARD_BUTTONS_SIZE);

            labelsLights[i] = new RoundLabel(String.valueOf(ENIGMA_ALPHABET.charAt(i)));
            labelsLights[i].setFocusable(false);
            labelsLights[i].setEnabled(false);
            labelsLights[i].setHorizontalAlignment(SwingConstants.CENTER);
            labelsLights[i].setBackground(LIGHTS_OFF);
            labelsLights[i].serBorderColor(LIGHTS_BORDER);
            labelsLights[i].setSize(KEYBOARD_BUTTONS_SIZE, KEYBOARD_BUTTONS_SIZE);


            if (i < 9) {
                btnsKeyboard[i].setLocation(startX + i * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_HORIZONTAL_SPACE),
                        keyboardStartY);
                labelsLights[i].setLocation(startX + i * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_HORIZONTAL_SPACE),
                        lightsStartY);
            }
            if (i >= 9 && i < 17) {
                btnsKeyboard[i].setLocation(startX + KEYBOARD_BUTTONS_SIZE / 2 + (i - 9) * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_HORIZONTAL_SPACE),
                        keyboardStartY + KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_VERTICAL_SPACE);
                labelsLights[i].setLocation(startX + KEYBOARD_BUTTONS_SIZE / 2 + (i - 9) * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_HORIZONTAL_SPACE),
                        lightsStartY + KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_VERTICAL_SPACE);
            }
            if (i >= 17) {
                btnsKeyboard[i].setLocation(startX + (i - 17) * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_HORIZONTAL_SPACE),
                        keyboardStartY + 2 * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_VERTICAL_SPACE));
                labelsLights[i].setLocation(startX + (i - 17) * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_HORIZONTAL_SPACE),
                        lightsStartY + 2 * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_VERTICAL_SPACE));
            }

            jPanelButtonskeyBoard.add(btnsKeyboard[i]);
            jPanelButtonsLights.add(labelsLights[i]);

        }

        c.add(jPanelButtonskeyBoard);
        c.add(jPanelButtonsLights);

        panelIO = new JPanel();

        panelIO.setBounds(800, 40, 700, 250);

        panelIO.setBackground(IO_PANEL_COLOR);


        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_JUSTIFIED);

        textFieldMsgOriginal = new JTextArea();
        textFieldMsgOriginal.setDisabledTextColor(Color.black);
        textFieldMsgOriginal.setBounds(20, 70, 300, 150);
        textFieldMsgOriginal.setBackground(TEXTES_COLOR);
        textFieldMsgOriginal.setEnabled(false);
        textFieldMsgOriginal.setPreferredSize(new Dimension(300, 20));
        textFieldMsgOriginal.setLineWrap(true);
        textFieldMsgOriginal.setWrapStyleWord(true);

        textFieldMsgEncrypted = new JTextArea();
        textFieldMsgEncrypted.setEnabled(false);
        textFieldMsgEncrypted.setDisabledTextColor(Color.black);
        textFieldMsgEncrypted.setBounds(panelIO.getWidth() - textFieldMsgOriginal.getX() - textFieldMsgOriginal.getWidth(), textFieldMsgOriginal.getY(), textFieldMsgOriginal.getWidth(), textFieldMsgOriginal.getHeight());
        textFieldMsgEncrypted.setBackground(TEXTES_COLOR);
        textFieldMsgEncrypted.setPreferredSize(new Dimension(textFieldMsgOriginal.getWidth(), textFieldMsgOriginal.getHeight()));
        textFieldMsgEncrypted.setLineWrap(true);
        textFieldMsgEncrypted.setWrapStyleWord(true);

        // TODO event click logic
        buttonSendMessage = new JButton("Send");

        buttonSendMessage.setBounds((panelIO.getWidth() - 70) / 2, 20, 70, 30);

        buttonSendMessage.setFocusable(false);

        buttonSendMessage.setBackground(MSG_SEND_BUTTON);


        panelIO.setLayout(null);

        panelIO.add(textFieldMsgEncrypted);

        panelIO.add(textFieldMsgOriginal);

        panelIO.add(buttonSendMessage);

        c.add(panelIO);

        rollersSelectionWindow = new RollersSelectionDialog(this);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //this.setResizable(false); // do not let you re put it full screen
        this.setVisible(true);
    }

    public void updateGraphic() {

    }


    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        int centerX = getWidth() / 2; // get the center of the window.

        g2d.drawLine(centerX, 0, centerX, getHeight()); // draw the lines.

        g2d.drawLine(centerX, getHeight() * 2 / 3, 0, getHeight() * 2 / 3);

        //System.out.println(getHeight() + "   " + getHeight());
    }

    public void addTextArea(TextArea textArea) {
        textAreaVector.add(textArea);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        RoundButton source = (RoundButton) e.getSource();

        if ((lastPressedKeyboardButton == null || lastPressedKeyboardButton != source) && source.getModel().isPressed()) {
            lastPressedKeyboardButton = source;

            char originalLetter = source.getText().charAt(0);
            plainText = plainText + originalLetter;

            //System.out.println((int)letter + "!");// debug

            char encryptedLetter = enigma.pushKey(originalLetter);

            plainText = plainText + originalLetter;
            encryptedText = encryptedText + encryptedLetter;

            //System.out.println(letter + " -> " + encryptedLetter);// debug
            textFieldMsgOriginal.setText(textFieldMsgOriginal.getText() + originalLetter);
            textFieldMsgEncrypted.setText(textFieldMsgEncrypted.getText() + encryptedLetter);
        }

        if (!textFieldMsgEncrypted.getText().isEmpty()) {
            int selectedLetterIndex = ENIGMA_ALPHABET.indexOf(textFieldMsgEncrypted.getText().charAt(textFieldMsgEncrypted.getText().length() - 1));
            if (source.getModel().isPressed()) {
                labelsLights[selectedLetterIndex].setBackground(LIGHTS_ON);
            } else {
                lastPressedKeyboardButton = null;
                labelsLights[selectedLetterIndex].setBackground(LIGHTS_OFF);
            }
        }
    }
}

