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

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.Renderable;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.contextmenu.GridContextLevel;
import org.apache.tapestry5.contextmenu.base.ContextMenuBase;
import org.apache.tapestry5.corelib.components.ProgressiveDisplay;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
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
 * @see ContextMenu
 * @see org.apache.tapestry5.contextmenu.ContextMenuClientEvent
 * @see org.apache.tapestry5.contextmenu.ContextMenuHideType
 * @see GridContextLevel
 * @see ContextMenuAjax
 * @since 5.3
 * @tapestrydoc
 */
public class ContextMenuAjax extends ContextMenuBase
{

    /**
     * The initial content to display until the actual content arrives. Defaults to "Loading ..." and an Ajax activity
     * icon, controlled by the css class t-loading which is also used in {@link ProgressiveDisplay}.
     */
    @Parameter(defaultPrefix = BindingConstants.LITERAL, value = "prop:initial")
    private Block initial;

    /**
     * Name of a function on the client-side Tapestry.ElementEffect object that is invoked after the elements's body
     * content has been updated. If not specified, then the basic "highlight" method is used, which performs a classic
     * "yellow fade" to indicate to the user that and update has taken place.
     */
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String update;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @Inject
    private ComponentResources resources;

    public Renderable getInitial()
    {
        return new Renderable()
        {
            public void render(MarkupWriter writer)
            {
                // The div containing the actual contextmenu
                writer.element("div").addClassName("t-loading");
                writer.write(resources.getMessages().get("context-menu-loading"));
                writer.end();
            }
        };
    }

    @Override
    protected RenderCommand renderMenu(final JSONObject spec, final String contextMenuId, final Object[] context)
    {
        /**
         * Link used to trigger ajax update on the zone surrounding the context menu. The event is caught in this mixin.
         */
        Link link = resources.createEventLink("showMenu", context);

        final String zoneId = "zone-" + contextMenuId;
        // spec for the Tapestry.ZoneManager(spec);
        JSONObject zoneManagerSpec = new JSONObject("element", zoneId, "url", link.toURI());

        if (InternalUtils.isNonBlank(update))
        {
            zoneManagerSpec.put("update", update.toLowerCase());
        }

        spec.put("zoneManagerSpec", zoneManagerSpec);

        javaScriptSupport.addInitializerCall("contextMenu", spec);

        return new RenderCommand()
        {
            public void render(MarkupWriter writer, RenderQueue queue)
            {
                queue.push(new RenderCommand()
                {
                    public void render(MarkupWriter writer, RenderQueue queue)
                    {
                        writer.end(); // end zone

                        writer.end(); // end contextmenu
                    }
                });

                queue.push((RenderCommand) initial);

                queue.push(new RenderCommand()
                {
                    public void render(MarkupWriter writer, RenderQueue queue)
                    {
                        /**
                         * Rendering a zone inside the context menu. We don't use the zone as the context menu because
                         * if we are trying to hide the ContextMenu (example onMouseout), and if the zone is not yet
                         * updated we won't be able to update the zone's style.display because of the zone update, so
                         * the zone will stay visible when it's not supposed to.
                         */

                        // the context menu
                        writer.element("div", "id", contextMenuId, "style", "display: none; position: absolute;")
                                .addClassName(T_CONTEXTMENU);

                        // the zone inside the context menu
                        final String zoneDiv = resources.getElementName("div");
                        Element e = writer.element(zoneDiv, "id", zoneId);
                        resources.renderInformalParameters(writer);
                        e.addClassName("t-zone");
                    }
                });
            }
        };

    }

    /**
     * Ajax event that shows the context menu block, triggered on the chosen client event.
     * 
     * @param context
     *            the context
     * @return the context menu block;
     */
    Block onShowMenu(EventContext context)
    {
        triggerEvent(context);

        return getContextMenuBlock();
    }
}
