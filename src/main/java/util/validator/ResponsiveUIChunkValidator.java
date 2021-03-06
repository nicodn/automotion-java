package util.validator;

import net.itarray.automotion.internal.AbstractValidator;
import net.itarray.automotion.internal.DriverFacade;
import net.itarray.automotion.internal.ResponsiveUIChunkValidatorBase;
import net.itarray.automotion.validation.ChunkUIElementValidator;
import net.itarray.automotion.validation.UISnapshot;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @deprecated As of release 2.0, replaced by {@link net.itarray.automotion.internal.ResponsiveUIChunkValidatorBase}
 */
@Deprecated
public class ResponsiveUIChunkValidator extends AbstractValidator implements ChunkValidator {

    public ResponsiveUIChunkValidator(UISnapshot snapshot, DriverFacade driver, List<WebElement> webElements) {
        super(driver, new ResponsiveUIChunkValidatorBase(snapshot, webElements, false));
        if (webElements.isEmpty()) {
            String message = "Set root web element";
            addError(message);
        }
    }

    @Override
    public ResponsiveUIChunkValidator drawMap() {
        super.drawMap();
        return this;
    }

    @Override
    public ResponsiveUIChunkValidator dontDrawMap() {
        super.dontDrawMap();
        return this;
    }

    protected ResponsiveUIChunkValidatorBase getBase() {
        return (ResponsiveUIChunkValidatorBase) super.getBase();
    }

    /**
     * Change units to Pixels or % (Units.PX, Units.PERCENT)
     *
     * @param units
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator changeMetricsUnitsTo(Units units) {
        getBase().changeMetricsUnitsTo(units);
        return this;
    }

    @Override
    public ResponsiveUIChunkValidator changeMetricsUnitsTo(net.itarray.automotion.validation.Units units) {
        getBase().changeMetricsUnitsTo(units);
        return this;
    }



    /**
     * Verify that elements are aligned in a grid view width specified amount of columns
     *
     * @param horizontalGridSize
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator alignedAsGrid(int horizontalGridSize) {
        getBase().alignedAsGrid(horizontalGridSize);
        return this;
    }

    /**
     * Verify that elements are aligned in a grid view width specified amount of columns and rows
     *
     * @param horizontalGridSize
     * @param verticalGridSize
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator alignedAsGrid(int horizontalGridSize, int verticalGridSize) {
        getBase().alignedAsGrid(horizontalGridSize, verticalGridSize);
        return this;
    }

    @Override
    public ChunkUIElementValidator areAlignedAsGridCells() {
        getBase().areAlignedAsGridCells();
        return this;
    }

    /**
     * Verify that every element in the list is not overlapped with another element from this list
     *
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator doNotOverlap() {
        getBase().doNotOverlap();
        return this;
    }

    /**
     * Verify that elements in the list have the same size
     *
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator haveEqualSize() {
        getBase().haveEqualSize();
        return this;
    }

    /**
     * Verify that elements in the list have the same width
     *
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator haveEqualWidth() {
        getBase().haveEqualWidth();
        return this;
    }

    /**
     * Verify that elements in the list have the same height
     *
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator haveEqualHeight() {
        getBase().haveEqualHeight();
        return this;
    }

    /**
     * Verify that elements in the list have not the same size
     *
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator haveDifferentSizes() {
        getBase().haveDifferentSizes();
        return this;
    }

    /**
     * Verify that elements in the list have not the same width
     *
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator haveDifferentWidths() {
        getBase().haveDifferentWidths();
        return this;
    }

    /**
     * Verify that elements in the list have not the same height
     *
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator haveDifferentHeights() {
        getBase().haveDifferentHeights();
        return this;
    }

    /**
     * Verify that elements in the list have the right offset
     *
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator areRightAligned() {
        getBase().areRightAligned();
        return this;
    }

    /**
     * Verify that elements in the list have the same left offset
     *
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator areLeftAligned() {
        getBase().areLeftAligned();
        return this;
    }

    /**
     * Verify that elements in the list have the same top offset
     *
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator areTopAligned() {
        getBase().areTopAligned();
        return this;
    }

    /**
     * Verify that elements in the list have the same bottom offset
     *
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator areBottomAligned() {
        getBase().areBottomAligned();
        return this;
    }

    /**
     * Verify that every element in the list have equal right and left offset (aligned horizontally in center)
     *
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator areCenteredOnPageVertically() {
        getBase().areCenteredOnPageVertically();
        return this;
    }

    /**
     * Verify that every element in the list have equal top and bottom offset (aligned vertically in center)
     *
     * @return ResponsiveUIChunkValidator
     */
    @Override
    public ResponsiveUIChunkValidator areCenteredOnPageHorizontally() {
        getBase().areCenteredOnPageHorizontally();
        return this;
    }

    /**
     * Verify that element(s) is(are) located inside of specified element
     *
     * @param containerElement
     * @param readableContainerName
     * @return ResponsiveUIValidator
     */
    @Override
    public ResponsiveUIChunkValidator areInsideOf(WebElement containerElement, String readableContainerName) {
        getBase().areInsideOf(containerElement, readableContainerName);
        return this;
    }
}