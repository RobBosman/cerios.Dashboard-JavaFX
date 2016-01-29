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
 * Enumeration used for Cells to determine how whether they grow to take
 * available space.
 *
 * For convenience, it is recommended to use the constants defined in
 * {@link org.jfxtras.scene.layout.GridConstraints} as static imports instead.
 *
 * @profile desktop
 *
 * @author Stephen Chin
 */
public enum Grow {
    /**
     * Component will always try to grow, sharing space equally with other
     * components that have a grow of ALWAYS.
     */
    ALWAYS,
    /**
     * If there are no other components with Grow set to ALWAYS will get an
     * equal share of the extra space.
     */
    SOMETIMES,
    /**
     * Disables grow behavior for this component.
     */
    NEVER;

    public static Grow max(Grow a, Grow b) {
        if (a == ALWAYS || b == ALWAYS) {
            return ALWAYS;
        } else if (a == SOMETIMES || b == SOMETIMES) {
            return SOMETIMES;
        } else {
            return NEVER;
        }
    }

    public static Grow min(Grow a, Grow b) {
        if (a == NEVER || b == NEVER) {
            return NEVER;
        } else if (a == SOMETIMES || b == SOMETIMES) {
            return SOMETIMES;
        } else {
            return ALWAYS;
        }
    }
}
