package br.ufrj.coppe.pinel.ligeiro;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.dialogs.ViewContentProvider;

/**
 * @author Roque Pinel
 *
 */
public class TestLigeiroLayout
{
	private Display display = new Display();
	private Shell parent = new Shell(display);

	public TestLigeiroLayout()
	{
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);

		Label viewLabel = new Label(parent, SWT.NONE);
		viewLabel.setText("Ligeiro Plug-in");
		Label emptyLabel = new Label(parent, SWT.NONE);
		emptyLabel.setText("");

		Label configurationLabel = new Label(parent, SWT.NONE);
		configurationLabel.setText("Configuration: ");
		configurationLabel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		parent.pack();
		parent.open();

		// Set up the event loop.
		while (!parent.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				// If no more entries in event queue
				display.sleep();
			}
		}

		display.dispose();
	}

	private void createViewer(Shell parent)
	{
//		TableViewer viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
//		viewer.setContentProvider(new ViewContentProvider());
//		viewer.setInput(getViewSite());
//
//		//TableViewer viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
//
//		//createColumns(parent, viewer);
//		final Table table = viewer.getTable();
//		table.setHeaderVisible(true);
//		table.setLinesVisible(true);
//
//		viewer.setContentProvider(new ArrayContentProvider());
//		// Get the content for the viewer, setInput will call getElements in the
//		// contentProvider
//		viewer.setInput(ModelProvider.INSTANCE.getPersons());
//		// Make the selection available to other views
//		getSite().setSelectionProvider(viewer);
//		// Set the sorter for the table
//
//		// Layout the viewer
//		GridData gridData = new GridData();
//		gridData.verticalAlignment = GridData.FILL;
//		gridData.horizontalSpan = 2;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.grabExcessVerticalSpace = true;
//		gridData.horizontalAlignment = GridData.FILL;
//		viewer.getControl().setLayoutData(gridData);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		new TestLigeiroLayout();
	}
}
