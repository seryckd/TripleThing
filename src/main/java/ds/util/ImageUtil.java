package ds.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImageUtil {

    public static BufferedImage loadImage(String name) throws IOException {
        InputStream is = ResourceUtil.loadResource(name);
        return ImageIO.read(is);
    }

    public static ImageIcon loadIcon(String name) {
        return new ImageIcon(Toolkit.getDefaultToolkit().createImage(name));
    }

    public static ImageIcon makeIconFromPNG(GraphicsConfiguration gc, BufferedImage pngImage) throws IOException {
        BufferedImage dest = gc.createCompatibleImage(pngImage.getWidth(), pngImage.getHeight(), 2);
        dest.getGraphics().drawImage(pngImage, 0, 0, pngImage.getWidth(), pngImage.getHeight(), 0, 0, pngImage.getWidth(), pngImage.getHeight(), (ImageObserver)null);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(dest, "PNG", bytes);
        return new ImageIcon(bytes.toByteArray());
    }

    public static BufferedImage copyImage(GraphicsConfiguration gc, BufferedImage src) {
        BufferedImage dest = gc.createCompatibleImage(src.getHeight(), src.getWidth(), 2);
        Graphics g = dest.createGraphics();
        g.drawImage(src, 0, 0, (ImageObserver)null);
        g.dispose();
        return dest;
    }

    public static BufferedImage lightenImage(BufferedImage src, BufferedImage dest) {
        float[] scales = new float[]{1.0F, 1.0F, 1.0F, 0.8F};
        float[] offsets = new float[4];
        RescaleOp rsOp = new RescaleOp(scales, offsets, (RenderingHints)null);
        ((Graphics2D)dest.getGraphics()).drawImage(src, rsOp, 0, 0);
        return dest;
    }
}
