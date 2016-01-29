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

import net.miginfocom.layout.ComponentWrapper;

/**
 * This class only exists because JavaFX (as of v1.2) cannot implement a Java interface that
 * returns a primitive type.  The JavaFX compiler is not smart enough to unbox the Integer
 * automatically and therefore it mismatches on the return type and generates a compiler error.
 *
 * The solution implemented here is to insert an abstract Java class into the hierarchy as an
 * adaptor that implements the problematic interface method by calling into a JavaFX-friendly
 * version of the method and doing the unboxing manually.  The JavaFX class, then, will extend
 * this class rather than implement the original interface.
 *
 * @author Dean Iverson
 */
public abstract class FxComponentWrapperAdaptor implements ComponentWrapper {

    public int getComponetType(boolean disregardScrollPane) {
        return getFxComponentType(disregardScrollPane).intValue();
    }
    
    public abstract Integer getFxComponentType( boolean disregardScrollPane );
}
