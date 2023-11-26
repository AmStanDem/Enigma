import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlugBoardJDialog extends JDialog implements ActionListener {

    public static final int DIALOG_WIDTH = 700, DIALOG_EIGHT = 260;
    public static final int BTN_WIDTH = 60, BTN_HEIGHT = 40, BTNS_HORIZONTAL_SPACE = 15, BTNS_VERTICAL_SPACE = 20;
    public static final Color COLORS_BTNS[] = {new Color(222, 231, 231),
            Color.red, Color.blue, Color.yellow, Color.cyan, Color.pink, Color.magenta, Color.green, Color.orange,
            new Color(30, 112, 48), new Color(113, 21, 131), new Color(23, 32, 141),
            new Color(17, 103, 114), new Color(145, 15, 33), new Color(112, 82, 14)
    };

    Container c;
    Enigma enigma;
    JButton[] btns;
    Gui owner;
    JButton selectedButton;
    PlugBoardJDialog(Gui owner){
        super(owner, "plug board configuration");
        setSize(DIALOG_WIDTH, DIALOG_EIGHT);
        setLocation((owner.getWidth() - getWidth())/2, (owner.getHeight() - getHeight())/2);

        this.owner = owner;
        c = getContentPane();
        c.setLayout(null);
        enigma = owner.enigma;
        selectedButton = null;

        createComponents();


        setResizable(false);
        setAlwaysOnTop(true);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setAutoRequestFocus(true);

        setVisible(true);
    }

    private void createComponents(){
        btns = new JButton[Gui.ALPHABET_LENGTH];

        //TODO: why are they not centered ?????
        int startX = ((DIALOG_WIDTH) - (9 * BTN_WIDTH + 8 * BTNS_HORIZONTAL_SPACE)) / 2;
        int keyboardStartY = 30;

        for (int i = 0; i < btns.length; i++)
        {
            btns[i] = new JButton(String.valueOf(Gui.ENIGMA_ALPHABET.charAt(i)));
            btns[i].setFocusable(false);
            btns[i].setSize(BTN_WIDTH, BTN_HEIGHT);
            btns[i].setForeground(Color.black);
            btns[i].addActionListener(this);



            if (i < 9) {
                btns[i].setLocation(startX + i * (BTN_WIDTH + BTNS_HORIZONTAL_SPACE),
                        keyboardStartY);
            }
            if (i >= 9 && i < 17) {
                btns[i].setLocation(startX + BTN_WIDTH / 2 + (i - 9) * (BTN_WIDTH + BTNS_HORIZONTAL_SPACE),
                        keyboardStartY + BTN_HEIGHT + BTNS_VERTICAL_SPACE);
            }
            if (i >= 17) {
                btns[i].setLocation(startX + (i - 17) * (BTN_WIDTH + BTNS_HORIZONTAL_SPACE),
                        keyboardStartY + 2 * (BTN_HEIGHT + BTNS_VERTICAL_SPACE));
            }

            c.add(btns[i]);
        }

        updateGraphic();
    }

    public void updateGraphic(){
        int[] plugBoard = enigma.getPlugBoard();
        for (int i = 0; i < btns.length; i++)
        {
            btns[Gui.ENIGMA_ALPHABET.indexOf(i + 'A')].setBackground(COLORS_BTNS[plugBoard[i]]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton pressedButton =(JButton) e.getSource();

        if(selectedButton == null)
        {
            selectedButton = pressedButton;
        }else{
            enigma.modifyPlugBoard(selectedButton.getText().charAt(0),pressedButton.getText().charAt(0));
            selectedButton = null;
        }

        updateGraphic();
    }
}
