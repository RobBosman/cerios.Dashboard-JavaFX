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

import javafx.scene.Group;
import javafx.scene.layout.Container;
import net.miginfocom.layout.ContainerWrapper;

/**
 * @profile desktop
 *
 * @author Dean Iverson
 */
package class FxContainerWrapper extends FxComponentWrapper, ContainerWrapper {
    var group:Group;
    
    init {
        group = if (component instanceof Group) component as Group else null;
    }
    
	/** 
     * @return The components of the container that wrapper is wrapping. Never <code>null</code>.
	 */
    override function getComponents() {
        var components = for (n in group.content) {
            if (Container.managed(n)) {
                FxComponentWrapper { component: n }
            } else {
                null;
            }
        }
        return components as nativearray of FxComponentWrapper;
    }

	/** 
     * @return The number of components that this parent has.
	 */
    override function getComponentCount():Integer {
        return sizeof group.content;
    }

	/** 
     * Returns the <code>LayoutHandler</code> (in Swing terms) that is handling the layout of this container.
     * If there exist no such class the method should return the same as {@link #getComponent()}, which is the
	 * container itself.
	 * @return The layout handler instance. Never <code>null</code>.
	 */
    override function getLayout() {
        return group;
    }

	/** 
     * @return If this container is using left-to-right group ordering.
	 */
    override function isLeftToRight() {
        return true;
    }

	/** 
     * Paints a cell to indicate where it is.
     * @param x The x coordinate to start the drwaing.
	 * @param y The x coordinate to start the drwaing.
	 * @param width The width to draw/fill
	 * @param height The height to draw/fill
	 */
    override function paintDebugCell(x:Integer, y:Integer, width:Integer, height:Integer) {
        // TODO: Use BoundsPainter
    }
}
