/*
* Main.fx
 *
 * Created on 23-mrt-2009, 15:53:08
 */

package miglayout;

import javafx.scene.effect.DropShadow;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jfxtras.scene.layout.MigLayout;

/**
 * @author bosmar
 */

Stage {
    title: "Mig Alignment Test"
    scene: Scene {
        width: 700
        height: 300
        fill: LinearGradient {
            stops: [
                Stop {
                    offset: 0.0,
                    color: Color.SILVER.ofTheWay( Color.WHITE, 0.35 ) as Color
                },
                Stop {
                    offset: 1.0,
                    color: Color.SILVER.ofTheWay( Color.BLACK, 0.35 ) as Color
                }
            ]
        }

        content: MigLayout {
            constraints: "fill, wrap, insets 0"
            rows: "[]50[]50[]"
            columns: "[]50[]50[]"
            content: [
                MigLayout.migNode( createText( "Left, Top" ),   "alignx left, aligny top" ),
                MigLayout.migNode( createText( "Center, Top" ),  "alignx center, aligny top" ),
                MigLayout.migNode( createText( "Right, Top" ),   "alignx right, aligny top" ),

                MigLayout.migNode( createText( "Left, Center" ), "alignx left, aligny center" ),
                MigLayout.migNode( createText( "Center, Center" ), "alignx center, aligny center" ),
                MigLayout.migNode( createText( "Right, Center" ), "alignx right, aligny center" ),

                // Note: you can just use ax and ay for short
                MigLayout.migNode( createText( "Left, Bottom" ), "ax left, ay bottom" ),
                MigLayout.migNode( createText( "Center, Bottom" ), "ax center, ay bottom" ),
                MigLayout.migNode( createText( "Right, Bottom" ), "ax right, ay bottom" ),
            ]
        }
    }
}

function createText( content:String ):Node {
    return Text {
        content: content
        font: Font {
            size: 24
        }
        cache: true
        effect: DropShadow {
            offsetX: 5
            offsetY: 5
            radius: 8
        }
    }
}