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
import org.jfxtras.scene.layout.LayoutConstants.*;

import org.jfxtras.util.SequenceUtil;

/**
 * Simple layout that stacks the contents on top of each other.  For regular
 * nodes this behaves identically to Group; however, for Nodes that implement
 * Resizable this will try to grow the width and height to the size of the
 * Deck, based on the Node's constraints.
 * <p>
 * Since all Nodes are rendered, this layout is ideally suited to work with
 * shaped or transparent components where the other children would be visible
 * underneath.
 *
 * @profile desktop
 *
 * @author Stephen Chin
 * @author Keith Combs
 */
public class Deck extends JFXContainer, DefaultLayout {
    /**
     * The horizontal position of each node within the Deck's width.
     * <p>
     * This may be overridden for individual nodes by setting the hpos variable
     * on the node's layoutInfo variable.
     *
     * @defaultvalue HPos.CENTER
     */
    public var nodeHPos:HPos = HPos.CENTER;

    /**
     * The vertical position of each node within the Deck's height.
     * <p>
     * This may be overridden for individual nodes by setting the vpos variable
     * on the node's layoutInfo variable.
     *
     * @defaultvalue VPos.CENTER
     */
    public var nodeVPos:VPos = VPos.CENTER;

    override function getMaxHeight():Number {
        if (sizeof content == 0) return Float.MAX_VALUE;
        return Sequences.max(for (node in content) getNodeMaxHeight(node)) as Number;
    }

    override function getMaxWidth():Number {
        if (sizeof content == 0) return Float.MAX_VALUE;
        return Sequences.max(for (node in content) getNodeMaxWidth(node)) as Number;
    }

    override function getMinHeight():Number {
        if (sizeof content == 0) return 0;
        return Sequences.max(for (node in content) getNodeMinHeight(node)) as Number;
    }

    override function getMinWidth():Number {
        if (sizeof content == 0) return 0;
        return Sequences.max(for (node in content) getNodeMinWidth(node)) as Number;
    }

    override function getPrefHeight(width):Number {
        if (sizeof content == 0) return 0;
        return Sequences.max(for (node in content) getNodePrefHeight(node, width)) as Number;
    }

    override function getPrefWidth(height):Number {
        if (sizeof content == 0) return 0;
        return Sequences.max(for (node in content) getNodePrefWidth(node, height)) as Number;
    }

    override var defaultLayoutInfo = GridLayoutInfo {
        fill: BOTH
        hgrow: ALWAYS
        vgrow: ALWAYS
    }

    var hgrow:Grow[] = bind for (c in getManaged(content)) {
        getNodeHGrow(c)
    }

    var vgrow:Grow[] = bind for (c in getManaged(content)) {
        getNodeVGrow(c)
    }

    override var defaultLayoutInfo = bind lazy GridLayoutInfo {
        fill: BOTH
        hgrow: SequenceUtil.fold(NEVER, hgrow, function(a, b) {Grow.max(a as Grow, b as Grow)}) as Grow
        vgrow: SequenceUtil.fold(NEVER, vgrow, function(a, b) {Grow.max(a as Grow, b as Grow)}) as Grow
    }

    override var layoutBounds = bind lazy BoundingBox {width: width, height: height};

    override function doLayout():Void {
        for (node in content) {
            layoutNode(node, 0, 0, width, height, nodeHPos, nodeVPos);
        }
    }
}
