package red; 

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

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
		
		//Right eye images might look corrupted if 3D was not enabled when they were taken.
		final boolean RIGHT = JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
			"Do you want to merge right eye images? (Might look broken)", "SETUP: Enable right eye?",
			JOptionPane.YES_NO_OPTION
		);
		
		//Ask if they want a custom template.
		final boolean TEMPLATE = JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
			"Do you want to use a template folder? (see README.md)", "SETUP: Use template?",
			JOptionPane.YES_NO_OPTION
		);
		
		//Get the input, output, and template directory (if needed)
		final File LOCATION_IN = getDirFromUser("[INFO] INPUT DIR", "Select the input directory!");
		final File LOCATION_OUT = getDirFromUser("[INFO] OUTPUT DIR", "Select the output directory!");
		final File LOCATION_CFG = (TEMPLATE ? getDirFromUser("[INFO] TEMPLATE DIR", "Select a folder with a template.cfg and .png!") : null);
		
		//Create lists of files and pairs.
		ArrayList<Image> images = new ArrayList<>();
		
		//Set up progress bar.
		JFrame frame = new JFrame("Progress...");
		Container content = frame.getContentPane();
		JProgressBar progressBar = new JProgressBar();
		Border border = BorderFactory.createTitledBorder("Setting Up...");
		progressBar.setBorder(border);
		progressBar.setStringPainted(true);
		content.add(progressBar, BorderLayout.NORTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(300, 90));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		//Read template file (if needed)
		BufferedImage template = null;
		int topX = 0;
		int topY = 0;
		int botX = 40;
		int botY = 240;
		if(TEMPLATE){
			Scanner in = new Scanner(new File(LOCATION_CFG.toString() + File.separator + "template.cfg"));
			template = ImageIO.read(new File(LOCATION_CFG + File.separator + "template.png"));
			
			while (in.hasNextLine()) {
				String line = in.nextLine();
				int x = Integer.parseInt(line.substring(line.indexOf(":") + 1, line.indexOf(",")));
				int y = Integer.parseInt(line.substring(line.indexOf(",") + 1));
				if(line.contains("top:")) {
					topX = x;
					topY = y;
				} else if(line.contains("bottom:")) {
					botX = x;
					botY = y;
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
		
		//Write images out to folder.
		int pos = 0;
		for(Image left : images) {
			progressBar.setValue((int)(((double)pos++/(double)images.size())*100d));
			if (left.getPos() != Position.LEFT) continue;
			
			//Find bottom image
			Image bot = find(left, images, Position.BOT);
			if (bot == null) continue;
			
			//We have both the left and bottom
			write(left, bot, template, LOCATION_OUT, topX, topY, botX, botY);
			
			//Check if the user actually wants right images
			if (!RIGHT || left.getType().RIGHT == null) continue;
			
			//Find right image
			Image right = find(left, images, Position.RIGHT);
			if (right == null) continue;
			
			//Write the right side image too.
			write(right, bot, template, LOCATION_OUT, topX, topY, botX, botY);
		}
		
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
		
		//Show the dialogue.
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			System.out.println(textShort + ": " + chooser.getSelectedFile());
			return chooser.getSelectedFile();
		}
		
		//If the user quits, stop the program.
		System.exit(0);
		return null; 
	}
	
	private static void write(Image top, Image bot, BufferedImage template, File dir, int topX, int topY, int botX, int botY) throws IOException {
		BufferedImage canvas = null;
		if (template == null) {
			canvas = new BufferedImage(400, 240*2, BufferedImage.TYPE_4BYTE_ABGR);
		} else {
			canvas = new BufferedImage(template.getWidth(), template.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			canvas.getGraphics().drawImage(template, 0, 0, null);
		}
		
		BufferedImage buffTop = ImageIO.read(top.getFile());
		BufferedImage buffBot = ImageIO.read(bot.getFile());
		canvas.createGraphics().drawImage(buffTop, topX, topY, null);
		canvas.createGraphics().drawImage(buffBot, botX, botY, null);
		//ImageIO.write(canvas, Image.TYPE, new File(dir + File.separator + top.getOutputName()));
		System.out.println("[INFO] WROTE: " + top.getOutputName());
	}
	
	private static Image find(Image image, ArrayList<Image> images, Position pos) {
		for (Image find : images) {
			if (image.getType() == find.getType() 
				&& image.getId().equals(find.getId()) 
				&& find.getPos() == pos) {
				return find;
			}
		}
		
		System.out.println("[WARN] MISSING " + pos + ": " + image.getOutputName());
		return null;
	}
}
