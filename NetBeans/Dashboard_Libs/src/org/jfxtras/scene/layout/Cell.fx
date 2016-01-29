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
import javafx.scene.CustomNode;
import javafx.scene.layout.Resizable;

/**
 * Wrapper for a Node that is placed inside a {@link Grid}.  This provides
 * additional control over how the Node is positioned and oriented in the
 * Grid including alignment, span, grow, and the ability to override Resizable
 * properties.
 *
 * @profile desktop
 *
 * @author Stephen Chin
 * @author Keith Combs
 */
public class Cell extends CustomNode, DefaultLayout, Resizable {
    /**
     * The content of this Cell will be constrained by the constraint variables when
     * placed inside a {@link Grid}.
     */
    public-init var content:Node;

    // hack - workaround for RT-4524
    override var layoutBounds = bind content.layoutBounds;

    override var layoutInfo = GridLayoutInfo {}

    override var defaultLayoutInfo = bind DefaultLayout.getClassDefault(content);

    /**
     * If set, will define how the node should be horizontally positioned within its allocated layout space.
     */
    public var hpos:HPos on replace {
        if (isInitialized(hpos)) {
            (layoutInfo as GridLayoutInfo).hpos = hpos
        }
    }

    /**
     * If set, will define how the node should be vertically positioned within its allocated layout space.
     */
    public var vpos:VPos on replace {
        if (isInitialized(vpos)) {
            (layoutInfo as GridLayoutInfo).vpos = vpos
        }
    }

    /**
     * Overrides the preferred height of the Node.
     */
    public var prefHeight:Number on replace {
        if (isInitialized(prefHeight)) {
            (layoutInfo as GridLayoutInfo).height = prefHeight
        }
    }

    /**
     * Overrides the preferred width of the Node.
     */
    public var prefWidth:Number on replace {
        if (isInitialized(prefWidth)) {
            (layoutInfo as GridLayoutInfo).width = prefWidth
        }
    }

    /**
     * Overrides the minimum height of the Node.
     */
    public var minHeight:Number on replace {
        if (isInitialized(minHeight)) {
            (layoutInfo as GridLayoutInfo).minHeight = minHeight
        }
    }

    /**
     * Overrides the minimum width of the Node.
     */
    public var minWidth:Number on replace {
        if (isInitialized(minWidth)) {
            (layoutInfo as GridLayoutInfo).minWidth = minWidth
        }
    }

    /**
     * Overrides the maximum height of the Node.
     */
    public var maxHeight:Number on replace {
        if (isInitialized(maxHeight)) {
            (layoutInfo as GridLayoutInfo).maxHeight = maxHeight
        }
    }

    /**
     * Overrides the maximum width of the Node.
     */
    public var maxWidth:Number on replace {
        if (isInitialized(maxWidth)) {
            (layoutInfo as GridLayoutInfo).maxWidth = maxWidth
        }
    }

    /**
     * If set, will cause the Node to take all available space in the
     * horizontal or vertical direction.  See {@link Fill} for more details.
     */
    public var fill:Fill on replace {
        if (isInitialized(fill)) {
            (layoutInfo as GridLayoutInfo).fill = fill
        }
    }

    /**
    * The number of columns that this Node will span.
     */
    public var hspan:Integer on replace {
        if (isInitialized(hspan)) {
            (layoutInfo as GridLayoutInfo).hspan = hspan
        }
    }

    /**
    * The number of rows that this Node will span.
     */
    public var vspan:Integer on replace {
        if (isInitialized(vspan)) {
            (layoutInfo as GridLayoutInfo).vspan = vspan
        }
    }

    /**
    * The priority for allocating unused space in the Grid.  Components with
     * the same horizontalGrow value will receive an equal portion of the Grid.
     * <p>
     * If unset this value will default to:
     * {@link GridConstraints.NEVER}
     * <p>
     * Note that extra space is first allocated to the Grid columnPercentages.
     */
    public var hgrow:Grow on replace {
        if (isInitialized(hgrow)) {
            (layoutInfo as GridLayoutInfo).hgrow = hgrow
        }
    }

    /**
    * The priority for allocating unused space in the Grid.  Components with
     * the same verticalGrow value will receive an equal portion of the Grid.
     * <p>
     * If unset this value will default to:
     * {@link GridConstraints.NEVER}
     * <p>
     * Note that extra space is first allocated to the Grid columnPercentages.
     */
    public var vgrow:Grow on replace {
        if (isInitialized(vgrow)) {
            (layoutInfo as GridLayoutInfo).vgrow = vgrow
        }
    }

    override function create() {
        return content;
    }

    override var width on replace {
        if (isInitialized(width)) {
            JFXContainer.setNodeWidth(content, width);
        }
    }

    override var height on replace {
        if (isInitialized(height)) {
            JFXContainer.setNodeHeight(content, height);
        }
    }

    override function getPrefWidth(height) {
        JFXContainer.getNodePrefWidth(content, height);
    }

    override function getPrefHeight(width) {
        JFXContainer.getNodePrefHeight(content, width);
    }

    override function getMinWidth() {
        JFXContainer.getNodeMinWidth(content);
    }

    override function getMinHeight() {
        JFXContainer.getNodeMinHeight(content);
    }

    override function getMaxWidth() {
        JFXContainer.getNodeMaxWidth(content);
    }

    override function getMaxHeight() {
        JFXContainer.getNodeMaxHeight(content);
    }

    override function toString() {
        "Cell \{content={content}, layoutInfo={layoutInfo}\}"
    }
}
