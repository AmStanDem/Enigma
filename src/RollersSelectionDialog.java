import javax.swing.*;
import java.awt.*;

public class RollersSelectionDialog extends JDialog
{
    public static String DIALOG_NAME = "rollers selection";

    Container c;


    RollersSelectionDialog(JFrame frame)
    {
        super(frame, DIALOG_NAME);
        c = getContentPane();



    }
}
