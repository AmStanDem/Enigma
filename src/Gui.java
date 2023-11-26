import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Gui extends JFrame implements ChangeListener, ActionListener
{
    public static int PORT = 60000;
    public static final String GUI_NAME = "Enigma machine";
    private static final File FRAME_IMAGE_ICON = new File("assets/images/Enigma_Icon.png");

    public static final String ENIGMA_ALPHABET = "QWERTZUIOASDFGHJKPYXCVBNML";
    public static final String SPECIAL_ALPHABET = "_,.;?!";
    public static final int ENIGMA_ALPHABET_LENGTH = ENIGMA_ALPHABET.length();
    public static final int SPECIAL_ALPHABET_LENGTH = SPECIAL_ALPHABET.length();

    private static final int KEYBOARD_BUTTONS_SIZE = 45;
    private static final int KEYBOARD_BUTTONS_HORIZONTAL_SPACE = 12;
    private static final int KEYBOARD_BUTTONS_VERTICAL_SPACE = 18;

    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    public static final Color COLOR_KEYBOARD_PRESSED_BUTTON = new Color(120, 120, 120);
    public static final Color COLOR_KEYBOARD_FOREGROUND = new Color(58, 58, 58);
    public static final Color COLOR_SPECIAL_KEYBOARD_FOREGROUND = new Color(29, 84, 75);
    public static final Color COLOR_KEYBOARD_BACKGROUND = new Color(180,180,180);
    public static final Color COLOR_KEYBOARD_BORDER = new Color(35, 35, 35);
    public static final Color COLOR_SPECIAL_KEYBOARD_BORDER = new Color(48, 67, 140);
    public static final Color COLOR_LIGHTS_OFF = new Color(250, 250, 221);
    public static final Color COLOR_LIGHTS_ON = new Color(255, 246, 65);
    public static final Color COLOR_LIGHTS_BORDER = new Color(255, 247, 31);
    public static final Color COLOR_TEXTS = new Color(255, 255, 255);
    public static final Color COLOR_IO_PANEL_COLOR = new Color(192, 192, 192);
    public static final Color COLOR_MSG_SEND_BUTTON = new Color(18, 243, 8);
    public static final Color COLOR_BTN_ENIGMA_CONFIGS = new Color(51, 80, 225);
    public static final Color COLOR_BTN_ENIGMA_CONFIGS_MARGIN = new Color(29, 29, 63);
    public static final Color COLOR_BTN_RECEIVER = new Color(239, 123, 6);
    public static final Color COLOR_BTN_SENDER = new Color(232, 196, 12);
    public static final Color COLOR_BTN_DISCONNECT = new Color(238, 8, 49);
    public static final Color COLOR_BTN_SET_CONNECTION = new Color(8, 238, 39);
    public static final Color COLOR_BTN_GENERIC_DISABLED = new Color(108, 105, 105);

    private static final int STATE_CONNECTION_CLOSED = 0;
    private static final int STATE_CONNECTION_BIND = 1;
    private static final int STATE_CONNECTION_OPEN = 2;
    private static final int STATE_CONNECTION_NOT_USED = 3;

    private boolean pressingABtn;
    private char currentlyPressedLetter;

    private int connectionState;
    private ServerThread serverThread;
    public ServerSocket sSocket;
    public Socket otherSocket;
    public BufferedReader input;
    public PrintWriter output;


    private final JPanel jPanelChat;
    private final JPanel jPanelButtonskeyBoard;
    private final JPanel jPanelButtonsLights;
    private final JTextArea textArea;
    private final JScrollPane jScrollPane;
    private Container c;
    private final RoundButton[] btnsKeyboard;
    private final RoundButton[] btnsSpecial;

    private final RoundLabel[] labelsLights;

    private final JPanel panelIO;
    private final JPanel panelConnection;
    private final JPanel[] pnlRollers;
    private final JTextField[] textRollers;

    private final JButton btnSendMessage;
    private final RoundButton btnSetRollers;
    private final RoundButton btnSetPlugBoard;

    private final JTextArea textFieldMsgEncrypted;
    private final JTextArea textFieldMsgOriginal;

    private final JTextField textMyIP;
    private final JTextField textOtherIP;
    private boolean havingIp;

    private JLabel labelConnectionStatus;
    private final RoundButton btnReceiver;
    private final RoundButton btnSender;
    private final JButton btnDisconnect;

    Enigma enigma;


    //selezione rolli tramite jdialog
    public Gui() {
        super(GUI_NAME);

        try {
            this.setIconImage(ImageIO.read(FRAME_IMAGE_ICON));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        enigma = new Enigma();

        pressingABtn = false;


        setSize(SCREEN_SIZE.width, SCREEN_SIZE.height);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        c = getContentPane();


        c.setLayout(null);
        c = getContentPane();

        connectionState = STATE_CONNECTION_NOT_USED;

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                char keyChar = Character.toUpperCase(e.getKeyChar());
                keyChar = keyChar == ' ' ? '_' : keyChar;//traduce the space key
                RoundButton pressedBtn;

                if (!pressingABtn)
                {
                    if(ENIGMA_ALPHABET.contains(String.valueOf(keyChar))){
                        pressedBtn = btnsKeyboard[ENIGMA_ALPHABET.indexOf(keyChar)];
                        buttonPressed(pressedBtn);
                    }
                    if(SPECIAL_ALPHABET.contains(String.valueOf(keyChar))){
                        pressedBtn = btnsSpecial[SPECIAL_ALPHABET.indexOf(keyChar)];
                        buttonPressed(pressedBtn);
                    }
                    //System.out.println("keyPressed");



                    updateGraphic();
                }


            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                char keyChar = Character.toUpperCase(e.getKeyChar());
                keyChar = keyChar == ' ' ? '_' : keyChar;//traduce the space key

                //System.out.println("keyReleased");
                if (keyChar == currentlyPressedLetter)
                {
                    if(ENIGMA_ALPHABET.contains(String.valueOf(keyChar))){
                        buttonReleased(btnsKeyboard[ENIGMA_ALPHABET.indexOf(keyChar)]);
                    }
                    if(SPECIAL_ALPHABET.contains(String.valueOf(keyChar))){
                        buttonReleased(btnsSpecial[SPECIAL_ALPHABET.indexOf(keyChar)]);
                    }

                    updateGraphic();

                }

            }

        });

        jPanelChat = new JPanel();
        jPanelChat.setLayout(null);
        jPanelChat.setBounds(0, 0, getWidth() / 2, getHeight() * 2 / 3 - 100);
        jPanelChat.setBackground(Color.GREEN);
        jPanelChat.setOpaque(true);
        jPanelChat.setVisible(true);

        textArea = new JTextArea();
        textArea.setBounds(0, 0, 100, 100);
        textArea.setEnabled(false);
        textArea.setDisabledTextColor(Color.BLACK);

        jScrollPane = new JScrollPane(textArea);
        jScrollPane.setBounds(0, 50, getWidth() / 2, 400);
        jPanelChat.add(jScrollPane);
        c.add(jPanelChat);

        jPanelButtonsLights = new JPanel();
        jPanelButtonsLights.setBounds(800, 290, 720, 250);
        jPanelButtonsLights.setLayout(null);
        //jPanelButtonsLights.setBackground(Color.RED);



        jPanelButtonskeyBoard = new JPanel();
        jPanelButtonskeyBoard.setBounds(800, 550, 720, 200);
        jPanelButtonskeyBoard.setLayout(null);

        btnsKeyboard = new RoundButton[ENIGMA_ALPHABET_LENGTH];
        labelsLights = new RoundLabel[ENIGMA_ALPHABET_LENGTH];
        btnsSpecial = new RoundButton[SPECIAL_ALPHABET_LENGTH];


        int startX = (jPanelButtonskeyBoard.getWidth() / 2) -
                ((9 * KEYBOARD_BUTTONS_SIZE + 8 * KEYBOARD_BUTTONS_HORIZONTAL_SPACE) / 2);
        int keyboardStartY = 20;
        int lightsStartY = 70;
        for (int i = 0; i < btnsKeyboard.length; i++)
        {

            btnsKeyboard[i] = new RoundButton(String.valueOf(ENIGMA_ALPHABET.charAt(i)));
            btnsKeyboard[i].setFocusable(false);
            btnsKeyboard[i].serBorderColor(COLOR_KEYBOARD_BORDER);
            btnsKeyboard[i].addChangeListener(this);
            btnsKeyboard[i].setSize(KEYBOARD_BUTTONS_SIZE, KEYBOARD_BUTTONS_SIZE);
            btnsKeyboard[i].setBackground(COLOR_KEYBOARD_BACKGROUND);
            btnsKeyboard[i].setForeground(COLOR_KEYBOARD_FOREGROUND);

            labelsLights[i] = new RoundLabel(String.valueOf(ENIGMA_ALPHABET.charAt(i)));
            labelsLights[i].setFocusable(false);
            labelsLights[i].setEnabled(false);
            labelsLights[i].setHorizontalAlignment(SwingConstants.CENTER);
            labelsLights[i].setBackground(COLOR_LIGHTS_OFF);
            labelsLights[i].serBorderColor(COLOR_LIGHTS_BORDER);
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

        int buttonXColumn = 3;
        for (int i = 0; i < btnsSpecial.length; i++)
        {
            btnsSpecial[i] = new RoundButton(String.valueOf(SPECIAL_ALPHABET.charAt(i)));
            btnsSpecial[i].setFocusable(false);
            btnsSpecial[i].serBorderColor(COLOR_SPECIAL_KEYBOARD_BORDER);
            btnsSpecial[i].addChangeListener(this);
            btnsSpecial[i].setSize(KEYBOARD_BUTTONS_SIZE, KEYBOARD_BUTTONS_SIZE);
            btnsSpecial[i].setBackground(COLOR_KEYBOARD_BACKGROUND);
            btnsSpecial[i].setForeground(COLOR_SPECIAL_KEYBOARD_FOREGROUND);


            btnsSpecial[i].setLocation(5 + i/buttonXColumn*(KEYBOARD_BUTTONS_SIZE + 5), 20 + (i%buttonXColumn) * (KEYBOARD_BUTTONS_SIZE + KEYBOARD_BUTTONS_VERTICAL_SPACE));


            jPanelButtonskeyBoard.add(btnsSpecial[i]);
        }


        c.add(jPanelButtonskeyBoard);
        c.add(jPanelButtonsLights);



        pnlRollers = new JPanel[enigma.getSelectedRollers().length];
        textRollers = new JTextField[enigma.getSelectedRollers().length];
        int pnlRollerWidth = 20;
        int pnlRollerHeight = 40;
        int pnlRollerSpace = 10;
        int pnlRollerStartX = jPanelButtonsLights.getWidth()/2 - pnlRollerSpace*(pnlRollers.length-1)/2 - pnlRollerWidth*(pnlRollers.length)/2;
        int pnlRollerStartY = 10;

        int textRollerWidth = pnlRollerWidth - 6;
        int textRollerHeight = pnlRollerHeight - 25;
        int textRollerOffsetX = (pnlRollerWidth - textRollerWidth)/2;
        int textRollerOffsetY = (pnlRollerHeight - textRollerHeight)/2;
        for(int i = 0; i < pnlRollers.length; i++){
            pnlRollers[i] = new JPanel();
            pnlRollers[i].setLayout(null);
            pnlRollers[i].setBackground(Color.BLACK);
            pnlRollers[i].setBounds(pnlRollerStartX + i*(pnlRollerWidth + pnlRollerSpace), pnlRollerStartY, pnlRollerWidth, pnlRollerHeight);
            jPanelButtonsLights.add(pnlRollers[i]);

            //TODO allain the text
            textRollers[i] = new JTextField(String.valueOf(enigma.getSelectedRollers()[pnlRollers.length - i - 1].getDisplayedLetter()));
            textRollers[i].setBackground(Color.WHITE);
            textRollers[i].setEnabled(false);
            textRollers[i].setDisabledTextColor(Color.black);
            textRollers[i].setBounds(textRollerOffsetX,textRollerOffsetY,textRollerWidth,textRollerHeight);
            textRollers[i].setHorizontalAlignment(JTextField.CENTER);

            pnlRollers[i].add(textRollers[i]);

        }

        panelIO = new JPanel();
        panelIO.setBounds(800, 40, 720, 250);
        panelIO.setBackground(COLOR_IO_PANEL_COLOR);


        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_JUSTIFIED);

        textFieldMsgOriginal = new JTextArea();
        textFieldMsgOriginal.setDisabledTextColor(Color.black);
        textFieldMsgOriginal.setBounds(20, 70, 300, 150);
        textFieldMsgOriginal.setBackground(COLOR_TEXTS);
        textFieldMsgOriginal.setEnabled(false);
        textFieldMsgOriginal.setPreferredSize(new Dimension(300, 20));
        textFieldMsgOriginal.setLineWrap(true);
        textFieldMsgOriginal.setWrapStyleWord(true);

        textFieldMsgEncrypted = new JTextArea();
        textFieldMsgEncrypted.setEnabled(false);
        textFieldMsgEncrypted.setDisabledTextColor(Color.black);
        textFieldMsgEncrypted.setBounds(panelIO.getWidth() - textFieldMsgOriginal.getX() - textFieldMsgOriginal.getWidth(), textFieldMsgOriginal.getY(), textFieldMsgOriginal.getWidth(), textFieldMsgOriginal.getHeight());
        textFieldMsgEncrypted.setBackground(COLOR_TEXTS);
        textFieldMsgEncrypted.setPreferredSize(new Dimension(textFieldMsgOriginal.getWidth(), textFieldMsgOriginal.getHeight()));
        textFieldMsgEncrypted.setLineWrap(true);
        textFieldMsgEncrypted.setWrapStyleWord(true);

        // TODO event click logic
        btnSendMessage = new JButton("Send");
        btnSendMessage.setBounds((panelIO.getWidth() - 70) / 2, 20, 70, 30);
        btnSendMessage.setFocusable(false);
        btnSendMessage.setBackground(COLOR_MSG_SEND_BUTTON);


        panelIO.setLayout(null);
        panelIO.add(textFieldMsgEncrypted);
        panelIO.add(textFieldMsgOriginal);
        panelIO.add(btnSendMessage);

        c.add(panelIO);

        btnSetRollers = new RoundButton("rollers");
        btnSetRollers.setFocusable(false);
        btnSetRollers.addActionListener(this);
        btnSetRollers.setBounds(620, 10,85,65);
        btnSetRollers.setBackground(COLOR_BTN_ENIGMA_CONFIGS);
        btnSetRollers.serBorderColor(COLOR_BTN_ENIGMA_CONFIGS_MARGIN);
        jPanelButtonskeyBoard.add(btnSetRollers);


        btnSetPlugBoard = new RoundButton("plug brd");
        btnSetPlugBoard.setFocusable(false);
        btnSetPlugBoard.addActionListener(this);
        btnSetPlugBoard.setBounds(btnSetRollers.getX(), jPanelButtonskeyBoard.getHeight() - btnSetRollers.getY() - btnSetRollers.getHeight(),btnSetRollers.getWidth(),btnSetRollers.getHeight());
        btnSetPlugBoard.setBackground(COLOR_BTN_ENIGMA_CONFIGS);
        btnSetPlugBoard.serBorderColor(COLOR_BTN_ENIGMA_CONFIGS_MARGIN);
        jPanelButtonskeyBoard.add(btnSetPlugBoard);


        // Gui connection zone

        panelConnection = new JPanel(null);
        panelConnection.setSize(700,220);
        panelConnection.setLocation(30,550);



        // debug color

        panelConnection.setBackground(Color.BLUE);
        btnReceiver = new RoundButton("Receiver");
        btnReceiver.setBounds(30,20,110,45);
        btnReceiver.setBackground(COLOR_BTN_RECEIVER);
        btnReceiver.setFocusable(false);
        btnReceiver.addActionListener(this);

        btnSender = new RoundButton("Sender");
        btnSender.setBounds(btnReceiver.getX(),btnReceiver.getY() + btnReceiver.getHeight() + 25,btnReceiver.getWidth(),btnReceiver.getHeight());
        btnSender.setBackground(COLOR_BTN_SENDER);
        btnSender.setFocusable(false);
        btnSender.addActionListener(this);

        btnDisconnect = new JButton("Disconnect");
        btnDisconnect.setBounds(btnSender.getX(),btnSender.getY() + btnSender.getHeight() + 25,btnSender.getWidth(),btnSender.getHeight());
        btnDisconnect.setEnabled(false);
        btnDisconnect.setBackground(COLOR_BTN_GENERIC_DISABLED);
        btnDisconnect.setFocusable(false);
        btnDisconnect.addActionListener(this);

        textMyIP = new JTextField("");
        try {
            textMyIP.setText(Inet4Address.getLocalHost().getHostAddress());
            havingIp = true;
        } catch (UnknownHostException e) {
            havingIp = false;
        }
        textMyIP.setEnabled(false);
        textMyIP.setDisabledTextColor(Color.BLACK);
        textMyIP.setBounds(btnReceiver.getX() + btnReceiver.getWidth() + 30, btnReceiver.getY() + btnReceiver.getHeight()/2 - 15, 100,30);

        textOtherIP = new JTextField();
        textOtherIP.setDisabledTextColor(Color.BLACK);
        textOtherIP.setBounds(btnSender.getX() + btnSender.getWidth() + 30, btnSender.getY() + btnSender.getHeight()/2 - 15, 100, 30);


        panelConnection.add(btnReceiver);
        panelConnection.add(btnSender);
        panelConnection.add(btnDisconnect);
        panelConnection.add(textMyIP);
        panelConnection.add(textOtherIP);

        c.add(panelConnection);



        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //this.setResizable(false); // do not let you re put it full screen
        this.setVisible(true);

        updateGraphic();

        new RollersSelectionDialog(this);//debug

    }

    public void updateGraphic() {
        for(int i = 0; i < textRollers.length; i++){
            textRollers[i].setText(String.valueOf(enigma.getSelectedRollers()[pnlRollers.length - i - 1].getDisplayedLetter()));
        }


        //update connection things
        btnSender.setEnabled(connectionState == STATE_CONNECTION_CLOSED);
        btnReceiver.setEnabled(connectionState == STATE_CONNECTION_CLOSED);
        btnDisconnect.setEnabled(true);

        btnSender.setBackground(btnSender.isEnabled() ? COLOR_BTN_SENDER : COLOR_BTN_GENERIC_DISABLED);
        btnReceiver.setBackground(btnReceiver.isEnabled() ? COLOR_BTN_RECEIVER : COLOR_BTN_GENERIC_DISABLED);
        if(connectionState == STATE_CONNECTION_NOT_USED){
            btnDisconnect.setBackground(COLOR_BTN_SET_CONNECTION);
            btnDisconnect.setText("connect");
        }else{
            btnDisconnect.setBackground(btnDisconnect.isEnabled() ? COLOR_BTN_DISCONNECT : COLOR_BTN_GENERIC_DISABLED);
            btnDisconnect.setText("disconnect");
        }

        textOtherIP.setEnabled(connectionState == STATE_CONNECTION_CLOSED);

    }

    private void buttonPressed(RoundButton button)
    {
        pressingABtn = true;
        currentlyPressedLetter = button.getText().charAt(0);
        char encryptedLetter = '?';

        if(ENIGMA_ALPHABET.contains(button.getText()))
        {
            encryptedLetter = enigma.pushKey(currentlyPressedLetter);

            writeCharacterOnGui(currentlyPressedLetter, encryptedLetter);
            changeLightsColor(labelsLights[getLastEncryptedLetterLightIndex()], true);
        }
        else if(SPECIAL_ALPHABET.contains(button.getText()))
        {
            encryptedLetter = currentlyPressedLetter;
            writeCharacterOnGui(currentlyPressedLetter, encryptedLetter);
        }
        changeEnigmaButtonsColor(button, true);

    }

    private void buttonReleased(RoundButton button)
    {

        pressingABtn = false;

        changeEnigmaButtonsColor(button, false);

        if(ENIGMA_ALPHABET.contains(button.getText())) {
            changeLightsColor(labelsLights[getLastEncryptedLetterLightIndex()], false);
        }

    }




    private void writeCharacterOnGui(char originalLetter, char encryptedLetter)
    {
        textFieldMsgOriginal.setText(textFieldMsgOriginal.getText() + originalLetter);
        textFieldMsgEncrypted.setText(textFieldMsgEncrypted.getText() + encryptedLetter);
    }


    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        int centerX = getWidth() / 2; // get the center of the window.

        g2d.drawLine(centerX, 0, centerX, getHeight()); // draw the lines.
        g2d.drawLine(centerX, getHeight() * 2 / 3, 0, getHeight() * 2 / 3);


    }

    private void changeEnigmaButtonsColor(RoundButton button, boolean isPressed)
    {
            button.setBackground(isPressed ? COLOR_KEYBOARD_PRESSED_BUTTON : COLOR_KEYBOARD_BACKGROUND);

    }
    private void changeLightsColor(RoundLabel light, boolean isLightened)
    {
        light.setBackground(isLightened ? COLOR_LIGHTS_ON : COLOR_LIGHTS_OFF);

    }

    private int getLastEncryptedLetterLightIndex()
    {
        return ENIGMA_ALPHABET.indexOf(textFieldMsgEncrypted.getText().charAt(textFieldMsgEncrypted.getText().length() - 1));
    }

    public static boolean validateIp(final String ip)
    {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return ip.matches(PATTERN);
    }
    protected void showErrorJDialog(String msg){
        showErrorJDialog(msg,200,80);
    }
    protected void showErrorJDialog(String msg, int width, int height){
        JDialog dialogTmp = new JDialog(this, "error");
        dialogTmp.add(new JLabel(msg));
        dialogTmp.setSize(width,height);
        dialogTmp.setLocation((this.getWidth() - width)/2, (this.getHeight() - height)/2);
        dialogTmp.setResizable(false);
        dialogTmp.setAlwaysOnTop(true);
        dialogTmp.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialogTmp.setAutoRequestFocus(true);
        dialogTmp.setVisible(true);

    }


    @Override
    public void stateChanged(ChangeEvent e)
    {

        RoundButton btn = (RoundButton) e.getSource();

        if (btn.getModel().isPressed() && !pressingABtn) {
            buttonPressed(btn);

            updateGraphic();
        }

        if (!btn.getModel().isPressed() && pressingABtn && btn.getText().charAt(0) == currentlyPressedLetter) {
            buttonReleased(btn);

            updateGraphic();
        }
    }


    /*
    * Logic for the buttonReceiver, buttonSender and buttonDisconnect-*/
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object source =  e.getSource();

        if (source.equals(btnReceiver))
        {
            // SERVER

            // When the button is pressed it should start the server which is refered to the automaticaly assigned IP.
            if(connectionState == STATE_CONNECTION_CLOSED){
                serverThread = new ServerThread(this);
                serverThread.start();
                connectionState = STATE_CONNECTION_BIND;

                textOtherIP.setText("");
                updateGraphic();
            }


        }
        else if (source.equals(btnSender))
        {
            // CLIENT
            if(connectionState == STATE_CONNECTION_CLOSED){


                //check ip
                if (validateIp(textOtherIP.getText()))
                //if the ip is accettable
                {
                    try {
                        otherSocket = new Socket(textOtherIP.getText(), PORT);
                        connectionState = STATE_CONNECTION_OPEN;

                    } catch (IOException ex) {
                        textOtherIP.setText("");
                        updateGraphic();
                        showErrorJDialog("could not connect to the ip");
                    }

                }else{
                    textOtherIP.setText("");
                    showErrorJDialog("invalid ip");
                    updateGraphic();
                }
            }
        }
        else if (source.equals(btnDisconnect))
        {
            /*
            * when it is pressed it should destroy the connection.
            *
            * */

            if (connectionState == STATE_CONNECTION_BIND){
                //System.out.println("CIAOOOOO");
                serverThread.interrupt();
                try {
                    sSocket.close();
                }
                catch (IOException ex)
                {
                    ; //TMCH
                }
                connectionState = STATE_CONNECTION_CLOSED;
                textOtherIP.setText("");
                updateGraphic();
            }
            else if (connectionState == STATE_CONNECTION_OPEN){

                connectionState = STATE_CONNECTION_CLOSED;
                textOtherIP.setText("");
                updateGraphic();
            }
            else if (connectionState == STATE_CONNECTION_CLOSED){

                connectionState = STATE_CONNECTION_NOT_USED;
                textOtherIP.setText("");
                updateGraphic();
            }
            else if (connectionState == STATE_CONNECTION_NOT_USED){

                connectionState = STATE_CONNECTION_CLOSED;
                textOtherIP.setText("");
                updateGraphic();
            }

        } else if (source.equals(btnSetPlugBoard))
        {
            new PlugBoardJDialog(this);
        }
        else if (source.equals(btnSetRollers))
        {
            new RollersSelectionDialog(this);
        }
    }
}

