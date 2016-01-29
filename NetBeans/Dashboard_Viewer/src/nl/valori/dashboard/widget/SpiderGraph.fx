/*
 * BudgetComponent.fx
 *
 * Created on 9-feb-2009, 17:26:31
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
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextOrigin;
import javafx.scene.transform.Rotate;
import nl.valori.dashboard.Constants;
import nl.valori.dashboard.model.GuiComponent;
import nl.valori.dashboard.model.GuiElement;
import nl.valori.dashboard.model.KpiHolder;
import org.jfxtras.scene.layout.Deck;
import org.jfxtras.scene.layout.MigLayout;
import javafx.scene.transform.Translate;

/**
 * @author Rob
 */

public class SpiderGraph extends CustomNode {

    public-init var guiComponent: GuiComponent;
    public var kpiHolder: KpiHolder;
    public var date: Date;

    def constants = new Constants();
    def xywh = constants.getXYWH(guiComponent.getLayout());
    def graphSize = Math.min((xywh[2] - 100) / 2, (xywh[3] - 50) / 2);

    var numSpokes = countSpokes();

    function countSpokes():Integer {
        var numSpokes = 0;
        while (guiComponent.getGuiElement("RISK_{numSpokes + 1}") != null) {
            numSpokes++;
        }
        return numSpokes
    }

    def titleText = Text {
        font: constants.titleFont
        fill: constants.titleFontColor
        content: guiComponent.getTitle()
    }
    def backPlate = Rectangle {
        width: xywh[2]
        height: xywh[3]
        fill: constants.backPlateColor
        effect: InnerShadow {
            offsetX: 2
            offsetY: 3
            color: Color.BLACK
        }
    }

    function createSpoke(guiElement:GuiElement, angle:Float):Node {
        def rad = 2 * Math.PI * angle / 360;
        return Group {
            content: [
                Line {
                    startX: 0
                    startY: 0
                    endX: graphSize
                    endY: 0
                    stroke: constants.backPlateFontColor
                    transforms: Rotate {
                        pivotX : 0.0
                        pivotY : 0.0
                        angle: angle
                    }
                }
                Group {
                    def flipAngle = if ((angle < 90) or (angle > 270)) {
                            0
                        } else {
                            180
                        };
                    content: Text {
                        font: constants.labelFont
                        fill: constants.backPlateFontColor
                        def percentage = kpiHolder.getValueSet(guiElement.getKpiVariable()).getValueAt(date);
                        content: "{guiElement.getLabel()}\n{formatPercentage(percentage)}"
                        effect: DropShadow {
                            color: constants.getAlertColor(kpiHolder.getAlertLevelAt(guiElement.getKpiVariable(), date),
                                constants.backPlateColor)
                            offsetY: 3
                            radius: 6
                        }
                        // Align text verticaly, so it will not overlap the graph.
                        textOrigin: if ((angle > 0 and angle < 90) or (angle > 180 and angle < 270)) {
                                TextOrigin.TOP
                            } else {
                                TextOrigin.BOTTOM
                            }
                        // Right-align the text at the left side of the graph by flipping it twice.
                        textAlignment: if ((angle < 90) or (angle > 270)) {
                                TextAlignment.LEFT
                            } else {
                                TextAlignment.RIGHT
                            }
                        rotate: flipAngle // rotate around center of text
                    }
                    transforms: Rotate {
                        pivotX: 0.0
                        pivotY: 0.0
                        angle: flipAngle
                    }
                    translateX: graphSize * Math.cos(rad)
                    translateY: graphSize * Math.sin(rad)
                }
            ]
        }
    }

    function createSpokes():Node[] {
        return for (i in [1..numSpokes]) {
            def angle = 360.0 * (i - 1) / numSpokes;
            [
                createSpoke(guiComponent.getGuiElement("RISK_{i}"), angle)
            ]
        }
    }

    function createWeb(kpiHolder:KpiHolder, date:Date):Node {
        def points = [
            for (i in [1..numSpokes]) {
                def angle = 2 * Math.PI * (i - 1) / numSpokes;
                def guiElement = guiComponent.getGuiElement("RISK_{i}");
                def valueSet = kpiHolder.getValueSet(guiElement.getKpiVariable());
                def value = valueSet.getValueAt(date);
                [
                    graphSize * value * Math.cos(angle),
                    graphSize * value * Math.sin(angle)
                ]
            }
        ];
        return Group {
            content: [
                Group {
                    content: [
                        // Alert: value > ThresholdMax
                        Polygon {
                            points: [
                                for (i in [1..numSpokes]) {
                                    def angle = 2 * Math.PI * (i - 1) / numSpokes;
                                    [
                                        graphSize * Math.cos(angle),
                                        graphSize * Math.sin(angle)
                                    ]
                                }
                            ]
                            fill: constants.alertColor
                            stroke: null
                        }
                        // Danger: value > ThresholdMin
                        Polygon {
                            points: [
                                for (i in [1..numSpokes]) {
                                    def angle = 2 * Math.PI * (i - 1) / numSpokes;
                                    def threshold = 0.7;
                                    [
                                        graphSize * threshold * Math.cos(angle),
                                        graphSize * threshold * Math.sin(angle)
                                    ]
                                }
                            ]
                            fill: constants.dangerColor
                            stroke: null
                        }
                        // NoSweat: value > ThresholdMin
                        Polygon {
                            points: [
                                for (i in [1..numSpokes]) {
                                    def angle = 2 * Math.PI * (i - 1) / numSpokes;
                                    def threshold = 0.5;
                                    [
                                        graphSize * threshold * Math.cos(angle),
                                        graphSize * threshold * Math.sin(angle)
                                    ]
                                }
                            ]
                            fill: constants.noSweatColor
                            stroke: null
                        }
                    ]
                }
                // Data
                Polygon {
                    points: points
                    fill: constants.targetColor
                }
                createSpokes()
            ]
            translateX: xywh[2] / 2
            translateY: xywh[3] / 2
        }
    }

    function formatPercentage(value:Double):String {
        return "{(value * 100.0).intValue()} %";
    }

    override public function create():Node {
        return MigLayout {
            content: [
                MigLayout.migNode(titleText, "wrap"),
                Group {
                    content: bind [
                        backPlate,
                        createWeb(kpiHolder, date)
                    ]
                    layoutInfo: MigLayout.nodeConstraints("grow, wrap")
                }
            ]
            translateX: xywh[0]
            translateY: xywh[1]
        }
    }
}