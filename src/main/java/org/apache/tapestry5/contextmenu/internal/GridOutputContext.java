package org.apache.tapestry5.contextmenu.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GridOutputContext
{

    private Map<Object, List<GridCellOutputContext>> rowValueToCellContext;

    public GridOutputContext()
    {
        rowValueToCellContext = new LinkedHashMap<Object, List<GridCellOutputContext>>();
    }

    public void add(GridCellOutputContext gridCellOutputContext)
    {
        Object row = gridCellOutputContext.getObjectValue();

        if (!rowValueToCellContext.containsKey(row))
            rowValueToCellContext.put(
                    gridCellOutputContext.getObjectValue(), new ArrayList<GridCellOutputContext>());

        rowValueToCellContext.get(row).add(gridCellOutputContext);
    }

    public Iterable<Object> rows()
    {
        return rowValueToCellContext.keySet();
    }

    public Iterable<GridCellOutputContext> properties(Object row)
    {
        return rowValueToCellContext.get(row);
    }
}
