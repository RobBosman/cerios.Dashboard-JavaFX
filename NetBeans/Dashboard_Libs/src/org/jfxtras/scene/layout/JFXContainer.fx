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

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Container;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.layout.Resizable;
import javafx.util.Math;
import org.jfxtras.scene.layout.GridLayoutInfo.GRID_DEFAULT;
import org.jfxtras.scene.layout.LayoutConstants.*;

/**
 * JFXtras extension of Container that provides versions of all the static
 * helper functions that respect ExtendedLayoutInfo.grow and DefaultLayout.
 *
 * @author Stephen Chin
 */
public class JFXContainer extends Container {}

/**
 * Utility function which returns the value of the node's layoutInfo managed
 * variable (if set), otherwise returns true.
 */
public function managed(node:Node):Boolean {
    if (node.layoutInfo != null and isInitialized(node.layoutInfo.managed)) {
        return node.layoutInfo.managed;
    }
    def dli = DefaultLayout.getClassDefault(node);
    if (dli != null and isInitialized(dli.managed)) {
        return dli.managed;
    }
    return true;
}

/**
 * Utility function which returns the sequence of nodes within the content
 * whose layout should be managed by its container.
 */
public function getManaged(content:Node[]):Node[] {
    return content[n|managed(n)];
}

/**
 * Utility function which returns the horizontal layout position of the Node
 * which defines how the node should be horizontally aligned within its layout
 * space if the width of the layout space is greater than the layout bounds
 * width of the node.
 * <p>
 * This version has no user default and will rely on class defaults instead.
 */
public function getNodeHPos(node:Node):HPos {
    getNodeHPos(node, null);
}

/**
 * Utility function which returns the horizontal layout position of the Node
 * which defines how the node should be horizontally aligned within its layout
 * space if the width of the layout space is greater than the layout bounds
 * width of the node.
 */
public function getNodeHPos(node:Node, fallback:HPos):HPos {
    if (node.layoutInfo instanceof LayoutInfo) {
        def li = node.layoutInfo as LayoutInfo;
        if (isInitialized(li.hpos)) {
            return li.hpos;
        }
    }
    def dli = DefaultLayout.getClassDefault(node);
    if (dli instanceof LayoutInfo) {
        def li = dli as LayoutInfo;
        if (isInitialized(li.hpos)) {
            return li.hpos;
        }
    }
    if (fallback != null) {
        return fallback;
    }
    return GRID_DEFAULT.hpos;
}

/**
 * Utility function which returns the vertical layout position of the Node
 * which defines how the node should be vertically aligned within its layout
 * space if the height of the layout space is greater than the layout bounds
 * height of the node.
 * <p>
 * This version has no user default and will rely on class defaults instead.
 */
public function getNodeVPos(node:Node):VPos {
    getNodeVPos(node, null);
}

/**
 * Utility function which returns the vertical layout position of the Node
 * which defines how the node should be vertically aligned within its layout
 * space if the height of the layout space is greater than the layout bounds
 * height of the node.
 */
public function getNodeVPos(node:Node, fallback:VPos):VPos {
    if (node.layoutInfo instanceof LayoutInfo) {
        def li = node.layoutInfo as LayoutInfo;
        if (isInitialized(li.vpos)) {
            return li.vpos;
        }
    }
    def dli = DefaultLayout.getClassDefault(node);
    if (dli instanceof LayoutInfo) {
        def li = dli as LayoutInfo;
        if (isInitialized(li.vpos)) {
            return li.vpos;
        }
    }
    if (fallback != null) {
        return fallback;
    }
    return GRID_DEFAULT.vpos;
}

/**
 * Utility function which returns the maximum height of the Node.
 */
public function getNodeMaxHeight(node:Node):Number {
    if (node instanceof Resizable) {
        if (node.layoutInfo instanceof ExtendedLayoutInfo) {
            def eli = node.layoutInfo as ExtendedLayoutInfo;
            if (isInitialized(eli.maxHeight) and eli.maxHeight != -1) {
                return eli.maxHeight;
            }
        }
        def dli = DefaultLayout.getClassDefault(node);
        if (dli instanceof ExtendedLayoutInfo) {
            def eli = dli as ExtendedLayoutInfo;
            if (isInitialized(eli.maxHeight) and eli.maxHeight != -1) {
                return eli.maxHeight;
            }
        }
        return (node as Resizable).getMaxHeight();
    }
    return node.layoutBounds.height;
}

/**
 * Utility function which returns the maximum width of the Node.
 */
public function getNodeMaxWidth(node:Node):Number {
    if (node instanceof Resizable) {
        if (node.layoutInfo instanceof ExtendedLayoutInfo) {
            def eli = node.layoutInfo as ExtendedLayoutInfo;
            if (isInitialized(eli.maxWidth) and eli.maxWidth != -1) {
                return eli.maxWidth;
            }
        }
        def dli = DefaultLayout.getClassDefault(node);
        if (dli instanceof ExtendedLayoutInfo) {
            def eli = dli as ExtendedLayoutInfo;
            if (isInitialized(eli.maxWidth) and eli.maxWidth != -1) {
                return eli.maxWidth;
            }
        }
        return (node as Resizable).getMaxWidth();
    }
    return node.layoutBounds.width;
}

/**
 * Utility function which returns the minimum height of the Node.
 */
public function getNodeMinHeight(node:Node):Number {
    if (node instanceof Resizable) {
        if (node.layoutInfo instanceof ExtendedLayoutInfo) {
            def eli = node.layoutInfo as ExtendedLayoutInfo;
            if (isInitialized(eli.minHeight) and eli.minHeight != -1) {
                return eli.minHeight;
            }
        }
        def dli = DefaultLayout.getClassDefault(node);
        if (dli instanceof ExtendedLayoutInfo) {
            def eli = dli as ExtendedLayoutInfo;
            if (isInitialized(eli.minHeight) and eli.minHeight != -1) {
                return eli.minHeight;
            }
        }
        return (node as Resizable).getMinHeight();
    }
    return node.layoutBounds.height;
}

/**
 * Utility function which returns the minimum width of the Node.
 */
public function getNodeMinWidth(node:Node):Number {
    if (node instanceof Resizable) {
        if (node.layoutInfo instanceof ExtendedLayoutInfo) {
            def eli = node.layoutInfo as ExtendedLayoutInfo;
            if (isInitialized(eli.minWidth) and eli.minWidth != -1) {
                return eli.minWidth;
            }
        }
        def dli = DefaultLayout.getClassDefault(node);
        if (dli instanceof ExtendedLayoutInfo) {
            def eli = dli as ExtendedLayoutInfo;
            if (isInitialized(eli.minWidth) and eli.minWidth != -1) {
                return eli.minWidth;
            }
        }
        return (node as Resizable).getMinWidth();
    }
    return node.layoutBounds.width;
}

/**
 * Utility function which returns the preferred height of the Node.
 */
public function getNodePrefHeight(node:Node):Number {
    return getNodePrefHeight(node, -1);
}

/**
 * Utility function which returns the preferred height of the Node for the given width.
 */
public function getNodePrefHeight(node:Node, width:Number):Number {
    if (node instanceof Resizable) {
        if (node.layoutInfo instanceof LayoutInfo) {
            def li = node.layoutInfo as LayoutInfo;
            if (isInitialized(li.height) and li.height != -1) {
                return li.height;
            }
        }
        def dli = DefaultLayout.getClassDefault(node);
        if (dli instanceof LayoutInfo) {
            def li = dli as LayoutInfo;
            if (isInitialized(li.height) and li.height != -1) {
                return li.height;
            }
        }
        return (node as Resizable).getPrefHeight(width);
    }
    return node.layoutBounds.height;
}

/**
 * Utility function which returns the preferred width of the Node.
 */
public function getNodePrefWidth(node:Node):Number {
    return getNodePrefWidth(node, -1);
}

/**
 * Utility function which returns the preferred width of the Node for the given height.
 */
public function getNodePrefWidth(node:Node, height:Number):Number {
    if (node instanceof Resizable) {
        if (node.layoutInfo instanceof LayoutInfo) {
            def li = node.layoutInfo as LayoutInfo;
            if (isInitialized(li.width) and li.width != -1) {
                return li.width;
            }
        }
        def dli = DefaultLayout.getClassDefault(node);
        if (dli instanceof LayoutInfo) {
            def li = dli as LayoutInfo;
            if (isInitialized(li.width) and li.width != -1) {
                return li.width;
            }
        }
        return (node as Resizable).getPrefWidth(height);
    }
    return node.layoutBounds.width;
}

/**
 * Utility function which returns the preferred width of the Node.
 */
public function getNodeFill(node:Node):Fill {
    if (node.layoutInfo instanceof ExtendedLayoutInfo) {
        def eli = node.layoutInfo as ExtendedLayoutInfo;
        if (isInitialized(eli.fill)) {
            return eli.fill;
        }
    }
    def dli = DefaultLayout.getClassDefault(node);
    if (dli instanceof ExtendedLayoutInfo) {
        def eli = dli as ExtendedLayoutInfo;
        if (isInitialized(eli.fill)) {
            return eli.fill;
        }
    }
    return GRID_DEFAULT.fill;
}

/**
 * Utility function which returns the horizontal span of the Node.
 */
public function getNodeHSpan(node:Node):Integer {
    if (node.layoutInfo instanceof GridLayoutInfo) {
        def gli = node.layoutInfo as GridLayoutInfo;
        if (isInitialized(gli.hspan)) {
            return gli.hspan;
        }
    }
    def dli = DefaultLayout.getClassDefault(node);
    if (dli instanceof GridLayoutInfo) {
        def gli = dli as GridLayoutInfo;
        if (isInitialized(gli.hspan)) {
            return gli.hspan;
        }
    }
    return GRID_DEFAULT.hspan;
}

/**
 * Utility function which returns the vertical span of the Node.
 */
public function getNodeVSpan(node:Node):Integer {
    if (node.layoutInfo instanceof GridLayoutInfo) {
        def gli = node.layoutInfo as GridLayoutInfo;
        if (isInitialized(gli.vspan)) {
            return gli.vspan;
        }
    }
    def dli = DefaultLayout.getClassDefault(node);
    if (dli instanceof GridLayoutInfo) {
        def gli = dli as GridLayoutInfo;
        if (isInitialized(gli.vspan)) {
            return gli.vspan;
        }
    }
    return GRID_DEFAULT.vspan;
}

/**
 * Utility function which returns the vertical Grow of the Node.

 */
public function getNodeHGrow(node:Node):Grow {
    if (node.layoutInfo instanceof GridLayoutInfo) {
        def gli = node.layoutInfo as GridLayoutInfo;
        if (isInitialized(gli.hgrow)) {
            return gli.hgrow;
        }
    }
    def dli = DefaultLayout.getClassDefault(node);
    if (dli instanceof GridLayoutInfo) {
        def gli = dli as GridLayoutInfo;
        if (isInitialized(gli.hgrow)) {
            return gli.hgrow;
        }
    }
    return GRID_DEFAULT.hgrow;
}

/**
 * Utility function which returns the horizontal Grow of the Node.
 */
public function getNodeVGrow(node:Node):Grow {
    if (node.layoutInfo instanceof GridLayoutInfo) {
        def gli = node.layoutInfo as GridLayoutInfo;
        if (isInitialized(gli.vgrow)) {
            return gli.vgrow;
        }
    }
    def dli = DefaultLayout.getClassDefault(node);
    if (dli instanceof GridLayoutInfo) {
        def gli = dli as GridLayoutInfo;
        if (isInitialized(gli.vgrow)) {
            return gli.vgrow;
        }
    }
    return GRID_DEFAULT.vgrow;
}

/**
 * Utility function which Lays out the node relative to the specified layout
 * area defined by areaX, areaY, areaW x areaH.
 * <p>
 * This differs from Container.layoutNode, because it also takes into account
 * ExtendedLayoutInfo.fill, and will attempt to constrain the node to fit the
 * bounding box.
 */
public function layoutNode(node:Node, areaX:Number, areaY:Number, areaW:Number, areaH:Number, hposIn:HPos, vposIn:VPos):Boolean {
    var hpos = getNodeHPos(node, hposIn);
    var vpos = getNodeVPos(node, vposIn);
    var fill = getNodeFill(node);
    var resized = false;
    if (node instanceof Resizable) {
        var newWidth = if (fill.isHorizontal()) {
            areaW;
        } else {
            Math.min(getNodePrefWidth(node), areaW);
        }
        var newHeight = if (fill.isVertical()) {
            areaH;
        } else {
            Math.min(getNodePrefHeight(node), areaH);
        }
        resized = resizeNode(node, newWidth, newHeight);
    }
    var layoutBounds = node.layoutBounds;
    node.layoutX = areaX - layoutBounds.minX;
    node.layoutY = areaY - layoutBounds.minY;
    if (hpos == LEFT) {
        // already taken care of
    } else if (hpos == CENTER) {
        node.layoutX += (areaW - layoutBounds.width) / 2;
    } else if (hpos == RIGHT or hpos == TRAILING) {
        node.layoutX += areaW - layoutBounds.width;
    }
    if (vpos == TOP or vpos == PAGE_START) {
        // already taken care of
    } else if (vpos == MIDDLE) {
        node.layoutY += (areaH - layoutBounds.height) / 2;
    } else if (vpos == BOTTOM or vpos == PAGE_END) {
        node.layoutY += areaH - layoutBounds.height;
    } else if (vpos == BASELINE) {
        // note: algorithm is wrong in the case where minY fluctuates
        node.layoutY = areaY + (areaH - layoutBounds.minY) / 2;
    }
    return resized;
}
