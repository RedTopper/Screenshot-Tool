package red; 

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
	
	public static File getDirFromUser(String shortText, String titleText) {
        JFileChooser chooser = new JFileChooser(); 
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setDialogTitle(titleText);
        int ask = chooser.showOpenDialog(null);
        if (ask == JFileChooser.APPROVE_OPTION) {
            System.out.println(shortText + ": " + chooser.getSelectedFile());
            return chooser.getSelectedFile();
        } 
    	System.exit(0);
    	return null; //Never reached
	}

    public static void main(String[] args) {
    	
    	//Template image
        BufferedImage buffTemplateOriginal = null;
    	
    	//Ask user some setup questions
        boolean RIGHT_ENABLED = JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                "Do you want to merge right eye images? These might cause problems if 3D was not enabled when the screenshots were taken!", "SETUP: Enable right eye?",
                JOptionPane.YES_NO_OPTION);
        boolean TEMPLATE = JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                "Do you want to use a template.cfg and template.png? See README.md for details.", "SETUP: Use template?",
                JOptionPane.YES_NO_OPTION);
        
        
        //Get the input, output, and template directory (if needed)
        File LOCATION_IN = getDirFromUser("INPUT DIR", "Select the input directory!");
        File LOCATION_OUT = getDirFromUser("OUTPUT DIR", "Select the output directory!");
        File LOCATION_CFG = (TEMPLATE ? getDirFromUser("TEMPLATE DIR", "Select a folder with a template.cfg and .png!") : null);
        
        //Create lists of files and pairs.
        File[] listOfFiles = LOCATION_IN.listFiles();
        ArrayList<String> files = new ArrayList<>();
        ArrayList<ImagePair> pairs = new ArrayList<>();
        
        //Positions of images in large image.
        int TOPX = 0;
        int TOPY = 0;
        int BOTX = 40;
        int BOTY = 240;
        
        //Set up progress bar.
        JFrame f = new JFrame("Progress...");
        Container content = f.getContentPane();
        Border border = BorderFactory.createTitledBorder("Matching images...");
        JProgressBar progressBar = new JProgressBar();
        progressBar.setBorder(border);
        progressBar.setStringPainted(true);
        content.add(progressBar, BorderLayout.NORTH);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(300, 95);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
                
        //Read template file (if needed)
        if(TEMPLATE){
            FileInputStream fis = null;
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
	        } catch (Exception e) {
	            e.printStackTrace();
	            return;
	        }
       }

        //Do not add directories to files list.
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                files.add(listOfFiles[i].getName());
            }
        }
        
        //Match all of the images with their counterparts.
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
                    if(!(alreadyMatched || pair.type == ImagePair.Type.UNKNOWN)) {
                        pairs.add(pair);
                        System.out.println("Matched: " + pair);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Failed to parse a number for " + files.get(one) + " and " + files.get(two));
                } catch (IndexOutOfBoundsException e) {
                    //System.err.println("Image in wrong naming form for " + files.get(one) + " and " + files.get(two));
                } catch (UnmatchedException e) {
					//No need to print, just continue matching
				}
            }
        }
        
        //Change text of border.
        border = BorderFactory.createTitledBorder("Writing images...");
        progressBar.setBorder(border);
        
        //Write images out to folder.
        for(int i = 0; i < pairs.size(); i++) {
            progressBar.setValue((int)(((double)i/(double)pairs.size())*100d));
            ImagePair pair = pairs.get(i);
            BufferedImage buffImageOne = null;
            BufferedImage buffImageTwo = null;
            try {
                buffImageOne = ImageIO.read(new File(LOCATION_IN + File.separator + pair.getOne()));
                buffImageTwo = ImageIO.read(new File(LOCATION_IN + File.separator +  pair.getTwo()));
                BufferedImage largeImage = (TEMPLATE ? copyImage(buffTemplateOriginal) : new BufferedImage(400, 240*2, BufferedImage.TYPE_4BYTE_ABGR));
                if(buffImageOne.getWidth() > buffImageTwo.getWidth()) {
                	largeImage.createGraphics().drawImage(buffImageOne, TOPX, TOPY, null);
                	largeImage.createGraphics().drawImage(buffImageTwo, BOTX, BOTY, null);
                } else {
                	largeImage.createGraphics().drawImage(buffImageTwo, TOPX, TOPY, null);
                	largeImage.createGraphics().drawImage(buffImageOne, BOTX, BOTY, null);
                }
                
                try {
                	if(pair.isLeft()) {
                    	ImageIO.write(largeImage, "png", new File(LOCATION_OUT + File.separator + pair.getName()));
                        System.out.println("Wrote left: " + pair);
                	} else if(RIGHT_ENABLED) {
                		ImageIO.write(largeImage, "png", new File(LOCATION_OUT + File.separator +  pair.getName()));
                        System.out.println("Wrote right: " + pair);
                	}
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        progressBar.setValue(100);
        progressBar.setString("All done!");
        f.setTitle("Done!");
        
        //Close after 3 seconds
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			//nothing
		} finally {
			System.exit(0);
		}
    }
    
    private static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }
}
