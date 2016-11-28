package util.validator;

import http.helpers.TextFinder;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import util.driver.DriverHelper;
import util.driver.PageValidator;
import util.general.HtmlReportBuilder;
import util.general.SystemHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static environment.EnvironmentFactory.isChrome;
import static util.validator.Constants.*;
import static util.validator.ResponsiveUIValidator.Units.PX;

public class ResponsiveUIValidator implements Validator {

    private static final int MIN_OFFSET = -10000;
    private final Logger LOG = Logger.getLogger(ResponsiveUIValidator.class);
    private WebDriver driver;
    private String rootElementReadableName;
    private WebElement rootElement;
    private File screenshot;
    private BufferedImage img;
    private Graphics2D g;
    private JSONArray errorMessage;
    private int xRoot;
    private int yRoot;
    private int widthRoot;
    private int heightRoot;
    private boolean withReport = false;
    private int pageWidth;
    private int pageHeight;
    private int rootElementRightOffset;
    private int rootElementBottomOffset;
    private boolean drawLeftOffsetLine = false;
    private boolean drawRightOffsetLine = false;
    private boolean drawTopOffsetLine = false;
    private boolean drawBottomOffsetLine = false;
    private Units units = PX;

    public ResponsiveUIValidator(WebDriver driver) {
        this.driver = driver;
        errorMessage = new JSONArray();
    }

    @Override
    public ResponsiveUIValidator init() {
        return new ResponsiveUIValidator(driver);
    }

    @Override
    public ResponsiveUIValidator findElement(WebElement element, String readableNameOfElement) {
        rootElement = element;
        rootElementReadableName = readableNameOfElement;
        xRoot = rootElement.getLocation().getX();
        yRoot = rootElement.getLocation().getY();
        widthRoot = rootElement.getSize().getWidth();
        heightRoot = rootElement.getSize().getHeight();
        pageWidth = driver.manage().window().getSize().getWidth();
        pageHeight = driver.manage().window().getSize().getHeight();
        rootElementRightOffset = pageWidth - xRoot + widthRoot;
        rootElementBottomOffset = pageHeight - yRoot + heightRoot;
        return this;
    }

    @Override
    public ResponsiveUIValidator changeMetricsUnitsTo(Units units) {
        this.units = units;
        return this;
    }

    @Override
    public ResponsiveUIValidator withLeftElement(WebElement element) {
        validateLeftElement(element);

        return this;
    }

    @Override
    public ResponsiveUIValidator withLeftElement(WebElement element, int minMargin, int maxMargin) {
        validateLeftElement(element, getInt(minMargin, true), getInt(maxMargin, true));

        return this;
    }

    @Override
    public ResponsiveUIValidator withRightElement(WebElement element) {
        validateRightElement(element);
        return this;
    }

    @Override
    public ResponsiveUIValidator withRightElement(WebElement element, int minMargin, int maxMargin) {
        validateRightElement(element, getInt(minMargin, true), getInt(maxMargin, true));
        return this;
    }

    @Override
    public ResponsiveUIValidator withTopElement(WebElement element) {
        validateAboveElement(element);
        return this;
    }

    @Override
    public ResponsiveUIValidator withTopElement(WebElement element, int minMargin, int maxMargin) {
        validateAboveElement(element, getInt(minMargin, false), getInt(maxMargin, false));
        return this;
    }

    @Override
    public ResponsiveUIValidator withBottomElement(WebElement element) {
        validateBelowElement(element);
        return this;
    }

    @Override
    public ResponsiveUIValidator withBottomElement(WebElement element, int minMargin, int maxMargin) {
        validateBelowElement(element, getInt(minMargin, false), getInt(maxMargin, false));
        return this;
    }

    @Override
    public ResponsiveUIValidator insideOf(WebElement element, String readableContainerName) {
        validateInsideOfContainer(element, readableContainerName);
        return this;
    }

    @Override
    public ResponsiveUIValidator notOverlapWith(WebElement element, String readableName) {
        validateOverlappingWithElements(element, readableName);
        return this;
    }

    @Override
    public ResponsiveUIValidator notOverlapWith(List<WebElement> elements) {
        for (WebElement element : elements) {
            validateOverlappingWithElements(element, "Element with class name: " + element.getAttribute("class"));
        }
        return this;
    }

    @Override
    public ResponsiveUIValidator sameOffsetLeftAs(WebElement element, String readableName) {
        validateLeftOffsetForElements(element, readableName);
        drawLeftOffsetLine = true;
        return this;
    }

    @Override
    public ResponsiveUIValidator sameOffsetLeftAs(List<WebElement> elements) {
        for (WebElement element : elements) {
            validateLeftOffsetForElements(element, "Element with class name: " + element.getAttribute("class"));
        }
        drawLeftOffsetLine = true;
        return this;
    }

    @Override
    public ResponsiveUIValidator sameOffsetRightAs(WebElement element, String readableName) {
        validateRightOffsetForElements(element, readableName);
        drawRightOffsetLine = true;
        return this;
    }

    @Override
    public ResponsiveUIValidator sameOffsetRightAs(List<WebElement> elements) {
        for (WebElement element : elements) {
            validateRightOffsetForElements(element, "Element with class name: " + element.getAttribute("class"));
        }
        drawRightOffsetLine = true;
        return this;
    }

    @Override
    public ResponsiveUIValidator sameOffsetTopAs(WebElement element, String readableName) {
        validateTopOffsetForElements(element, readableName);
        drawTopOffsetLine = true;
        return this;
    }

    @Override
    public ResponsiveUIValidator sameOffsetTopAs(List<WebElement> elements) {
        for (WebElement element : elements) {
            validateTopOffsetForElements(element, "Element with class name: " + element.getAttribute("class"));
        }
        drawTopOffsetLine = true;
        return this;
    }

    @Override
    public ResponsiveUIValidator sameOffsetBottomAs(WebElement element, String readableName) {
        validateBottomOffsetForElements(element, readableName);
        drawBottomOffsetLine = true;
        return this;
    }

    @Override
    public ResponsiveUIValidator sameOffsetBottomAs(List<WebElement> elements) {
        for (WebElement element : elements) {
            validateBottomOffsetForElements(element, "Element with class name: " + element.getAttribute("class"));
        }
        drawBottomOffsetLine = true;
        return this;
    }

    @Override
    public ResponsiveUIValidator sameWidthAs(WebElement element, String readableName) {
        validateSameWidth(element, readableName);
        return this;
    }

    @Override
    public ResponsiveUIValidator sameWidthAs(List<WebElement> elements) {
        for (WebElement element : elements) {
            validateSameWidth(element, "Element with class name: " + element.getAttribute("class"));
        }
        return this;
    }

    @Override
    public ResponsiveUIValidator minWidth(int width) {
        validateMinWidth(getInt(width, true));
        return this;
    }

    @Override
    public ResponsiveUIValidator maxWidth(int width) {
        validateMaxWidth(getInt(width, true));
        return this;
    }

    @Override
    public ResponsiveUIValidator widthBetween(int min, int max) {
        validateMinWidth(getInt(min, true));
        validateMaxWidth(getInt(max, true));
        return this;
    }

    @Override
    public ResponsiveUIValidator sameHeightAs(WebElement element, String readableName) {
        validateSameHeight(element, readableName);
        return this;
    }

    @Override
    public ResponsiveUIValidator sameHeightAs(List<WebElement> elements) {
        for (WebElement element : elements) {
            validateSameHeight(element, "Element with class name: " + element.getAttribute("class"));
        }
        return this;
    }

    @Override
    public ResponsiveUIValidator minHeight(int height) {
        validateMinHeight(getInt(height, false));
        return this;
    }

    @Override
    public ResponsiveUIValidator maxHeight(int height) {
        validateMaxHeight(getInt(height, false));
        return this;
    }

    @Override
    public ResponsiveUIValidator sameSizeAs(WebElement element, String readableName) {
        validateSameSize(element, readableName);
        return this;
    }

    @Override
    public ResponsiveUIValidator sameSizeAs(List<WebElement> elements) {
        for (WebElement element : elements) {
            validateSameSize(element, "Element with class name: " + element.getAttribute("class"));
        }
        return this;
    }

    @Override
    public ResponsiveUIValidator heightBetween(int min, int max) {
        validateMinHeight(getInt(min, false));
        validateMaxHeight(getInt(max, false));
        return this;
    }

    @Override
    public ResponsiveUIValidator minOffset(int top, int right, int bottom, int left) {
        if (getInt(top, false) > MIN_OFFSET && getInt(right, true) > MIN_OFFSET && getInt(bottom, false) > MIN_OFFSET && getInt(left, true) > MIN_OFFSET) {
            validateMinOffset(getInt(top, false), getInt(right, true), getInt(bottom, false), getInt(left, true));
        }
        return this;
    }

    @Override
    public ResponsiveUIValidator maxOffset(int top, int right, int bottom, int left) {
        if (getInt(top, false) > MIN_OFFSET && getInt(right, true) > MIN_OFFSET && getInt(bottom, false) > MIN_OFFSET && getInt(left, true) > MIN_OFFSET) {
            validateMaxOffset(getInt(top, false), getInt(right, true), getInt(bottom, false), getInt(left, true));
        }
        return this;
    }

    @Override
    public ResponsiveUIValidator withCssValue(String cssProperty, String... args) {
        String cssValue = rootElement.getCssValue(cssProperty);

        if (!cssValue.equals("")) {
            for (String val : args) {
                if (!TextFinder.textIsFound(val, cssValue)) {
                    putJsonDetailsWithoutElement(String.format("Expected value of '%s' is '%s'. Actual is '%s'", cssProperty, val, cssValue));
                }
            }
        }else{
            putJsonDetailsWithoutElement(String.format("Element '%s' does not have css property '%s'", rootElementReadableName, cssProperty));
        }
        return this;
    }

    @Override
    public ResponsiveUIValidator drawMap() {
        withReport = true;
        return this;
    }

    @Override
    public boolean validate() {
        JSONObject jsonResults = new JSONObject();
        jsonResults.put(ERROR_KEY, false);

        if (rootElement != null) {
            if (!errorMessage.isEmpty()) {
                jsonResults.put(ERROR_KEY, true);
                jsonResults.put(DETAILS, errorMessage);
            }

            if (withReport && !errorMessage.isEmpty()) {
                try {
                    screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    img = ImageIO.read(screenshot);
                } catch (Exception e) {
                    LOG.error("Failed to create screenshot file: " + e.getMessage());
                }

                if (!errorMessage.isEmpty()) {
                    JSONObject rootDetails = new JSONObject();
                    rootDetails.put(X, xRoot);
                    rootDetails.put(Y, yRoot);
                    rootDetails.put(WIDTH, widthRoot);
                    rootDetails.put(HEIGHT, heightRoot);

                    jsonResults.put(ROOT_ELEMENT, rootDetails);
                    jsonResults.put(ELEMENT_NAME, rootElementReadableName);
                    jsonResults.put(SCREENSHOT, rootElementReadableName.replace(" ", "") + "-" + screenshot.getName());
                }

                long ms = System.currentTimeMillis();
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TARGET_AUTOMOTION_JSON + rootElementReadableName.replace(" ", "") + "-automotion" + ms + ".json"), StandardCharsets.UTF_8))) {
                    writer.write(jsonResults.toJSONString());
                } catch (IOException ex) {
                    LOG.error("Cannot create json report: " + ex.getMessage());
                }
                try {
                    File file = new File(TARGET_AUTOMOTION_JSON + rootElementReadableName.replace(" ", "") + "-automotion" + ms + ".json");
                    if (file.getParentFile().mkdirs()) {
                        if (file.createNewFile()) {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                            writer.write(jsonResults.toJSONString());
                            writer.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if ((boolean) jsonResults.get(ERROR_KEY)) {
                    drawScreenshot();
                }
            }
        } else {
            jsonResults.put(ERROR_KEY, true);
            jsonResults.put(DETAILS, "Set root web element");
        }

        return !((boolean) jsonResults.get(ERROR_KEY));
    }

    @Override
    public void generateReport() {
        try {
            new HtmlReportBuilder().buildReport();
        } catch (IOException | ParseException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void drawScreenshot() {
        g = img.createGraphics();

        drawRoot(Color.RED);

        for (Object obj : errorMessage) {
            JSONObject det = (JSONObject) obj;
            JSONObject details = (JSONObject) det.get(REASON);
            JSONObject numE = (JSONObject) details.get(ELEMENT);

            if (numE != null) {
                float x = (float) numE.get(X);
                float y = (float) numE.get(Y);
                float width = (float) numE.get(WIDTH);
                float height = (float) numE.get(HEIGHT);

                g.setColor(Color.MAGENTA);
                g.setStroke(new BasicStroke(2));
                if (SystemHelper.isRetinaDisplay(g) && isChrome()) {
                    g.drawRect(2 * (int) x, 2 * (int) y, 2 * (int) width, 2 * (int) height);
                } else {
                    g.drawRect((int) x, (int) y, (int) width, (int) height);
                }
            }
        }

        try {
            ImageIO.write(img, "png", screenshot);
            File file = new File(TARGET_AUTOMOTION_IMG + rootElementReadableName.replace(" ", "") + "-" + screenshot.getName());
            FileUtils.copyFile(screenshot, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void validateRightOffsetForElements(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            if (!elementsHasEqualLeftRightOffset(false, element)) {
                putJsonDetailsWithElement(String.format("Element '%s' has not the same right offset as element '%s'", rootElementReadableName, readableName), element);
            }
        }
    }

    private void validateLeftOffsetForElements(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            if (!elementsHasEqualLeftRightOffset(true, element)) {
                putJsonDetailsWithElement(String.format("Element '%s' has not the same left offset as element '%s'", rootElementReadableName, readableName), element);
            }
        }
    }

    private void validateTopOffsetForElements(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            if (!elementsHasEqualTopBottomOffset(true, element)) {
                putJsonDetailsWithElement(String.format("Element '%s' has not the same top offset as element '%s'", rootElementReadableName, readableName), element);
            }
        }
    }

    private void validateBottomOffsetForElements(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            if (!elementsHasEqualTopBottomOffset(false, element)) {
                putJsonDetailsWithElement(String.format("Element '%s' has not the same bottom offset as element '%s'", rootElementReadableName, readableName), element);
            }
        }
    }

    private void validateOverlappingWithElements(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            if (elementsAreOverlapped(element)) {
                putJsonDetailsWithElement(String.format("Element '%s' is overlapped with element '%s' but should not", rootElementReadableName, readableName), element);
            }
        }
    }

    private void validateMaxOffset(int top, int right, int bottom, int left) {
        if (xRoot > left) {
            putJsonDetailsWithoutElement(String.format("Expected max left offset of element  '%s' is: %spx. Actual left offset is: %spx", rootElementReadableName, left, xRoot));
        }
        if (yRoot > top) {
            putJsonDetailsWithoutElement(String.format("Expected max top offset of element '%s' is: %spx. Actual top offset is: %spx", rootElementReadableName, top, yRoot));
        }
        if (rootElementRightOffset > right) {
            putJsonDetailsWithoutElement(String.format("Expected max right offset of element  '%s' is: %spx. Actual right offset is: %spx", rootElementReadableName, right, rootElementRightOffset));
        }
        if (rootElementBottomOffset > bottom) {
            putJsonDetailsWithoutElement(String.format("Expected max bottom offset of element  '%s' is: %spx. Actual bottom offset is: %spx", rootElementReadableName, bottom, rootElementBottomOffset));
        }
    }

    private void validateMinOffset(int top, int right, int bottom, int left) {
        if (xRoot < left) {
            putJsonDetailsWithoutElement(String.format("Expected min left offset of element  '%s' is: %spx. Actual left offset is: %spx", rootElementReadableName, left, xRoot));
        }
        if (yRoot < top) {
            putJsonDetailsWithoutElement(String.format("Expected min top offset of element  '%s' is: %spx. Actual top offset is: %spx", rootElementReadableName, top, yRoot));
        }
        if (rootElementRightOffset < right) {
            putJsonDetailsWithoutElement(String.format("Expected min top offset of element  '%s' is: %spx. Actual right offset is: %spx", rootElementReadableName, right, rootElementRightOffset));
        }
        if (rootElementBottomOffset < bottom) {
            putJsonDetailsWithoutElement(String.format("Expected min bottom offset of element  '%s' is: %spx. Actual bottom offset is: %spx", rootElementReadableName, bottom, rootElementBottomOffset));
        }
    }

    private void validateMaxHeight(int height) {
        if (heightRoot > height) {
            putJsonDetailsWithoutElement(String.format("Expected max height of element  '%s' is: %spx. Actual height is: %spx", rootElementReadableName, height, heightRoot));
        }
    }

    private void validateMinHeight(int height) {
        if (heightRoot < height) {
            putJsonDetailsWithoutElement(String.format("Expected min height of element '%s' is: %spx. Actual height is: %spx", rootElementReadableName, height, heightRoot));
        }
    }

    private void validateSameHeight(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            int h = element.getSize().getHeight();
            if (h != heightRoot) {
                putJsonDetailsWithElement(String.format("Element '%s' has not the same height as '%s'. Height of '%s' is %spx. Height of '%s' is %spx", rootElementReadableName, readableName, rootElementReadableName, heightRoot, readableName, h), element);
            }
        }
    }

    private void validateMaxWidth(int width) {
        if (widthRoot > width) {
            putJsonDetailsWithoutElement(String.format("Expected max width of element '%s' is: %spx. Actual width is: %spx", rootElementReadableName, width, widthRoot));
        }
    }

    private void validateMinWidth(int width) {
        if (widthRoot < width) {
            putJsonDetailsWithoutElement(String.format("Expected min width of element '%s' is: %spx. Actual width is: %spx", rootElementReadableName, width, widthRoot));
        }
    }

    private void validateSameWidth(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            int w = element.getSize().getWidth();
            if (w != widthRoot) {
                putJsonDetailsWithElement(String.format("Element '%s' has not the same width as '%s'. Width of '%s' is %spx. Width of '%s' is %spx", rootElementReadableName, readableName, rootElementReadableName, widthRoot, readableName, w), element);
            }
        }
    }

    private void validateSameSize(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            int h = element.getSize().getHeight();
            int w = element.getSize().getWidth();
            if (h != heightRoot || w != widthRoot) {
                putJsonDetailsWithElement(String.format("Element '%s' has not the same size as '%s'. Size of '%s' is %spx x %spx. Size of '%s' is %spx x %spx", rootElementReadableName, readableName, rootElementReadableName, widthRoot, heightRoot, readableName, w, h), element);
            }
        }
    }

    private void validateInsideOfContainer(WebElement element, String readableContainerName) {
        float xContainer = element.getLocation().getX();
        float yContainer = element.getLocation().getY();
        float widthContainer = element.getSize().getWidth();
        float heightContainer = element.getSize().getHeight();

        if (xRoot < xContainer || yRoot < yContainer || (xRoot + widthRoot) > (xContainer + widthContainer) || (yRoot + heightRoot) > (yContainer + heightContainer)) {
            putJsonDetailsWithElement(String.format("Element '%s' is not inside of '%s'", rootElementReadableName, readableContainerName), element);
        }
    }

    private void validateBelowElement(WebElement element, int minMargin, int maxMargin) {
        int yBelowElement = element.getLocation().getY();
        int marginBetweenRoot = yBelowElement - yRoot + heightRoot;
        if (marginBetweenRoot < minMargin || marginBetweenRoot > maxMargin) {
            putJsonDetailsWithElement(String.format("Below element aligned not properly. Expected margin should be between %spx and %spx. Actual margin is %spx", minMargin, maxMargin, marginBetweenRoot), element);
        }
    }

    private void validateBelowElement(WebElement element) {
        List<WebElement> elements = new ArrayList<>();
        elements.add(rootElement);
        elements.add(element);

        if (!PageValidator.elementsAreAlignedVertically(elements)) {
            putJsonDetailsWithoutElement("Below element aligned not properly");
        }
    }

    private void validateAboveElement(WebElement element, int minMargin, int maxMargin) {
        int yAboveElement = element.getLocation().getY();
        int heightAboveElement = element.getSize().getHeight();
        int marginBetweenRoot = yRoot - yAboveElement + heightAboveElement;
        if (marginBetweenRoot < minMargin || marginBetweenRoot > maxMargin) {
            putJsonDetailsWithElement(String.format("Above element aligned not properly. Expected margin should be between %spx and %spx. Actual margin is %spx", minMargin, maxMargin, marginBetweenRoot), element);
        }
    }

    private void validateAboveElement(WebElement element) {
        List<WebElement> elements = new ArrayList<>();
        elements.add(element);
        elements.add(rootElement);

        if (!PageValidator.elementsAreAlignedVertically(elements)) {
            putJsonDetailsWithoutElement("Above element aligned not properly");
        }
    }

    private void validateRightElement(WebElement element, int minMargin, int maxMargin) {
        int xRightElement = element.getLocation().getX();
        int marginBetweenRoot = xRightElement - xRoot + widthRoot;
        if (marginBetweenRoot < minMargin || marginBetweenRoot > maxMargin) {
            putJsonDetailsWithElement(String.format("Right element aligned not properly. Expected margin should be between %spx and %spx. Actual margin is %spx", minMargin, maxMargin, marginBetweenRoot), element);
        }
    }

    private void validateRightElement(WebElement element) {
        List<WebElement> elements = new ArrayList<>();
        elements.add(rootElement);
        elements.add(element);

        if (!PageValidator.elementsAreAlignedHorizontally(elements)) {
            putJsonDetailsWithoutElement("Right element aligned not properly");
        }
    }

    private void validateLeftElement(WebElement leftElement, int minMargin, int maxMargin) {
        int xLeftElement = leftElement.getLocation().getX();
        int widthLeftElement = leftElement.getSize().getWidth();
        int marginBetweenRoot = xRoot - xLeftElement + widthLeftElement;
        if (marginBetweenRoot < minMargin || marginBetweenRoot > maxMargin) {
            putJsonDetailsWithElement(String.format("Left element aligned not properly. Expected margin should be between %spx and %spx. Actual margin is %spx", minMargin, maxMargin, marginBetweenRoot), leftElement);
        }
    }

    private void validateLeftElement(WebElement leftElement) {
        List<WebElement> elements = new ArrayList<>();
        elements.add(leftElement);
        elements.add(rootElement);

        if (!PageValidator.elementsAreAlignedHorizontally(elements)) {
            putJsonDetailsWithoutElement("Left element aligned not properly");
        }
    }

    private boolean elementsAreOverlapped(WebElement elementOverlapWith) {
        Point elLoc = elementOverlapWith.getLocation();
        Dimension elSize = elementOverlapWith.getSize();
        return (xRoot > elLoc.x && yRoot > elLoc.y && xRoot < elLoc.x + elSize.width && yRoot < elLoc.y + elSize.height)
                || (xRoot + widthRoot > elLoc.x && yRoot > elLoc.y && xRoot + widthRoot < elLoc.x + elSize.width && yRoot < elLoc.y + elSize.height)
                || (xRoot > elLoc.x && yRoot + heightRoot > elLoc.y && xRoot < elLoc.x + elSize.width && yRoot + heightRoot < elLoc.y + elSize.height)
                || (xRoot + widthRoot > elLoc.x && yRoot + heightRoot > elLoc.y && xRoot + widthRoot < elLoc.x + elSize.width && yRoot + widthRoot < elLoc.y + elSize.height);
    }

    private boolean elementsHasEqualLeftRightOffset(boolean isLeft, WebElement elementToCompare) {
        Point elLoc = elementToCompare.getLocation();
        Dimension elSize = elementToCompare.getSize();

        if (isLeft) {
            return xRoot == elLoc.getX();
        } else {
            return (pageWidth - xRoot + widthRoot) == (pageWidth - elLoc.getX() + elSize.getWidth());
        }
    }

    private boolean elementsHasEqualTopBottomOffset(boolean isTop, WebElement elementToCompare) {
        Point elLoc = elementToCompare.getLocation();
        Dimension elSize = elementToCompare.getSize();

        if (isTop) {
            return yRoot == elLoc.getY();
        } else {
            return (pageHeight - yRoot + heightRoot) == (pageHeight - elLoc.getY() + elSize.getHeight());
        }
    }

    private void drawRoot(Color color) {
        g.setColor(color);
        g.setStroke(new BasicStroke(2));
        if (SystemHelper.isRetinaDisplay(g) && isChrome()) {
            g.drawRect(2 * xRoot, 2 * yRoot, 2 * widthRoot, 2 * heightRoot);
        } else {
            g.drawRect(xRoot, yRoot, widthRoot, heightRoot);
        }

        g.setStroke(new BasicStroke(1));
        g.setColor(Color.ORANGE);
        if (drawLeftOffsetLine) {
            if (SystemHelper.isRetinaDisplay(g) && isChrome()) {
                g.drawLine(2 * xRoot, 0, 2 * xRoot, 2 * pageHeight);
            } else {
                g.drawLine(xRoot, 0, xRoot, pageHeight);
            }
        }
        if (drawRightOffsetLine) {
            if (SystemHelper.isRetinaDisplay(g) && isChrome()) {
                g.drawLine(2 * (xRoot + widthRoot), 0, 2 * (xRoot + widthRoot), 2 * pageHeight);
            } else {
                g.drawLine(xRoot + widthRoot, 0, xRoot + widthRoot, pageHeight);
            }
        }
        if (drawTopOffsetLine) {
            if (SystemHelper.isRetinaDisplay(g) && isChrome()) {
                g.drawLine(0, 2 * yRoot, 2 * pageWidth, 2 * yRoot);
            } else {
                g.drawLine(0, yRoot, pageWidth, yRoot);
            }
        }
        if (drawBottomOffsetLine) {
            if (SystemHelper.isRetinaDisplay(g) && isChrome()) {
                g.drawLine(0, 2 * (yRoot + heightRoot), 2 * pageWidth, 2 * (yRoot + heightRoot));
            } else {
                g.drawLine(0, yRoot + heightRoot, pageWidth, yRoot + heightRoot);
            }
        }
    }

    private void putJsonDetailsWithoutElement(String message) {
        JSONObject details = new JSONObject();
        JSONObject mes = new JSONObject();
        mes.put(MESSAGE, message);
        details.put(REASON, mes);
        errorMessage.add(details);
    }

    private void putJsonDetailsWithElement(String message, WebElement element) {
        float xContainer = element.getLocation().getX();
        float yContainer = element.getLocation().getY();
        float widthContainer = element.getSize().getWidth();
        float heightContainer = element.getSize().getHeight();

        JSONObject details = new JSONObject();
        JSONObject elDetails = new JSONObject();
        elDetails.put(X, xContainer);
        elDetails.put(Y, yContainer);
        elDetails.put(WIDTH, widthContainer);
        elDetails.put(HEIGHT, heightContainer);
        JSONObject mes = new JSONObject();
        mes.put(MESSAGE, message);
        mes.put(ELEMENT, elDetails);
        details.put(REASON, mes);
        errorMessage.add(details);
    }

    private int getInt(int i, boolean horizontal) {
        if (units.equals(PX)) {
            return i;
        } else {
            if (horizontal) {
                return (i * pageWidth) / 100;
            } else {
                return (i * pageHeight) / 100;
            }
        }
    }

    public enum Units {
        PX,
        PERCENT
    }
}

