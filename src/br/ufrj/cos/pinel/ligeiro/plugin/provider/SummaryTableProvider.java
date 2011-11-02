package br.ufrj.cos.pinel.ligeiro.plugin.provider;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.cos.pinel.ligeiro.plugin.data.SummaryElement;

/**
 * @author Roque Pinel
 *
 */
public class SummaryTableProvider
{
	private List<SummaryElement> elements;

	/**
	 * Default constructor.
	 */
	public SummaryTableProvider()
	{
		elements = new ArrayList<SummaryElement>();
	}

	/**
	 * @return the elements
	 */
	public List<SummaryElement> getElements()
	{
		return elements;
	}

	/**
	 * @param element the element to be added
	 */
	public void addElement(SummaryElement result)
	{
		elements.add(result);
	}

	/**
	 * Clear the results.
	 */
	public void clear()
	{
		elements.clear();
	}
}
