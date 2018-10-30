package gameengine;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

class InfoPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int TEXT_AREA_HEIGHT = 40;
    private static final int CHARACTER_WIDTH = 60;
    private static final int FONT_SIZE = 12;

    private final JTextArea textArea  = new JTextArea(TEXT_AREA_HEIGHT, CHARACTER_WIDTH);

    InfoPanel() {
        JScrollPane scrollPane = new JScrollPane(textArea);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        textArea.setEditable(false);
        textArea.setFont(new Font("monospaced", Font.PLAIN, FONT_SIZE));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    void addText(String text) {
        textArea.setText(textArea.getText()+"\n"+text);
    }

    void clearScreen() {
        textArea.setText("");
    }
}
