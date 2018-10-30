package gameengine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

class CommandPanel extends JPanel  {

    private static final long serialVersionUID = 1L;
    private static final int FONT_SIZE = 14;

    private final JTextField commandField =  new JTextField();
    private final LinkedList<String> commandBuffer  = new LinkedList<>();

    CommandPanel() {
        class AddActionListener implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                synchronized(commandBuffer) {
                    commandBuffer.add(commandField.getText());
                    commandField.setText("");
                    commandBuffer.notify();
                }
            }
        }
        ActionListener listener = new AddActionListener();
        commandField.addActionListener(listener);
        commandField.setFont(new Font("Times New Roman", Font.PLAIN, FONT_SIZE));
        setLayout(new BorderLayout());
        add(commandField, BorderLayout.CENTER);
    }

    String getCommand() {
        String command;
        synchronized(commandBuffer) {
            while (commandBuffer.isEmpty()) {
                try {
                    commandBuffer.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            command = commandBuffer.pop();
        }
        return command;
    }

}
