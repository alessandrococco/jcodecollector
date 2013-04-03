/*
 * Copyright 2006-2013 Alessandro Cocco.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jcodecollector.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

/**
 * A modified version of FlowLayout that allows containers using this Layout to
 * behave in a reasonable manner when placed inside a JScrollPane
 * 
 * @author Babu Kalakrishnan
 */
public class ModifiedFlowLayout extends FlowLayout {
    private static final long serialVersionUID = -171022815224628999L;

    public ModifiedFlowLayout() {
        super();
    }

    public ModifiedFlowLayout(int align) {
        super(align);
    }

    public ModifiedFlowLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    public Dimension minimumLayoutSize(Container target) {
        return computeSize(target, false);
    }

    public Dimension preferredLayoutSize(Container target) {
        return computeSize(target, true);
    }

    private Dimension computeSize(Container target, boolean minimum) {
        synchronized (target.getTreeLock()) {
            int hgap = getHgap();
            int vgap = getVgap();
            int w = target.getWidth();

            // Let this behave like a regular FlowLayout (single row)
            // if the container hasn't been assigned any size yet
            if (w == 0)
                w = Integer.MAX_VALUE;

            Insets insets = target.getInsets();
            if (insets == null)
                insets = new Insets(0, 0, 0, 0);
            int reqdWidth = 0;

            int maxwidth = w - (insets.left + insets.right + hgap * 2);
            int n = target.getComponentCount();
            int x = 0;
            int y = insets.top;
            int rowHeight = 0;

            for (int i = 0; i < n; i++) {
                Component c = target.getComponent(i);
                if (c.isVisible()) {
                    Dimension d = minimum ? c.getMinimumSize()
                            : c.getPreferredSize();
                    if ((x == 0) || ((x + d.width) <= maxwidth)) {
                        if (x > 0) {
                            x += hgap;
                        }
                        x += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    } else {
                        x = d.width;
                        y += vgap + rowHeight;
                        rowHeight = d.height;
                    }
                    reqdWidth = Math.max(reqdWidth, x);
                }
            }
            y += rowHeight;
            return new Dimension(reqdWidth + insets.left + insets.right, y);
        }
    }
}
