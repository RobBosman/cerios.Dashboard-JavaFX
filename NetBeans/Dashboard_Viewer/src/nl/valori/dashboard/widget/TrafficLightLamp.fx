/*
 * Stoplicht.fx
 *
 * Created on 3-feb-2009, 10:16:56
 */

package nl.valori.dashboard.widget;

import javafx.scene.CustomNode;
import javafx.scene.effect.InnerShadow;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

/**
 * @author Rob
 */
public class TrafficLightLamp extends CustomNode {

    public-init var color:Color;
    public var switchedOn:Boolean;

    override public function create():Node {
        return Circle {
            radius: 22
            fill: RadialGradient {
                centerX: -5,
                centerY: -5,
                radius: 12,
                proportional: false
                stops: [
                    Stop {
                        offset: 0.0
                        color: Color.WHITE},
                    Stop {
                        offset: 1.0
                        color: color
                    }
                ]
            }
            stroke: Color.BLACK
            opacity: bind if (switchedOn) 1.0 else 0.3
            effect: bind if (switchedOn) null else {
                InnerShadow{
                    offsetX:1
                    offsetY:3
                    color: Color.BLACK
                }
            }
        }
    }
}