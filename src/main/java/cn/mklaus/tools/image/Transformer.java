package cn.mklaus.tools.image;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * 图片转换变形工具类
 *
 * @author klaus
 * Created on 2018/8/23 下午2:42
 */
public class Transformer {

    public static BufferedImage scale(BufferedImage im, int w, int h) {
        BufferedImage re = new BufferedImage(w, h, im.getType());
        Graphics2D g = re.createGraphics();
        g.drawImage(im, 0, 0, w, h, null);
        g.dispose();
        return re;
    }

    public static BufferedImage zoomScale(BufferedImage im, int w, int h, Color bgColor) {
        int ow = im.getWidth();
        int oh = im.getHeight();

        float or = (float)ow / (float)oh;
        float nr = (float)w / (float)h;

        int x = 0;
        int y = 0;
        int nw = 0;
        int nh = 0;

        if (or > nr) {
            nw = w;
            nh = (int)((float)w / or);
            y = (h - nh) / 2;
        } else if (or < nr) {
            nh = h;
            nw = (int)((float)h * or);
            x = (w - nw) / 2;
        } else {
            nw = w;
            nh = h;
        }

        BufferedImage re = new BufferedImage(w, h, im.getType());
        Graphics2D g = re.createGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, w, h);

        g.drawImage(im, x, y, nw, nh, null);
        g.dispose();

        return re;
    }

    public static BufferedImage clipScale(BufferedImage im, int w, int h) {
        int oW = im.getWidth();
        int oH = im.getHeight();
        float oR = (float)oW / (float)oH;
        float nR = (float)w / (float)h;

        int nW;
        int nH;
        int x;
        int y;

        if (oR > nR) {
            nW = h * oW / oH;
            nH = h;
            x = (w - nW) / 2;
            y = 0;
        } else if (oR < nR) {
            nW = w;
            nH = w * oH / oW;
            x = 0;
            y = (h - nH) / 2;
        } else {
            nW = w;
            nH = h;
            x = 0;
            y = 0;
        }

        BufferedImage re = new BufferedImage(w, h, im.getType());
        re.createGraphics().drawImage(im, x, y, nW, nH,null);
        return re;
    }

    public static BufferedImage roundedCornerRadio(BufferedImage im, int percent) {
        if (percent < 1 || percent > 100) {
            throw new IllegalArgumentException("percent value between 1 and 100");
        }
        int corner = Math.min(im.getHeight(), im.getWidth());
        corner = corner * percent / 100;
        return roundCorner(im, corner);
    }

    public static BufferedImage roundCorner(BufferedImage im, int corner) {
        int w = im.getWidth();
        int h = im.getHeight();

        BufferedImage re = new BufferedImage(w, h,  BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = re.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, corner, corner));

        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(im, 0, 0, null);

        g2.dispose();
        return re;
    }

    public static BufferedImage alpha(BufferedImage im, float alpha) {
        int w = im.getWidth();
        int h = im.getHeight();
        BufferedImage re = new BufferedImage(w, h,  BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = re.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.drawImage(im, 0, 0,null);
        return re;
    }

    public static BufferedImage transparentBackground(BufferedImage im, Color saveColor) {
        return transparentBackground(im, null, saveColor);
    }

    /**
     * 将图片背景变成透明
     * @param im                图片
     * @param removeColor       需要透明的颜色
     * @param saveColor         需要保留的颜色
     * @return
     */
    private static BufferedImage transparentBackground(BufferedImage im, Color removeColor, Color saveColor) {
        BufferedImage bg = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < im.getWidth(); i++) {
            for (int j = 0; j < im.getHeight(); j++) {
                int rgb = im.getRGB(i, j);
                int R = (rgb & 0xff0000) >> 16;
                int G = (rgb & 0xff00) >> 8;
                int B = (rgb & 0xff);
                if (saveColor != null) {
                    if (!(R == saveColor.getRed() && G == saveColor.getGreen() & B == saveColor.getBlue())) {
                        rgb = (1 << 24)| (rgb & 0x00ffffff);
                    }
                } else {
                    if ((R == removeColor.getRed() && G == removeColor.getGreen() & B == removeColor.getBlue())) {
                        rgb = (1 << 24)| (rgb & 0x00ffffff);
                    }
                }
                bg.setRGB(i,j, rgb);
            }
        }
        return bg;
    }

}
