package red; 

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

public class Main {

    public static void main(String[] args) {
        JFileChooser chooser = new JFileChooser(); 
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        File LOCATION_IN = null;
        File LOCATION_OUT = null;
        File LOCATION_CFG = null;
        
        chooser.setDialogTitle("Select the input directory!");
        int ask = chooser.showOpenDialog(null);
        if (ask == JFileChooser.APPROVE_OPTION) {
            LOCATION_IN = chooser.getSelectedFile();
            System.out.println(LOCATION_IN);
        } else if(ask == JFileChooser.CANCEL_OPTION) {
        	return;
        }
        chooser.setDialogTitle("Select the output directory!");
        ask = chooser.showOpenDialog(null);
        if (ask == JFileChooser.APPROVE_OPTION) {
            LOCATION_OUT = chooser.getSelectedFile();
            System.out.println(LOCATION_OUT);
        } else if(ask == JFileChooser.CANCEL_OPTION) {
        	return;
        }
        boolean RIGHT_ENABLED = JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                "Do you want to also merge right eye images? These might cause problems if 3D was not enabled when the screenshots were taken!", "Enable right eye?",
                JOptionPane.YES_NO_OPTION);
        boolean TEMPLATE = JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                "Do you want to use the template.cfg and template.png? see README.md for details.", "Use template?",
                JOptionPane.YES_NO_OPTION);
             
        FileInputStream fis;
        BufferedImage buffTemplateOriginal = null;
        
        int TOPX = 0;
        int TOPY = 0;
        int BOTX = 40;
        int BOTY = 240;
                
        if(TEMPLATE){
            chooser.setDialogTitle("Select the folder with template.cfg and .png!");
            ask = chooser.showOpenDialog(null);
            if (ask == JFileChooser.APPROVE_OPTION) {
                LOCATION_CFG = chooser.getSelectedFile();
                System.out.println(LOCATION_CFG);
                try {
                    fis = new FileInputStream(new File(LOCATION_CFG.toString() + File.separator + "template.cfg"));
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if(line.contains("top:")) {
                            TOPX = Integer.parseInt(line.substring(line.indexOf(":") + 1, line.indexOf(",")));
                            TOPY = Integer.parseInt(line.substring(line.indexOf(",") + 1));
                        } else if(line.contains("bottom:")) {
                            BOTX = Integer.parseInt(line.substring(line.indexOf(":") + 1, line.indexOf(",")));
                            BOTY = Integer.parseInt(line.substring(line.indexOf(",") + 1));
                        }
                    }
                 
                    br.close();
                    buffTemplateOriginal = ImageIO.read(new File(LOCATION_CFG + File.separator + "template.png"));
                } catch (FileNotFoundException e1) {
                    TEMPLATE = false;
                    e1.printStackTrace();
                } catch (IOException e) {
                    TEMPLATE = false;
                    e.printStackTrace();
                }
            } else if(ask == JFileChooser.CANCEL_OPTION) {
            	return;
            }
       }
        
        File[] listOfFiles = LOCATION_IN.listFiles();
        ArrayList<String> files = new ArrayList<>();
        ArrayList<ImagePair> pairs = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                files.add(listOfFiles[i].getName());
            }
        }
        
        for (int one = 0; one < files.size(); one++) {
            for (int two = 0; two < files.size(); two++) {
                try {
                    ImagePair pair = new ImagePair(files.get(one), files.get(two));
                    boolean alreadyMatched = false;
                    for(int i = 0; i < pairs.size(); i++) {
                        if(pair.equals(pairs.get(i))) {
                            alreadyMatched = true;
                        }
                    }
                    if(!alreadyMatched) {
                        pairs.add(pair);
                    }
                } catch (UnmatchedException e) {
                    //System.err.println("Failed to find a match for " + files.get(one) + " and " + files.get(two));
                } catch (NumberFormatException e) {
                    System.err.println("Failed to parse a number for " + files.get(one) + " and " + files.get(two));
                } catch (StringIndexOutOfBoundsException e) {
                    //System.err.println("Image in wrong naming form for " + files.get(one) + " and " + files.get(two));
                }
            }
        }
        
        JFrame f = new JFrame("Progress...");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container content = f.getContentPane();
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        Border border = BorderFactory.createTitledBorder("Writing images...");
        progressBar.setBorder(border);
        content.add(progressBar, BorderLayout.NORTH);
        f.setSize(300, 95);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        
        for(int i = 0; i < pairs.size(); i++) {
            ImagePair pair = pairs.get(i);
            progressBar.setValue((int)(((double)i/(double)pairs.size())*100d));
            System.out.println("Merge: " + pair);
            BufferedImage buffImageOne = null;
            BufferedImage buffImageTwo = null;
            try {
                buffImageOne = ImageIO.read(new File(LOCATION_IN + File.separator + pair.getOne()));
                buffImageTwo = ImageIO.read(new File(LOCATION_IN + File.separator +  pair.getTwo()));
                if(!TEMPLATE) {
                    BufferedImage finalImg = new BufferedImage(400, 240*2, BufferedImage.TYPE_4BYTE_ABGR);
                    if(buffImageOne.getWidth() > buffImageTwo.getWidth()) {
                        finalImg.createGraphics().drawImage(buffImageOne, 0, 0, null);
                        finalImg.createGraphics().drawImage(buffImageTwo, 40, 240, null);
                    } else {
                        finalImg.createGraphics().drawImage(buffImageTwo, 0, 0, null);
                        finalImg.createGraphics().drawImage(buffImageOne, 40, 240, null);
                    }
                    if(pair.isLeft()) {
                        try {
                            ImageIO.write(finalImg, "png", new File(LOCATION_OUT + File.separator +  "scr_" + pair.getNumber() +"_MERGED.png"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } 
                    if(!pair.isLeft() && RIGHT_ENABLED){
                        try {
                            ImageIO.write(finalImg, "png", new File(LOCATION_OUT + File.separator + "scr_" + pair.getNumber() +"_MERGED_RIGHT.png"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    BufferedImage buffTemplate = copyImage(buffTemplateOriginal);
                    if(buffImageOne.getWidth() > buffImageTwo.getWidth()) {
                        buffTemplate.createGraphics().drawImage(buffImageOne, TOPX, TOPY, null);
                        buffTemplate.createGraphics().drawImage(buffImageTwo, BOTX, BOTY, null);
                    } else {
                        buffTemplate.createGraphics().drawImage(buffImageTwo, TOPX, TOPY, null);
                        buffTemplate.createGraphics().drawImage(buffImageOne, BOTX, BOTY, null);
                    }
                    if(pair.isLeft()) {
                        try {
                            ImageIO.write(buffTemplate, "png", new File(LOCATION_OUT + File.separator + "scr_" + pair.getNumber() +"_MERGED.png"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } 
                    if(!pair.isLeft() && RIGHT_ENABLED){
                        try {
                            ImageIO.write(buffTemplate, "png", new File(LOCATION_OUT + File.separator +  "scr_" + pair.getNumber() +"_MERGED_RIGHT.png"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        progressBar.setValue(100);
        progressBar.setString("All done!");
    }
    
    private static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }
}
