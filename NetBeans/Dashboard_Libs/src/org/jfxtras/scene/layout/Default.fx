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

import javafx.reflect.FXClassType;
import javafx.reflect.FXLocal;
import javafx.scene.Node;
import javafx.scene.layout.LayoutInfoBase;
import javafx.reflect.FXContext;
import javafx.scene.layout.LayoutInfo;
import org.jfxtras.scene.layout.LayoutConstants.*;
import javafx.scene.control.ScrollBar;

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
package class Default {
    public-init var classType:FXClassType;
    public-init var calculateDefault:function(node:Node):LayoutInfoBase;
    public-init var default:LayoutInfoBase;

    public function getDefault(node:Node, type:FXClassType) {
        return if (node != null and classType == type) {
            if (calculateDefault != null) {
                default = calculateDefault(node);
            }
            default;
        } else {
            null;
        }
    }

    override function toString() {
        "Default \{classType={classType}, default={default}\}"
    }
}

package def CLASS_DEFAULTS = [
    Default {
        classType: FXContext.getInstance().findClass("javafx.scene.text.Text");
        default: LayoutInfo {
            hpos: LEFT
            vpos: BASELINE
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.scene.control.Button");
        default: LayoutInfo {
            hpos: LEFT
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.scene.control.CheckBox");
        default: GridLayoutInfo {
            hpos: LEFT
            hgrow: SOMETIMES
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.scene.control.RadioButton");
        default: GridLayoutInfo {
            hpos: LEFT
            hgrow: SOMETIMES
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.scene.control.Hyperlink");
        default: LayoutInfo {
            hpos: LEFT
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.scene.control.Label");
        default: LayoutInfo {
            hpos: LEFT
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.scene.control.ListView");
        default: GridLayoutInfo {
            fill: BOTH
            hgrow: ALWAYS
            vgrow: ALWAYS
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.scene.control.ProgressBar");
        default: GridLayoutInfo {
            fill: HORIZONTAL
            hgrow: SOMETIMES
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.scene.control.ProgressIndicator");
        default: LayoutInfo {
            hpos: LEFT
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.scene.control.ScrollBar");
        calculateDefault: function(node) {
            GridLayoutInfo {
                hpos: RIGHT
                vpos: BOTTOM
                fill: bind if ((node as ScrollBar).vertical) VERTICAL else HORIZONTAL;
                hgrow: bind if (not (node as ScrollBar).vertical) ALWAYS else NEVER;
                vgrow: bind if ((node as ScrollBar).vertical) ALWAYS else NEVER;
            }
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.scene.control.Slider");
        default: GridLayoutInfo {
            fill: HORIZONTAL
            hgrow: SOMETIMES
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.scene.control.TextBox");
        default: GridLayoutInfo {
            fill: HORIZONTAL
            hgrow: ALWAYS
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.ext.swing.SwingButton");
        default: LayoutInfo {
            hpos: LEFT
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.ext.swing.SwingComboBox");
        default: GridLayoutInfo {
            fill: HORIZONTAL
            hgrow: SOMETIMES
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.ext.swing.SwingLabel");
        default: LayoutInfo {
            hpos: LEFT
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.ext.swing.SwingList");
        default: GridLayoutInfo {
            fill: BOTH
            hgrow: ALWAYS
            vgrow: ALWAYS
            minWidth: 46  // allow lists to shrink with scroll bars
            minHeight: 46 // allow lists to shrink with scroll bars
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.ext.swing.SwingRadioButton");
        default: GridLayoutInfo {
            fill: HORIZONTAL
            hgrow: SOMETIMES
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.ext.swing.SwingScrollPane");
        default: GridLayoutInfo {
            fill: BOTH
            hgrow: ALWAYS
            vgrow: ALWAYS
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.ext.swing.SwingSlider");
        default: GridLayoutInfo {
            fill: HORIZONTAL
            hgrow: SOMETIMES
        }
    },
    Default {
        classType: FXContext.getInstance().findClass("javafx.ext.swing.SwingTextField");
        default: GridLayoutInfo {
            fill: HORIZONTAL
            hgrow: ALWAYS
        }
    }
];
