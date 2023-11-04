import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileHandler {
    private File file;
    private static FileHandler instance;
    private BufferedImage imageData;
    private byte[] audioHeader;
    private byte[] audioData;
    void setFile(File file){
        this.file = file;
    }

    public BufferedImage getImageData(){ return imageData; }
    public byte[] getAudioData(){ return audioData; }
    public byte[] getAudioHeader(){ return audioHeader; }

    public void setImageData(){
        audioHeader = null;
        audioData = null;
        try {
            imageData = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("[!] PNG READING FAILED");
        }
    }

    public void setAudioData(){
        imageData = null;
        audioHeader = new byte[40];
        audioData = new byte[(int)(file.length()-40)];

        try {
            InputStream inputStream = new FileInputStream(file);
            inputStream.read(audioHeader,0,40);
            inputStream.read(audioData);
        } catch (IOException e) {
            System.out.println("[!] WAV READING FAILED");
        }

    }

    File getFile(){
        return file;
    }

    public static FileHandler getInstance(){
        if (instance == null) {
            instance = new FileHandler();
        }
        return instance;
    }

}
