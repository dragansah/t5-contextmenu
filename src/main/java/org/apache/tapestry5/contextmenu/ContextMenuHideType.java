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
 * The client event on which the ContextMenu is hidden. The enum's values correspond to DOM events.
 * <p>
 * These events need to be stored in an enum, and not passed as pure strings to the ContextMenu mixins because each
 * event causes a little extra work except assigning the corresponding event handler on the context menu DOM object, for
 * example the hide event of the context menu.
 * 
 * @see {@link ContextMenuClientEvent}
 * @see {@link ContextMenu}
 * @see {@link ContextMenuAjax}
 * @since 5.3
 * @tapestrydoc
 */
public enum ContextMenuHideType
{
    /**
     * The context menu is hidden on the mousedown event. This is the default context menu behavior.
     */
    MOUSEDOWN,

    /**
     * The context menu is hidden on the mousedown event.
     */
    MOUSEOUT
}
