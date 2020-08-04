package ds.triplething;

import ds.util.ImageUtil;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JLabel;


@SuppressWarnings("serial")
public class JLabelEx extends JLabel {
	
	private BufferedImage image;
	
	public JLabelEx(String fileName) {
		try {
			image = ImageUtil.loadImage("ds/triplething/graphics/" + fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

}
