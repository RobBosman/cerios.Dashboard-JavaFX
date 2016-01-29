/*
 * ProgressBar.fx
 *
 * Created on 4-feb-2009, 16:29:09
 */
package nl.valori.dashboard.widget;

import java.util.Date;
import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.Node;
import nl.valori.dashboard.Constants;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.dashboard.widget.AddSubtractGraph;
import nl.valori.dashboard.widget.BusinessCaseGraph;
import nl.valori.dashboard.widget.ProgressBar;
import nl.valori.dashboard.widget.RisksView;
import nl.valori.dashboard.widget.SpeedOMeter;
import nl.valori.dashboard.widget.TrafficLight;
import nl.valori.dashboard.widget.UnsupportedView;

/**
 * @author Rob
 */
public class KPIView extends CustomNode {

    public var kpiHolder: KpiHolder;
    public var date: Date;
    public-read var width: Float = 0;

    def constants = new Constants();

    function getGuiComponents():Group {
        for (guiComponent in kpiHolder.getGuiLayout().getGuiComponents()) {
            def xywh = constants.getXYWH(guiComponent.getLayout());
            def w = xywh[0] + xywh[2];
            if (width < w) {
                width = w;
            }
        };
        return Group {
            content: [
                for (guiComponent in kpiHolder.getGuiLayout().getGuiComponents()) {
                    if (guiComponent.getType().equals("BUSINESS_CASE_GRAPH")) {
                        BusinessCaseGraph {
                            guiComponent: guiComponent
                            kpiHolder: bind kpiHolder
                            date: bind date
                        }
                    } else if (guiComponent.getType().equals("ADD_SUBTRACT_GRAPH")) {
                        AddSubtractGraph {
                            guiComponent: guiComponent
                            kpiHolder: bind kpiHolder
                            date: bind date
                        }
                    } else if (guiComponent.getType().equals("PROGRESS_BAR")) {
                        ProgressBar {
                            guiComponent: guiComponent
                            kpiHolder: bind kpiHolder
                            date: bind date
                        }
                    } else if (guiComponent.getType().equals("RISKS_VIEW")) {
                        RisksView {
                            guiComponent: guiComponent
                            kpiHolder: bind kpiHolder
                            date: bind date
                        }
                    } else if (guiComponent.getType().equals("SPEED_O_METER")) {
                        SpeedOMeter {
                            guiComponent: guiComponent
                            kpiHolder: bind kpiHolder
                            date: bind date
                        }
                    } else if (guiComponent.getType().equals("SPIDER_GRAPH")) {
                        SpiderGraph {
                            guiComponent: guiComponent
                            kpiHolder: bind kpiHolder
                            date: bind date
                        }
                    } else if (guiComponent.getType().equals("TRAFFIC_LIGHT")) {
                        TrafficLight {
                            guiComponent: guiComponent
                            kpiHolder: bind kpiHolder
                            date: bind date
                        }
                    } else {
                        UnsupportedView {
                            guiComponent: guiComponent
                            kpiHolder: bind kpiHolder
                            date: bind date
                        }
                    }
                }
            ]
        }
    }

    override public function create():Node {
        return getGuiComponents()
    }
}