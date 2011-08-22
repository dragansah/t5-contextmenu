package org.apache.tapestry5.contextmenu.internal;

import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.corelib.base.AbstractPropertyOutput;
import org.apache.tapestry5.corelib.components.GridCell;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.FieldHandle;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodHandle;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticField;
import org.apache.tapestry5.plastic.PlasticMethod;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;

public class GridCellWorker implements ComponentClassTransformWorker2
{
    private Environment environment;

    public GridCellWorker(Environment environment)
    {
        this.environment = environment;
    }

    FieldHandle objectFieldHandle = null;

    MethodHandle getPropertyModelMethodHandle = null;

    public void transform(PlasticClass plasticClass, TransformationSupport support,
            MutableComponentModel model)
    {
        if (plasticClass.getClassName().equals(AbstractPropertyOutput.class.getName()))
        {
            for (PlasticField field : plasticClass.getAllFields())
            {
                if (field.getName().equals("object"))
                {
                    objectFieldHandle = field.getHandle();
                    break;
                }
            }

            for (PlasticMethod m : plasticClass.getMethods())
                if (m.getDescription().methodName.toLowerCase().equals("getpropertymodel"))
                {
                    getPropertyModelMethodHandle = m.getHandle();
                    break;
                }
        }

        if (plasticClass.getClassName().equals(GridCell.class.getName()))
        {
            PlasticMethod beginRender = null;
            for (PlasticMethod m : plasticClass.getMethods())
                if (m.getDescription().methodName.toLowerCase().equals("beginrender"))
                {
                    beginRender = m;
                    break;
                }

            beginRender.addAdvice(new MethodAdvice()
            {
                public void advise(MethodInvocation invocation)
                {
                    invocation.proceed();

                    GridOutputContext gridOutputContext = environment.peek(GridOutputContext.class);
                    if (gridOutputContext == null)
                    {
                        gridOutputContext = new GridOutputContext();
                        environment.push(GridOutputContext.class, gridOutputContext);
                    }
                    PropertyModel pm = (PropertyModel) getPropertyModelMethodHandle.invoke(
                            invocation.getInstance()).getReturnValue();

                    Object objectValue = objectFieldHandle.get(invocation.getInstance());

                    Object propertyValue = pm.getConduit() != null ? pm.getConduit().get(
                            objectValue) : null;

                    String propertyName = pm.getPropertyName();

                    gridOutputContext.add(new GridCellOutputContext(objectValue, propertyName,
                            propertyValue));
                }
            });
        }
    }
}
