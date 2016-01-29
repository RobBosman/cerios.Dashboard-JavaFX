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

import java.lang.Object;
import javafx.scene.control.TextBox;
import javafx.scene.layout.Container;
import javafx.scene.layout.Resizable;
import javafx.scene.Node;
import javafx.stage.Screen;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;


/**
 * @returns The layout hash code for the supplied node.  The layout hash code is a number that can
 * be computed quickly and, by comparing the result to a previously returned result, will let the
 * caller know whether the MigLayout Grid needs to be recreated.
 */
package function getLayoutHashCode( component:Node ) {
    var h = 0;

    if (component instanceof Resizable) {
        def r = component as Resizable;
        h += (r.getMaxWidth() as Integer) + ((r.getMaxHeight() as Integer) * 32); // << 0, << 5
        h += ((r.getPrefWidth(-1) as Integer) * 1024) + ((r.getPrefHeight(-1) as Integer) * 32768); // << 10, << 15
        h += ((r.getMinWidth() as Integer) * 1048576) + ((r.getMinHeight() as Integer) * 33554432); // << 20, << 25
    } else {
        h = (component.layoutBounds.width + (component.layoutBounds.height * 65536)) as Integer;  // << 16
    }

    if (component.visible) {
        h += 1324511;
    }

    if (Container.managed(component)) {
        h += 1324513;
    }

    if (component.id.length() > 0) {
        h += component.id.hashCode();
    }

    return h as Integer;
}

/**
 * @profile desktop
 *
 * @author Dean Iverson
 */
package class FxComponentWrapper extends FxComponentWrapperAdaptor {
    public-init var component:Node;

	/** 
     * Returns the actual object that this wrapper is aggregating. This might be needed for getting
	 * information about the object that the wrapper interface does not provide.
	 * <p>
	 * If this is a container the container should be returned instead.
	 * @return The actual object that this wrapper is aggregating. Not <code>null</code>.
	 */
    override function getComponent(): Object {
        return component;
    }

	/** 
     * @return The current x coordinate for this component.
	 */
    override function getX():Integer {
        return component.layoutBounds.minX as Integer;
    }

	/**
     * @return The current y coordinate for this component.
	 */
    override function getY():Integer {
        return component.layoutBounds.minY as Integer;
    }

	/** 
     * @return The current width for this component.
	 */
    override function getWidth():Integer {
        return component.layoutBounds.width as Integer;
    }

	/**
     * @return The current height for this component.
	 */
    override function getHeight():Integer {
        return component.layoutBounds.height as Integer;
    }

	/**
     * @return The screen x-coordinate for the upper left coordinate of the component layoutable bounds.
	 */
    override function getScreenLocationX():Integer {
        var boundsInScene = component.localToScene(component.boundsInLocal);
        return (component.scene.stage.x + component.scene.x + boundsInScene.minX) as Integer;
    }

	/**
     * @return The screen y-coordinate for the upper left coordinate of the component layoutable bounds.
	 */
    override function getScreenLocationY():Integer {
        var boundsInScene = component.localToScene(component.boundsInLocal);
        return (component.scene.stage.y + component.scene.y + boundsInScene.minY) as Integer;
    }

	/** 
     * Returns the minimum width of the component.
     * @param hHint The Size hint for the other dimension. An implementation can use this value or the
	 *              current size for the widget in this dimension, or a combination of both, to calculate
     *              the correct size.<br>
	 *              Use -1 to denote that there is no hint. This corresponds with SWT.DEFAULT.
	 * @return The minimum width of the component.
	 * @since 3.5. Added the hint as a parameter knowing that a correction and recompilation is neccessary for
	 *             any implmenting classes. This change was worth it though.
	 */
    override function getMinimumWidth(hHint:Integer):Integer {
        Container.getNodeMinWidth(component) as Integer;
    }

	/** 
     * Returns the minimum height of the component.
     * @param wHint The Size hint for the other dimension. An implementation can use this value or the
	 *              current size for the widget in this dimension, or a combination of both, to calculate
     *              the correct size.<br>
	 *              Use -1 to denote that there is no hint. This corresponds with SWT.DEFAULT.
	 * @return The minimum height of the component.
	 * @since 3.5. Added the hint as a parameter knowing that a correction and recompilation is neccessary for
	 *             any implmenting classes. This change was worth it though.
	 */
    override function getMinimumHeight(wHint:Integer):Integer {
        Container.getNodeMinHeight(component) as Integer;
    }

	/** 
     * Returns the preferred width of the component.
     * @param hint The Size hint for the other dimension. An implementation can use this value or the
	 *              current size for the widget in this dimension, or a combination of both, to calculate
     *              the correct size.<br>
	 *              Use -1 to denote that there is no hint. This corresponds with SWT.DEFAULT.
	 * @return The preferred width of the component.
	 * @since 3.5. Added the hint as a parameter knowing that a correction and recompilation is neccessary for
	 *             any implmenting classes. This change was worth it though.
	 */
    override function getPreferredWidth(height:Integer):Integer {
        Container.getNodePrefWidth(component, height) as Integer;
    }

	/** 
     * Returns the preferred height of the component.
     * @param wHint The Size hint for the other dimension. An implementation can use this value or the
	 *              current size for the widget in this dimension, or a combination of both, to calculate
     *              the correct size.<br>
	 *              Use -1 to denote that there is no hint. This corresponds with SWT.DEFAULT.
	 * @return The preferred height of the component.
	 * @since 3.5. Added the hint as a parameter knowing that a correction and recompilation is neccessary for
	 *             any implmenting classes. This change was worth it though.
	 */
    override function getPreferredHeight(width:Integer):Integer {
        Container.getNodePrefHeight(component, width) as Integer;
    }

	/** 
     * Returns the maximum width of the component.
     * @param hHint The Size hint for the other dimension. An implementation can use this value or the
	 *              current size for the widget in this dimension, or a combination of both, to calculate
     *              the correct size.<br>
	 * Use -1 to denote that there is no hint. This corresponds with SWT.DEFAULT.
	 * @return The maximum width of the component.
	 * @since 3.5. Added the hint as a parameter knowing that a correction and recompilation is neccessary for
	 *             any implmenting classes. This change was worth it though.
	 */
	  override function getMaximumWidth(hHint:Integer):Integer {
        Container.getNodeMaxWidth(component) as Integer;
    }

	/** 
     * Returns the maximum height of the component.
     * @param wHint The Size hint for the other dimension. An implementation can use this value or the
	 *              current size for the widget in this dimension, or a combination of both, to calculate
     *              the correct size.<br>
	 *              Use -1 to denote that there is no hint. This corresponds with SWT.DEFAULT.
	 * @return The maximum height of the component.
	 * @since 3.5. Added the hint as a parameter knowing that a correction and recompilation is neccessary for
	 *             any implmenting classes. This change was worth it though.
	 */
    override function getMaximumHeight(wHint:Integer):Integer {
        Container.getNodeMaxHeight(component) as Integer;
    }

	/** 
   * Sets the component's bounds.
   * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param width The width.
	 * @param height The height.
	 */
    override function setBounds(x:Integer, y:Integer, width:Integer, height:Integer) {
        //println( "'{component.id}' current bounds: {component.layoutBounds}" );
        //println( "'{component.id}' setBounds: {x}, {y}, {width}, {height}" );
        Container.layoutNode(component, x, y, width, height);
        //println( "'{component.id}' new bounds: {component.layoutBounds}" );
    }

	/** 
     * Returns if the component's visibility is set to <code>true</code>. This should not return if the component is
     * actually visibile, but if the visibility is set to true or not.
	 * @return <code>true</code> means visible.
	 */
    override function isVisible():Boolean {
        component.visible;
    }

	/** 
     * Returns the baseline for the component given the suggested height.
     * @param width The width to calculate for if other than the current. If <code>-1</code> the current size should be used.
	 * @param height The height to calculate for if other than the current. If <code>-1</code> the current size should be used.
	 * @return The baseline from the top or -1 if not applicable.
	 */
	override function getBaseline(width:Integer, height:Integer):Integer {
        // TODO: Handle this for Text or TextBox
        return -1;
    }

	/** 
     * Returns if the component has a baseline and if it can be retrieved. Should for instance return
     * <code>false</code> for Swing before mustang.
	 * @return If the component has a baseline and if it can be retrieved.
	 */
    override function hasBaseline():Boolean {
        // TODO: Handle this for Text or TextBox
        return false;
    }

	/**
     * @return The parent container for this component or <code>null</code> if there isn't one.
	 */
    override function getParent():ContainerWrapper {
        if (component.parent != null) {
            return FxContainerWrapper { component: component.parent }
        }
        return null;
    }

	/** 
     * Returns the pixel unit factor for the horizontal or vertical dimension.
     * <p>
	 * The factor is 1 for both dimensions on the normal font in a JPanel on Windows. The factor should 
     * increase with a bigger "X".
	 * <p>
	 * This is the Swing version:
	 * <pre>
	 * Rectangle2D r = fm.getStringBounds("X", parent.getGraphics());
	 * wFactor = r.getWidth() / 6;
	 * hFactor = r.getHeight() / 13.27734375f;
	 * </pre>
	 * @param isHor If it is the horizontal factor that should be returned.
	 * @return The factor.
	 */
	override function getPixelUnitFactor(isHor:Boolean) {
        // TODO: How to calculate this using the common profile?
        return 1.0;
    }

	/** 
     * Returns the DPI (Dots Per Inch) of the screen the component is currently in or for the default
     * screen if the component is not visible.
	 * <p>
	 * If headless mode {@link net.miginfocom.layout.PlatformDefaults#getDefaultDPI} will be returned.
	 * @return The DPI.
	 */
    override function getHorizontalScreenDPI():Integer {
        return Screen.primary.dpi as Integer;
    }

	/** 
     * Returns the DPI (Dots Per Inch) of the screen the component is currently in or for the default
     * screen if the component is not visible.
	 * <p>
	 * If headless mode {@link net.miginfocom.layout.PlatformDefaults#getDefaultDPI} will be returned.
	 * @return The DPI.
	 */
    override function getVerticalScreenDPI():Integer {
        return Screen.primary.dpi as Integer;
    }

	/** 
     * Returns the pixel size of the screen that the component is currently in or for the default
     * screen if the component is not visible or <code>null</code>.
	 * <p>
	 * If in headless mode <code>1024</code> is returned.
	 * @return The screen size. E.g. <code>1280</code>.
	 */
    override function getScreenWidth():Integer {
        return Screen.primary.bounds.width as Integer;
    }

	/** 
     * Returns the pixel size of the screen that the component is currently in or for the default
     * screen if the component is not visible or <code>null</code>.
	 * <p>
	 * If in headless mode <code>768</code> is returned.
	 * @return The screen size. E.g. <code>1024</code>.
	 */
    override function getScreenHeight():Integer {
        return Screen.primary.bounds.height as Integer;
    }

	/** 
     * Returns a String id that can be used to reference the component in link constraints. This value should
     * return the default id for the component. The id can be set for a component in the constraints and if
	 * so the value returned by this method will never be used. If there are no sensible id for the component
	 * <code>null</code> should be returned.
	 * <p>
	 * For instance the Swing implementation returns the string returned from <code>Component.getName()</code>.
	 * @return The string link id or <code>null</code>.
	 */
    override function getLinkId():String {
        return component.id;
    }

	/** 
     * Returns a hash code that should be resonably different for anything that might change the layout. This
     * value is used to know if the component layout needs to clear any caches.
	 * @return A hash code that should be resonably different for anything that might change the layout. 
     *         Returns -1 if the widget is disposed.
	 */
    override function getLayoutHashCode():Integer {
        getLayoutHashCode( component );
    }

	/** 
     * Returns the padding on a component by component basis. This method can be overridden to return padding
     * to compensate for example for borders that have shadows or where the outer most pixel is not the visual
     * "edge" to align to.
	 * <p>
	 * Default implementation returnes <code>null</code> for all components except for Windows XP's JTabbedPane 
     * which will return new Insets(0, 0, 2, 2).
	 * <p>
	 * <b>NOTE!</B> To reduce generated garbage the returned padding should never be changed so that the same
     * insets can be returned many times.
     *
	 * @return <code>null</code> if no padding. <b>NOTE!</B> To reduce cenerated garbage the returned padding 
     *         should never be changed so that the same insets can be returned many times. [top, left, bottom, right]
	 */
    override function getVisualPadding() {
        return null;
    }

    /**
     * Paints component outline to indicate where it is.
     */
    override function paintDebugOutline() {
        // TODO: Use BoundsPainter

    }

	/** 
     * Returns the type of component that this wrapper is wrapping.
     * <p>
	 * This method can be invoked often so the result should be cachecomponent.layoutBounds
	 * @param disregardScrollPane If <code>true</code> any wrapping scroll pane should be disregarded and the type
	 *                            of the scrolled component should be returnecomponent.layoutBounds
	 * @return The type of component that this wrapper is wrapping. E.g. {@link #TYPE_LABEL}.
	 */
    override function getFxComponentType(disregardScrollPane:Boolean) {
        if (component instanceof TextBox) {
            return TYPE_TEXT_FIELD;
        } 
        else
        if (component instanceof Container) {
            return TYPE_CONTAINER;
        } else {
            return TYPE_UNKNOWN;
        }
    }

    override function hashCode():Integer {
        return component.hashCode();
    }

    /**
     * This needs to be overridden so that different wrappers that hold the same component compare
     * as equal.  Otherwise, Grid won't be able to layout the components correctly.
     */
    override function equals(o:Object):Boolean {
        if (not (o instanceof ComponentWrapper)) {
            return false;
        }

        return getComponent().equals((o as ComponentWrapper).getComponent());
    }
}
