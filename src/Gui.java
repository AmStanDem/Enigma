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
import java.net.Socket;
import java.util.Vector;

public class Gui extends JFrame implements ChangeListener, ActionListener {
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
    public static final Color KEYBOARD_NON_PRESSED_BUTTON = new Color(110, 110, 110);
    public static final Color KEYBOARD_PRESSED_BUTTON = new Color(58, 58, 58);

    public static final Color DEFAULT_KEYBOARD_BACKGROUND_COLOR = new Color(192,192,192);
    public static final Color KEYBOARD_BORDER = new Color(35, 35, 35);
    public static final Color LIGHTS_OFF = new Color(250, 250, 221);
    public static final Color LIGHTS_ON = new Color(255, 246, 65);
    public static final Color LIGHTS_BORDER = new Color(255, 247, 31);
    public static final Color TEXTES_COLOR = new Color(255, 255, 255);
    public static final Color IO_PANEL_COLOR = new Color(192, 192, 192);
    public static final Color MSG_SEND_BUTTON = new Color(18, 243, 8);
    public static final Color BTN_ENIGMA_CONFIGS = new Color(51, 80, 225);
    public static final Color BTN_ENIGMA_CONFIGS_MARGIN = new Color(29, 29, 63);

    private boolean pressingABtn;

    private int codeLetterPressedBtn;


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

    private RoundLabel[] labelsLights;

    private JPanel panelIO;
    private JPanel[] pnlRollers;
    private JTextArea[] textRollers;

    private JButton btnSendMessage;
    private RoundButton btnSetRollers, btnSetPlugBoard;

    private JTextArea textFieldMsgEncrypted, textFieldMsgOriginal;

    private JTextField textFieldIP;

    private JLabel labelConnectionStatus;

    Enigma enigma;
    String plainText, encryptedText;


    //selezione rolli tramite jdialog
    public Gui() {
        super(GUI_NAME);

        try {
            this.setIconImage(ImageIO.read(IMAGE_ICON));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        enigma = new Enigma();
        plainText = "";
        encryptedText = "";

        pressingABtn = false;


        setSize(SCREEN_SIZE.width, SCREEN_SIZE.height);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        c = getContentPane();


        c.setLayout(null);
        c = getContentPane();

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyChar = Character.toUpperCase(e.getKeyChar());

                if (!pressingABtn && keyChar >= 'A' && keyChar <= 'Z')
                {
                    //System.out.println("keyPressed");

                    int buttonIndex =  ENIGMA_ALPHABET.indexOf(keyChar);

                    codeLetterPressedBtn = keyChar;

                    //System.out.println(keyChar);
                    buttonPressed(btnsKeyboard[buttonIndex]);
                    changeButtonsColor(btnsKeyboard[buttonIndex],KEYBOARD_NON_PRESSED_BUTTON, KEYBOARD_PRESSED_BUTTON);
                    labelsLights[getLastEncryptedLetterLightIndex()].setBackground(LIGHTS_ON);

                    updateGraphic();

                    pressingABtn = true;
                }


            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                int keyChar = Character.toUpperCase(e.getKeyChar());

                //System.out.println("keyReleased");
                if (keyChar == codeLetterPressedBtn)
                {
                    buttonReleased(btnsKeyboard[ENIGMA_ALPHABET.indexOf(keyChar)]);
                    pressingABtn = false;

                }

            }

        });

        jPanelChat = new JPanel();
        jPanelChat.setLayout(null);
        jPanelChat.setBounds(0, 0, getWidth() / 2, getHeight() * 2 / 3);
        jPanelChat.setBackground(Color.BLUE);
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
        jPanelButtonsLights.setBackground(Color.RED);

        jPanelButtonskeyBoard = new JPanel();
        jPanelButtonskeyBoard.setBounds(800, 550, 720, 200);
        jPanelButtonskeyBoard.setLayout(null);
        jPanelButtonskeyBoard.setBackground(Color.BLUE);


        btnsKeyboard = new RoundButton[ALPHABET_LENGTH];
        labelsLights = new RoundLabel[ALPHABET_LENGTH];


        int startX = (jPanelButtonskeyBoard.getWidth() / 2) -
                ((9 * KEYBOARD_BUTTONS_SIZE + 8 * KEYBOARD_BUTTONS_HORIZONTAL_SPACE) / 2);
        int keyboardStartY = 20;
        int lightsStartY = 70;
        for (int i = 0; i < btnsKeyboard.length; i++)
        {

            btnsKeyboard[i] = new RoundButton(String.valueOf(ENIGMA_ALPHABET.charAt(i)));
            btnsKeyboard[i].setFocusable(false);
            btnsKeyboard[i].setForeground(KEYBOARD_NON_PRESSED_BUTTON);
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



        pnlRollers = new JPanel[enigma.getSelectedRollers().length];
        textRollers = new JTextArea[enigma.getSelectedRollers().length];
        int pnlRollerWidth = 20;
        int pnlRollerHeight = 40;
        int pnlRollerSpace = 10;
        int pnlRollerStartX = jPanelButtonsLights.getWidth()/2 - pnlRollerSpace*(pnlRollers.length-1)/2 - pnlRollerWidth*(pnlRollers.length)/2;
        int pnlRollerStartY = 10;

        int textRollerWidth = pnlRollerWidth - 10;
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
            textRollers[i] = new JTextArea(String.valueOf(enigma.getSelectedRollers()[pnlRollers.length - i - 1].getDisplayedLetter()));
            textRollers[i].setBackground(Color.WHITE);
            textRollers[i].setEnabled(false);
            textRollers[i].setDisabledTextColor(Color.black);
            textRollers[i].setBounds(textRollerOffsetX,textRollerOffsetY,textRollerWidth,textRollerHeight);
            textRollers[i].setAlignmentY(Component.CENTER_ALIGNMENT);
            textRollers[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            pnlRollers[i].add(textRollers[i]);

        }

        panelIO = new JPanel();
        panelIO.setBounds(800, 40, 720, 250);
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
        btnSendMessage = new JButton("Send");
        btnSendMessage.setBounds((panelIO.getWidth() - 70) / 2, 20, 70, 30);
        btnSendMessage.setFocusable(false);
        btnSendMessage.setBackground(MSG_SEND_BUTTON);


        panelIO.setLayout(null);
        panelIO.add(textFieldMsgEncrypted);
        panelIO.add(textFieldMsgOriginal);
        panelIO.add(btnSendMessage);

        c.add(panelIO);

        rollersSelectionWindow = new RollersSelectionDialog(this);

        btnSetRollers = new RoundButton("rollers");
        btnSetRollers.setFocusable(false);
        btnSetRollers.addActionListener(this);
        btnSetRollers.setBounds(620, 10,85,65);
        btnSetRollers.setBackground(BTN_ENIGMA_CONFIGS);
        btnSetRollers.serBorderColor(BTN_ENIGMA_CONFIGS_MARGIN);
        jPanelButtonskeyBoard.add(btnSetRollers);


        btnSetPlugBoard = new RoundButton("plug brd");
        btnSetPlugBoard.setFocusable(false);
        btnSetPlugBoard.addActionListener(this);
        btnSetPlugBoard.setBounds(btnSetRollers.getX(), jPanelButtonskeyBoard.getHeight() - btnSetRollers.getY() - btnSetRollers.getHeight(),btnSetRollers.getWidth(),btnSetRollers.getHeight());
        btnSetPlugBoard.setBackground(BTN_ENIGMA_CONFIGS);
        btnSetPlugBoard.serBorderColor(BTN_ENIGMA_CONFIGS_MARGIN);
        jPanelButtonskeyBoard.add(btnSetPlugBoard);




        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //this.setResizable(false); // do not let you re put it full screen
        this.setVisible(true);
    }

    public void updateGraphic() {
        for(int i = 0; i < textRollers.length; i++){
            textRollers[i].setText(String.valueOf(enigma.getSelectedRollers()[pnlRollers.length - i - 1].getDisplayedLetter()));
        }


    }

    private void buttonPressed(RoundButton button)
    {
        char originalLetter = button.getText().charAt(0);
        plainText = plainText + originalLetter;



        char encryptedLetter = enigma.pushKey(originalLetter);

        plainText = plainText + originalLetter;
        encryptedText = encryptedText + encryptedLetter;

        writeCharacter(originalLetter, encryptedLetter);

        updateGraphic();



    }

    private void buttonReleased(RoundButton button)
    {

        changeButtonsColor(button, DEFAULT_KEYBOARD_BACKGROUND_COLOR, KEYBOARD_NON_PRESSED_BUTTON);
        labelsLights[getLastEncryptedLetterLightIndex()].setBackground(LIGHTS_OFF);

        updateGraphic();
    }




    private void writeCharacter(char originalLetter, char encryptedLetter)
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

    public void addTextArea(TextArea textArea) {
        textAreaVector.add(textArea);
    }

    private void changeButtonsColor(RoundButton button, Color backgroundColor, Color foregroundColor)
    {
       button.setBackground(backgroundColor);
       button.setForeground(foregroundColor);
    }

    private int getLastEncryptedLetterLightIndex()
    {
        return ENIGMA_ALPHABET.indexOf(textFieldMsgEncrypted.getText().charAt(textFieldMsgEncrypted.getText().length() - 1));
    }




    @Override
    public void stateChanged(ChangeEvent e)
    {

        RoundButton btn = (RoundButton) e.getSource();

        if (btn.getModel().isPressed() && !pressingABtn) {
            codeLetterPressedBtn = btn.getText().charAt(0);

            buttonPressed(btn);
            changeButtonsColor(btn,KEYBOARD_NON_PRESSED_BUTTON, KEYBOARD_PRESSED_BUTTON);
            labelsLights[getLastEncryptedLetterLightIndex()].setBackground(LIGHTS_ON);

            pressingABtn = true;
        }

        if (!btn.getModel().isPressed() && pressingABtn && btn.getText().charAt(0) == codeLetterPressedBtn) {
            buttonReleased(btn);
            pressingABtn = false;

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}

