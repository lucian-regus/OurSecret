import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GUI extends JFrame {
    private JPanel rightSidePanel,leftSidePanel;
    private JLabel statusPanel,imagePanel;
    private JTextField inputTextField;
    private JButton loadFileButton,loadDataButton,extractDataButton;
    private JRadioButton lastBitButton,last4BitsButton;
    private Listener listener;
    private static GUI instance;

    private GUI(){
        super("OurSecret");
        setLayout(new GridLayout(1,2));
        listener = new Listener();

        leftSidePanel = new JPanel(new GridBagLayout());
        leftSidePanel.setBackground(new Color(0x181818));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        statusPanel = new JLabel("STATUS");
        statusPanel.setPreferredSize(new Dimension(250,50));
        statusPanel.setHorizontalAlignment(JLabel.CENTER);
        statusPanel.setBackground(new Color(0x53a653));
        statusPanel.setBorder(BorderFactory.createLineBorder(new Color(0x376e37),2));
        statusPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.setForeground(Color.WHITE);

        setImagePanel("");

        loadFileButton = new JButton("Load File");
        loadFileButton.setFocusable(false);
        loadFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadFileButton.setPreferredSize(new Dimension(250,50));
        loadFileButton.setBackground(new Color(0x181818));
        loadFileButton.setForeground(Color.WHITE);
        loadFileButton.addActionListener(listener);

        gbc.gridx = 0;
        gbc.gridy = 0;
        leftSidePanel.add(statusPanel,gbc);
        gbc.gridy = 1;
        leftSidePanel.add(imagePanel,gbc);
        gbc.gridy = 2;
        leftSidePanel.add(loadFileButton,gbc);

        rightSidePanel = new JPanel(new GridBagLayout());
        rightSidePanel.setBackground(new Color(0x181818));

        inputTextField = new JTextField();
        inputTextField.setPreferredSize(new Dimension(250,50));
        inputTextField.setBackground(new Color(0x2B2B2B));
        inputTextField.setForeground(Color.WHITE);
        inputTextField.setBorder(null);

        loadDataButton = new JButton("Load Data");
        loadDataButton.setFocusable(false);
        loadDataButton.setPreferredSize(new Dimension(250,50));
        loadDataButton.setBackground(new Color(0x181818));
        loadDataButton.setForeground(Color.WHITE);
        loadDataButton.addActionListener(listener);

        extractDataButton = new JButton("Extract Data");
        extractDataButton.setFocusable(false);
        extractDataButton.setPreferredSize(new Dimension(250,50));
        extractDataButton.setBackground(new Color(0x181818));
        extractDataButton.setForeground(Color.WHITE);
        extractDataButton.addActionListener(listener);

        lastBitButton = new JRadioButton("Last Bit");
        lastBitButton.setFocusable(false);
        lastBitButton.setBackground(new Color(0x181818));
        lastBitButton.setForeground(Color.WHITE);
        last4BitsButton = new JRadioButton("Last 4 Bits");
        last4BitsButton.setFocusable(false);
        last4BitsButton.setBackground(new Color(0x181818));
        last4BitsButton.setForeground(Color.WHITE);

        ButtonGroup group = new ButtonGroup();
        group.add(lastBitButton);
        group.add(last4BitsButton);
        lastBitButton.setSelected(true);

        JPanel radioPanel = new JPanel();
        radioPanel.setBackground(new Color(0x181818));
        radioPanel.add(lastBitButton);
        radioPanel.add(last4BitsButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightSidePanel.add(inputTextField,gbc);

        gbc.gridy = 1;
        rightSidePanel.add(loadDataButton,gbc);

        gbc.gridy = 2;
        rightSidePanel.add(extractDataButton,gbc);

        gbc.gridy = 3;
        rightSidePanel.add(radioPanel,gbc);

        add(leftSidePanel);
        add(rightSidePanel);

        setLocationRelativeTo(null);
        setSize(700, 500);
        setIconImage(new ImageIcon("../assets/icon.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    public JLabel getStatusPanel(){ return statusPanel; }
    public JTextField getInputTextField(){ return inputTextField; }
    public JRadioButton getLastBitButton(){ return lastBitButton; }
    public JRadioButton getLast4BitsButtonBitButton(){ return last4BitsButton; }

    public void setImagePanel(String image) {
        if(image.isBlank()){
            imagePanel = new JLabel();
            imagePanel.setPreferredSize(new Dimension(300,300));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 1;
            leftSidePanel.add(imagePanel, gbc);

            revalidate();
            repaint();
            return;
        }
        JLabel newImagePanel = null;
        BufferedImage originalImage = null;

        try {
            if(Functions.checkFile().equals("WAV")){
                originalImage = ImageIO.read(new File("../assets/audio.jpg"));
            }
            else {
                originalImage = ImageIO.read(new File(image));
            }
        } catch (Exception e) {
            System.out.println("[!] The image file could not be found");
        }
        int width = 260;
        int height = (int) ((double) originalImage.getHeight() / originalImage.getWidth() * width);
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        newImagePanel = new JLabel(new ImageIcon(scaledImage));
        newImagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (imagePanel != null) {
            leftSidePanel.remove(imagePanel);
        }

        imagePanel = newImagePanel;
        imagePanel.setPreferredSize(new Dimension(300,300));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1;
        leftSidePanel.add(imagePanel, gbc);

        revalidate();
        repaint();
    }

    private class Listener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getSource() == loadFileButton){
                Functions.loadFile();
            }

            if(e.getSource() == loadDataButton){
                Functions.loadData();
            }

            if(e.getSource() == extractDataButton){
                Functions.extractData();
            }
        }
    }
    public static GUI getInstance(){
        if (instance == null) {
            instance = new GUI();
        }
        return instance;
    }

}
