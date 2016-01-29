package JSONReadTest;

import javafx.io.http.HttpRequest;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import java.io.InputStream;
import java.lang.String;
import nl.valori.dashboard.model.KpiHolder;

/**
 * @author Rob
 */

def xstream = new XStream(new JettisonMappedXmlDriver());
xstream.alias("kpiHolder", KpiHolder.class);

var data:String;

var httpRequest = HttpRequest {
    location: "http://localhost:8080/dashboard/server/JSON"
    onInput: function(is: InputStream) {
        try {
            var kpiHolder = xstream.fromXML(is) as KpiHolder;
            data = kpiHolder.getName();
        } finally {
            is.close();
        }
    }
    onException: function(ex: java.lang.Exception) {
        data = "Exception: {ex.getMessage()}";
    }
};
httpRequest.start();

Stage {
    title: "Application title"
    width: 250
    height: 80
    scene: Scene {
        content: [
            Text {
                font : Font {
                    size : 16
                }
                x: 10
                y: 30
                content: bind "data = '{data}'"
            }
        ]
    }
}