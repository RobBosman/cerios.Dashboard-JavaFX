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

/**
 * Determines how available space is utilized by within a Layout.  Setting
 * a fill of VERTICAL or HORIZONTAL will cause the Node to take all available
 * space in that orientation.  Since the entire cell is being filled, this
 * negates the effect of h/vpos.
 *
 * This will not affect the relative sizing of this component and others in the
 * same layout.  Use width/height to request additional space in combination
 * with FILL.
 *
 * @profile common
 *
 * @author Stephen Chin
 */
public enum Fill {
    /**
     * Component will take all available vertical space within the layout cell
     * regardless of min/max/pref width and height.
     */
    VERTICAL,
    /**
     * Component will take all available horizontal space within the layout cell
     * regardless of min/max/pref width and height.
     */
    HORIZONTAL,
    /**
     * Component will take all available space within the layout cell
     * regardless of min/max/pref width and height.
     */
    BOTH,
    /**
     * Component size will be determined by the mix/max/pref width and height.
     */
    NONE;

    /**
     * Convenience function that returns true is this Fill applies to the
     * vertical orientation.
     *
     * @return True if the value is VERTICAL or BOTH
     */
    public boolean isVertical() {
        return this == VERTICAL || this == BOTH;
    }

    /**
     * Convenience function that returns true is this Fill applies to the
     * horizontal orientation.
     *
     * @return True if the value is HORIZONTAL or BOTH
     */
    public boolean isHorizontal() {
        return this == HORIZONTAL || this == BOTH;
    }
}
