/*
* Main.fx
 *
 * Created on 2-feb-2009, 16:36:46
 */

package nl.valori.dashboard;

import java.util.Date;
import javafx.ext.swing.SwingSlider;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.ShapeSubtract;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextOrigin;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import nl.valori.dashboard.Constants;
import nl.valori.dashboard.model.Period;
import nl.valori.dashboard.widget.KPIView;
import nl.valori.dashboard.widget.Navigation;

import nl.valori.space.Space;

import nl.valori.dashboard.remote.DashboardRemoteSpace;

/**
 * @author Rob
 */
def constants = new Constants();

def dashboardRemoteSpace = new DashboardRemoteSpace();
Space.getInstance().setFiller(dashboardRemoteSpace);

def kpiHolderRoot = dashboardRemoteSpace.getRootKpiHolders().iterator().next();
def width = kpiHolderRoot.getGuiLayout().getFloatProperty("width");
def height = kpiHolderRoot.getGuiLayout().getFloatProperty("height");
def navWidth = kpiHolderRoot.getGuiLayout().getFloatProperty("navWidth");

def spacing = 5;
def titleHeight = 40;
def titleInsetX = 200;
def timeLineHeight = 60;
def navHeight = height - titleHeight - timeLineHeight - 20;
def viewWidth = width - navWidth - 2 * spacing;
def viewHeight = height - titleHeight - timeLineHeight;
def sliderX = navWidth + 110;

var slider: SwingSlider;
var navigation = Navigation {
    kpiHolderRoot: kpiHolderRoot
    width: navWidth - 2 * spacing
    height: navHeight
    translateX: spacing
    translateY: titleHeight + spacing
}

def fullPeriod = kpiHolderRoot.getFullPeriod();
def totalDays = fullPeriod.getNumDays();
var selectedPeriod = bind navigation.selectedKpiHolder.getFullPeriod();
var selectedDate = bind getSelectedDate(slider.value, selectedPeriod);

function getSelectedDate(sliderValue:Integer, period:Period):Date {
    if ((period == null) or (period.begin == null) or (totalDays == 0)) {
        return constants.today;
    } else {
        def offsetMillis = Period.MILLIS_PER_DAY * sliderValue.floatValue() * period.getNumDays() / totalDays;
        return new Date(period.begin.getTime() + offsetMillis);
    }
}
function getInitialSliderValue():Integer {
    if (fullPeriod.spans(constants.today)) {
        return new Period(fullPeriod.begin, constants.today).getNumDays();
    } else {
        return 0;
    }
}
function getExpirationMark():Text {
    if (constants.today.getTime() < constants.expirationDate.getTime()) {
        return null
    } else {
        return Text {
            font: Font {
                name: "Verdana"
                size: 64
            }
            content: "This DEMO version has expired!"
            fill: Color.RED
            transforms: Rotate { angle: -30 }
            translateX: 80
            translateY: titleHeight + spacing + navHeight
        }
    }
}

slider = SwingSlider {
    minimum: 0
    maximum: totalDays
    value: getInitialSliderValue()
    vertical: false
    width: viewWidth - 230 - 2 * spacing
    translateX: sliderX
    translateY: 35
}

def valoriLogo = Group {
    content: [
        ImageView {
            image: Image {
                url: "{constants.codeBaseURL}images/Valori_logo_background.jpg"
            }
            fitWidth: width
            fitHeight: timeLineHeight
        }
        ImageView {
            image: Image {
                url: "{constants.codeBaseURL}images/Valori_logo.jpg"
            }
            fitHeight: timeLineHeight
            preserveRatio: true
        }
    ]
    translateY: titleHeight + spacing + navHeight + spacing
}
def customLogo = Group {
    content: [
        // TODO - select correct custom logo
        ImageView {
            image: Image {
                url: "{constants.codeBaseURL}images/Logo_background.jpg"
            }
            fitWidth: width
            fitHeight: titleHeight
        }
        ImageView {
            image: Image {
                url: "{constants.codeBaseURL}images/Logo.jpg"
            }
            fitHeight: titleHeight
            preserveRatio: true
        }
    ]
}

def titleInsetRect = Rectangle {
    width: width - navWidth
    height: titleHeight
    fill: constants.titleBackgroundColor
    translateX: navWidth
}
def title = Text {
    textOrigin: TextOrigin.TOP
    font: Font {
        name: "Verdana"
        size: 28
    }
    fill: constants.backgroundColor
    content: bind navigation.selectedKpiHolder.getName()
    effect: DropShadow {
        offsetX: 2
        offsetY: 2
        radius: 5
        color: Color.BLACK
    }
    translateX: navWidth + spacing
    translateY: spacing
}

def kpiView = KPIView {
    kpiHolder: bind navigation.selectedKpiHolder
    date: bind selectedDate
    translateX: navWidth
    translateY: titleHeight + spacing
}

def timeLine = Group {
    def timeLineLabelText = Text {
        font: Font {
            name: "Verdana"
            size: 18
            embolden: true
        }
        fill: constants.titleFontColor
        content: "Time line"
        translateX: spacing + navWidth + spacing
        translateY: 35
    }
    def timeStuff = Group {
        content: [
            // mainTimeLine
            Line {
                startX: getPosX(fullPeriod.begin)
                startY: 0
                endX: getPosX(fullPeriod.end)
                endY: 0
                strokeWidth: 2
                stroke: constants.titleFontColor
                translateY: 20
            },
            if (fullPeriod.spans(constants.today)) {
                // todayLine
                Line {
                    startX: 0
                    startY: 0
                    endX: 0
                    endY: 20
                    stroke: constants.titleFontColor
                    translateX: getPosX(constants.today)
                    translateY: 10
                }
            } else [],
            // scaleLine1
            Polyline {
                var pos1 = bind getPosX(selectedPeriod.begin);
                var pos2 = getPosX(fullPeriod.begin);
                points: bind [
                    pos1, 0,
                    pos1, 10,
                    pos2, 25
                ]
                strokeWidth: 2
                stroke: constants.titleBackgroundColor
                translateY: 15
            }
            // scaleLine2
            Polyline {
                var pos1 = bind getPosX(selectedPeriod.end);
                var pos2 = getPosX(fullPeriod.end);
                points: bind [
                    pos1, 0,
                    pos1, 10,
                    pos2, 25
                ]
                strokeWidth: 2
                stroke: constants.titleBackgroundColor
                translateY: 15
            }
            // selectedDateMark
            Polygon {
                points: [
                    0, 0,
                    -6, 12,
                    6, 12
                ]
                fill: constants.backgroundColor
                translateX: bind getPosX(selectedDate)
                translateY: 20
            }
            // dateGroup
            Group {
                content: [
                    Rectangle {
                        width: 120
                        height: 30
                        fill: constants.backgroundColor
                        effect: InnerShadow{
                            offsetX: 2
                            offsetY: 2
                            color: Color.BLACK
                        }
                    }
                    Text {
                        textOrigin: TextOrigin.TOP
                        font: Font {
                            name: "Verdana"
                            size: 18
                            embolden: true
                        }
                        fill: constants.titleFontColor
                        content: bind constants.DATE_FORMAT.format(selectedDate)
                        translateX: 5
                        translateY: 5
                    }
                ]
                translateX: viewWidth - 230
                translateY: 15
            }
        ]
        translateX: sliderX
    }
    content: [
        timeLineLabelText, timeStuff, slider
    ]
    translateY: titleHeight + spacing + navHeight + spacing
}
function getPosX(date:Date):Float {
    def sliderGripWidth = 15;
    var posX = 0.0;
    if (totalDays > 0) {
        def offsetDays = Period.getNumDays(fullPeriod.begin, date);
        posX = (slider.width - sliderGripWidth) * offsetDays / totalDays;
    }
    return sliderGripWidth / 2 + posX;
}

def valoriArcs = Group {
    content: [
        Group {
            content: [
                Arc {
                    fill: Color.WHITE
                    radiusX: 15
                    radiusY: 15
                    startAngle: 0
                    length: 90
                    type: ArcType.ROUND
                }
                ShapeSubtract {
                    fill: Color.WHITE
                    a: Arc {
                        radiusX: 60
                        radiusY: 60
                        startAngle: 0
                        length: 90
                        type: ArcType.ROUND
                    }
                    b: Arc {
                        radiusX: 50
                        radiusY: 50
                        startAngle: 0
                        length: 90
                        type: ArcType.ROUND
                    }
                }
                ShapeSubtract {
                    fill: Color.WHITE
                    a: Arc {
                        radiusX: 75
                        radiusY: 75
                        startAngle: 0
                        length: 90
                        type: ArcType.ROUND
                    }
                    b: Arc {
                        radiusX: 65
                        radiusY: 65
                        startAngle: 0
                        length: 90
                        type: ArcType.ROUND
                    }
                }
            ]
            opacity: 0.1
            transforms: Scale {
                x: width / 100.0
                y: width / 100.0
            }
        }
    ]
    translateY: titleHeight + spacing + navHeight + spacing + timeLineHeight
}

Stage {
    title: "Valori Dashboard"
    width: width + 8
    height: height + 17
    scene: Scene {
        fill: constants.backgroundColor
        content: [
            valoriArcs,
            customLogo, titleInsetRect, title,
            navigation, kpiView,
            valoriLogo, timeLine,
            getExpirationMark()
        ]
    }
}