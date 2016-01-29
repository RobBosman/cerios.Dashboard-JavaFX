/*
 * Stoplicht.fx
 *
 * Created on 3-feb-2009, 10:16:56
 */

package nl.valori.dashboard.widget;

import java.util.Date;
import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import nl.valori.dashboard.model.GuiComponent;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.dashboard.model.Period;
import nl.valori.dashboard.widget.TrafficLightLamp;

/**
 * @author Rob
 */
public class TrafficLight extends CustomNode {

    public-init var guiComponent: GuiComponent;
    public var kpiHolder: KpiHolder;
    public var date: Date;

    public var level: Integer;

    def achtergrond = Rectangle {
        width: 80
        height: 200
        arcWidth: 80
        arcHeight: 80
        stroke: Color.LIGHTGREY
        strokeWidth: 3
        fill: LinearGradient {
            startX: 0.0,
            startY: 0.0,
            endX: 0.0,
            endY: 1.0,
            proportional: true
            stops: [
                Stop {
                    offset: 0.0
                    color: Color.web("#606060")
                },
                Stop {
                    offset: 1.0
                    color: Color.web("#202020")
                }
            ]
        }
    }
    def rood = TrafficLightLamp {
        color: Color.web("#FF1010")
        switchedOn: bind level > 1
        translateX: achtergrond.width / 2
        translateY: 50
    }
    def oranje = TrafficLightLamp {
        color: Color.web("#FFAA10")
        switchedOn: bind level == 1
        translateX: achtergrond.width / 2
        translateY: 100
    }
    def groen = TrafficLightLamp {
        color: Color.web("#10FF10")
        switchedOn: bind level < 1
        translateX: achtergrond.width / 2
        translateY: 150
    }

    override public function create():Node {
        return Group {
            content: [
                achtergrond,
                rood,
                oranje,
                groen
            ]
        }
    }
}