package org.apache.tapestry5.contextmenu.internal;

public class GridCellOutputContext
{
    private final Object objectValue;

    private final String propertyName;

    private final Object propertyValue;

    public GridCellOutputContext(Object objectValue, String propertyName, Object propertyValue)
    {
        super();
        this.objectValue = objectValue;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    public Object getObjectValue()
    {
        return objectValue;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public Object getPropertyValue()
    {
        return propertyValue;
    }
}
