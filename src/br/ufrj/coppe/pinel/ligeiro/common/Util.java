package br.ufrj.coppe.pinel.ligeiro.common;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import br.ufrj.coppe.pinel.ligeiro.data.InputFile;

/**
 * @author Roque Pinel
 *
 */
public class Util
{
	public static int NOT_FOUND = -1;

	public static boolean addInputFile(Table table, String path)
	{
		InputFile inputFile = new InputFile(path);

		if (Util.containsInputFile(table, inputFile) == NOT_FOUND)
		{
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(inputFile.toString());
			item.setData(inputFile);
			return true;
		}

		return false;
	}

	public static boolean removeInputFiles(Table table, TableItem[] items)
	{
		boolean anyRemoved = false;

		for (TableItem item : items)
		{
			int i = containsInputFile(table, item.getData());

			if (i != NOT_FOUND)
			{
				table.remove(i);
				anyRemoved = true;
			}
		}

		return anyRemoved;
	}

	public static int containsInputFile(Table table, Object object)
	{
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++)
		{
			if (items[i].getData().equals(object))
			{
				return i;
			}
		}

		return NOT_FOUND;
	}

	public static TableViewerColumn createTableViewerColumn(final TableViewer viewer, String title)
	{
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		// trying to keep the column with a good width
		column.setWidth(title.length() * 6 + 50);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}
}
