import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Arrays;

public class Functions {
    static byte[] PNG = {-119, 80, 78, 71, 13, 10, 26, 10};
    static byte[] WAV_FIRST = {82, 73, 70, 70};
    static byte[] WAV_LAST = {87, 65, 86, 69};
    static JFileChooser fileChooser = new JFileChooser();

    public static void loadFile() {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            FileHandler.getInstance().setFile(fileChooser.getSelectedFile());
            String extension = checkFile();

            if (extension.equals("PNG")) {
                GUI.getInstance().setImagePanel(FileHandler.getInstance().getFile().getAbsolutePath());
                FileHandler.getInstance().setImageData();
            }
            if (extension.equals("WAV")) {
                GUI.getInstance().setImagePanel(FileHandler.getInstance().getFile().getAbsolutePath());
                FileHandler.getInstance().setAudioData();
            }
            if (extension.equals("null")) {
                GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0xBA1C1B), 2));
                GUI.getInstance().getStatusPanel().setText("FILE FORMAT IS NOT SUPPORTED");
            }
        }

    }

    public static void loadData() {
        if (FileHandler.getInstance().getFile() == null) {
            GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0xBA1C1B), 2));
            GUI.getInstance().getStatusPanel().setText("FILE MISSING");
            GUI.getInstance().getStatusPanel().repaint();
            return;
        }

        if (GUI.getInstance().getInputTextField().getText().isBlank()) {
            GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0xBA1C1B), 2));
            GUI.getInstance().getStatusPanel().setText("NO DATA TO LOAD");
            GUI.getInstance().repaint();
            return;
        }

        StringBuilder binaryString = new StringBuilder();
        for (int i = 0; i < GUI.getInstance().getInputTextField().getText().length(); i++)
            binaryString.append(String.format("%8s", Integer.toBinaryString((int) GUI.getInstance().getInputTextField().getText().charAt(i))).replace(' ', '0'));

        String data_binary = binaryString.toString();
        int[] bits = new int[data_binary.length() + 8];

        for (int i = 0; i < data_binary.length(); i++)
            bits[i] = Character.getNumericValue(data_binary.charAt(i));

        if (checkFile().equals("PNG")) {
            int imageWidth = FileHandler.getInstance().getImageData().getWidth();
            int imageHeight = FileHandler.getInstance().getImageData().getWidth();

            if (GUI.getInstance().getLastBitButton().isSelected()) {
                if (data_binary.length() / 3 + data_binary.length() % 3 + 1 > imageWidth * imageHeight) {
                    GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0xBA1C1B), 2));
                    GUI.getInstance().getStatusPanel().setText("DATA EXCEEDS IMAGE CAPACITY");
                    GUI.getInstance().repaint();
                    return;
                }

                int mode = FileHandler.getInstance().getImageData().getRGB(0, 0);
                int mode_pixel;
                int mode_red = (mode >> 16) & 0xFF;
                int mode_alpha = (mode >> 24) & 0xFF;
                int mode_green = (mode >> 8) & 0xFF;
                int mode_blue = mode & 0xFF;
                mode_red &= 0xF0;
                mode_red |= 0x01;

                mode_pixel = (mode_alpha << 24) | (mode_red << 16) | (mode_green << 8) | mode_blue;
                FileHandler.getInstance().getImageData().setRGB(0, 0, mode_pixel);

                int position = 0;
                int i;
                int j = 0;
                outerloop:
                for (i = 0; i < imageHeight; i++) {
                    for (j = 1; j < imageWidth; j++) {
                        int pixel;
                        int rgb = FileHandler.getInstance().getImageData().getRGB(j, i);
                        int alpha = (rgb >> 24) & 0xFF;
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        if (position == bits.length) break outerloop;
                        red = (red & 0xFE) | bits[position++];
                        if (position == bits.length) {
                            pixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                            FileHandler.getInstance().getImageData().setRGB(j, i, pixel);
                            break outerloop;
                        }
                        green = (green & 0xfe) | bits[position++];
                        if (position == bits.length) {
                            pixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                            FileHandler.getInstance().getImageData().setRGB(j, i, pixel);
                            break outerloop;
                        }
                        blue = (blue & 0xfe) | bits[position++];

                        pixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                        FileHandler.getInstance().getImageData().setRGB(j, i, pixel);
                    }
                }

                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


                int result = fileChooser.showOpenDialog(null);
                if ((result == JFileChooser.APPROVE_OPTION)) {
                    try {
                        File directory = fileChooser.getSelectedFile();
                        String filePath = directory.getAbsolutePath() + File.separator + "output.png";
                        ImageIO.write(FileHandler.getInstance().getImageData(), "png", new File(filePath));
                    } catch (IOException ex) {
                    }
                }
                GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0x376e37), 2));
                GUI.getInstance().getStatusPanel().setText("DATA LOADED SUCCEFULLY");
                GUI.getInstance().repaint();
            } else {
                if (data_binary.length() / 12 + data_binary.length() % 12 > imageWidth * imageHeight) {
                    GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0xBA1C1B), 2));
                    GUI.getInstance().getStatusPanel().setText("DATA EXCEEDS IMAGE CAPACITY");
                    GUI.getInstance().repaint();
                    return;
                }
                int mode = FileHandler.getInstance().getImageData().getRGB(0, 0);
                int mode_pixel;
                int mode_red = (mode >> 16) & 0xFF;
                int mode_alpha = (mode >> 24) & 0xFF;
                int mode_green = (mode >> 8) & 0xFF;
                int mode_blue = mode & 0xFF;
                mode_red &= 0xF0;
                mode_red |= 0x4;

                mode_pixel = (mode_alpha << 24) | (mode_red << 16) | (mode_green << 8) | mode_blue;
                FileHandler.getInstance().getImageData().setRGB(0, 0, mode_pixel);


                int position = 0;
                int i;
                int j = 0;
                outerloop:
                for (i = 0; i < imageHeight; i++) {
                    for (j = 1; j < imageWidth; j++) {
                        int pixel;
                        int rgb = FileHandler.getInstance().getImageData().getRGB(j, i);
                        int alpha = (rgb >> 24) & 0xFF;
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        if (position == bits.length) break outerloop;

                        red &= 0xF0;
                        red |= (bits[position++] << 3) | (bits[position++] << 2) | (bits[position++] << 1) | (bits[position++]);
                        if (position == bits.length) {
                            pixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                            FileHandler.getInstance().getImageData().setRGB(j, i, pixel);
                            break outerloop;
                        }

                        green &= 0xF0;
                        green |= (bits[position++] << 3) | (bits[position++] << 2) | (bits[position++] << 1) | (bits[position++]);
                        if (position == bits.length) {
                            pixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                            FileHandler.getInstance().getImageData().setRGB(j, i, pixel);
                            break outerloop;
                        }

                        blue &= 0xF0;
                        blue |= (bits[position++] << 3) | (bits[position++] << 2) | (bits[position++] << 1) | (bits[position++]);

                        pixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                        FileHandler.getInstance().getImageData().setRGB(j, i, pixel);
                    }
                }

                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        File directory = fileChooser.getSelectedFile();
                        String filePath = directory.getAbsolutePath() + File.separator + "output.png";
                        ImageIO.write(FileHandler.getInstance().getImageData(), "png", new File(filePath));
                    } catch (IOException ex) {
                    }
                }
                GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0x376e37), 2));
                GUI.getInstance().getStatusPanel().setText("DATA LOADED SUCCEFULLY");
                GUI.getInstance().repaint();
            }
        }
        if (checkFile().equals("WAV")) {
            if (GUI.getInstance().getLastBitButton().isSelected()) {
                if (bits.length > FileHandler.getInstance().getAudioData().length) {
                    GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0xBA1C1B), 2));
                    GUI.getInstance().getStatusPanel().setText("DATA EXCEEDS IMAGE CAPACITY");
                    GUI.getInstance().repaint();
                    return;
                }
                FileHandler.getInstance().getAudioData()[0] &= 0xF0;
                FileHandler.getInstance().getAudioData()[0] |= 0x01;

                int position = 0;
                for (int i = 1; i < FileHandler.getInstance().getAudioData().length; i++) {
                    FileHandler.getInstance().getAudioData()[i] &= 0xFE;
                    FileHandler.getInstance().getAudioData()[i] |= bits[position++];
                    if (position == bits.length) break;
                }
            }
            if (GUI.getInstance().getLast4BitsButtonBitButton().isSelected()) {
                if (bits.length / 4 + bits.length % 4 > FileHandler.getInstance().getAudioData().length) {
                    GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0xBA1C1B), 2));
                    GUI.getInstance().getStatusPanel().setText("DATA EXCEEDS IMAGE CAPACITY");
                    GUI.getInstance().repaint();
                    return;
                }

                FileHandler.getInstance().getAudioData()[0] &= 0xF0;
                FileHandler.getInstance().getAudioData()[0] |= 0x4;

                int position = 0;
                for (int i = 1; i < FileHandler.getInstance().getAudioData().length; i++) {
                    FileHandler.getInstance().getAudioData()[i] &= 0xF0;
                    FileHandler.getInstance().getAudioData()[i] |= (bits[position++] << 3) | (bits[position++] << 2) | (bits[position++] << 1) | (bits[position++]);
                    if (position == bits.length) break;
                }
            }

            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    File directory = fileChooser.getSelectedFile();
                    File outputFile = new File(directory.getAbsolutePath() + File.separator + "output.wav");
                    FileOutputStream outputStream = new FileOutputStream(outputFile);
                    outputStream.write(FileHandler.getInstance().getAudioHeader());
                    outputStream.write(FileHandler.getInstance().getAudioData());
                    outputStream.close();

                } catch (IOException f) {
                }
            }
        }
    }
    public static void extractData() {
        if (FileHandler.getInstance().getFile() == null) {
            GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0xBA1C1B), 2));
            GUI.getInstance().getStatusPanel().setText("FILE MISSING");
            GUI.getInstance().getStatusPanel().repaint();
            return;
        }
        String extension = checkFile();
        StringBuilder extractedData = new StringBuilder();

        if (extension.equals("PNG")) {
            int pixel = FileHandler.getInstance().getImageData().getRGB(0, 0);
            int mode = (pixel >> 16) & 0xFF;
            int last4bits = mode & 0x0F;
            boolean found = false;

            if (last4bits == 1) {
                int counter = 0;
                int sum = 0;
                break_for:
                for (int i = 0; i < FileHandler.getInstance().getImageData().getHeight(); i++) {
                    for (int j = 1; j < FileHandler.getInstance().getImageData().getWidth(); j++) {
                        int rgb = FileHandler.getInstance().getImageData().getRGB(j, i);
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;
                        extractedData.append(red & 0x01);
                        counter++;
                        sum += red & 0x01;
                        if (counter % 8 == 0) {
                            if (sum == 0) {
                                found = true;
                                break break_for;
                            }
                            extractedData.append(" ");
                            sum = 0;
                        }
                        extractedData.append(green & 0x01);
                        counter++;
                        sum += green & 0x01;
                        if (counter % 8 == 0) {
                            if (sum == 0) {
                                found = true;
                                break break_for;
                            }
                            extractedData.append(" ");
                            sum = 0;
                        }
                        extractedData.append(blue & 0x01);
                        counter++;
                        sum += blue & 0x01;
                        if (counter % 8 == 0) {
                            if (sum == 0) {
                                found = true;
                                break break_for;
                            }
                            extractedData.append(" ");
                            sum = 0;
                        }
                    }
                }
                if (found) {
                    StringBuilder asciiString = new StringBuilder();
                    String[] binaryValues = extractedData.toString().split(" ");

                    for (String binary : binaryValues) {
                        int decimalValue = Integer.parseInt(binary, 2);
                        char asciiChar = (char) decimalValue;
                        asciiString.append(asciiChar);
                    }

                    GUI.getInstance().getInputTextField().setText(asciiString.toString().trim());
                    GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0x376e37), 2));
                    GUI.getInstance().getStatusPanel().setText("DATA SUCCEFULLY EXTRACTED");
                    GUI.getInstance().repaint();
                    return;
                } else {
                    GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0xBA1C1B), 2));
                    GUI.getInstance().getStatusPanel().setText("NO DATA AVAILABLE TO EXTRACT");
                    GUI.getInstance().repaint();
                    return;
                }
            }
            if (last4bits == 4) {
                int counter = 0;
                int sum = 0;
                break_for:
                for (int i = 0; i < FileHandler.getInstance().getImageData().getHeight(); i++) {
                    for (int j = 1; j < FileHandler.getInstance().getImageData().getWidth(); j++) {
                        int rgb = FileHandler.getInstance().getImageData().getRGB(j, i);
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        extractedData.append(String.format("%4s", Integer.toBinaryString(red & 0x0F)).replace(' ', '0'));
                        counter += 4;
                        sum += red & 0x0F;
                        if (counter % 8 == 0) {
                            if (sum == 0) {
                                found = true;
                                break break_for;
                            }
                            extractedData.append(" ");
                            sum = 0;
                        }
                        extractedData.append(String.format("%4s", Integer.toBinaryString(green & 0x0F)).replace(' ', '0'));
                        counter += 4;
                        sum += green & 0x0F;
                        if (counter % 8 == 0) {
                            if (sum == 0) {
                                found = true;
                                break break_for;
                            }
                            extractedData.append(" ");
                            sum = 0;
                        }
                        extractedData.append(String.format("%4s", Integer.toBinaryString(blue & 0x0F)).replace(' ', '0'));
                        counter += 4;
                        sum += blue & 0x0F;
                        if (counter % 8 == 0) {
                            if (sum == 0) {
                                found = true;
                                break break_for;
                            }
                            extractedData.append(" ");
                            sum = 0;
                        }
                    }
                }
                if (found) {
                    StringBuilder asciiString = new StringBuilder();
                    String[] binaryValues = extractedData.toString().split(" ");

                    for (String binary : binaryValues) {
                        int decimalValue = Integer.parseInt(binary, 2);
                        char asciiChar = (char) decimalValue;
                        asciiString.append(asciiChar);
                    }

                    GUI.getInstance().getInputTextField().setText(asciiString.toString().trim());
                    GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0x376e37), 2));
                    GUI.getInstance().getStatusPanel().setText("DATA SUCCEFULLY EXTRACTED");
                    GUI.getInstance().repaint();
                    return;
                }
            }
            GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0xBA1C1B), 2));
            GUI.getInstance().getStatusPanel().setText("NO DATA AVAILABLE TO EXTRACT");
            GUI.getInstance().repaint();
            return;
        }
        if (extension.equals("WAV")) {
            int last4bits = FileHandler.getInstance().getAudioData()[0] & 0x0F;

            int counter = 0;
            int sum = 0;
            boolean found = false;

            if (last4bits == 1) {
                for (int i = 1; i < FileHandler.getInstance().getAudioData().length; i++) {
                    extractedData.append(FileHandler.getInstance().getAudioData()[i] & 0x01);
                    counter++;
                    sum += FileHandler.getInstance().getAudioData()[i] & 0x01;
                    if (counter % 8 == 0) {
                        if (sum == 0) {
                            found = true;
                            break;
                        }
                        extractedData.append(" ");
                        sum = 0;
                    }
                }
                if (found) {
                    StringBuilder asciiString = new StringBuilder();
                    String[] binaryValues = extractedData.toString().split(" ");

                    for (String binary : binaryValues) {
                        int decimalValue = Integer.parseInt(binary, 2);
                        char asciiChar = (char) decimalValue;
                        asciiString.append(asciiChar);
                    }

                    GUI.getInstance().getInputTextField().setText(asciiString.toString().trim());
                    GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0x376e37), 2));
                    GUI.getInstance().getStatusPanel().setText("DATA SUCCEFULLY EXTRACTED");
                    GUI.getInstance().repaint();
                    return;
                }
                GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0xBA1C1B),2));
                GUI.getInstance().getStatusPanel().setText("NO DATA AVAILABLE TO EXTRACT");
                GUI.getInstance().repaint();
                return;
            }
            if(last4bits == 4){
                for(int i = 1 ; i < FileHandler.getInstance().getAudioData().length ; i++){
                    extractedData.append(String.format("%4s",Integer.toBinaryString(FileHandler.getInstance().getAudioData()[i] & 0x0F)).replace(' ', '0'));
                    counter+= 4;
                    sum += FileHandler.getInstance().getAudioData()[i] & 0x0F;;
                    if(counter % 8 == 0){
                        if(sum == 0){
                            found = true;
                            break;
                        }
                        extractedData.append(" ");
                        sum = 0;
                    }
                }

                if(found){
                    StringBuilder asciiString = new StringBuilder();
                    String[] binaryValues = extractedData.toString().split(" ");

                    for (String binary : binaryValues) {
                        int decimalValue = Integer.parseInt(binary, 2);
                        char asciiChar = (char) decimalValue;
                        asciiString.append(asciiChar);
                    }

                    GUI.getInstance().getInputTextField().setText(asciiString.toString().trim());
                    GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0x376e37),2));
                    GUI.getInstance().getStatusPanel().setText("DATA SUCCEFULLY EXTRACTED");
                    GUI.getInstance().repaint();
                    return;
                }
            }
            GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0xBA1C1B),2));
            GUI.getInstance().getStatusPanel().setText("NO DATA AVAILABLE TO EXTRACT");
            GUI.getInstance().repaint();
        }
    }
    public static String checkFile(){
        String extension = FileHandler.getInstance().getFile().getName().substring(FileHandler.getInstance().getFile().getName().lastIndexOf(".")+1);

        if(extension.equals("png")){
            byte[] magicBytes = new byte[8];
            try {
                FileInputStream check = new FileInputStream(FileHandler.getInstance().getFile());
                check.read(magicBytes);
            } catch (IOException e) {
                System.out.println("[!] FILE NOT FOUND");
            }
            if(Arrays.equals(PNG,magicBytes)){
                GUI.getInstance().getStatusPanel().setText("FILE LOADED SUCCESSFULLY");
                GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0x376e37),2));
                return "PNG";
            }
        }

        if(extension.equals("wav")){
            byte[] magicBytes = new byte[12];
            try {
                FileInputStream check = new FileInputStream(FileHandler.getInstance().getFile());
                check.read(magicBytes);
            } catch (IOException e) {
                System.out.println("File not found");
            }
            for(int i = 0 ; i < 4 ; i++)
                if( magicBytes[i] != WAV_FIRST[i] ){
                    GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0xBA1C1B),2));
                    GUI.getInstance().getStatusPanel().setText("FILE FORMAT IS NOT WAVE");
                    return "null";
                }

            for(int i = 8 ; i < 12 ; i++)
                if( magicBytes[i] != WAV_LAST[i-8] ){
                    GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0xBA1C1B),2));
                    GUI.getInstance().getStatusPanel().setText("FILE FORMAT IS NOT WAVE");
                    return "null";
                }
            GUI.getInstance().getStatusPanel().setText("FILE LOADED SUCCESSFULLY");
            GUI.getInstance().getStatusPanel().setBorder(BorderFactory.createLineBorder(new Color(0x376e37),2));
            return "WAV";
        }
        return "null";
    }
}
