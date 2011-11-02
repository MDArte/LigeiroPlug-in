package br.ufrj.cos.pinel.ligeiro.plugin.comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import br.ufrj.cos.pinel.ligeiro.plugin.data.Result;

/**
 * @author Roque Pinel
 *
 */
public class ResultTableComparator extends ViewerComparator
{
	private int propertyIndex;

	private boolean descending;

	public ResultTableComparator()
	{
		propertyIndex = 0;
		descending = true;
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
		Result result1 = (Result) o1;
		Result result2 = (Result) o2;

		int rc = 0;
		switch (propertyIndex)
		{
			case 0:
				rc = result1.getElement().compareTo(result2.getElement());
				break;
			case 1:
				rc = result1.getType().compareTo(result2.getType());
				break;
			case 2:
				rc = result1.getRet_ftr() - result2.getRet_ftr();
				break;
			case 3:
				rc = result1.getDet() - result2.getDet();
				break;
			case 4:
				rc = result1.getComplexity().compareTo(result2.getComplexity());
				break;
			case 5:
				rc = result1.getComplexityValue() - result2.getComplexityValue();
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
