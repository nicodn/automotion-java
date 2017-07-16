package net.itarray.automotion.tests;

import net.itarray.automotion.validation.ChunkUIElementValidator;
import net.itarray.automotion.validation.UIElementValidator;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static rectangles.DummyWebElement.createElement;

public class NativeNamingBackwardsCompatibilityTest {

    private WebElement reference;
    private List<WebElement> references;
    private UIElementValidator validator;
    private ChunkUIElementValidator chunkValidator;

    @Before
    public void setUp() {
        reference = createElement(17, 19, 67, 89);
        references = Arrays.asList(reference);
        validator = spy(UIElementValidator.class);
        chunkValidator = spy(ChunkUIElementValidator.class);
    }


    @Test
    public void withLeftElement() {
        validator.withLeftElement(reference);
        verify(validator).isRightOf(reference);
    }

    @Test
    public void withLeftElementInRange() {
        validator.withLeftElement(reference, 5, 7);
        verify(validator).isRightOf(reference, 5, 7);
    }

    @Test
    public void withRightElement() {
        validator.withRightElement(reference);
        verify(validator).isLeftOf(reference);
    }

    @Test
    public void withRightElementInRange() {
        validator.withRightElement(reference, 5, 7);
        verify(validator).isLeftOf(reference, 5, 7);
    }

    @Test
    public void withTopElement() {
        validator.withTopElement(reference);
        verify(validator).isBelow(reference);
    }

    @Test
    public void withTopElementInRange() {
        validator.withTopElement(reference, 5, 7);
        verify(validator).isBelow(reference, 5, 7);
    }

    @Test
    public void withBottomElement() {
        validator.withBottomElement(reference);
        verify(validator).isAbove(reference);
    }

    @Test
    public void withBottomElementInRange() {
        validator.withBottomElement(reference, 5, 7);
        verify(validator).isAbove(reference, 5, 7);
    }

    @Test
    public void withCssValue() {
            validator.withCssValue("key", "value");
        verify(validator).hasCssValue("key", "value");
    }

    @Test
    public void withoutCssValue() {
            validator.withoutCssValue("key", "value");
        verify(validator).doesNotHaveCssValue("key", "value");
    }

    @Test
    public void sameOffsetLeftAs() {
            validator.sameOffsetLeftAs(reference, "reference");
        verify(validator).isVerticallyLeftAlignedWith(reference, "reference");
    }

    @Test
    public void sameOffsetLeftAsList() {
            validator.sameOffsetLeftAs(references);
        verify(validator).isVerticallyLeftAlignedWith(references);
    }

    @Test
    public void sameLeftOffsetChunk() {
        chunkValidator.sameLeftOffset();
        verify(chunkValidator).areVerticallyLeftAligned();
    }
    
    @Test
    public void sameOffsetRightAs() {
            validator.sameOffsetRightAs(reference, "reference");
        verify(validator).isVerticallyRightAlignedWith(reference, "reference");
    }

    @Test
    public void sameOffsetRightAsList() {
            validator.sameOffsetRightAs(references);
        verify(validator).isVerticallyRightAlignedWith(references);
    }

    @Test
    public void sameRightOffsetChunk() {
        chunkValidator.sameRightOffset();
        verify(chunkValidator).areVerticallyRightAligned();
    }

    @Test
    public void sameOffsetTopAs() {
            validator.sameOffsetTopAs(reference, "reference");
        verify(validator).isHorizontallyTopAlignedWith(reference, "reference");
    }

    @Test
    public void sameOffsetTopAsList() {
            validator.sameOffsetTopAs(references);
        verify(validator).isHorizontallyTopAlignedWith(references);
    }

    @Test
    public void sameTopOffsetChunk() {
        chunkValidator.sameTopOffset();
        verify(chunkValidator).areHorizontallyTopAligned();
    }

    @Test
    public void sameOffsetBottomAs() {
            validator.sameOffsetBottomAs(reference, "reference");
        verify(validator).isHorizontallyBottomAlignedWith(reference, "reference");
    }

    @Test
    public void sameOffsetBottomAsList() {
            validator.sameOffsetBottomAs(references);
        verify(validator).isHorizontallyBottomAlignedWith(references);
    }
    
    @Test
    public void sameBottomOffsetChunk() {
        chunkValidator.sameBottomOffset();
        verify(chunkValidator).areHorizontallyBottomAligned();
    }

    @Test
    public void minWidth() {
        validator.minWidth(9);
        verify(validator).hasWidthGreaterOrEqualTo(9);
    }

    @Test
    public void maxWidth() {
        validator.maxWidth(9);
        verify(validator).hasWidthLessOrEqualTo(9);
    }

    @Test
    public void widthBetween() {
        validator.widthBetween(9, 13);
        verify(validator).hasWidthBetween(9, 13);
    }

    @Test
    public void minHeight() {
        validator.minHeight(9);
        verify(validator).hasHeightGreaterOrEqualTo(9);
    }

    @Test
    public void maxHeight() {
        validator.maxHeight(9);
        verify(validator).hasHeightLessOrEqualTo(9);
    }

    @Test
    public void heightBetween() {
        validator.heightBetween(9, 13);
        verify(validator).hasHeightBetween(9, 13);
    }

}
