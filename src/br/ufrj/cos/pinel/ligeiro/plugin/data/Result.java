package br.ufrj.cos.pinel.ligeiro.plugin.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Roque Pinel
 *
 */
public class Result
{
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
		propertyChangeSupport.firePropertyChange("element", this.element, this.element = element);
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
		propertyChangeSupport.firePropertyChange("det", this.det, this.det = det);
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
		propertyChangeSupport.firePropertyChange("ret_ftr", this.ret_ftr, this.ret_ftr = ret_ftr);
	}

	/**
	 * @return the complexity
	 */
	public String getComplexity()
	{
		return complexity;
	}

	/**
	 * @param complexity the complexity to set
	 */
	public void setComplexity(String complexity)
	{
		propertyChangeSupport.firePropertyChange("complexity", this.complexity, this.complexity = complexity);
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
		propertyChangeSupport.firePropertyChange("complexityValue", this.complexityValue, this.complexityValue = complexityValue);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return element;
	}
}
