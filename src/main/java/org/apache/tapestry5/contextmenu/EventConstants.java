package org.apache.tapestry5.contextmenu;

public class EventConstants
{
    /**
     * Event triggered by the {@link org.apache.tapestry5.contextmenu.components.ContextMenu}
     * component to inform its container of what context (if any) is available. It is not expected
     * for the event handler to have a return value.
     * 
     * @see org.apache.tapestry5.contextmenu.mixins.ContextMenu
     * @see org.apache.tapestry5.contextmenu.mixins.ContextMenuAjax
     */
    public static final String CONTEXTMENU = "contextMenu";
}
