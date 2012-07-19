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
	 * Clear the results.
	 */
	public void clear()
	{
		elements.clear();
	}

	public SummaryElement getSummaryClass()
	{
		return getSummaryFromType(SummaryElement.CLASS);
	}

	public SummaryElement getSummaryEntity()
	{
		return getSummaryFromType(SummaryElement.ENTITY);
	}

	public SummaryElement getSummaryService()
	{
		return getSummaryFromType(SummaryElement.SERVICE);
	}

	public SummaryElement getSummaryUseCase()
	{
		return getSummaryFromType(SummaryElement.USE_CASE);
	}

	public SummaryElement getSummaryDependency()
	{
		return getSummaryFromType(SummaryElement.DEPENDENCY);
	}

	private SummaryElement getSummaryFromType(int internalType)
	{
		SummaryElement summaryEntity = null;

		for (SummaryElement element : elements)
		{
			if (element.getInternalType() ==  internalType)
				summaryEntity = element;
		}

		if (summaryEntity == null)
		{
			summaryEntity = new SummaryElement(internalType);
			elements.add(summaryEntity);
		}

		return summaryEntity;
	}
}
