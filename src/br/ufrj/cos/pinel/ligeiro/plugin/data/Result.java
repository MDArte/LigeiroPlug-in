package br.ufrj.cos.pinel.ligeiro.plugin.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import br.ufrj.cos.pinel.ligeiro.plugin.messages.Messages;

/**
 * @author Roque Pinel
 *
 */
public class Result implements IPropertySource
{
	private static final String NAMESPACE_ID = "Result.namespace"; //$NON-NLS-1$
	private static final String ELEMENT_ID = "Result.element"; //$NON-NLS-1$
	private static final String TYPE_ID = "Result.type"; //$NON-NLS-1$
	private static final String RET_FTR_ID = "Result.ret_ftr"; //$NON-NLS-1$
	private static final String DET_ID = "Result.det"; //$NON-NLS-1$
	private static final String COMPLEXITY_ID = "Result.complexity"; //$NON-NLS-1$
	private static final String COMPLEXITY_VALUE_ID = "Result.complexityValue"; //$NON-NLS-1$
	private static final IPropertyDescriptor[] DESCRIPTORS =
		{
			new PropertyDescriptor(NAMESPACE_ID, Messages.LigeiroView_results_table_namespace),
			new PropertyDescriptor(ELEMENT_ID, Messages.LigeiroView_results_table_element),
			new PropertyDescriptor(TYPE_ID, Messages.LigeiroView_results_table_type),
			new PropertyDescriptor(RET_FTR_ID, Messages.LigeiroView_results_table_ret_ftr),
			new PropertyDescriptor(DET_ID, Messages.LigeiroView_results_table_det),
			new PropertyDescriptor(COMPLEXITY_ID, Messages.LigeiroView_results_table_complexity),
			new PropertyDescriptor(COMPLEXITY_VALUE_ID, Messages.LigeiroView_results_table_complexity_value),
		};

	private String namespace;

	private String element;

	private String type;

	private int det;

	private int ret_ftr;

	private String complexity;

	private int complexityValue;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * Default contructor.
	 */
	public Result()
	{
		// empty
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace()
	{
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace)
	{
		propertyChangeSupport.firePropertyChange("namespace", this.namespace, this.namespace = namespace); //$NON-NLS-1$
	}

	/**
	 * @return the element
	 */
	public String getElement()
	{
		return element;
	}

	/**
	 * @param element the element to set
	 */
	public void setElement(String element)
	{
		propertyChangeSupport.firePropertyChange("element", this.element, this.element = element); //$NON-NLS-1$
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		// may not always be setted
		return type != null ? type : ""; //$NON-NLS-1$
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type)
	{
		propertyChangeSupport.firePropertyChange("type", this.type, this.type = type); //$NON-NLS-1$
	}

	/**
	 * @return the det
	 */
	public int getDet()
	{
		return det;
	}

	/**
	 * @param det the det to set
	 */
	public void setDet(int det)
	{
		propertyChangeSupport.firePropertyChange("det", this.det, this.det = det); //$NON-NLS-1$
	}

	/**
	 * @return the ret_ftr
	 */
	public int getRet_ftr()
	{
		return ret_ftr;
	}

	/**
	 * @param ret_ftr the ret_ftr to set
	 */
	public void setRet_ftr(int ret_ftr)
	{
		propertyChangeSupport.firePropertyChange("ret_ftr", this.ret_ftr, this.ret_ftr = ret_ftr); //$NON-NLS-1$
	}

	/**
	 * @return the complexity
	 */
	public String getComplexity()
	{
		// may not always be setted
		return complexity != null ? complexity : ""; //$NON-NLS-1$
	}

	/**
	 * @param complexity the complexity to set
	 */
	public void setComplexity(String complexity)
	{
		propertyChangeSupport.firePropertyChange("complexity", this.complexity, this.complexity = complexity); //$NON-NLS-1$
	}

	/**
	 * @return the complexityValue
	 */
	public int getComplexityValue()
	{
		return complexityValue;
	}

	/**
	 * @param complexityValue the complexityValue to set
	 */
	public void setComplexityValue(int complexityValue)
	{
		propertyChangeSupport.firePropertyChange("complexityValue", this.complexityValue, this.complexityValue = complexityValue); //$NON-NLS-1$
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return element;
	}
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue()
	{
		return null;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		return DESCRIPTORS;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id)
	{
		try
		{
			if(NAMESPACE_ID.equals(id))
			{
				return getNamespace();
			}
			else if(ELEMENT_ID.equals(id))
			{
				return getElement();
			}
			else if(TYPE_ID.equals(id))
			{
				return getType();
			}
			else if(RET_FTR_ID.equals(id))
			{
				return getRet_ftr();
			}
			else if(DET_ID.equals(id))
			{
				return getDet();
			}
			else if(COMPLEXITY_ID.equals(id))
			{
				return getComplexity();
			}
			else if(COMPLEXITY_VALUE_ID.equals(id))
			{
				return getComplexityValue();
			}
		}
		catch(Exception e)
		{
			// ignoring
		}

		return null;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id)
	{
		return false;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id)
	{
		// empty
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value)
	{
		// empty
	}
}
