/*
 * ProgressBar.fx
 *
 * Created on 4-feb-2009, 16:29:09
 */

package nl.valori.dashboard.widget;

import java.lang.Math;
import java.util.Date;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.CustomNode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextOrigin;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import nl.valori.dashboard.Constants;
import nl.valori.dashboard.model.GuiComponent;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.dashboard.model.Period;
import org.jfxtras.scene.layout.MigLayout;

/**
 * @author Rob
 */
public class SpeedOMeter extends CustomNode {

    public-init var guiComponent: GuiComponent;
    public var kpiHolder: KpiHolder;
    public var date: Date;

    def constants = new Constants();
    def xywh = constants.getXYWH(guiComponent.getLayout());
    def spacing = 10;
    def scale = 0.75;

    def guiElementTarget = guiComponent.getGuiElement("TARGET");
    def guiElementActual = guiComponent.getGuiElement("ACTUAL");
    var valueSetTarget = bind kpiHolder.getValueSet(guiElementTarget.getKpiVariable());
    var valueSetActual = bind kpiHolder.getValueSet(guiElementActual.getKpiVariable());
    var period = bind getPeriod(date);

    function getPeriod(date:Date):Period {
        if (kpiHolder.getFullPeriod() != null) {
            return kpiHolder.getFullPeriod().getPeriodTill(date)
        } else {
            return new Period(date, date);
        }
    }

    def titleText = Text {
        font: constants.titleFont
        fill: constants.titleFontColor
        content: guiComponent.getTitle()
    }
    def arc = Arc {
        centerX: 100 * scale
        centerY: 100 * scale
        radiusX: 100 * scale
        radiusY: 100 * scale
        startAngle: -45
        length: 270
        type: ArcType.CHORD
        fill: constants.backPlateColor
    }
    def scaleMarks = Group {
        def numMarks = 10;
        content: [
            for (i in [1..numMarks]) {
                Line {
                    startX: arc.centerX - 1
                    startY: 0
                    endX: arc.centerX - 12
                    endY: 0
                    strokeWidth: 4
                    transforms: [
                        Rotate {
                            angle: -(arc.startAngle + arc.length * (i - 1) / (numMarks - 1))
                        }
                    ]
                    translateX: arc.centerX
                    translateY: arc.centerY
                }
            }
        ]
    }
    def backPlate = Group {
        content: [ arc, scaleMarks ]
        effect: InnerShadow {
            offsetX: 2
            offsetY: 3
            color: Color.BLACK
        }
    }
    def targetHand = Polygon {
        points: [
            0, 100,
            120, 105,
            120, 95
        ]
        fill: constants.targetColor
        transforms: [
            Rotate {
                pivotX: arc.centerX
                pivotY: arc.centerY
                angle: bind getAngle(valueSetTarget.getDelta(period));
            }
            Scale {
                x: scale
                y: scale
            }
        ]
    }

    var handValue: Float = 0.0;
    var handValueOld: Float = 0.0;
    var handValueNew: Float = 0.0;
    var tl = Timeline {
        keyFrames: [
            KeyFrame {
                values: [ handValue => handValueOld ]
                time: 0s
                canSkip: false
            }
            KeyFrame {
                values: [
                    handValue => handValueNew tween Interpolator.EASEOUT
                ]
                time: 0.5s
                canSkip: false
            }
        ]
    }
    var value = bind valueSetActual.getDelta(period) on replace oldValue {
        handValueOld = oldValue;
        handValueNew = value;
        tl.playFromStart();
    }
    def actualHand = Group {
        content: [
            Polygon {
                points: [
                    0, 100,
                    120, 105,
                    120, 95
                ]
                strokeWidth: 5
                fill: constants.actualColor
                transforms: [
                    Rotate {
                        pivotX: arc.centerX
                        pivotY: arc.centerY
                        angle: bind getAngle(handValue)
                    }
                    Scale {
                        x: scale
                        y: scale
                    }
                ]
            }
            Circle {
                centerX: arc.centerX
                centerY: arc.centerY
                radius: 8
                fill: RadialGradient {
                    centerX: arc.centerX - 2,
                    centerY: arc.centerY - 2,
                    radius: 8,
                    proportional: false
                    stops: [
                        Stop {
                            offset: 0.0
                            color: Color.WHITE
                        },
                        Stop {
                            offset: 1.0
                            color: constants.actualColor
                        }
                    ]
                }
            }
        ]
        effect: DropShadow {
            offsetX: 2
            offsetY: 4
            radius: 5
            color: Color.BLACK
        }
        opacity: 1.0 // TODO bind if ((kpi.getActual().getFullPeriod() != null) and (kpi.getActual().getFullPeriod().spans(date))){ 1.0 } else { 0.5 }

    }

    def actualValueRect = Rectangle {
        width: 55
        height: 20
        fill: constants.backgroundColor
        effect: InnerShadow {
            offsetX: 2
            offsetY: 4
            radius: 5
            color: Color.BLACK
        }
    }
    def actualValueText = Text {
        textOrigin: TextOrigin.TOP
        font: constants.valueFont
        fill: constants.fontColor
        content: bind guiElementActual.format(valueSetActual.getDelta(period))
        translateX: 5
        translateY: 5
    }
    def unitText = Text {
        textOrigin: TextOrigin.TOP
        font: constants.labelFont
        fill: constants.backPlateFontColor
        content: guiElementActual.getUnit()
        translateX: actualValueRect.width + 5
        translateY: 5
    }
    def actualValue = Group {
        content: [ actualValueRect, actualValueText, unitText ]
        translateX: 50
        translateY: 95
    }

    function getAngle(val:Float):Float {
        if ((val.isNaN()) or (val < 0)) {
            return arc.startAngle;
        }
        def maximum = valueSetTarget.getDeltaMaximum(period);
        if ((maximum.isNaN()) or (maximum == 0.0)) {
            return arc.startAngle;
        } else {
            return arc.startAngle + arc.length * Math.min(val, maximum) / maximum;
        }
    }

    override public function create():Node {
        return MigLayout {
            content: [
                MigLayout.migNode(titleText, "cell 0 0"),
                Group{
                    content: [
                        backPlate, actualValue,
                        targetHand, actualHand
                    ]
                    layoutInfo: MigLayout.nodeConstraints("cell 0 1, grow")
                }
            ]
            translateX: xywh[0]
            translateY: xywh[1]
        }
    }
}