package view;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class StartFrame extends JFrame {

    BackgroundPanel bgp;
    public StartFrame(int frameSize){
        this.setTitle("Let's play Othello!");
        this.setLayout(null);

        Insets inset = this.getInsets();
        this.setSize(frameSize + inset.left + inset.right+120, 430);
        this.setLocationRelativeTo(null);

        JButton startBtn = new JButton("Start");
        startBtn.setSize(120,50);
        startBtn.setLocation(350,270);
        add(startBtn);
        startBtn.addActionListener(e -> {
            GameFrame mainFrame = new GameFrame(800);
            mainFrame.setVisible(true);
            this.dispose();//close the startFrame
        });
        //background picture
        bgp=new BackgroundPanel((new ImageIcon("C:\\Users\\Cooper\\Pictures\\Screenshots\\sport.png")).getImage());
        bgp.setBounds(0,0,900,400 );
        add(bgp);

    }
}


class BackgroundPanel extends JPanel{
    Image im;
    public BackgroundPanel(Image im)
    {
        this.im=im;
        this.setOpaque(true);
    }
    //Draw the background.
    public void paintComponent(Graphics g)
    {
        super.paintComponents(g);
        g.drawImage(im,0,0,this.getWidth(),this.getHeight(),this);

    }
}
