package br.ufrj.cos.pinel.ligeiro.plugin.common;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import br.ufrj.cos.pinel.ligeiro.plugin.comparator.ITableComparator;
import br.ufrj.cos.pinel.ligeiro.plugin.data.InputFile;
import br.ufrj.cos.pinel.ligeiro.plugin.views.Messages;

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

		if (path != null  && Util.containsInputFile(table, inputFile) == NOT_FOUND)
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

	public static TableViewerColumn createTableViewerColumn(final TableViewer viewer, final ITableComparator comparator, String title, int width, final int colNumber)
	{
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(width);
		column.setResizable(true);
		column.setMoveable(true);

		column.addSelectionListener(
			new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent event)
				{
					comparator.setColumn(colNumber);
					int dir = comparator.getDirection();
					viewer.getTable().setSortDirection(dir);
					viewer.refresh();
				}
			}
		);

		return viewerColumn;
	}

	public static String getInputFileTableCSV(Table table)
	{
		StringBuilder sb = new StringBuilder();

		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++)
		{
			InputFile inputFile = (InputFile) items[i].getData();

			sb.append(inputFile.getPath());

			if (i < items.length - 1)
			{
				sb.append(Constants.CSV_DELIMITER);
			}
		}

		return sb.toString();
	}

	public static void appendMessage(StringBuilder sbMessage, String message)
	{
		if (sbMessage.length() > 0)
			sbMessage.append('\n');
		sbMessage.append(message);
	}

	public static String getLoadReportType(String type, boolean plural)
	{
		String mType = null;

		if (type.equals(br.ufrj.cos.pinel.ligeiro.common.Constants.XML_CLASS))
		{
			if (plural)
				mType = Messages.getString("LigeiroView.xml.type.class.plural");
			else
				mType = Messages.getString("LigeiroView.xml.type.class");
		}
		else if (type.equals(br.ufrj.cos.pinel.ligeiro.common.Constants.XML_DEPENDENCY))
		{
			if (plural)
				mType = Messages.getString("LigeiroView.xml.type.dependency.plural");
			else
				mType = Messages.getString("LigeiroView.xml.type.dependency");
		}
		else if (type.equals(br.ufrj.cos.pinel.ligeiro.common.Constants.XML_ENTITY))
		{
			if (plural)
				mType = Messages.getString("LigeiroView.xml.type.entity.plural");
			else
				mType = Messages.getString("LigeiroView.xml.type.entity");
		}
		else if (type.equals(br.ufrj.cos.pinel.ligeiro.common.Constants.XML_SERVICE))
		{
			if (plural)
				mType = Messages.getString("LigeiroView.xml.type.service.plural");
			else
				mType = Messages.getString("LigeiroView.xml.type.service");
		}
		else if (type.equals(br.ufrj.cos.pinel.ligeiro.common.Constants.XML_USE_CASE))
		{
			if (plural)
				mType = Messages.getString("LigeiroView.xml.type.use.case.plural");
			else
				mType = Messages.getString("LigeiroView.xml.type.use.case");
		}

		return mType;
	}
}
