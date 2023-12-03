import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RollersSelectionDialog extends JDialog implements ActionListener {

    public static final int DIALOG_WIDTH = 700, DIALOG_HEIGHT = 400;
    private static final int BUTTON_SIZE = 100;
    private static final int JTEXTFIELD_SIZE_WIDTH = 80;
    private static final int JTEXTFIELD_SIZE_HEIGHT = 15;
    private static final int BTN_INCREASE_DECREASE_SIZE = 45;
    public static final String DIALOG_NAME = "Rollers Configuration";

    private Container container;
    private Enigma enigma;
    private Gui owner;

    private JButton[] rollerButtons;
    private JTextField txtFieldOffset, txtFieldAdvancementOffset;
    private JButton btnIncreaseOffset, btnDecreaseOffset, btnIncreaseAdvancementOffset, btnDecreaseAdvancementOffset;
    private JPanel panel;

    public RollersSelectionDialog(Gui owner) {
        super(owner, DIALOG_NAME);
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocation((owner.getWidth() - getWidth()) / 2, (owner.getHeight() - getHeight()) / 2);

        this.owner = owner;
        container = getContentPane();
        container.setLayout(null);
        enigma = owner.enigma;

        createComponents();

        setResizable(false);
        setAlwaysOnTop(true);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setAutoRequestFocus(true);

        setVisible(true);
    }

    private void createComponents() {
        panel = new JPanel(null);
        panel.setBounds(DIALOG_WIDTH / 3, 0, DIALOG_WIDTH / 2, DIALOG_HEIGHT / 2);
        panel.setBackground(Color.BLUE);

        rollerButtons = new JButton[owner.enigma.getSelectedRollers().length];

        for (int i = 0; i < rollerButtons.length; i++) {
            rollerButtons[i] = new JButton("Roller " + (i + 1));
            rollerButtons[i].setActionCommand(String.valueOf(i));
            rollerButtons[i].setBounds(10 + (i * BUTTON_SIZE + 8), 10, BUTTON_SIZE, BUTTON_SIZE);
            rollerButtons[i].setFocusable(false);
            rollerButtons[i].addActionListener(this);

            panel.add(rollerButtons[i]);
        }

        txtFieldOffset = createTextField(enigma.getAvailableRoller(0).getCurrentOffset(), 100, 120);
        txtFieldAdvancementOffset = createTextField(enigma.getAvailableRoller(0).getAdvancementOffset(),
                txtFieldOffset.getX() + 80, 120);

        btnDecreaseOffset = createButton("-", panel.getX() - 200, 150);
        btnIncreaseOffset = createButton("+", btnDecreaseOffset.getX() + 50, 150);
        btnDecreaseAdvancementOffset = createButton("-", btnIncreaseOffset.getX() + 100, 150);
        btnIncreaseAdvancementOffset = createButton("+", btnIncreaseOffset.getX() + 150, 150);

        panel.add(txtFieldOffset);
        panel.add(txtFieldAdvancementOffset);
        panel.add(btnIncreaseOffset);
        panel.add(btnDecreaseOffset);
        panel.add(btnIncreaseAdvancementOffset);
        panel.add(btnDecreaseAdvancementOffset);

        container.add(panel);
    }

    private JTextField createTextField(int value, int x, int y) {
        JTextField textField = new JTextField(String.valueOf(value));
        textField.setBounds(x, y, JTEXTFIELD_SIZE_WIDTH, JTEXTFIELD_SIZE_HEIGHT);
        textField.setEnabled(false);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setDisabledTextColor(Color.BLACK);
        return textField;
    }

    private JButton createButton(String label, int x, int y) {
        JButton button = new JButton(label);
        button.setBounds(x, y, BTN_INCREASE_DECREASE_SIZE, BTN_INCREASE_DECREASE_SIZE);
        button.addActionListener(this);
        button.setFocusable(false);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnIncreaseOffset || e.getSource() == btnDecreaseOffset) {
            // Handle increase/decrease offset actions
        } else {
            int index = Integer.parseInt(e.getActionCommand());
            txtFieldOffset.setText(String.valueOf(enigma.getAvailableRoller(index).getCurrentOffset()));
            txtFieldAdvancementOffset.setText(String.valueOf(enigma.getAvailableRoller(index).getAdvancementOffset()));
        }
    }
}