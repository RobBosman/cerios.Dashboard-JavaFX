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

import javafx.scene.Node;
import org.jfxtras.scene.layout.LayoutConstants.*;

/**
 * Extends LayoutInfo with additional parameters specific to Grids.
 *
 * @profile common
 *
 * @author Stephen Chin
 */
public class GridLayoutInfo extends ExtendedLayoutInfo {

    /**
    * The number of columns that this Node will span.
     */
    public var hspan:Integer on replace {
        requestLayout();
    }

    /**
    * The number of rows that this Node will span.
     */
    public var vspan:Integer on replace {
        requestLayout();
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
        requestLayout();
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
        requestLayout();
    }

    override function toString() {
        "GridLayoutInfo \{hspan={hspan}, vspan={vspan}, hgrow={hgrow}, vgrow={vgrow}, extendedLayoutInfo={super.toString()}\}"
    }
}

public def GRID_DEFAULT = GridLayoutInfo {
    hpos: EXTENDED_DEFAULT.hpos
    vpos: EXTENDED_DEFAULT.vpos
    hspan: 1
    vspan: 1
    hgrow: NEVER
    vgrow: NEVER
}
