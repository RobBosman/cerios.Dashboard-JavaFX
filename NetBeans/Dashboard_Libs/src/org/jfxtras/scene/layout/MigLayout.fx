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

package org.jfxtras.scene.layout;

import java.util.HashMap;
import javafx.geometry.BoundingBox;
import javafx.scene.Group;
import javafx.scene.layout.Container;
import javafx.scene.layout.Resizable;
import javafx.scene.Node;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.Grid;
import net.miginfocom.layout.ConstraintParser;
import org.jfxtras.scene.layout.LayoutConstants.*;

import net.miginfocom.layout.CC;

/**
 * MigLayout node for JavaFX.
 * <p>
 * This node uses the popular MigLayout Java layout manager to perform layout of
 * JavaFx nodes.  If you are familiar with MigLayout out for Java, you should feel
 * right at home.  MigLayout is an advanced grid-based layout manager that also
 * support absolute positioning and docking (similar to Swing's BorderLayout)
 * where nodes are docked to the north, south, east, or west side of the layout.
 * <p>
 * <blockquote><pre>
 * MigLayout {
 *     constraints: "fill"
 *     content: Rectangle {
 *         width: 100
 *         height: 100
 *         layoutInfo: MigNodeLayoutInfo {
 *             constraints: "center"
 *         }
 *     }
 * }
 * </pre></blockquote>
 * <p>
 * This file also contains module functions that can reduce the boiler-plate code
 * required to set the layoutInfo.  The nodeConstraints function takes a MigLayout
 * constraints string and returns a MigNodeLayoutInfo object.  Using this function
 * the previous example can be rewritten as below.
 * <p>
 * <blockquote><pre>
 * MigLayout {
 *     constraints: "fill"
 *     content: Rectangle {
 *         width: 100
 *         height: 100
 *         layoutInfo: nodeConstraints( "center" )
 *     }
 * }
 * </pre></blockquote>
 * <p>
 * TODO:
 *  - Support GridLayoutInfo using LC and AC
 *
 * @profile desktop
 *
 * @author Dean Iverson
 */
public class MigLayout extends Container, DefaultLayout {
    /**
     * The layout constraints string.  All of the valid MigLayout constraints except for
     * "debug" are supported.  For example: wrap, insets, nogrid, fill, flow, align, etc.
     */
    public var constraints = "" on replace {
        constraints = ConstraintParser.prepare( constraints );
        migConstraints = ConstraintParser.parseLayoutConstraint(constraints);
        invalidateGrid();
        requestLayout();
     }

    /**
     * The column constraints.  All of the valid MigLayout column constraints are supported:
     * sizegroup, fill, grow, align, gap.
     */
    public var columns = "" on replace {
        columns = ConstraintParser.prepare( columns );
        colConstraints = ConstraintParser.parseColumnConstraints( columns );
        invalidateGrid();
        requestLayout();
    }

    /**
     * The row constraints.  All of the valid MigLayout row constraints are supported:
     * sizegroup, fill, grow, align, gap.
     */
    public var rows = "" on replace {
        rows = ConstraintParser.prepare( rows );
        rowConstraints = ConstraintParser.parseRowConstraints( rows );
        invalidateGrid();
        requestLayout();
    }

    override var content on replace {
        delete layoutHashCodes;
        invalidateGrid();
        requestLayout();
    }

    /**
     * Define the default layout info for the MigLayout node itself.  This allows
     * a MigLayout node to provide a sensible default for how it should be laid out
     * when it is placed into other containers.
     */
    override var defaultLayoutInfo = GridLayoutInfo {
        fill: BOTH
        hgrow: ALWAYS
        vgrow: ALWAYS
    }

    override var layoutBounds = bind lazy BoundingBox {width: width, height: height}

    var grid: Grid;
    var migConstraints = new LC();
    var colConstraints = new AC();
    var rowConstraints = new AC();
    var parentWrapper = FxContainerWrapper { component: this }
    var widths = [ 0, 200, Integer.MAX_VALUE];
    var heights = [ 0, 200, Integer.MAX_VALUE];
    var layoutHashCodes:Integer[];

    /**
     * Perform an initial calculation of the min/pref/max widths and heights sequences
     * once all the member variables are initialized.
     */
    postinit {
      updateSizes();
    }

    override function getMinWidth() {widths[0]}
    override function getMinHeight() {heights[0]}
    override function getPrefWidth(height) {widths[1]}
    override function getPrefHeight(width) {heights[1]}
    override function getMaxWidth() {widths[2]}
    override function getMaxHeight() {heights[2]}

    override function doLayout():Void {
        //println( "====== In Layout for {id} ======" );
        validateGrid();

        def bounds = [ 0, 0, width as Integer, height as Integer ] as nativearray of Integer;
        grid.layout( bounds, migConstraints.getAlignX(), migConstraints.getAlignY(), false, false );

        updateSizes();
        //println( "MigLayout '{id}' layoutBounds is {layoutBounds}");
        //println( "MigLayout '{id}' width is {width}, height is {height}");
        //println( "MigLayout '{id}' min/pref/max width: {getMinWidth()}/{getPrefWidth(0)}/{getMaxWidth()}, "
        //         "min/pref/max height: {getMinHeight()}/{getPrefHeight(0)}/{getMaxHeight()}");
        //println( "================================" );
    }

    /**
     * Updates the min/pref/max widths and heights.
     */
    function updateSizes():Void {
        validateGrid();
        widths = grid.getWidth();
        heights = grid.getHeight();
    }

    /**
     * If the grid is invalid it will be recreated.
     */
    function validateGrid():Void {
        if (isGridValid()) {
            if (sizeof layoutHashCodes == 0) {
                invalidateGrid();
            } else {
                for (node in content) {
                    var layoutHashCode = layoutHashCodes[indexof node];
                    if (FxComponentWrapper.getLayoutHashCode(node) != layoutHashCode) {
                        invalidateGrid();
                    }
                }
            }
        }

        if (not isGridValid()) {
            createGrid();
        }
    }

    /**
     * Marks the grid for recreation the next time a layout is performed.
     */
    function invalidateGrid():Void {
        grid = null;
    }

    /**
     * @returns true if the grid is invalid.
     */
    function isGridValid() {
        grid != null;
    }

    /**
     * Create a new grid.
     */
    function createGrid():Void {
        var constraintMap = new HashMap();
        delete layoutHashCodes;

        for (node in content) {
            insert FxComponentWrapper.getLayoutHashCode( node ) into layoutHashCodes;

            if (managed(node)) {
                var cc:CC = null;
                if (node.layoutInfo instanceof MigNodeLayoutInfo) {
                    cc = (node.layoutInfo as MigNodeLayoutInfo).cc;
                }
                constraintMap.put( FxComponentWrapper{ component: node }, cc );
            }
        }
        grid = new Grid( parentWrapper, migConstraints, rowConstraints, colConstraints, constraintMap, null );
    }
}

/**
 * This function is a short cut for creating a MigNodeLayoutInfo object with the provided constraints.
 */
public function nodeConstraints( constraints:String ):MigNodeLayoutInfo {
    MigNodeLayoutInfo {
        constraints: constraints
    }
}

/**
 * Use this function to associate the provided constraints string with the provided node's layout info.
 * This can be helpful if you already have a node that was created elsewhere and you just need to add
 * some node constraints before adding it to a MigLayout.
 */
public function migNode( node:Node, constraints:String ) {
    node.layoutInfo = nodeConstraints( constraints );
    node;
} 