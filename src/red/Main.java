package red; 

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

public class Main {
	
	public static void main(String[] args) {
		try {
			run();
		} catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.toString(), "Error - See Console", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	//Just throw everything since the program doesn't handle errors.
	public static void run() throws NumberFormatException, IOException, InterruptedException {
		
		//Template image
		BufferedImage template = null;
		
		//Right eye images might look corrupted if 3D was not enabled when they were taken.
		final boolean RIGHT_ENABLED = JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
			"Do you want to merge right eye images? (Might look broken)", "SETUP: Enable right eye?",
			JOptionPane.YES_NO_OPTION
		);
		
		//Ask if they want a custom template.
		final boolean TEMPLATE = JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
			"Do you want to use a template folder? (see README.md)", "SETUP: Use template?",
			JOptionPane.YES_NO_OPTION
		);
		
		
		//Get the input, output, and template directory (if needed)
		final File LOCATION_IN = getDirFromUser("INPUT DIR", "Select the input directory!");
		final File LOCATION_OUT = getDirFromUser("OUTPUT DIR", "Select the output directory!");
		final File LOCATION_CFG = (TEMPLATE ? getDirFromUser("TEMPLATE DIR", "Select a folder with a template.cfg and .png!") : null);
		
		//Create lists of files and pairs.
		ArrayList<Image> images = new ArrayList<>();
		
		//Positions of images in large image.
		int TOPX = 0;
		int TOPY = 0;
		int BOTX = 40;
		int BOTY = 240;
		
		//Set up progress bar.
		JFrame frame = new JFrame("Progress...");
		Container content = frame.getContentPane();
		JProgressBar progressBar = new JProgressBar();
		Border border = BorderFactory.createTitledBorder("Matching images...");
		progressBar.setBorder(border);
		progressBar.setStringPainted(true);
		content.add(progressBar, BorderLayout.NORTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(300, 90));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		//Read template file (if needed)
		if(TEMPLATE){
			Scanner in = new Scanner(new File(LOCATION_CFG.toString() + File.separator + "template.cfg"));
			template = ImageIO.read(new File(LOCATION_CFG + File.separator + "template.png"));
			
			while (in.hasNextLine()) {
				String line = in.nextLine();
				int x = Integer.parseInt(line.substring(line.indexOf(":") + 1, line.indexOf(",")));
				int y = Integer.parseInt(line.substring(line.indexOf(",") + 1));
				if(line.contains("top:")) {
					TOPX = x;
					TOPY = y;
				} else if(line.contains("bottom:")) {
					BOTX = x;
					BOTY = y;
				}
			}
			
			in.close();
		}

		//Create image objects
		for (File file : LOCATION_IN.listFiles()) {
			Image image = new Image(file);
			if (image.getType() == Type.UNKNOWN) continue;
			images.add(image);
		}
		
		//Change text of border.
		border = BorderFactory.createTitledBorder("Writing images...");
		progressBar.setBorder(border);
		
//		//Write images out to folder.
//		for(int i = 0; i < pairs.size(); i++) {
//			progressBar.setValue((int)(((double)i/(double)pairs.size())*100d));
//			ImagePair pair = pairs.get(i);
//			BufferedImage buffImageOne = null;
//			BufferedImage buffImageTwo = null;
//			try {
//				buffImageOne = ImageIO.read(new File(LOCATION_IN + File.separator + pair.getOne()));
//				buffImageTwo = ImageIO.read(new File(LOCATION_IN + File.separator +  pair.getTwo()));
//				BufferedImage largeImage = (TEMPLATE ? copyImage(template) : new BufferedImage(400, 240*2, BufferedImage.TYPE_4BYTE_ABGR));
//				if(buffImageOne.getWidth() > buffImageTwo.getWidth()) {
//					largeImage.createGraphics().drawImage(buffImageOne, TOPX, TOPY, null);
//					largeImage.createGraphics().drawImage(buffImageTwo, BOTX, BOTY, null);
//				} else {
//					largeImage.createGraphics().drawImage(buffImageTwo, TOPX, TOPY, null);
//					largeImage.createGraphics().drawImage(buffImageOne, BOTX, BOTY, null);
//				}
//				
//				try {
//					if(pair.isLeft()) {
//						ImageIO.write(largeImage, "png", new File(LOCATION_OUT + File.separator + pair.getName()));
//						System.out.println("Wrote left: " + pair);
//					} else if(RIGHT_ENABLED) {
//						ImageIO.write(largeImage, "png", new File(LOCATION_OUT + File.separator +  pair.getName()));
//						System.out.println("Wrote right: " + pair);
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				} 
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		
		progressBar.setValue(100);
		progressBar.setString("All done!");
		frame.setTitle("Done!");
		
		//Close after 3 seconds
		Thread.sleep(3000);
		System.exit(0);
	}
	
	private static File getDirFromUser(String textShort, String textTitle) {
		JFileChooser chooser = new JFileChooser(); 
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setDialogTitle(textTitle);
		
		//Show the dialogue. If the user quits, stop the program
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			System.out.println(textShort + ": " + chooser.getSelectedFile());
			return chooser.getSelectedFile();
		}
		
		System.exit(0);
		return null; //Never reached
	}
	
	private static BufferedImage copyImage(BufferedImage source){
		BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = b.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return b;
	}
}
