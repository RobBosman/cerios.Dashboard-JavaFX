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

import javafx.geometry.BoundingBox;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.*;
import javafx.util.*;
import org.jfxtras.util.*;
import org.jfxtras.scene.layout.LayoutConstants.*;

/**
 * This container uses a flexible Grid layout algorithm to position and size
 * its children.  Creating a new Grid is as easy as constructing an instance,
 * adding in a list of Rows, and populating whatever Nodes you want to display.
 * <p>
 * Reasonable defaults are provided for most of the common JavaFX components,
 * but for more control you can wrap Nodes in {@link Cell} objects and set
 * various constraints such as grow, span, and preferred dimensions.
 * <p>
 * Here is a sample Grid to create a form:
 * <blockquote><pre>
 * Grid {
 *     rows: [
 *         row([Text {content: "Username:"}, TextBox {}]),
 *         row([Text {content: "Password:"}, TextBox {}]),
 *         row([Cell {content: TextBox {}, hspan: 2}])
 *     ]
 * }
 * </pre></blockquote>
 * <p>
 * For more information about using Grids, please refer to the test examples in
 * the JavaFX source code, or see my blog:
 * <a href="http://steveonjava.wordpress.com/">http://steveonjava.wordpress.com/</a>
 *
 * @profile desktop
 *
 * @author Stephen Chin
 * @author Keith Combs
 */
// todo - make use of other Container methods (see comment below)
public class Grid extends JFXContainer, DefaultLayout {
    /**
     * A decimal value between 0 and 1 that designates the percentage of space
     * the row with the matching index will consume of any available space
     * in the container.
     * <p>
     * If the total amount of grow values exceeds 100% (1.0), then the whole
     * area will be filled proportionally by percentage.
     * <p>
     * If the total amount of grow values is less than 100%, then the remaining
     * amount will be empty.
     */
    public var growRows:Number[] on replace {
        requestLayout();
    }

    /**
     * A decimal value between 0 and 1 that designates the percentage of space
     * the column with the matching index will consume of any available space
     * in the container.
     * <p>
     * If the total amount of grow values exceeds 100% (1.0), then the whole
     * area will be filled proportionally by percentage.
     * <p>
     * If the total amount of grow values is less than 100%, then the remaining
     * amount will be empty.
     */
    public var growColumns:Number[] on replace {
        requestLayout();
    }

    /**
     * The size of the border that will be placed around this Grid.  The border
     * is both empty and transparent.  Set this to 0 to remove the border.
     */
    public var border:Number = 3 on replace {
        requestLayout();
    }

    /**
     * The width of the horizontal gaps between cells.  This can be set to zero
     * to have cells flush against each other.
     */
    public var hgap:Number = 3 on replace {
        requestLayout();
    }

    /**
     * The width of the vertical gaps between cells.  This can be set to zero
     * to have cells flush against each other.
     */
    public var vgap:Number = 3 on replace {
        requestLayout();
    }

    /**
     * The horizontal position of each node within the Grid cell.
     * <p>
     * This may be overridden for individual nodes by setting the hpos variable
     * on the node's layoutInfo variable.
     *
     * @defaultvalue HPos.LEFT
     */
    public var nodeHPos:HPos = HPos.LEFT;

    /**
     * The vertical position of each node within the Grid cell.
     * <p>
     * This may be overridden for individual nodes by setting the vpos variable
     * on the node's layoutInfo variable.
     *
     * @defaultvalue VPos.CENTER
     */
    public var nodeVPos:VPos = VPos.CENTER;

    var rowMaximum:Number[];

    var columnMaximum:Number[];

    var rowMinimum:Number[];

    var columnMinimum:Number[];

    var rowPreferred:Number[];

    var columnPreferred:Number[];

    var rowActual:Number[];

    var columnActual:Number[];

    override function getMaxHeight() {recalculateSizes(); sum(rowMaximum) + (rowMaximum.size() - 1) * vgap + border * 2}

    override function getMaxWidth() {recalculateSizes(); sum(columnMaximum) + (columnMaximum.size() - 1) * hgap + border * 2}

    override function getMinHeight() {recalculateSizes(); sum(rowMinimum) + (rowMinimum.size() - 1) * vgap + border * 2}

    override function getMinWidth() {recalculateSizes(); sum(columnMinimum) + (columnMinimum.size() - 1) * hgap + border * 2}

    override function getPrefHeight(width) {recalculateSizes(); sum(rowPreferred) + (rowPreferred.size() - 1) * vgap + border * 2}

    override function getPrefWidth(height) {recalculateSizes(); sum(columnPreferred) + (columnPreferred.size() - 1) * hgap + border * 2}

    function sum(numbers:Number[]):Number {
        var total:Number = 0;
        for (number in numbers) {
            total += number;
        }
        return total;
    }

    function createSequence(length:Integer, value:Object):Object[] {
        return for (i in [1..length]) value;
    }

    function createNumberSequence(length:Integer, value:Number):Number[] {
        return for (i in [1..length]) value;
    }

    public var rows:Row[] on replace {
        var newContent = for (row in rows) {
            row.cells
        }
        // use a stable update so toFront and toBack work correctly
        content = SequenceUtil.stableUpdate(content, newContent) as Node[];
    }

    var hgrow:Grow[] = bind for (r in rows) {
        for (c in getManaged(r.cells)) {
            getNodeHGrow(c)
        }
    }

    var vgrow:Grow[] = bind for (r in rows) {
        for (c in getManaged(r.cells)) {
            getNodeVGrow(c)
        }
    }

    override var width on replace {
        layoutDirty = true;
    }

    override var height on replace {
        layoutDirty = true;
    }

    // todo - add in hpos and vpos
    override var defaultLayoutInfo = bind lazy GridLayoutInfo {
        fill: BOTH
        hgrow: SequenceUtil.fold(NEVER, hgrow, function(a, b) {Grow.max(a as Grow, b as Grow)}) as Grow
        vgrow: SequenceUtil.fold(NEVER, vgrow, function(a, b) {Grow.max(a as Grow, b as Grow)}) as Grow
    }

    override var layoutBounds = bind lazy BoundingBox {width: width, height: height}

    var layoutDirty = true;

    function recalculateSizes() {
        if (not layoutDirty or rows.isEmpty()) {
            return;
        }
        var numRows = sizeof rows;
        var rowSizes = for (row in rows)
            SequenceUtil.sum(for (cell in getManaged(row.cells)) {
                getNodeHSpan(cell);
            });
        var numColumns = Sequences.max(rowSizes) as Integer;
        rowMaximum = createNumberSequence(numRows, 0);
        columnMaximum = createNumberSequence(numColumns, 0);
        rowMinimum = createNumberSequence(numRows, 0);
        columnMinimum = createNumberSequence(numColumns, 0);
        rowPreferred = createNumberSequence(numRows, 0);
        columnPreferred = createNumberSequence(numColumns, 0);
        rowActual = createNumberSequence(numRows, 0);
        columnActual = createNumberSequence(numColumns, 0);
        var rowGrow = createSequence(numRows, NEVER) as Grow[];
        var columnGrow = createSequence(numColumns, NEVER) as Grow[];
        for (row in rows) {
            var columnIndex = 0;
            for (node in getManaged(row.cells)) {
                var maximumHeight = getNodeMaxHeight(node);
                var maximumWidth = getNodeMaxWidth(node);
                var minimumHeight = getNodeMinHeight(node);
                var minimumWidth = getNodeMinWidth(node);
                var preferredHeight = getNodePrefHeight(node);
                var preferredWidth = getNodePrefWidth(node);
                var hspan = getNodeHSpan(node);
                if (hspan > 1) {
                    minimumWidth = 0;
                    preferredWidth = 0;
                }
                rowMaximum[indexof row] = Math.max(rowMaximum[indexof row], maximumHeight);
                columnMaximum[columnIndex] = Math.max(columnMaximum[columnIndex], maximumWidth);
                rowMinimum[indexof row] = Math.max(rowMinimum[indexof row], minimumHeight);
                columnMinimum[columnIndex] = Math.max(columnMinimum[columnIndex], minimumWidth);
                rowPreferred[indexof row] = Math.max(rowPreferred[indexof row], preferredHeight);
                columnPreferred[columnIndex] = Math.max(columnPreferred[columnIndex], preferredWidth);
                rowActual[indexof row] = Math.max(rowActual[indexof row], Math.max(preferredHeight, minimumHeight));
                columnActual[columnIndex] = Math.max(columnActual[columnIndex], Math.max(preferredWidth, minimumWidth));
                rowGrow[indexof row] = Grow.max(rowGrow[indexof row], getNodeVGrow(node));
                if (hspan == 1) {
                    columnGrow[columnIndex] = Grow.max(columnGrow[columnIndex], getNodeHGrow(node));
                }
                columnIndex += hspan;
            }
        }
        // todo - process rows with column or row span to make sure they get min and preferred sizes
        doResizeRows();
        doResizeColumns();
        doCellRowGrow(rowGrow);
        doCellColumnGrow(columnGrow);
        layoutDirty = false;
    }

    function doResizeRows() {
        var rowTotal = vgap * (rowActual.size() - 1) + border * 2;
        for (actual in rowActual) {
            rowTotal += actual;
        }
        var rowAvailable = height - rowTotal;
        if (rowAvailable > 0) {
            // handle growing
            var rowGrowTotal = 0.0;
            for (grow in growRows) {
                rowGrowTotal += grow;
            }
            if (rowGrowTotal < 1) {
                rowAvailable *= rowGrowTotal;
            }
            if (rowGrowTotal > 0) {
                for (actual in rowActual where indexof actual < growRows.size()) {
                    rowActual[indexof actual] += rowAvailable * growRows[indexof actual] / rowGrowTotal;
                }
            }
        } else if (rowAvailable < 0) {
            // handle shrinking
            var minimumHeight = vgap * (rowMinimum.size() - 1) + border * 2;
            for (minimum in rowMinimum) {
                minimumHeight += minimum;
            }
            if (rowTotal > minimumHeight) {
                var newHeight = Math.max(minimumHeight, height);
                var shrinkPercent = (rowTotal - newHeight) / (rowTotal - minimumHeight);
                for (minimum in rowMinimum) {
                    rowActual[indexof minimum] -= (rowActual[indexof minimum] - minimum) * shrinkPercent
                }
            }
        }
    }

    function doResizeColumns() {
        var columnTotal = hgap * (columnActual.size() - 1) + border * 2;
        for (actual in columnActual) {
            columnTotal += actual;
        }
        var columnAvailable = width - columnTotal;
        if (columnAvailable > 0) {
            // handle growing
            var columnGrowTotal = 0.0;
            for (grow in growColumns) {
                columnGrowTotal += grow;
            }
            if (columnGrowTotal < 1) {
                columnAvailable *= columnGrowTotal;
            }
            if (columnGrowTotal > 0) {
                for (actual in columnActual where indexof actual < growColumns.size()) {
                    columnActual[indexof actual] += columnAvailable * growColumns[indexof actual] / columnGrowTotal;
                }
            }
        } else if (columnAvailable < 0) {
            // handle shrinking
            var minimumWidth = hgap * (columnMinimum.size() - 1) + border * 2;
            for (minimum in columnMinimum) {
                minimumWidth += minimum;
            }
            if (columnTotal > minimumWidth) {
                var newWidth = Math.max(minimumWidth, width);
                var shrinkPercent = (columnTotal - newWidth) / (columnTotal - minimumWidth);
                for (minimum in columnMinimum) {
                    columnActual[indexof minimum] -= (columnActual[indexof minimum] - minimum) * shrinkPercent
                }
            }
        }
    }

    function doCellRowGrow(rowGrow:Grow[]) {
        var rowTotal = vgap * (rowActual.size() - 1);
        for (actual in rowActual) {
            rowTotal += actual;
        }
        var rowAvailable = height - border * 2 - rowTotal;
        if (rowAvailable > 0) {
            var numAlways = rowGrow[g|g == ALWAYS].size();
            if (numAlways > 0) {
                for (grow in rowGrow where grow == ALWAYS) {
                    rowActual[indexof grow] += rowAvailable / numAlways;
                }
            } else {
                var numSometimes = rowGrow[g|g == SOMETIMES].size();
                for (grow in rowGrow where grow == SOMETIMES) {
                    rowActual[indexof grow] += rowAvailable / numSometimes;
                }
            }
        }
    }

    function doCellColumnGrow(columnGrow:Grow[]) {
        var columnTotal = hgap * (columnActual.size() - 1);
        for (actual in columnActual) {
            columnTotal += actual;
        }
        var columnAvailable = width - border * 2 - columnTotal;
        if (columnAvailable > 0) {
            var numAlways = columnGrow[g|g == ALWAYS].size();
            if (numAlways > 0) {
                for (grow in columnGrow where grow == ALWAYS) {
                    columnActual[indexof grow] += columnAvailable / numAlways;
                }
            } else {
                var numSometimes = columnGrow[g|g == SOMETIMES].size();
                for (grow in columnGrow where grow == SOMETIMES) {
                    columnActual[indexof grow] += columnAvailable / numSometimes;
                }
            }
        }
    }

    override function doLayout():Void {
        recalculateSizes();
        var x:Number = border;
        var y:Number = border;
        for (row in rows) {
            var columnIndex = 0;
            for (node in getManaged(row.cells)) {
                var areaX = x;
                var areaY = y;
                var areaW = columnActual[columnIndex++];
                for (i in [2..getNodeHSpan(node)]) {
                    areaW += columnActual[columnIndex++] + hgap;
                }
                var areaH = rowActual[indexof row];
                layoutNode(node, areaX, areaY, areaW, areaH, nodeHPos, nodeVPos);
                x += areaW + hgap;
            }
            x = border;
            y += rowActual[indexof row] + vgap;
        }
        layoutDirty = true;
    }

    override function toString() {
        "Grid \{border={border}, hgap={hgap}, vgap={vgap}, nodeHPos={nodeHPos}, nodeVPos={nodeVPos}, rows={rows.toString()}\}"
    }
}
