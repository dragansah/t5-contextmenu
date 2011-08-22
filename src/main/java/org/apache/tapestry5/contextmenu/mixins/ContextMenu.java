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

package org.apache.tapestry5.contextmenu.mixins;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.contextmenu.GridContextLevel;
import org.apache.tapestry5.contextmenu.base.ContextMenuBase;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.runtime.RenderCommand;
import org.apache.tapestry5.runtime.RenderQueue;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * Context menu mixin that adds context menu behavior on the component it is applied on. The mixin renders a div
 * containing a block passed with the {@link ContextMenuBase#contextMenu} parameter, which fires an
 * {@link EventConstants#CONTEXTMENU} event before it renders the block. In the non ajax version of the mixin the event
 * is fired during rendering of the Context Menu element. In the ajax version of the mixin the event is fired after an
 * ajax call triggered by the corresponding DOM event (set with the {@link ContextMenuBase#clientEvent} parameter). In
 * the Ajax version all the context parameters are encoded using a {@link ValueEncoder}.
 * <p>
 * This mixin has special behavior when used with the {@link org.apache.tapestry5.corelib.components.Grid} component and
 * can be configured to be used in 3 levels configured with {@link ContextMenuBase#menuLevel}.
 * <p>
 * <ul>
 * <li>{@link GridContextLevel#CELL}: A context menu DOM element is build for each cell in the grid, which is the
 * default behavior. The {@link EventConstants#CONTEXTMENU} event context has the following structure:
 * <ol>
 * <li>Object object: the current object being rendered (for the current row).</li>
 * <li>String propertyName: the name of the property rendered in the grid cell.</li>
 * <li>Object propertyValue: the value of the property rendered in the grid cell.</li>
 * <li>Object... context: the {@link ContextMenuBase#context} parameter of the mixin.</li>
 * </ol>
 * <p>
 * The objectValue and the propertyValue can be encoded back to a concrete type because they are encoded to and decoded
 * back the client via a corresponding {@link ValueEncoder}.
 * <p>
 * Usage example:
 * <code>void onContextMenu(Object object, String propertyName, Object propertyValue, Object... context)</code></li> <br>
 * <li>{@link GridContextLevel#ROW}: A context menu DOM element is build for each row in the grid. The
 * {@link EventConstants#CONTEXTMENU} event context has the following structure:
 * <ol>
 * <li>Object object: the current object being rendered (for the current row).</li>
 * <li>Object... context: the {@link ContextMenuBase#context} parameter of the mixin.</li>
 * </ol>
 * <p>
 * The objectValue can be encoded back to a concrete type because it is encoded to and decoded back the client via a
 * corresponding {@link ValueEncoder}.
 * <p>
 * Usage example: <code>void onContextMenu(Object object, Object... context)</code></li>
 * <li>{@link GridContextLevel#GRID}: Only one context menu DOM element is build for the entire grid.
 * <p>
 * Usage example: <code>void onContextMenu(Object... context)</code></li>
 * </ul>
 * 
 * @see ContextMenuAjax
 * @see org.apache.tapestry5.contextmenu.ContextMenuClientEvent
 * @see org.apache.tapestry5.contextmenu.ContextMenuHideType
 * @see GridContextLevel
 * @see ContextMenuAjax
 * @since 5.3
 * @tapestrydoc
 */
public class ContextMenu extends ContextMenuBase
{
    @Inject
    private JavaScriptSupport javaScriptSupport;

    @Override
    protected RenderCommand renderMenu(JSONObject spec, final String contextMenuId, final Object[] context)
    {
        javaScriptSupport.addInitializerCall("contextMenu", spec);

        return new RenderCommand()
        {
            public void render(MarkupWriter writer, RenderQueue queue)
            {
                queue.push(new RenderCommand()
                {
                    public void render(MarkupWriter writer, RenderQueue queue)
                    {
                        writer.end();
                    }
                });

                queue.push((RenderCommand) getContextMenuBlock());

                queue.push(new RenderCommand()
                {
                    public void render(MarkupWriter writer, RenderQueue queue)
                    {
                        triggerEvent(context);

                        writer.element("div", "id", contextMenuId, "style", "display: none; position: absolute;")
                                .addClassName(T_CONTEXTMENU);
                    }
                });
            }
        };
    }
}
