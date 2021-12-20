package view;


import controller.GameController;
import model.GameSound;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    public static GameController controller;
    private ChessBoardPanel chessBoardPanel;
    private StatusPanel statusPanel;
    private JCheckBox checkBox;
    private JComboBox<String> cb;
    private JLabel logLabel;

    public GameFrame(int frameSize) {

        this.setTitle("2021F CS102A Project Reversi");
        this.setLayout(null);

        //获取窗口边框的长度，将这些值加到主窗口大小上，这能使窗口大小和预期相符
        Insets inset = this.getInsets();
        this.setSize(frameSize + inset.left + inset.right, frameSize + inset.top + inset.bottom);

        this.setLocationRelativeTo(null);


        chessBoardPanel = new ChessBoardPanel((int) (this.getWidth() * 0.8), (int) (this.getHeight() * 0.7));
        chessBoardPanel.setLocation((this.getWidth() - chessBoardPanel.getWidth()) / 2, (this.getHeight() - chessBoardPanel.getHeight()) / 3);

        statusPanel = new StatusPanel((int) (this.getWidth() * 0.8), (int) (this.getHeight() * 0.1));
        statusPanel.setLocation((this.getWidth() - chessBoardPanel.getWidth()) / 2, 0);
        controller = new GameController(chessBoardPanel, statusPanel, this);
        controller.setGamePanel(chessBoardPanel);

        this.add(chessBoardPanel);
        this.add(statusPanel);


        JButton restartBtn = new JButton("Restart");
        restartBtn.setSize(120, 50);
        restartBtn.setLocation((this.getWidth() - chessBoardPanel.getWidth()) / 2 - 80, (this.getHeight() + chessBoardPanel.getHeight()) / 2 - 20);
        add(restartBtn);
        restartBtn.addActionListener(e -> {
            controller.resetGame();
            printLog("click restart Btn");
        });

        JButton undoBtn = new JButton("Undo");
        undoBtn.setSize(120, 50);
        undoBtn.setLocation(restartBtn.getX() + restartBtn.getWidth() + 30, restartBtn.getY());
        add(undoBtn);
        undoBtn.addActionListener(e -> {
            if (controller.getWhitePlayerLevel() > 0) {
                JOptionPane.showMessageDialog(this, "Undo operation cannot be used in PvsAI mode.", "Tips", JOptionPane.WARNING_MESSAGE);
            }
            controller.undoOperation();
            printLog("click undo Btn");
        });
        JButton loadGameBtn = new JButton("Load");
        loadGameBtn.setSize(120, 50);
        loadGameBtn.setLocation(undoBtn.getX() + undoBtn.getWidth() + 30, restartBtn.getY());
        add(loadGameBtn);
        loadGameBtn.addActionListener(e -> {
            printLog("clicked Load Btn");
            String filePath = JOptionPane.showInputDialog(this, "input the path here");
            if (filePath == null || filePath.isEmpty()) {
                return;
            }
            boolean ret = controller.readFileData(filePath);
            if (ret) {
                JOptionPane.showMessageDialog(this, "Archive loaded successfully", "Tips", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton saveGameBtn = new JButton("Save");
        saveGameBtn.setSize(120, 50);
        saveGameBtn.setLocation(loadGameBtn.getX() + loadGameBtn.getWidth() + 30, restartBtn.getY());
        add(saveGameBtn);
        saveGameBtn.addActionListener(e -> {
            printLog("clicked Save Btn");
            String filePath = JOptionPane.showInputDialog(this, "input the path here");
            if (filePath == null || filePath.isEmpty()) {
                return;
            }
            if (!filePath.endsWith(".txt")) {
                filePath += ".txt";
            }
            controller.writeDataToFile(filePath);
            JOptionPane.showMessageDialog(this, "Archive saved successfully", "Tips", JOptionPane.WARNING_MESSAGE);
        });

        JLabel label = new JLabel("Cheat Model");
        label.setSize(120, 40);
        label.setFont(new Font("Calibri", Font.ITALIC, 18));
        label.setLocation(saveGameBtn.getX() + saveGameBtn.getWidth() + 30, restartBtn.getY());
        add(label);

        checkBox = new JCheckBox();
        checkBox.setSize(20, 20);
        checkBox.setLocation(saveGameBtn.getX() + saveGameBtn.getWidth() + 75, restartBtn.getY() + 30);
        add(checkBox);
        checkBox.addItemListener(e -> {
            controller.setCheatMode(checkBox.isSelected());
        });

        label = new JLabel("logger:");
        label.setSize(220, 40);
        label.setLocation(restartBtn.getX(), restartBtn.getY() + 40);
        add(label);

        logLabel = new JLabel();
        logLabel.setSize(500, 40);
        logLabel.setLocation(label.getX(), label.getY() + 20);
        add(logLabel);

        label = new JLabel("WHITE PLAYER: ");
        label.setSize(120, 40);
        label.setFont(new Font("Calibri", Font.ITALIC, 18));
        label.setLocation(0, 100);
        add(label);

        String[] choice = new String[]{"Human", "AI(Level1)", "AI(Level2)"};
        cb = new JComboBox<>();
        for (String ss : choice) {
            cb.addItem(ss);
        }
        cb.setLocation(6, 150);
        cb.setSize(110, 40);
        add(cb);
        cb.addItemListener(e -> {
            int index = cb.getSelectedIndex();
            printLog("WHITER PLAYER is " + choice[index]);
            controller.resetGame();
        });


        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GameSound.playBackgroundSound();
    }

    public void setCheatMode(boolean cheatMode) {
        checkBox.setSelected(cheatMode);
    }

    public void setWhitePlayerLevel(int index) {
        cb.setSelectedIndex(index);
    }

    public int getWhitePlayerLevel() {
        return cb.getSelectedIndex();
    }

    public void printLog(String msg) {
        logLabel.setText(msg);
    }
}
