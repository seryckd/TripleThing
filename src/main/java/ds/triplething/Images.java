package ds.triplething;

import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import ds.lib.utils.ImageUtil;

public class Images {
	
	static public Integer ICO_BLANK = new Integer(0);
	static public Integer ICO_ROCK = new Integer(2);

	static public Integer ICO_GRASS = new Integer(8);
	static public Integer ICO_BUSH = new Integer(9);
	static public Integer ICO_TREE1 = new Integer(10);
	static public Integer ICO_TREE2 = new Integer(11);
	static public Integer ICO_HOUSE1 = new Integer(12);
	static public Integer ICO_HOUSE2 = new Integer(13);
	static public Integer ICO_HOUSE3 = new Integer(14);
	static public Integer ICO_STORE1 = new Integer(15);
	static public Integer ICO_STORE2 = new Integer(16);
	static public Integer ICO_STORE3 = new Integer(17);
	static public Integer ICO_STORE4 = new Integer(18);
	static public Integer ICO_FACTORY1 = new Integer(20);
	static public Integer ICO_FACTORY2 = new Integer(21);
	static public Integer ICO_FACTORY3 = new Integer(22);
	static public Integer ICO_MYSTERY = new Integer(24);
	static public Integer ICO_BOMB = new Integer(25);
	static public Integer ICO_EXPLODE = new Integer(26);
	
	static public Integer ICO_A_EQUALS = new Integer(41);
	static public Integer ICO_A_SELECT = new Integer(42);
	static public Integer ICO_A_MATCH = new Integer(43);
	static public Integer ICO_A_MATCH_MAIN = new Integer(44);
	
	static String imageDir = "ds/triplething/graphics/";
	static HashMap<Integer, String> imagePath = new HashMap<Integer, String> ();
	
	static HashMap<Integer, ImageIcon> icons = new HashMap<Integer, ImageIcon>();
	static HashMap<Integer, ImageIcon> smallIcons = new HashMap<Integer, ImageIcon>();
	static HashMap<String, ImageIcon> specialIcons = new HashMap<String, ImageIcon>();
	static ArrayList<BufferedImage> numbers = new ArrayList<BufferedImage>();
	
	/**
	 * Called before anything else
	 * @param gc
	 */
	static public void init(GraphicsConfiguration gc)
	{
		sgc = gc;
		
		imagePath.put(ICO_A_SELECT, "a-selected.png");
		imagePath.put(ICO_A_MATCH, "SelectionOverlay.png");
		imagePath.put(ICO_A_MATCH_MAIN, "SelectionMainOverlay.png");
		imagePath.put(ICO_A_EQUALS, "a-equals.png");
		
		imagePath.put(ICO_BLANK, "blank.png");
		imagePath.put(ICO_ROCK, "Rock.png");
		imagePath.put(ICO_GRASS, "grass.png");
		imagePath.put(ICO_BUSH, "bush.png");
		imagePath.put(ICO_MYSTERY, "question.png");
		imagePath.put(ICO_BOMB, "bomb.png");
		imagePath.put(ICO_EXPLODE, "explode.png");

		imagePath.put(ICO_TREE1, "tree.png");
		imagePath.put(ICO_TREE2, "tree2.png");
		imagePath.put(ICO_HOUSE1, "house1.png");
		imagePath.put(ICO_HOUSE2, "house2.png");
		imagePath.put(ICO_HOUSE3, "house3.png");
		imagePath.put(ICO_STORE1, "store1.png");
		imagePath.put(ICO_STORE2, "store2.png");
		imagePath.put(ICO_STORE3, "store3.png");		
		imagePath.put(ICO_STORE4, "store4.png");		

		imagePath.put(ICO_FACTORY1, "factory1.png");
		imagePath.put(ICO_FACTORY2, "factory2.png");
		imagePath.put(ICO_FACTORY3, "factory3.png");
		
		cacheNumbers();
	}
			
	static GraphicsConfiguration sgc = null;
			
	static void cacheImage(Integer id, String fileName) {
		try {
			
			icons.put(id, getPNGIcon(imageDir, fileName));
			smallIcons.put(id,getSmallPNGIcon(imageDir, fileName));
		} catch(IOException ex) {
			System.out.println(ex);
		}
	}
	
	static void cacheNumbers() {
		try {
			BufferedImage image = ImageUtil.loadImage(imageDir + "numbers.png");
			int destWidth = 20;
			int destHeight = 30;
			
			for(int t=0; t<10; t++) {
				BufferedImage subImage = image.getSubimage(0 + t*30, 0, 30, 38);
				BufferedImage dest = sgc.createCompatibleImage(
						destWidth,
						destHeight,
						Transparency.BITMASK);
				
				dest.getGraphics().drawImage(subImage, 
						0, 0, destWidth, destHeight,
						0, 0, subImage.getWidth(), subImage.getHeight(),
						null);
			
				numbers.add(dest);
			}
			
		} catch(IOException ex) {
			System.out.println(ex);
		}		
	}
	
	static BufferedImage loadBackground(String fileName) {
		try {
			return ImageUtil.loadImage(imageDir + fileName);
		} catch (IOException ex) {
			System.out.println(ex);
			return null;
		}		
	}
		
	
	// Return the given icon with the highlight overlay (a blue border)
	public static Icon getHighlightIcon(Integer id) {		
		return getIconWithOverlay(id, new Integer[] {ICO_A_SELECT}, true);		
	}	
	
	// Return the given icon with the selected overlay (a red dot)
	public static Icon getSelectedIcon(Integer id) {
		return getIconWithOverlay(id, new Integer[] {ICO_A_MATCH}, true);		
	}
	
	// Return the given icon with a possible selection overlay (highlight & selection)
	public static Icon getPossibleSelectedIcon(Integer id) {
		return getIconWithOverlay(id, new Integer[] {ICO_A_MATCH, ICO_A_SELECT}, true);		
	}
	static Icon getIconWithOverlay(Integer base, Integer[] overlays, boolean isLighter) {
		try {
			String key = base.toString();
			for(Integer overlay : overlays)
				key += "_" + overlay;
						
			ImageIcon ret = specialIcons.get(key); 
			if (ret == null) {
				ret = icons.get(base);
				for(Integer overlay : overlays)
					ret = addOverlay(ret, getIcon(overlay), isLighter);
					
				specialIcons.put(key, ret);
			}
			
			return ret;
		} catch(IOException e) {
			System.out.println("getIconWithOverlay exception: " + e.toString());
			return null;
		}
	}	
	
	/**
	 * Use lazy caching
	 * @param id
	 * @return
	 */
	public static ImageIcon getIcon(Integer id) {
		ImageIcon ico = icons.get(id);
		
		if (ico == null) {
			try {
				ico = getPNGIcon(imageDir, imagePath.get(id));
			} catch (IOException e) {
				e.printStackTrace();
			}
			icons.put(id, ico);
		}
			
		return ico;
	}
		
	public static Icon getSmallIcon(Integer id) {
		ImageIcon ico = smallIcons.get(id);
		
		if (ico == null) {
			try {
				ico = getSmallPNGIcon(imageDir, imagePath.get(id));
			} catch (IOException e) {
				e.printStackTrace();
			}
			smallIcons.put(id, ico);
		}
			
		return ico;
	}
	
	public static Icon getNumber(int num) {
		String sNum = Integer.toString(num);
		int len = sNum.length();
		
		// 20,30
		
		BufferedImage dest = sgc.createCompatibleImage(
				len*20,
				30,
				Transparency.BITMASK);
						
		for(int q=0; q<sNum.length(); q++) {
			char c = sNum.charAt(q);
		
			int c1=0;
			switch(c) {
			case '0' : c1 = 0; break;
			case '1' : c1 = 1; break;
			case '2' : c1 = 2; break;
			case '3' : c1 = 3; break;
			case '4' : c1 = 4; break;
			case '5' : c1 = 5; break;
			case '6' : c1 = 6; break;
			case '7' : c1 = 7; break;
			case '8' : c1 = 8; break;
			case '9' : c1 = 9; break;
			}
			BufferedImage img = numbers.get(c1);
			
			// dest is first, source is second
			dest.createGraphics().drawImage(img, 
					img.getWidth()*q, 0, img.getWidth()*q+img.getWidth(), img.getHeight(),
					0, 0, img.getWidth(), img.getHeight(),
					null);
		}
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
	    ImageIO.write(dest, "PNG", bytes);
		} 
		catch(IOException iox) {
			System.out.println("error creating PNG " + iox);
		}
	    
	    return new ImageIcon(bytes.toByteArray());
	}
		
	static ImageIcon getPNGIcon(String dir, String fileName) throws IOException {
		//BufferedImage image = ImageUtil.loadImage(dir + fileName);
		//return makePNGIcon(image, 60, 60);
		return new ImageIcon(Images.class.getResource("/" + dir + fileName));
	}

	static ImageIcon getSmallPNGIcon(String dir, String fileName) throws IOException {
		BufferedImage image = ImageUtil.loadImage(dir + fileName);
		return makePNGIcon(image, 40, 40);
	}
	
	static ImageIcon makePNGIcon(BufferedImage image, int width, int height) throws IOException {
		
		BufferedImage dest = sgc.createCompatibleImage(
				width,
				height,
				Transparency.BITMASK);
		
		dest.getGraphics().drawImage(image, 
				0, 0, width, height,
				0, 0, image.getWidth(), image.getHeight(),
				null);
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    ImageIO.write(dest, "PNG", bytes);
	    
	    return new ImageIcon(bytes.toByteArray());
	}
	
	static ImageIcon addOverlay(ImageIcon orig, ImageIcon overlay, boolean fLighten) throws IOException {
				
		BufferedImage dest = sgc.createCompatibleImage(
				orig.getIconWidth(),
				orig.getIconHeight(),
				Transparency.BITMASK);

		if (fLighten) {
			BufferedImage dest1 = sgc.createCompatibleImage(
					orig.getIconWidth(),
					orig.getIconHeight(),
					Transparency.BITMASK);
	
			dest1.getGraphics().drawImage(orig.getImage(), 
					0, 0, orig.getIconWidth(), orig.getIconHeight(),
					0, 0, orig.getIconWidth(), orig.getIconHeight(),
					null);

			ImageUtil.lightenImage(dest1, dest);
 		} else {
			dest.getGraphics().drawImage(orig.getImage(), 
					0, 0, orig.getIconWidth(), orig.getIconHeight(),
					0, 0, orig.getIconWidth(), orig.getIconHeight(),
					null);
 			
 		}
		
		// on Windows the overlay overwrites the original image
		// specifically says "Transparent pixels do not affect whatever pixels are already there."
		// its labels, not drawImage() that is the problem

		dest.getGraphics().drawImage(overlay.getImage(), 
				0, 0, orig.getIconWidth(), orig.getIconHeight(),	// dest rectangle
				0, 0, overlay.getIconWidth(), overlay.getIconHeight(),	// orig rectangle
				null);

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    ImageIO.write(dest, "PNG", bytes);
	    
	    return new ImageIcon(bytes.toByteArray());
	}
	
}
