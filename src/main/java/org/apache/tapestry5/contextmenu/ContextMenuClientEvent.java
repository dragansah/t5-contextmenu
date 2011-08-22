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
 * The client event on which the ContextMenu is triggered (shown). The enum's values correspond to DOM events.
 * <p>
 * These events need to be stored in an enum, and not passed as a string parameters to the ContextMenu mixins because
 * each event causes a little extra work in javascript, that includes not only assigning the corresponding event handler
 * on the context menu DOM function, but also for example assigning a corresponding hide event for the context menu DOM
 * element.
 * 
 * @see {@link ContextMenuHideType}
 * @see {@link ContextMenu}
 * @see {@link ContextMenuAjax}
 * @since 5.3
 * @tapestrydoc
 */
public enum ContextMenuClientEvent
{
    /**
     * Corresponds to contextmenu DOM event. This is the default context menu behavior.
     */
    CONTEXT,

    /**
     * Corresponds to the mousedown DOM event.
     */
    MOUSEDOWN,

    /**
     * Corresponds to the mouseover DOM event
     */
    MOUSEOVER,

    /**
     * Corresponds to the mousemove DOM event.
     */
    MOUSEMOVE
}
