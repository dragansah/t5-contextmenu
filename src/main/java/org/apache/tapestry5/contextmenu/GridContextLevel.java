// Copyright 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.contextmenu;

/**
 * The level at which mixins (like the context menu mixin) are used with the
 * {@link org.apache.tapestry5.corelib.components.Grid} component.
 * <p>
 * The three different levels express the level of granularity at which context data about the grid is stored by the
 * corresponding mixin.
 * 
 * @see {@link ContextMenu}
 * @see {@link ContextMenuAjax}
 * @since 5.3
 * @tapestrydoc
 */
public enum GridContextLevel
{
    /**
     * Grid context data is stored on grid cell level. The context data is per cell.
     */
    CELL,

    /**
     * Grid context data is stored on grid row level. The context data is per row.
     */
    ROW,

    /**
     * Grid context data is stored on the whole grid.
     */
    GRID
}
