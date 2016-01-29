/*
 * Copyright (c) 2008-2009, JFXtras Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of JFXtras nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jfxtras.util;

import javafx.geometry.*;
import javafx.scene.shape.*;
import javafx.util.Math;

/**
 * Collection of utilities to convert between Java AWT and JavaFX Geometry
 * objects.
 *
 * @profile desktop
 *
 * @author Stephen Chin
 * @author Steven Bixby
 */
public abstract class GeometryUtil {} // placeholder to generate javadoc

/**
 * Converts a JavaFX Geometry Point2D to a Java AWT Point2D.
 */
public function pointToJava(point:Point2D):java.awt.geom.Point2D {
    return if (point == null) null else new java.awt.Point(point.x, point.y);
}

/**
 * Converts a Java AWT Point2D to a JavaFX Geometry Point2D.
 */
public function pointToJavaFX(point:java.awt.geom.Point2D):Point2D {
    return if (point == null) null else Point2D {x: point.getX(), y: point.getY()}
}

/**
 * Converts a JavaFX Geometry Rectangle2D to a Java AWT Rectangle2D.
 */
public function rectangleToJava(rectangle:Rectangle2D):java.awt.geom.Rectangle2D {
    return if (rectangle == null) null else new java.awt.Rectangle(rectangle.minX, rectangle.minY, rectangle.width, rectangle.height);
}

/**
 * Converts a Java AWT Rectangle2D to a JavaFX Geometry Rectangle2D.
 */
public function rectangleToJavaFX(rectangle:java.awt.geom.Rectangle2D):Rectangle2D {
    return if (rectangle == null) null else Rectangle2D {minX: rectangle.getX(), minY: rectangle.getY(), width: rectangle.getWidth(), height: rectangle.getHeight()}
}

/**
 * Rotates a set of PathElement primitives around an implied origin of 0,0
 * by angle theta in *RADIANS* - creating new primitives as copied &
 * rotated versions of the passed-in primitives.
 */
public function rotatePathElementsAroundPoint(pathSegment:PathElement[], theta:Number):PathElement[] {
    var elements:PathElement[];
    var cTheta = Math.cos(theta);
    var sTheta = Math.sin(theta);
    for (shp in pathSegment) {
        if (shp instanceof MoveTo) {
            var mtShp = (shp as MoveTo);
            insert MoveTo {
                        x: roundIfClose((cTheta * mtShp.x) - (sTheta * mtShp.y));
                        y: roundIfClose((sTheta * mtShp.x) + (cTheta * mtShp.y));
            } into elements;
        } else if (shp instanceof LineTo ) {
            var lineShp = (shp as LineTo);
            insert LineTo {
                        // Rounding so float errors rounded rather than truncated.
                        x: roundIfClose((cTheta * lineShp.x) - (sTheta * lineShp.y));
                        y: roundIfClose((sTheta * lineShp.x) + (cTheta * lineShp.y));
                    } into elements;
        } else if (shp instanceof ArcTo ) {
            var arcShp = (shp as ArcTo);
            insert ArcTo {
                        // Rounding so float errors rounded rather than truncated.
                        x: roundIfClose((cTheta * arcShp.x) - (sTheta * arcShp.y));
                        y: roundIfClose((sTheta * arcShp.x) + (cTheta * arcShp.y));
                        radiusX: arcShp.radiusX;
                        radiusY: arcShp.radiusY;
                        sweepFlag: arcShp.sweepFlag;
                    } into elements;
        }
    }
    return elements;
}

/**
 * Rotates a set of PathElement primitives around an implied origin of 0,0
 * by angle theta in *RADIANS* - changing the primitives IN PLACE.
 */
public function rotatePathElementsAroundPointInPlace(pathSegment:PathElement[], theta:Number) {
    var cTheta = Math.cos(theta);
    var sTheta = Math.sin(theta);
    var x;
    var y;
    for (shp in pathSegment) {
        if (shp instanceof MoveTo) {
            var mtShp = (shp as MoveTo);
            x = roundIfClose((cTheta * mtShp.x) - (sTheta * mtShp.y));
            y = roundIfClose((sTheta * mtShp.x) + (cTheta * mtShp.y));
            mtShp.x = x;
            mtShp.y = y;
        } else if (shp instanceof LineTo ) {
            var lineShp = (shp as LineTo);
            x = roundIfClose((cTheta * lineShp.x) - (sTheta * lineShp.y));
            y = roundIfClose((sTheta * lineShp.x) + (cTheta * lineShp.y));
            lineShp.x = x;
            lineShp.y = y;
        } else if (shp instanceof ArcTo ) {
            var arcShp = (shp as ArcTo);
            x = roundIfClose((cTheta * arcShp.x) - (sTheta * arcShp.y));
            y = roundIfClose((sTheta * arcShp.x) + (cTheta * arcShp.y));
            arcShp.x = x;
            arcShp.y = y;
        }
    }
}


/**
 * Local function to round "sufficiently close" Number values to whole numbers,
 * to prevent unnecessary aliasing in drawing shapes.
 */
function roundIfClose(val:Number):Number {
    var nVal = Math.round(val);
    return if (Math.abs(val-nVal) < 0.001) then nVal else val;
}

/**
 * Translates a set of PathElements by passed-in values of x & y, returning
 * translated copies of the original PathElements, thus preserving them.
 */
public function translatePathElements(pathSegment:PathElement[], tx:Number, ty:Number) {
    var elements:PathElement[];
    for (shp in pathSegment) {
        if (shp instanceof MoveTo) {
            var mtShp = (shp as MoveTo);
            insert MoveTo {
                x: mtShp.x + tx;
                y: mtShp.y + ty;
            } into elements;
        } else if (shp instanceof LineTo ) {
            var lineShp = (shp as LineTo);
            insert LineTo {
                x: lineShp.x + tx;
                y: lineShp.y + ty;
            } into elements;
        } else if (shp instanceof ArcTo ) {
            var arcShp = (shp as ArcTo);
            insert ArcTo {
                x: arcShp.x + tx;
                y: arcShp.y + ty;
                radiusX: arcShp.radiusX;
                radiusY: arcShp.radiusY;
                sweepFlag: arcShp.sweepFlag;
            } into elements;
        }
    }
    return elements;
}


/**
 * Translates a set of PathElements by passed-in values of x & y, operating
 * on the existing PathElements in-place.
 */
public function translatePathElementsInPlace(pathSegment:PathElement[], tx:Number, ty:Number) {
    for (shp in pathSegment) {
        if (shp instanceof MoveTo) {
            var mtShp = (shp as MoveTo);
            mtShp.x += tx;
            mtShp.y += ty;
        } else if (shp instanceof LineTo ) {
            var lineShp = (shp as LineTo);
            lineShp.x += tx;
            lineShp.y += ty;
        } else if (shp instanceof ArcTo ) {
            var arcShp = (shp as ArcTo);
            arcShp.x += tx;
            arcShp.y += ty;
        }
    }
}

