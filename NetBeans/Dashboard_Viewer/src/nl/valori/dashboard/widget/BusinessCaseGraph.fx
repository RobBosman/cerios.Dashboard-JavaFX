/*
 * ProgressBar.fx
 *
 * Created on 4-feb-2009, 16:29:09
 */

package nl.valori.dashboard.widget;

import java.lang.Math;
import java.util.Date;
import javafx.scene.CustomNode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextOrigin;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import nl.valori.dashboard.Constants;
import nl.valori.dashboard.model.GuiComponent;
import nl.valori.dashboard.model.GuiElement;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.dashboard.widget.TrafficLight;
import org.jfxtras.scene.layout.MigLayout;

/**
 * @author Rob
 */
public class BusinessCaseGraph extends CustomNode {

    var guiElementTotalBudget: GuiElement;
    var guiElementExpectedReturn: GuiElement;
    var guiElementEstimatedCosts: GuiElement;
    var maximum = Float.NaN;

    public-init var guiComponent: GuiComponent on replace oldValue {
        guiElementTotalBudget = guiComponent.getGuiElement("TOTAL_BUDGET");
        guiElementExpectedReturn = guiComponent.getGuiElement("EXPECTED_RETURN");
        guiElementEstimatedCosts = guiComponent.getGuiElement("ESTIMATED_COSTS");
        maximum = Float.NaN;
    };
    public var kpiHolder: KpiHolder on replace oldValue { maximum = Float.NaN; };
    public var date: Date;

    def constants = new Constants();
    def xywh = constants.getXYWH(guiComponent.getLayout());
    def width = xywh[2];
    def barWidth = 40;
    def graphHeight = 200;
    def spacing = 3;

    var expectedReturnInitial = bind getInitial(kpiHolder);
    var expectedReturn = bind kpiHolder.getValueSet(guiElementExpectedReturn.getKpiVariable()).getValueAt(date);
    var totalBudget = bind kpiHolder.getValueSet(guiElementTotalBudget.getKpiVariable()).getValueAt(date);
    var estimatedCosts = bind kpiHolder.getValueSet(guiElementEstimatedCosts.getKpiVariable()).getValueAt(date);

    def titleText = Text {
        font: constants.titleFont
        fill: constants.titleFontColor
        content: guiComponent.getTitle()
    }
    def mainRect = Rectangle {
        width: width
        height: graphHeight
        fill: bind constants.getAlertColor(getAlertLevelAt(kpiHolder, date))
    }
    def expectedReturnInitialLine = Line {
        startX: 0
        startY: 0
        endX: width
        endY: 0
        translateY: bind getGraphPos(expectedReturnInitial)
        strokeWidth: 3
        stroke: constants.targetColor
        opacity: 0.6
    }
    def totalBudgetInitialLine = Line {
        startX: 0
        startY: 0
        endX: width
        endY: 0
        translateY: bind getGraphPos(totalBudget)
        strokeWidth: 3
        stroke: constants.targetColor
        opacity: 0.6
    }
    def expectedReturnInitialRect = Rectangle {
        x: 20
        y: bind getGraphPos(expectedReturnInitial)
        width: barWidth
        height: bind graphHeight - getGraphPos(expectedReturnInitial)
        fill: constants.targetColor
        effect: DropShadow{
            offsetX: 2
            offsetY: 2
            color: Color.BLACK
        }
    }
    def totalBudgetRect = Rectangle {
        x: expectedReturnInitialRect.x + barWidth + 5
        y: bind getGraphPos(totalBudget)
        width: barWidth
        height: bind graphHeight - getGraphPos(totalBudget)
        fill: constants.targetColor
        effect: DropShadow{
            offsetX: 2
            offsetY: 2
            color: Color.BLACK
        }
    }
    def expectedReturnActualRect = Rectangle {
        x: totalBudgetRect.x + barWidth + 40
        y: bind getGraphPos(expectedReturn)
        width: barWidth
        height: bind graphHeight - getGraphPos(expectedReturn)
        fill: bind LinearGradient {
            startX: 0.0,
            startY: 0.0,
            endX: 1.0,
            endY: 1.0,
            proportional: true
            stops: [
                Stop {
                    offset: 0.0
                    color: constants.actualColor
                }
                Stop {
                    offset: 1.0
                    color: constants.actualGradientColor
                }
            ]
        }
        effect: DropShadow{
            offsetX: 2
            offsetY: 2
            color: Color.BLACK
        }
    }
    def estimatedCostsRect = Rectangle {
        x: expectedReturnActualRect.x + barWidth + 5
        y: bind getGraphPos(estimatedCosts)
        width: barWidth
        height: bind graphHeight - getGraphPos(estimatedCosts)
        fill: bind LinearGradient {
            startX: 0.0,
            startY: 0.0,
            endX: 1.0,
            endY: 1.0,
            proportional: true
            stops: [
                Stop {
                    offset: 0.0
                    color: constants.actualColor
                }
                Stop {
                    offset: 1.0
                    color: constants.actualGradientColor
                }
            ]
        }
        effect: DropShadow{
            offsetX: 2
            offsetY: 2
            color: Color.BLACK
        }
    }

    def expectedReturnInitialValue = Text {
        textOrigin: TextOrigin.TOP
        textAlignment: TextAlignment.RIGHT
        font: constants.valueFont
        fill: constants.backPlateFontColor
        content: bind "{guiElementExpectedReturn.format(expectedReturnInitial)} {guiElementExpectedReturn.getUnit()}"
        translateX: expectedReturnInitialRect.x - 10
        translateY: bind expectedReturnInitialRect.y - 15
    }
    def totalBudgetValue = Text {
        textOrigin: TextOrigin.TOP
        textAlignment: TextAlignment.RIGHT
        font: constants.valueFont
        fill: constants.backPlateFontColor
        content: bind "{guiElementTotalBudget.format(totalBudget)} {guiElementTotalBudget.getUnit()}"
        translateX: totalBudgetRect.x - 10
        translateY: bind totalBudgetRect.y - 15
        visible: bind not(totalBudget.isNaN())
    }
    def expectedReturnActualValue = Text {
        textOrigin: TextOrigin.TOP
        textAlignment: TextAlignment.RIGHT
        font: constants.valueFont
        fill: constants.backPlateFontColor
        content: bind "{guiElementExpectedReturn.format(expectedReturn)} {guiElementExpectedReturn.getUnit()}"
        translateX: expectedReturnActualRect.x - 10
        translateY: bind expectedReturnActualRect.y - 15
        visible: bind not(expectedReturn.isNaN())
    }
    def estimatedCostsValue = Text {
        textOrigin: TextOrigin.TOP
        textAlignment: TextAlignment.RIGHT
        font: constants.valueFont
        fill: constants.backPlateFontColor
        content: bind "{guiElementEstimatedCosts.format(estimatedCosts)} {guiElementEstimatedCosts.getUnit()}"
        translateX: estimatedCostsRect.x - 10
        translateY: bind estimatedCostsRect.y - 15
        visible: bind not(estimatedCosts.isNaN())
    }
    def trafficLight = TrafficLight {
        level: bind getAlertLevelAt(kpiHolder, date)
        effect: DropShadow{
            offsetX: 2
            offsetY: 2
            color: Color.BLACK
        }
        transforms: Scale {
            x: 0.5
            y: 0.5
        }
        translateX: mainRect.width - 50
        translateY: 10
    }

    def textRotation = Rotate {
        pivotX: 0.0
        pivotY: 0.0
        angle: 30.0
    }
    def expectedReturnInitialText = Text {
        font: constants.labelFont
        fill: constants.fontColor
        content: "Expected return (initial)"
        transforms: textRotation
        translateX: expectedReturnInitialRect.x
    }
    def totalBudgetText = Text {
        font: constants.labelFont
        fill: constants.fontColor
        content: "Total budget"
        transforms: textRotation
        translateX: totalBudgetRect.x
    }
    def expectedReturnActualText = Text {
        font: constants.labelFont
        fill: constants.fontColor
        content: "Expected return"
        transforms: textRotation
        translateX: expectedReturnActualRect.x
    }
    def estimatedCostsText = Text {
        font: constants.labelFont
        fill: constants.fontColor
        content: "Estimated costs"
        transforms: textRotation
        translateX: estimatedCostsRect.x
    }

    function getInitial(kpiHolder:KpiHolder):Float {
        if (kpiHolder.getChildren().isEmpty()) {
            return kpiHolder.getValueSet(guiElementExpectedReturn.getKpiVariable()).getValueAt(kpiHolder.getFullPeriod().begin);
        }
        var val:Number = 0;
        for (kpiHolderChild in kpiHolder.getChildren()) {
            val += getInitial(kpiHolderChild);
        }
        return val;
    }

    function getGraphPos(val:Float):Float {
        if (maximum.isNaN()) {
            if ((guiComponent == null) or (kpiHolder == null)) {
                return graphHeight;
            }
            def expectedReturnInitial = kpiHolder.getValueSet(guiElementExpectedReturn.getKpiVariable()).getValueAt(kpiHolder.getFullPeriod().begin);
            def expectedReturnMax = kpiHolder.getValueSet(guiElementExpectedReturn.getKpiVariable()).getMaximum(kpiHolder.getFullPeriod());
            def totalBudgetMax = kpiHolder.getValueSet(guiElementTotalBudget.getKpiVariable()).getMaximum(kpiHolder.getFullPeriod());
            def estimatedCostsMax = kpiHolder.getValueSet(guiElementEstimatedCosts.getKpiVariable()).getMaximum(kpiHolder.getFullPeriod());
            maximum = Math.max(Math.max(Math.max(expectedReturnInitial, expectedReturnMax), totalBudgetMax), estimatedCostsMax);
        }
        if ((val.isNaN()) or (maximum.isNaN()) or (maximum == 0)) {
            return graphHeight;
        }
        return graphHeight - 0.8 * graphHeight * val / maximum;
    }

    function getAlertLevelAt(kpiHolder:KpiHolder, date: Date):Integer {
        return Math.max(kpiHolder.getAlertLevelAt(guiElementExpectedReturn.getKpiVariable(), date),
        kpiHolder.getAlertLevelAt(guiElementEstimatedCosts.getKpiVariable(), date));
    }

    override public function create():Node {
        def backPlate = Group {
            content: [ mainRect, expectedReturnInitialLine, totalBudgetInitialLine ]
            effect: InnerShadow{
                offsetX: 2
                offsetY: 2
                color: Color.BLACK
            }
        }
        return MigLayout {
            content: [
                MigLayout.migNode(titleText, "spanx, wrap"),
                Group {
                    content: [
                        backPlate,
                        Group {
                            content: [
                                expectedReturnInitialRect, totalBudgetRect, expectedReturnActualRect, estimatedCostsRect,
                                expectedReturnInitialValue,
                                totalBudgetValue,
                                expectedReturnActualValue,
                                estimatedCostsValue
                            ]
                        }
                        trafficLight
                    ]
                    layoutInfo: MigLayout.nodeConstraints("span, wrap")
                },
                Group {
                    content: [
                        expectedReturnInitialText,
                        totalBudgetText,
                        expectedReturnActualText,
                        estimatedCostsText
                    ]
                    layoutInfo: MigLayout.nodeConstraints("spanx")
                    translateX: 50
                    translateY: -20
                }
            ]
            translateX: xywh[0]
            translateY: xywh[1]
        }
    }
}