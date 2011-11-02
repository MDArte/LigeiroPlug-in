package br.ufrj.cos.pinel.ligeiro.plugin.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Roque Pinel
 *
 */
public class SummaryElement
{
	private String type;

	private int total;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * Default contructor.
	 */
	public SummaryElement()
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
		propertyChangeSupport.firePropertyChange("type", this.type, this.type = type);
	}

	/**
	 * @return the total
	 */
	public int getTotal()
	{
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(int total)
	{
		propertyChangeSupport.firePropertyChange("total", this.total, this.total = total);
	}
}
