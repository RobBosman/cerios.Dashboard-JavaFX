/*
 * Constants.fx
 *
 * Created on 5-feb-2009, 20:01:09
 */
package nl.valori.dashboard;

import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * @author Rob
 */
public class Constants {
    public def codeBaseURL = {
        def packageName = getClass().getPackage().getName();
        __DIR__.substring(0, __DIR__.length() - packageName.length() - 1);
    };

    public def DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public def today = new Date();
    public def expirationDate = DATE_FORMAT.parse("01-01-2999");

    public def noSweatColor = Color.rgb(87, 181, 64);
    public def dangerColor = Color.rgb(255, 250, 43);
    public def alertColor = Color.rgb(231, 61, 56);

    public def fontColor = Color.rgb(255, 255, 255);
    public def titleFontColor = Color.rgb(255, 255, 255);
    public def titleBackgroundColor = Color.rgb(200, 200, 200);
    public def backgroundColor = Color.rgb(48, 58, 134);
    public def backPlateColor = Color.rgb(216, 216, 216);
    public def backPlateFontColor = Color.rgb(0, 0, 0);
    public def targetColor = Color.rgb(65, 90, 180);
    public def actualColor = Color.rgb(225, 175, 75);
    public def actualGradientColor = Color.rgb(160, 130, 50);

    public def titleFont = Font {
        name: "Verdana"
        size: 18
    }
    public def labelFont = Font {
        name: "Verdana"
        size: 12
    }
    public def valueFont = Font {
        name: "Verdana"
        embolden: true
        size: 12
    }

    public function getAlertColor(alertLevel:Integer):Color {
        return getAlertColor(alertLevel, backPlateColor);
    }

    public function getAlertColor(alertLevel:Integer, defaultColor:Color):Color {
        return
        if (alertLevel >= 2) alertColor
        else if (alertLevel == 1) dangerColor
        else defaultColor
    }

    public function getXYWH(layout:String):Float[] {
        def format = layout.split(":");
        def values = format[1].trim().split("[^\\-0-9\\.]+");
        return [
            for (value in values) {
                Float.parseFloat(value)
            }
        ]
    }
}