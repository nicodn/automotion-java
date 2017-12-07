package net.itarray.automotion.internal;

import net.itarray.automotion.internal.geometry.Direction;
import net.itarray.automotion.internal.geometry.ExtendGiving;
import net.itarray.automotion.internal.geometry.MetricSpace;
import net.itarray.automotion.internal.geometry.Interval;
import net.itarray.automotion.internal.geometry.Rectangle;
import net.itarray.automotion.internal.geometry.Scalar;
import net.itarray.automotion.internal.geometry.Vector;
import net.itarray.automotion.internal.properties.Context;
import net.itarray.automotion.tools.general.SystemHelper;
import net.itarray.automotion.tools.helpers.TextFinder;
import net.itarray.automotion.validation.properties.Condition;
import net.itarray.automotion.internal.properties.ElementPropertyExpression;
import net.itarray.automotion.validation.properties.Expression;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.itarray.automotion.internal.geometry.Direction.*;
import static net.itarray.automotion.internal.geometry.Rectangle.ORIGIN_CORNER;
import static net.itarray.automotion.internal.geometry.Scalar.scalar;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

public class UIElement {
    private static final String PIXELS = "px";

    private final String name;
    private final boolean quoteName;
    private final Rectangle rectangle;
    private final CSSSource cssSource;

    private UIElement(String name, Rectangle rectangle, CSSSource cssSource, boolean quoteName) {
        this.name = name;
        this.quoteName = quoteName;
        this.rectangle = rectangle;
        this.cssSource = cssSource;
    }

    @Deprecated
    public Rectangle getRectangle() {
        return rectangle;
    }

    public static UIElement asElement(WebElement webElement) {
        return new UIElement(defaultName(webElement), Rectangle.rectangle(webElement), new SeleniumCSSSource(webElement), true);
    }

    public static UIElement asElement(WebElement webElement, String name) {
        return new UIElement(name, Rectangle.rectangle(webElement), new SeleniumCSSSource(webElement), true);
    }

    public static UIElement asElement(Rectangle rectangle, String name) {
        return new UIElement(name, rectangle, new NoCSSSource(), true);
    }

    public static List<UIElement> asElements(List<WebElement> webElements) {
        return webElements.stream().map(UIElement::asElement).collect(Collectors.toList());
    }

    public static List<UIElement> asNumberedList(List<UIElement> elements) {
        ArrayList<UIElement> numbered = new ArrayList<>(elements.size());
        for (int i = 0; i < elements.size(); i++) {
            UIElement element = elements.get(i);
            numbered.add(new UIElement(String.format("#%d:%s", i+1, element.rectangle), element.rectangle, element.cssSource, false));
        }
        return numbered;
    }

    private static String defaultName(WebElement webElement) {
        Point location = webElement.getLocation();
        Dimension size = webElement.getSize();
        return String.format("with properties: tag=[%s], id=[%s], class=[%s], text=[%s], coord=[%s,%s], size=[%s,%s]",
                webElement.getTagName(),
                webElement.getAttribute("id"),
                webElement.getAttribute("class"),
                getShortenedText(webElement.getText()),
                String.valueOf(location.getX()),
                String.valueOf(location.getY()),
                String.valueOf(size.getWidth()),
                String.valueOf(size.getHeight()));
    }

    public Scalar getBegin(Direction direction) {
        return direction.begin(rectangle);
    }

    public Scalar getEnd(Direction direction) {
        return direction.end(rectangle);
    }

    public <V extends MetricSpace<V>> V getExtend(ExtendGiving<V> direction) {
        return direction.extend(rectangle);
    }

    public Scalar getX() {
        return getOrigin().getX();
    }

    public Vector getOrigin() {
        return rectangle.getOrigin();
    }

    public Scalar getY() {
        return getOrigin().getY();
    }

    public Scalar getWidth() {
        return RIGHT.extend(rectangle);
    }

    public Scalar getHeight() {
        return DOWN.extend(rectangle);
    }

    public Vector getSize() {
        return ORIGIN_CORNER.extend(rectangle);
    }

    public Vector getCorner() {
        return rectangle.getCorner();
    }

    private boolean hasEqualBegin(UIElement other, Direction direction) {
        return getBegin(direction).equals(other.getBegin(direction));
    }

    private boolean hasEqualEnd(UIElement other, Direction direction) {
        return getEnd(direction).equals(other.getEnd(direction));
    }

    public boolean hasEqualLeftOffsetAs(UIElement other) {
        return hasEqualBegin(other, RIGHT);
    }

    public boolean hasEqualRightOffsetAs(UIElement other) {
        return hasEqualBegin(other, LEFT);
    }

    public boolean hasEqualTopOffsetAs(UIElement other) {
        return hasEqualBegin(other, DOWN);
    }

    public boolean hasEqualBottomOffsetAs(UIElement other) {
        return hasEqualBegin(other, UP);
    }

    private <V extends MetricSpace<V>> boolean hasEqualExtendAs(UIElement other, ExtendGiving<V> direction, Context context) {
        Expression<Boolean> equal = Expression.equalTo(
                ElementPropertyExpression.extend(direction, this),
                ElementPropertyExpression.extend(direction, this));
        return getExtend(direction).equals(other.getExtend(direction));
    }

    public boolean hasSameWidthAs(UIElement other, Context context) {
        return hasEqualExtendAs(other, RIGHT, context);
    }

    public boolean hasSameHeightAs(UIElement other, Context context) {
        return hasEqualExtendAs(other, DOWN, context);
    }

    public boolean hasSameSizeAs(UIElement other, Context context) {
        return hasEqualExtendAs(other, Rectangle.ORIGIN_CORNER, context);
    }

    public boolean overlaps(UIElement other) {
        return rectangle.intersects(other.rectangle);
    }

    public Scalar getOffset(Direction direction, UIElement page) {
        return direction.signedDistance(getEnd(direction), page.getEnd(direction));
    }

    public Scalar getTopOffset(UIElement page) {
        return getOffset(UP, page);
    }

    public Scalar getBottomOffset(UIElement page) {
        return getOffset(DOWN, page);
    }

    public Scalar getLeftOffset(UIElement page) {
        return getOffset(LEFT, page);
    }

    public Scalar getRightOffset(UIElement page) {
        return getOffset(RIGHT, page);
    }

    public boolean hasEqualOppositeOffsets(Direction direction, UIElement page) {
        return getOffset(direction, page).equals(getOffset(direction.opposite(), page));
    }

    public boolean hasSuccessor(Direction direction, UIElement possibleSuccessor) {
        return signedDistanceToSuccessor(direction, possibleSuccessor).isGreaterOrEqualTo(scalar(0));
    }

    public Scalar signedDistanceToSuccessor(Direction direction, UIElement successor) {
        return direction.signedDistance(direction.end(rectangle), direction.begin(successor.rectangle));
    }

    public  boolean hasRightElement(UIElement rightElement) {
        return hasSuccessor(RIGHT, rightElement);
    }

    public  boolean hasLeftElement(UIElement leftElement) {
        return leftElement.hasSuccessor(RIGHT, this);
    }

    public boolean hasBelowElement(UIElement bottomElement) {
        return hasSuccessor(DOWN, bottomElement);
    }

    public boolean hasAboveElement(UIElement aboveElement) {
        return aboveElement.hasSuccessor(DOWN, this);
    }

    public String getCssValue(String cssProperty) {
        return cssSource.getCssValue(cssProperty);
    }

    public String getName() {
        return name;
    }

    public String getQuotedName() {
        return quoteName ? String.format("'%s'", name) : name;
    }

    private static String getShortenedText(String text) {
        int maxLength = 13;
        if (text.length() <= maxLength) {
            return text;
        }
        String postfix = "...";
        return text.substring(0, maxLength-postfix.length()) + postfix;
    }

    public boolean contains(UIElement other) {
        return rectangle.contains(other.rectangle);
    }

    public boolean contains(Rectangle other) {
        return rectangle.contains(other);
    }

    public void validateLeftAlignedWith(UIElement element, Errors errors) {
        validateEqualEnd(LEFT, element, errors);
    }

    public void validateRightAlignedWith(UIElement element, Errors errors) {
        validateEqualEnd(RIGHT, element, errors);
    }

    public void validateTopAlignedWith(UIElement element, Errors errors) {
        validateEqualEnd(UP, element, errors);
    }

    public void validateBottomAlignedWith(UIElement element, Errors errors) {
        validateEqualEnd(DOWN, element, errors);
    }

    public void validateEqualEnd(Direction direction, UIElement element, Errors errors) {
        if (!hasEqualEnd(element, direction)) {
            errors.add(
                    String.format("Element %s has not the same %s offset as element %s",
                            getQuotedName(),
                            direction.endName(),
                            element.getQuotedName()),
                    element);
        }
    }

    public void validateSameSize(UIElement element, Context context, Errors errors) {
        validateSameExtend(ORIGIN_CORNER, element, context, errors);
    }

    public void validateSameHeight(UIElement element, Context context, Errors errors) {
        validateSameExtend(DOWN, element, context, errors);
    }

    public void validateSameWidth(UIElement element, Context context, Errors errors) {
        validateSameExtend(RIGHT, element, context, errors);
    }

    public <V extends MetricSpace<V>> void validateSameExtend(ExtendGiving<V> direction, UIElement element, Context context, Errors errors) {
        if (!hasEqualExtendAs(element, direction, context)) {
            errors.add(
                    String.format("Element %s has not the same %s as element %s. %s of %s is %s. %s of element is %s",
                            getQuotedName(),
                            direction.extendName(),
                            element.getQuotedName(),
                            capitalize(direction.extendName()),
                            getQuotedName(),
                            direction.extend(rectangle).toStringWithUnits(PIXELS),
                            capitalize(direction.extendName()),
                            direction.extend(element.rectangle).toStringWithUnits(PIXELS)),
                    element);
        }
    }

    public <V extends MetricSpace<V>> void validateNotSameExtend(ExtendGiving<V> direction, UIElement element, Context context, Errors errors) {
        if (hasEqualExtendAs(element, direction, context)) {
            errors.add(
                    String.format("Element %s has the same %s as element %s. %s of %s is %s. %s of element is %s",
                            getQuotedName(),
                            direction.extendName(),
                            element.getQuotedName(),
                            capitalize(direction.extendName()),
                            getQuotedName(),
                            direction.extend(rectangle).toStringWithUnits(PIXELS),
                            capitalize(direction.extendName()),
                            direction.extend(element.rectangle).toStringWithUnits(PIXELS)),
                    element);
        }
    }

    public void validateNotSameSize(UIElement element, Context context, Errors errors) {
        validateNotSameExtend(ORIGIN_CORNER, element, context, errors);
    }


    public void validateIsRightOf(UIElement leftElement, Errors errors) {
        validateSuccessor(LEFT, leftElement, errors);
    }

    public void validateIsRightOf(UIElement element, Condition<Scalar> condition, Context context, Errors errors) {
        validateSuccessor(LEFT, element, condition, context, errors);
    }

    public void validateIsLeftOf(UIElement rightElement, Errors errors) {
        validateSuccessor(RIGHT, rightElement, errors);
    }

    public void validateIsLeftOf(UIElement element, Condition<Scalar> condition, Context context, Errors errors) {
        validateSuccessor(RIGHT, element, condition, context, errors);
    }

    public void validateIsBelow(UIElement aboveElement, Errors errors) {
        validateSuccessor(UP, aboveElement, errors);
    }

    public void validateIsBelow(UIElement element, Condition<Scalar> condition, Context context, Errors errors) {
        validateSuccessor(UP, element, condition, context, errors);
    }

    public void validateIsAbove(UIElement belowElement, Errors errors) {
        validateSuccessor(DOWN, belowElement, errors);
    }

    public void validateIsAbove(UIElement element, Condition<Scalar> condition, Context context, Errors errors) {
        validateSuccessor(DOWN, element, condition, context, errors);
    }

    public void validateSuccessor(Direction direction, UIElement toBeValidatedSuccessor, Errors errors) {
        if (!hasSuccessor(direction, toBeValidatedSuccessor)) {
            errors.add(
                    String.format("%s element aligned not properly",
                            direction.afterName()),
                    toBeValidatedSuccessor);
        }
    }

    public void validateSuccessor(Direction direction, UIElement toBeValidatedSuccessor, Condition<Scalar> condition, Context context, Errors errors) {
        Scalar signedDistance = signedDistanceToSuccessor(direction, toBeValidatedSuccessor);
        if (!signedDistance.satisfies(condition, context, direction)) {
            errors.add(
                    String.format("%s element aligned not properly. Expected margin should be %s. Actual margin is %s",
                            direction.afterName(),
                            condition.getDescription(context, direction),
                            signedDistance.toStringWithUnits(PIXELS)),
                    toBeValidatedSuccessor);
        }
    }

    public void validateOverlappingWithElement(UIElement element, Errors errors) {
        if (!overlaps(element)) {
            errors.add(
                    String.format("Element %s is not overlapped with element %s but should be",
                            getQuotedName(),
                            element.getQuotedName()),
                    element);
        }
    }

    public void validateNotOverlappingWithElement(UIElement element, Errors errors) {
        if (overlaps(element)) {
            errors.add(
                    String.format("Element %s is overlapped with element %s but should not",
                            getQuotedName(),
                            element.getQuotedName()),
                    element);
        }
    }

    public void validateLeftOffset(Condition condition, UIElement page, Context context, Errors errors) {
        validateOffset(LEFT, condition, page, context, errors);
    }

    public void validateRightOffset(Condition condition, UIElement page, Context context, Errors errors) {
        validateOffset(RIGHT, condition, page, context, errors);
    }

    public void validateTopOffset(Condition condition, UIElement page, Context context, Errors errors) {
        validateOffset(UP, condition, page, context, errors);
    }

    public void validateBottomOffset(Condition condition, UIElement page, Context context, Errors errors) {
        validateOffset(DOWN, condition, page, context, errors);
    }

    public void validateOffset(Direction direction, Condition condition, UIElement page, Context context, Errors errors) {
        if (!getOffset(direction, page).satisfies(condition, context, direction)) {
            errors.add(
                    String.format("Expected %s offset of element %s to be %s. Actual %s offset is: %s",
                            direction.endName(),
                            getQuotedName(),
                            condition.getDescription(context, direction),
                            direction.endName(),
                            getOffset(direction, page).toStringWithUnits(PIXELS)));
        }
    }

    public void validateCenteredOnVertically(UIElement page, Errors errors) {
        validateCentered(RIGHT, page, errors);
    }

    public void validateCenteredOnHorizontally(UIElement page, Errors errors) {
        validateCentered(DOWN, page, errors);
    }

    public void validateCentered(Direction direction, UIElement page, Errors errors) {
        Direction opposite = direction.opposite();
        if (!hasEqualOppositeOffsets(direction, page)) {
            errors.add(
                    String.format("Element %s has not equal %s and %s offset. %s offset is %s, %s is %s",
                            getQuotedName(),
                            opposite.endName(),
                            direction.endName(),
                            capitalize(opposite.endName()),
                            getOffset(opposite, page).toStringWithUnits(PIXELS),
                            direction.endName(),
                            getOffset(direction, page).toStringWithUnits(PIXELS)),
                    this);
        }
    }

    public void validateHeight(Condition<Scalar> condition, Context context, Errors errors) {
        validateExtend(DOWN, condition, context, errors);
    }

    public void validateWidth(Condition<Scalar> condition, Context context, Errors errors) {
        validateExtend(RIGHT, condition, context, errors);
    }

    public void validateExtend(Direction direction, Condition<Scalar> condition, Context context, Errors errors) {
        ElementPropertyExpression<Scalar> property = ElementPropertyExpression.extend(direction, this);
        Expression<Boolean> assertion = condition.applyTo(property);
        if (!assertion.evaluateIn(context, direction)) {
            errors.add(
                    assertion.getDescription(context, direction));
        }
    }

    public void validateDoesNotHaveCssValue(String cssProperty, String[] args, Errors errors) {
        String cssValue = getCssValue(cssProperty);

        if (!cssValue.equals("")) {
            for (String val : args) {
                val = !val.startsWith("#") ? val : SystemHelper.hexStringToARGB(val);
                if (TextFinder.textIsFound(val, cssValue)) {
                    errors.add(String.format("CSS property '%s' should not contain value '%s'. Actual value is '%s'", cssProperty, val, cssValue));
                }
            }
        } else {
            errors.add(
                    String.format("Element %s does not have css property '%s'",
                            getQuotedName(),
                            cssProperty));
        }
    }

    public void validateHasCssValue(String cssProperty, String[] args, Errors errors) {
        String cssValue = getCssValue(cssProperty);

        if (!cssValue.equals("")) {
            for (String val : args) {
                val = !val.startsWith("#") ? val : SystemHelper.hexStringToARGB(val);
                if (!TextFinder.textIsFound(val, cssValue)) {
                    errors.add(String.format("Expected value of '%s' is '%s'. Actual value is '%s'", cssProperty, val, cssValue));
                }
            }
        } else {
            errors.add(
                    String.format("Element %s does not have css property '%s'",
                            getQuotedName(),
                            cssProperty));
        }
    }

    public void validateInsideOfContainer(UIElement containerElement, Errors errors) {
        if (!containerElement.contains(this)) {
            errors.add(
                    String.format("Element '%s' is not inside of '%s'",
                            getName(),
                            containerElement.getName()),
                    containerElement);
        }
    }

    public void validateInsideOfContainer(UIElement element, Errors errors, Scalar top, Scalar left, Scalar right, Scalar bottom) {
        Vector originPadding = new Vector(left, top);
        Vector cornerPadding = new Vector(right, bottom);

        Rectangle paddedRoot = new Rectangle(
                getOrigin().minus(originPadding),
                getCorner().plus(cornerPadding));

        Vector originOffset = getOrigin().minus(element.getOrigin());
        Vector cornerOffset = getCorner().minus(element.getCorner());

        if (!element.contains(paddedRoot)) {
            errors.add(
                    String.format("Padding of element %s is incorrect. Expected padding: top[%s], right[%s], bottom[%s], left[%s]. Actual padding: top[%s], right[%s], bottom[%s], left[%s]",
                            getQuotedName(),
                            originPadding.getY(),
                            cornerPadding.getX(),
                            cornerPadding.getY(),
                            originPadding.getX(),
                            originOffset.getY(),
                            cornerOffset.getX(),
                            cornerOffset.getY(),
                            originOffset.getX()),
                    element);
        }
    }

    public Interval getYInterval() {
        return getInterval(Direction.DOWN);
    }

    public Interval getXInterval() {
        return getInterval(Direction.RIGHT);
    }

    private Interval getInterval(Direction direction) {
        return Interval.interval(getBegin(direction), getEnd(direction));
    }
}
