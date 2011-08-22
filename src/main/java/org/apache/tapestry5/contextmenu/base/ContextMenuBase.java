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

package org.apache.tapestry5.contextmenu.base;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.contextmenu.ContextMenuClientEvent;
import org.apache.tapestry5.contextmenu.ContextMenuHideType;
import org.apache.tapestry5.contextmenu.EventConstants;
import org.apache.tapestry5.contextmenu.GridContextLevel;
import org.apache.tapestry5.contextmenu.internal.GridCellOutputContext;
import org.apache.tapestry5.contextmenu.internal.GridOutputContext;
import org.apache.tapestry5.contextmenu.mixins.ContextMenu;
import org.apache.tapestry5.contextmenu.mixins.ContextMenuAjax;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.runtime.RenderCommand;
import org.apache.tapestry5.runtime.RenderQueue;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for {@link ContextMenu} and {@link ContextMenuAjax}. Handles all the rendering scenarios given by
 * {@link ContextMenuBase#gridLevel}, {@link ContextMenuBase#clientEvent} and {@link ContextMenuBase#hideType}. The
 * actual rendering of the menu is left to the implementing classes using one abstract method
 * {@link ContextMenuBase#renderMenu}
 * 
 * @see ContextMenu
 * @see ContextMenuAjax

 * @tapestrydoc
 */
@Import(library = "contextmenu.js", stylesheet="contextmenu.css")
@Events(EventConstants.CONTEXTMENU)
public abstract class ContextMenuBase
{
    protected static final String T_CONTEXTMENU = "t-contextmenu";

    /**
     * A block providing the content for the context menu.
     */
    @Parameter(name = "contextmenu", defaultPrefix = BindingConstants.BLOCK)
    private Block contextMenuBlock;

    protected Block getContextMenuBlock()
    {
        return contextMenuBlock;
    }

    /**
     * If provided, this is the event context, which will be provided via the
     * {@link org.apache.tapestry5.contextmenu.EventConstants#CONTEXTMENU event}.
     */
    @Parameter
    private Object[] context;

    /**
     * The level at which the context menu is applied to a {@link Grid}. The default value is
     * {@link ContextMenuGridLevel#CELL}. This parameter is only used if the mixin is aplied by a {@link Grid}.
     */
    @Parameter(value = "CELL", defaultPrefix = BindingConstants.LITERAL)
    private GridContextLevel gridLevel;

    /**
     * The javascript client event that triggers the context menu
     */
    @Parameter(value = "CONTEXT", defaultPrefix = BindingConstants.LITERAL)
    private ContextMenuClientEvent clientEvent;

    /**
     * The hide type for the context menu
     */
    @Parameter(value = "MOUSEDOWN", defaultPrefix = BindingConstants.LITERAL)
    private ContextMenuHideType hideType;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @Inject
    private Environment environment;

    @InjectContainer
    private Object container;

    @Inject
    private ComponentResources resources;

    private boolean isGrid;

    Object afterRender(MarkupWriter writer)
    {
        return render();
    }

    RenderCommand render()
    {
        final String containerId = containerElementId();

        isGrid = environment.peek(GridOutputContext.class) != null;

        /**
         * Render either a regular context menu or {@link GridContextMenuLevel.GRID} level context menu if the context
         * menu is applied to a {@link Grid}.
         */
        if (!isGrid || gridLevel == GridContextLevel.GRID)
        {
            final String contextMenuId = javaScriptSupport.allocateClientId(resources);
            if (environment.peek(GridOutputContext.class) != null) environment.pop(GridOutputContext.class);

            return renderMenu(containerId, contextMenuId, context);
        }

        /**
         * Render a {@link GridContextMenuLevel.CELL} level context menu on a {@link Grid}.
         */
        GridOutputContext gridOutputContext = environment.pop(GridOutputContext.class);
        if (gridLevel == GridContextLevel.CELL)
        {
            final List<RenderCommand> renderCommands = new ArrayList<RenderCommand>();
            int i = 0;
            for (Object row : gridOutputContext.rows())
            {
                for (GridCellOutputContext gridCellContext : gridOutputContext.properties(row))
                {
                    final String contextMenuId = javaScriptSupport.allocateClientId(resources);

                    renderCommands
                            .add(renderMenuForGridCell(containerId, contextMenuId, i++, context, gridCellContext));
                }
            }

            // render all
            return new RenderCommand()
            {
                public void render(MarkupWriter writer, RenderQueue queue)
                {
                    for (int i = renderCommands.size() - 1; i >= 0; i--)
                        queue.push(renderCommands.get(i));
                }
            };
        }

        /**
         * Render a {@link GridContextMenuLevel.ROW} level context menu on a {@link Grid}.
         */
        if (gridLevel == GridContextLevel.ROW)
        {
            int i = 0;
            final List<RenderCommand> renderCommands = new ArrayList<RenderCommand>();
            for (Object row : gridOutputContext.rows())
            {
                final String contextMenuId = javaScriptSupport.allocateClientId(resources);

                renderCommands.add(renderMenuForGridRow(containerId, contextMenuId, i++, context, row));
            }

            // render all
            return new RenderCommand()
            {
                public void render(MarkupWriter writer, RenderQueue queue)
                {
                    for (int i = renderCommands.size() - 1; i >= 0; i--)
                        queue.push(renderCommands.get(i));
                }
            };
        }

        throw new IllegalStateException("Should not be here");
    }

    /**
     * Returns an id of the context menu element.
     * <p/>
     * If the containing component is a {@link ClientElement} than return the clientId, thus forcing render of the
     * container's id. If, not than return null, which means that the menu element will be taken to be the previous
     * sibling of the contextMenu element (see contextmenu.js).
     * <p/>
     * This can result in undesirable effects if the containing component renders several elements with no root element
     * like the TextField for example (rendering a text field and a trailing icon), so in this case the context menu
     * would work on the icon and not on the text field.
     * 
     * @return the id of the menu element or null stating that the container is not an instance of {@link ClientElement}
     */
    private String containerElementId()
    {
        /**
         * So much we can do if don't now the id of the parent component Let's hope the component is just one HTML
         * element. Example for breaking this is {@link TextField} which renders a trailing icon, so the textField will
         * not get the contextMenu. But the {@link TextField} is a {@link ClientElement} so it is covered by the
         * previous case.
         */
        if (container instanceof ClientElement) return String.format("%s", ((ClientElement) container).getClientId());

        return null;
    }

    /**
     * Triggers a {@link EventConstants.CONTEXTMENU} event
     * 
     * @param context
     */
    protected void triggerEvent(EventContext context)
    {
        resources.triggerContextEvent(EventConstants.CONTEXTMENU, context, null);
    }

    /**
     * Triggers a {@link EventConstants.CONTEXTMENU} event
     * 
     * @param context
     */
    protected void triggerEvent(Object[] context)
    {
        resources.triggerEvent(EventConstants.CONTEXTMENU, context, null);
    }

    /**
     * Should be used by inheriting classes
     */
    protected ContextMenuClientEvent getClientEvent()
    {
        return clientEvent;
    }

    /**
     * Should be used by inheriting classes
     */
    protected ContextMenuHideType getHideType()
    {
        return hideType;
    }

    /**
     * Renders the actual context menu. Should be overridden by inheriting classes.
     * 
     * @param spec
     *            the json spec that should be sent to the contextmenu.js t5 initializer
     * @param contextMenuId
     *            id of the context menu element
     * @param context
     *            the menu context
     * @return a RenderCommand that renders the context menu
     */
    protected abstract RenderCommand renderMenu(JSONObject spec, String contextMenuId, Object[] context);

    protected RenderCommand renderMenu(String elementId, String contextMenuId, Object[] context)
    {
        return renderMenu(getSpec(elementId, contextMenuId, null, null), contextMenuId, context);
    }

    protected RenderCommand renderMenuForGridRow(String elementId, String contextMenuId, Integer gridRowIndex,
            Object[] context, Object objectValue)
    {
        return renderMenu(
                getSpec(elementId, contextMenuId, gridRowIndex, null), contextMenuId, mergeContexts(new Object[]
                { objectValue }, context));
    }

    protected RenderCommand renderMenuForGridCell(String elementId, String contextMenuId, Integer gridCellIndex,
            Object[] context, GridCellOutputContext cellContext)
    {
        return renderMenu(
                getSpec(elementId, contextMenuId, null, gridCellIndex),
                contextMenuId,
                mergeContexts(
                        new Object[]
                        { cellContext.getObjectValue(), cellContext.getPropertyName(), cellContext.getPropertyValue() },
                        context));
    }

    protected JSONObject getSpec(String elementId, String contextMenuId, Integer gridRowIndex, Integer gridCellIndex)
    {
        return new JSONObject("elementId", elementId,

        "contextMenuId", contextMenuId,

        "gridCellIndex", gridCellIndex == null ? null : String.valueOf(gridCellIndex),

        "gridRowIndex", gridRowIndex == null ? null : String.valueOf(gridRowIndex),

        "clientEvent", String.valueOf(getClientEvent().ordinal()),

        "hideType", String.valueOf(getHideType().ordinal()));
    }

    private Object[] mergeContexts(Object[] context1, Object[] context2)
    {
        if (context1 == null && context2 == null) return new Object[0];
        if (context1 == null) return context2;
        if (context2 == null) return context1;

        Object[] context = new Object[context1.length + context2.length];
        int i = 0;
        for (Object o : context1)
            context[i++] = o;
        i = 0;
        for (Object o : context2)
            context[i++] = o;

        return context;
    }
}
