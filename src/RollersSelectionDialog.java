import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RollersSelectionDialog extends JDialog implements ActionListener
{
    public static String DIALOG_NAME = "rollers selection";

    Container c;


    RollersSelectionDialog(JFrame frame)
    {
        super(owner, DIALOG_NAME);
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocation((owner.getWidth() - getWidth())/2, (owner.getHeight() - getHeight())/2);

        this.owner = owner;
        c = getContentPane();
        c.setLayout(null);
        enigma = owner.enigma;

        createComponents();

        setResizable(false);
        setAlwaysOnTop(true);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setAutoRequestFocus(true);

        setVisible(true);

    }

    private void createComponents()
    {

        panel = new JPanel(null);
        panel.setBounds(DIALOG_WIDTH/3, 0, DIALOG_WIDTH/2,DIALOG_HEIGHT/2);
        panel.setBackground(Color.BLUE);

        btnsRollers = new JButton[owner.enigma.getSelectedRollers().length];

        for (int i = 0; i < btnsRollers.length; i++)
        {
            btnsRollers[i] = new JButton("Roller "+(i+1));
            btnsRollers[i].setActionCommand(String.valueOf(i));
            btnsRollers[i].setBounds(10 + (i * BUTTON_SIZE + 8),10,BUTTON_SIZE,BUTTON_SIZE);
            btnsRollers[i].setFocusable(false);
            btnsRollers[i].addActionListener(this);

            panel.add(btnsRollers[i]);
        }

        txtFieldOffset = new JTextField(String.valueOf(enigma.getAvailableRoller(0).getCurrentOffset()));
        txtFieldOffset.setBounds(100, 120, JTEXTFIELD_SIZE_WIDTH, JTEXTFIELD_SIZE_HEIGHT);
        txtFieldOffset.setEnabled(false);
        txtFieldOffset.setHorizontalAlignment(JTextField.CENTER);
        txtFieldOffset.setDisabledTextColor(Color.BLACK);

        txtFieldadvancementOffset = new JTextField(String.valueOf(enigma.getAvailableRoller(0).getAdvancementOffset()));
        txtFieldadvancementOffset.setBounds(txtFieldOffset.getX() + 80, 120, JTEXTFIELD_SIZE_WIDTH, JTEXTFIELD_SIZE_HEIGHT);
        txtFieldadvancementOffset.setEnabled(false);
        txtFieldadvancementOffset.setDisabledTextColor(Color.BLACK);
        txtFieldadvancementOffset.setHorizontalAlignment(JTextField.CENTER);

        btnDecreaseOffSet = new JButton("-");
        btnDecreaseOffSet.setBounds(panel.getX() - 200,150,BTN_INCREASE_DECREASE_SIZE, BTN_INCREASE_DECREASE_SIZE);
        btnDecreaseOffSet.addActionListener(this);
        btnDecreaseOffSet.setFocusable(false);

        btnIncreaseOffset = new JButton("+");
        btnIncreaseOffset.setBounds(btnDecreaseOffSet.getX()+50,150,BTN_INCREASE_DECREASE_SIZE, BTN_INCREASE_DECREASE_SIZE);
        btnIncreaseOffset.addActionListener(this);
        btnIncreaseOffset.setFocusable(false);



        btnDecreaseAdvancementOffSet = new JButton("-");
        btnDecreaseAdvancementOffSet.setBounds(btnIncreaseOffset.getX()+100,150,BTN_INCREASE_DECREASE_SIZE, BTN_INCREASE_DECREASE_SIZE);
        btnDecreaseAdvancementOffSet.addActionListener(this);
        btnDecreaseAdvancementOffSet.setFocusable(false);

        btnIncreaseAdvancementOffset = new JButton("+");
        btnIncreaseAdvancementOffset.setBounds(btnIncreaseOffset.getX()+150,150,BTN_INCREASE_DECREASE_SIZE,BTN_INCREASE_DECREASE_SIZE);
        btnIncreaseAdvancementOffset.addActionListener(this);
        btnIncreaseAdvancementOffset.setFocusable(false);



        panel.add(txtFieldOffset);
        panel.add(txtFieldadvancementOffset);
        panel.add(btnIncreaseOffset);
        panel.add(btnDecreaseOffSet);
        panel.add(btnIncreaseAdvancementOffset);
        panel.add(btnDecreaseAdvancementOffSet);

        c.add(panel);


    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnIncreaseOffset || e.getSource() == btnDecreaseOffSet)
        {

        }
        else
        {
            int index = Integer.parseInt(e.getActionCommand());

            txtFieldOffset.setText(String.valueOf(enigma.getAvailableRoller(index).getCurrentOffset()));
            txtFieldadvancementOffset.setText(String.valueOf(enigma.getAvailableRoller(index).getAdvancementOffset()));
        }


    }
}
