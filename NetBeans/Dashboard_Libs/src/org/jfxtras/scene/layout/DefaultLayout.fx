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

import javafx.reflect.FXLocal;
import javafx.scene.Node;
import javafx.scene.layout.LayoutInfoBase;

/**
 * Mixin for Controls or Nodes that want to participate fully in resizable layouts.
 * The defaultConstraints is designed for the component author to set their
 * desired layout settings and the constraints variable is meant for end users
 * to override the defaults.  See the {@link Constraints} class for more details on
 * the available options.
 *
 * @profile desktop
 *
 * @author Stephen Chin
 */
public mixin class DefaultLayout {
    /**
     * The default layout info for this Node.
     * <p>
     * Usually this will be initialized
     * by the Node subclass to a static LayoutInfo for all instances.  Any values
     * set on this will override the default Constraint values.
     */
    public-read protected var defaultLayoutInfo:LayoutInfoBase;
}

public function getClassDefault(node:Node):LayoutInfoBase {
    if (node == null) return null;
    if (node instanceof DefaultLayout) {
        return (node as DefaultLayout).defaultLayoutInfo
    }
    var type = FXLocal.getContext().makeClassRef(node.getClass());
    for (default in Default.CLASS_DEFAULTS) {
        var layoutInfo = default.getDefault(node, type);
        if (layoutInfo != null) {
            return layoutInfo;
        }
    }
    return null;
}
