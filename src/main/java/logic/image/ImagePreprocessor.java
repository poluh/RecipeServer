package logic.image;


import logic.image.Geometry.Point;
import logic.network.Network;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ImagePreprocessor {

    private BufferedImage image;
    private int[][] allImagePixels;
    private int[] imageSignals;
    private Set<ImagePreprocessor> otherImagePreprocessors = new HashSet<>();
    private List<Point> pointsObject = new ArrayList<>();
    private static int WHITE_RGB = Color.WHITE.getRGB();
    private static int BLACK_RGB = Color.BLACK.getRGB();

    private void create(BufferedImage image) {
        this.image = image;
        updatePixelsImage();
    }

    public ImagePreprocessor(BufferedImage image) {
        create(image);
    }

    public ImagePreprocessor(String path) {
        try {
            create(ImageIO.read(new File(path)));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error open image");
        }
    }

    public void resize(BufferedImage img, int newW, int newH) {
        try {
            this.image = Thumbnails.of(img).forceSize(newW, newH).asBufferedImage();
            updatePixelsImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage copy(BufferedImage image) {
        BufferedImage imageCopy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics graphics = imageCopy.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return imageCopy;
    }

    public void toGrayImage() {
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                Color pixelColor = new Color(allImagePixels[i][j]);
                int rgb = (pixelColor.getRed() + pixelColor.getGreen() + pixelColor.getBlue()) / 3;
                Color newPixelColor = new Color(rgb, rgb, rgb);
                image.setRGB(i, j, newPixelColor.getRGB());
            }
        }
        updatePixelsImage();
    }

    public void cropImage() {
        Point topPoint = findAnchorPoint(Course.TOP);
        Point leftPoint = findAnchorPoint(Course.LEFT);
        Point leftTopCropPoint = new Point(leftPoint.x, topPoint.y);
        Point leftBottomCropPoint = new Point(leftPoint.x, findAnchorPoint(Course.BOTTOM).y);
        Point rightTopCropPoint = new Point(findAnchorPoint(Course.RIGHT).x, topPoint.y);

        int cropWidth = leftTopCropPoint.distance(rightTopCropPoint);
        int cropHeight = leftTopCropPoint.distance(leftBottomCropPoint);
        this.image = image.getSubimage(leftTopCropPoint.x, leftTopCropPoint.y, cropWidth, cropHeight);
        updatePixelsImage();
    }

    private enum Course {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT;
    }


    private Point findAnchorPoint(Course course) {

        int imageHeight = image.getHeight() - 2;
        int imageWidth = image.getWidth() - 2;

        int firstLimiter = course == Course.RIGHT || course == Course.LEFT ? imageWidth : imageHeight;
        int secondLimiter = course == Course.TOP || course == Course.BOTTOM ? imageWidth : imageHeight;

        if (course == Course.LEFT || course == Course.TOP) {
            for (int i = 1; i < firstLimiter; i++) {
                for (int j = 1; j < secondLimiter; j++) {
                    Point answerPoint = isCorrectedPoint(i, j, course);
                    if (!answerPoint.equals(new Point(-1, -1))) {
                        return answerPoint;
                    }
                }
            }
        } else {
            for (int i = firstLimiter; i >= 1; i--) {
                for (int j = secondLimiter; j >= 1; j--) {
                    Point answerPoint = isCorrectedPoint(i, j, course);
                    if (!answerPoint.equals(new Point(-1, -1))) {
                        return answerPoint;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Point not found");
    }

    private Point isCorrectedPoint(int i, int j, Course course) {
        boolean courseLeftOrRight = course == Course.LEFT || course == Course.RIGHT;
        int xSupportPixels = courseLeftOrRight ? i : j;
        int ySupportPixels = courseLeftOrRight ? j : i;
        int topLeftPixel = allImagePixels[xSupportPixels - 1][ySupportPixels - 1];
        int bottomRightPixel = allImagePixels[xSupportPixels + 1][ySupportPixels + 1];
        if (topLeftPixel - bottomRightPixel != 0) {
            return new Point(xSupportPixels, ySupportPixels);
        }
        return new Point(-1, -1);
    }

    public void binarizaid() {
        brightnessOfPixels();
        int treshold = searchTresholdBinarizaid();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (allImagePixels[i][j] > treshold) {
                    image.setRGB(i, j, WHITE_RGB);
                } else {
                    image.setRGB(i, j, BLACK_RGB);
                }
            }
        }
        updatePixelsImage();
    }

    private int searchTresholdBinarizaid() {
        int maxBrightness = 255;
        int minBrightness = 0;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int currentBrightness = allImagePixels[i][j];
                maxBrightness = Math.max(currentBrightness, maxBrightness);
                minBrightness = Math.min(currentBrightness, minBrightness);
            }
        }
        return maxBrightness - thresholdCounting(maxBrightness, minBrightness);
    }

    private int[] createBarChart(int maxBrightness, int minBrightness) {
        int barChartSize = maxBrightness - minBrightness + 1;
        int[] barChart = new int[barChartSize];
        for (int i = 0; i < image.getHeight() * image.getWidth(); i++) {
            int x = (i < image.getWidth()) ? i : (i % image.getWidth());
            int y = (x == i) ? 0 : (i / image.getWidth());
            barChart[allImagePixels[x][y] - minBrightness]++;
        }
        return barChart;
    }

    private int thresholdCounting(int maxBrightness, int minBrightness) {
        int[] barChart = createBarChart(maxBrightness, minBrightness);
        int sumAllColumnHeights = 0;
        int sumAllCHAndMiddle = 0;
        for (int i = 0; i <= maxBrightness - minBrightness; i++) {
            sumAllColumnHeights += barChart[i];
            sumAllCHAndMiddle += i * barChart[i];
        }

        double maxSigma = -1;
        int threshold = 0;
        int alpha = 0;
        int beta = 0;
        for (int i = 0; i < maxBrightness - minBrightness; i++) {
            alpha += i * barChart[i];
            beta += barChart[i];
            double probability = (double) beta / sumAllColumnHeights;
            double intermediateMiddleSum = (double) alpha / beta -
                    (double) (sumAllCHAndMiddle - alpha) /
                            (sumAllColumnHeights - beta);
            double sigma = probability * (1 - probability) * intermediateMiddleSum * intermediateMiddleSum;

            if (sigma > maxSigma) {
                maxSigma = sigma;
                threshold = i;
            }
        }
        return threshold;
    }


    private void updatePixelsImage() {
        int[][] allPixels = new int[image.getWidth()][image.getHeight()];

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                allPixels[i][j] = image.getRGB(i, j);
            }
        }
        this.allImagePixels = allPixels;
    }

    private void brightnessOfPixels() {
        int[][] brightnessOfPixels = new int[image.getWidth()][image.getHeight()];

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                Color currentColor = new Color(image.getRGB(i, j));
                int brightness = (int) (0.2125 * currentColor.getRed() +
                        0.7154 * currentColor.getGreen() +
                        0.0721 * currentColor.getBlue());
                brightnessOfPixels[i][j] = brightness;
            }
        }
        this.allImagePixels = brightnessOfPixels;
    }

    public void saveImage(BufferedImage bufferedImage) {
        saveImage(bufferedImage, "image.png");
    }

    public void saveImage(BufferedImage image, String fileName) {
        try {
            ImageIO.write(image, "PNG", new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public int[] getImageSignals() {
        imageSignals = new int[Network.NUMBER_OF_NEURON];
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                imageSignals[i * image.getWidth() + j] = image.getRGB(i, j) == WHITE_RGB ? 1 : 0;
            }
        }

        return imageSignals;
    }

    public void devidedIntoSeveralImages() {
        cropImage();
        List<Set<Integer>> setList = new ArrayList<>();
        for (int i = 10; i < image.getWidth(); i++) {
            Set<Integer> oneLane = new HashSet<>();
            for (int j = 10; j < image.getHeight(); j++) {
                oneLane.add(allImagePixels[i][j]);
            }
            System.out.println(oneLane);
            if (oneLane.size() == 1) {
                setList.add(oneLane);
                if (setList.size() == 20) {
                    BufferedImage subImage = copy(image.getSubimage(0, 0, i, image.getHeight()));
                    otherImagePreprocessors.add(
                            new ImagePreprocessor(subImage));
                    image = image.getSubimage(i, 0, image.getWidth() - subImage.getWidth(), image.getHeight());
                    cropImage();
                    devidedIntoSeveralImages();
                }
            } else {
                setList.clear();
            }
        }
    }

    public Set<ImagePreprocessor> getOtherImagePreprocessors() {
        return otherImagePreprocessors;
    }
}
