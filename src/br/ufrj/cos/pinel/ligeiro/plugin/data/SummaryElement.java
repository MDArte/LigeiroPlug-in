package br.ufrj.cos.pinel.ligeiro.plugin.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Roque Pinel
 *
 */
public class SummaryElement
{
	public static int CLASS = 0;
	public static int ENTITY = 1;
	public static int SERVICE = 2;
	public static int USE_CASE = 3;
	public static int DEPENDENCY = 4;

	private int internalType;

	private String type;

	private int total;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * Default contructor.
	 */
	public SummaryElement(int internalType)
	{
		this.internalType = internalType;
		this.total = 0;
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
	 * @return the internalType
	 */
	public int getInternalType()
	{
		return internalType;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type)
	{
		propertyChangeSupport.firePropertyChange("type", this.type, this.type = type); //$NON-NLS-1$
	}

	/**
	 * @return the total
	 */
	public int getTotal()
	{
		return total;
	}

	/**
	 * @param value the value to increase
	 */
	public void addTotal(int value)
	{
		propertyChangeSupport.firePropertyChange("total", this.total, this.total = this.total + value); //$NON-NLS-1$
	}
}
