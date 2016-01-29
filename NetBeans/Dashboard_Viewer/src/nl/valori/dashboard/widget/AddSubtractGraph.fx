/*
 * ProgressBar.fx
 *
 * Created on 4-feb-2009, 16:29:09
 */

package nl.valori.dashboard.widget;

import java.lang.Math;
import java.util.Date;
import javafx.scene.CustomNode;
import javafx.scene.effect.InnerShadow;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import nl.valori.dashboard.Constants;
import nl.valori.dashboard.model.AccumulatedValue;
import nl.valori.dashboard.model.GuiComponent;
import nl.valori.dashboard.model.GuiElement;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.dashboard.model.Period;
import org.jfxtras.scene.layout.MigLayout;

/**
 * @author Rob
 */
public class AddSubtractGraph extends CustomNode {

    var guiElementAdded: GuiElement;
    var guiElementSubtracted: GuiElement;
    var guiElementDiff: GuiElement;
    var fullPeriod: Period;
    var maximum = Float.NaN;
    public-init var guiComponent: GuiComponent on replace oldValue {
        guiElementAdded = guiComponent.getGuiElement("ADDED");
        guiElementSubtracted = guiComponent.getGuiElement("SUBTRACTED");
        guiElementDiff = guiComponent.getGuiElement("DIFF");
        maximum = Float.NaN;
    };
    public var kpiHolder: KpiHolder on replace oldValue {
        fullPeriod = kpiHolder.getFullPeriod();
        maximum = Float.NaN;
    };
    public var date: Date;

    def constants = new Constants();
    def xywh = constants.getXYWH(guiComponent.getLayout());

    var valueSetAdded = bind kpiHolder.getValueSet(guiElementAdded.getKpiVariable());
    var valueSetSubtracted = bind kpiHolder.getValueSet(guiElementSubtracted.getKpiVariable());

    def titleText = Text {
        font: constants.titleFont
        fill: constants.titleFontColor
        content: guiComponent.getTitle()
    }
    def mainRect = Rectangle {
        width: xywh[2]
        height: xywh[3]
        fill: constants.backPlateColor
        effect: InnerShadow {
            offsetX: 2
            offsetY: 3
            color: Color.BLACK
        }
    }
    def addedGraph = Polygon {
        points: bind getPolygonPoints(valueSetAdded.getAccumulatedValues(fullPeriod))
        fill: constants.targetColor
    }
    def subtractedGraph = Polygon {
        points: bind getPolygonPoints(valueSetSubtracted.getAccumulatedValues(fullPeriod))
        fill: LinearGradient {
            startX: 0.0
            startY: 1.0
            endX: 0.0
            endY: 0.0
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
    }
    def diffLine = Polyline {
        points: bind getPolylinePoints(valueSetAdded.getAccumulatedDiffs(fullPeriod, valueSetSubtracted))
        strokeWidth: 0.01
        stroke: Color.YELLOW
    }
    def graphs = Group {
        content: [
            addedGraph, subtractedGraph, diffLine
        ]
        transforms: Scale {
            x: mainRect.width
            y: -mainRect.height * 0.9
        }
        translateY: mainRect.height
    }
    def timeLine = Line {
        startX: 0
        startY: 0
        endX: 0
        endY: mainRect.height
        strokeWidth: 1
        stroke: Color.BLACK
        translateX: bind getPosX(date) * mainRect.width
    }

    def addedText = Text {
        font: constants.labelFont
        fill: constants.backPlateFontColor
        content: guiElementAdded.getLabel()
    }
    def addedValueText = Text {
        font: constants.labelFont
        fill: constants.backPlateFontColor
        content: bind "{guiElementAdded.format(valueSetAdded.getValueAt(date))} {guiElementDiff.getUnit()}"
    }
    def subtractedText = Text {
        font: constants.labelFont
        fill: constants.backPlateFontColor
        content: guiElementSubtracted.getLabel()
    }
    def subtractedValueText = Text {
        font: constants.labelFont
        fill: constants.backPlateFontColor
        content: bind "{guiElementSubtracted.format(valueSetSubtracted.getValueAt(date))} {guiElementDiff.getUnit()}"
    }
    def diffText = Text {
        font: constants.labelFont
        fill: constants.backPlateFontColor
        content: guiElementDiff.getLabel()
    }
    def diffValueText = Text {
        font: constants.valueFont
        fill: constants.backPlateFontColor
        content: bind "{guiElementDiff.format(valueSetAdded.getValueAt(date)
            - valueSetSubtracted.getValueAt(date))} {guiElementDiff.getUnit()}"
    }

    function getPolygonPoints(accumulatedValues:AccumulatedValue[]):Float[] {
        if (accumulatedValues.isEmpty()) {
            return [
                0, 0,
                1, 0
            ];
        } else {
            def lastAccumulatedValue = accumulatedValues[
            accumulatedValues.size() - 1];
            return [
                for (accumulatedValue in accumulatedValues) {
                    [
                        getPosX(accumulatedValue.date),
                        getPosY(accumulatedValue.value)
                    ]
                }
                // extend graph beyond last available data point
                1, getPosY(lastAccumulatedValue.value),
                1, 0
            ]
        }
    }

    function getPolylinePoints(accumulatedValues:AccumulatedValue[]):Float[] {
        if (accumulatedValues.isEmpty()) {
            return [
                0, 0,
                1, 0
            ];
        } else {
            def lastAccumulatedValue = accumulatedValues[
            accumulatedValues.size() - 1];
            return [
                0, 0,
                getPosX(accumulatedValues.get(0).date), 0,
                for (accumulatedValue in accumulatedValues) {
                    [
                        getPosX(accumulatedValue.date),
                        getPosY(accumulatedValue.value)
                    ]
                }
                // extend graph beyond last available data point
                1, getPosY(lastAccumulatedValue.value)
            ];
        }
    }

    bound function getPosX(date:Date): Float {
        def beginMillis = fullPeriod.begin.getTime();
        def totalMillis = fullPeriod.end.getTime() - beginMillis;
        return (date.getTime() - beginMillis).floatValue() / totalMillis
    }

    function getPosY(val:Float):Float {
        if (maximum.isNaN()) {
            if ((guiComponent == null) or (kpiHolder == null)) {
                return 0;
            }
            maximum = Math.max(
                kpiHolder.getValueSet(guiElementAdded.getKpiVariable()).getMaximum(fullPeriod),
                kpiHolder.getValueSet(guiElementSubtracted.getKpiVariable()).getMaximum(fullPeriod));
        }
        return val / maximum
    }

    override public function create():Node {
        return MigLayout {
            constraints: "fill"
            content: [
                MigLayout.migNode(titleText, "cell 0 0"),
                Group {
                    layoutInfo: MigLayout.nodeConstraints("cell 0 1, grow")
                    content: [
                        mainRect, graphs, timeLine,
                        MigLayout {
                            columns: "[]10[]"
                            content: [
                                MigLayout.migNode(addedText, "cell 0 0"),
                                MigLayout.migNode(addedValueText, "cell 1 0, ax right"),
                                MigLayout.migNode(subtractedText, "cell 0 1"),
                                MigLayout.migNode(subtractedValueText, "cell 1 1, ax right"),
                                MigLayout.migNode(diffText, "cell 0 2"),
                                MigLayout.migNode(diffValueText, "cell 1 2, ax right")
                            ]
                        }
                    ]
                }
            ]
            translateX: xywh[0]
            translateY: xywh[1]
        }
    }
}