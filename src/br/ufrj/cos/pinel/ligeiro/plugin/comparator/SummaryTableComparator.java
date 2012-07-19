package br.ufrj.cos.pinel.ligeiro.plugin.comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import br.ufrj.cos.pinel.ligeiro.plugin.data.SummaryElement;

/**
 * @author Roque Pinel
 *
 */
public class SummaryTableComparator extends ViewerComparator implements ITableComparator
{
	private int propertyIndex;

	private boolean descending;

	public SummaryTableComparator()
	{
		propertyIndex = 0;
		descending = false;
	}

	public int getDirection()
	{
		return descending ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column)
	{
		if (column == propertyIndex)
		{
			// same column as last sort; toggle the direction
			descending = !descending;
		}
		else
		{
			// new column; do an ascending sort
			propertyIndex = column;
			descending = true;
		}
	}

	@Override
	public int compare(Viewer viewer, Object o1, Object o2)
	{
		SummaryElement element1 = (SummaryElement) o1;
		SummaryElement element2 = (SummaryElement) o2;

		int rc = 0;
		switch (propertyIndex)
		{
			case 0:
				rc = element1.getType().compareTo(element2.getType());
				break;
			case 1:
				rc = element1.getTotal() - element2.getTotal();
				break;
			default:
				rc = 0;
		}

		// if descending order, flip the direction
		if (descending)
		{
			rc = -rc;
		}

		return rc;
	}
}
