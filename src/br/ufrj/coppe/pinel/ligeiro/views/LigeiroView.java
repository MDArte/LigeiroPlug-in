package br.ufrj.coppe.pinel.ligeiro.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import br.ufrj.coppe.pinel.ligeiro.data.Result;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class LigeiroView extends ViewPart
{
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.ufrj.coppe.pinel.ligeiro.views.LigeiroView";

	private TableViewer dfViewer;
	private TableViewer tfViewer;

	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	/**
	 * The constructor.
	 */
	public LigeiroView()
	{
		super();
	}

	private FormToolkit toolkit;
	private ScrolledForm form;

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText(Messages.getString("LigeiroView.title"));

//		GridData gd = new GridData();
//		gd.horizontalAlignment = GridData.HORIZONTAL_ALIGN_FILL;
//		gd.widthHint = 200;
//		gd.heightHint = 100;
//		form.setLayoutData(gd);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		form.getBody().setLayout(layout);

		createFilesSection(form.getBody());

		createControlSection(form.getBody());

		createResultSection(form.getBody());

//		// Create the help context id for the viewer's control
//		PlatformUI.getWorkbench().getHelpSystem()
//				.setHelp(viewer.getControl(), "LigeiroPlug-in.viewer");
//		makeActions();
//		hookContextMenu();
//		hookDoubleClickAction();
//		contributeToActionBars();

		form.reflow(true);
	}

	private void createFilesSection(Composite parent)
	{
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;

		Section filesSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		filesSection.setText(Messages.getString("LigeiroView.files.section.title"));
		filesSection.setLayoutData(gd);

		Composite filesComposite = toolkit.createComposite(filesSection, SWT.WRAP);
		filesSection.setClient(filesComposite);
		GridLayout layout = new GridLayout(2, true);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		filesComposite.setLayout(layout);

		// Statistic

		Composite statisticComposite = toolkit.createComposite(filesComposite, SWT.WRAP);
		layout = new GridLayout(2, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		statisticComposite.setLayout(layout);

		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = true;

		Label tableLabel = new Label(statisticComposite, SWT.NONE);
		tableLabel.setText(Messages.getString("LigeiroView.files.statistic.table.label"));
		tableLabel.setLayoutData(gd);

		Table table = toolkit.createTable(statisticComposite, SWT.NULL);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.widthHint = 300;
		table.setLayoutData(gd);
		toolkit.paintBordersFor(statisticComposite);

		Composite buttonComposite = toolkit.createComposite(statisticComposite, SWT.WRAP);
		layout = new GridLayout(1, true);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		buttonComposite.setLayout(layout);

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);

		// add
		Button button = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.statistic.add.button.label"), SWT.PUSH);
		button.setLayoutData(gd);
		button.addSelectionListener(
				new SelectionListener()
				{
					@Override
					public void widgetDefaultSelected(SelectionEvent event)
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void widgetSelected(SelectionEvent event)
					{
						// TODO Auto-generated method stub
					}
				}
		);

		// remove
		button = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.statistic.remove.button.label"), SWT.PUSH);
		button.setLayoutData(gd);
		button.addSelectionListener(
				new SelectionListener()
				{
					@Override
					public void widgetDefaultSelected(SelectionEvent event)
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void widgetSelected(SelectionEvent event)
					{
						// TODO Auto-generated method stub
					}
				}
		);

		// Dependency

		Composite dependencyComposite = toolkit.createComposite(filesComposite, SWT.WRAP);
		layout = new GridLayout(2, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		dependencyComposite.setLayout(layout);

		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = true;

		tableLabel = new Label(dependencyComposite, SWT.NONE);
		tableLabel.setText(Messages.getString("LigeiroView.files.dependency.table.label"));
		tableLabel.setLayoutData(gd);

		table = toolkit.createTable(dependencyComposite, SWT.NULL);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.widthHint = 300;
		table.setLayoutData(gd);
		toolkit.paintBordersFor(dependencyComposite);

		buttonComposite = toolkit.createComposite(dependencyComposite, SWT.WRAP);
		layout = new GridLayout(1, true);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		buttonComposite.setLayout(layout);

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);

		// add
		button = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.dependency.add.button.label"), SWT.PUSH);
		button.setLayoutData(gd);
		button.addSelectionListener(
				new SelectionListener()
				{
					@Override
					public void widgetDefaultSelected(SelectionEvent event)
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void widgetSelected(SelectionEvent event)
					{
						// TODO Auto-generated method stub
					}
				}
		);

		// remove
		button = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.dependency.remove.button.label"), SWT.PUSH);
		button.setLayoutData(gd);
		button.addSelectionListener(
				new SelectionListener()
				{
					@Override
					public void widgetDefaultSelected(SelectionEvent event)
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void widgetSelected(SelectionEvent event)
					{
						// TODO Auto-generated method stub
					}
				}
		);
	}

	private void createControlSection(Composite parent)
	{
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;

		Section controlSection = toolkit.createSection(parent, Section.TITLE_BAR);
		controlSection.setText(Messages.getString("LigeiroView.control.section.title"));
		controlSection.setLayoutData(gd);

		Composite controlComposite = toolkit.createComposite(controlSection, SWT.WRAP);
		controlSection.setClient(controlComposite);

		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		controlComposite.setLayout(layout);

		Label configurationFileLabel = new Label(controlComposite, SWT.NONE);
		configurationFileLabel.setText(Messages.getString("LigeiroView.control.configuration.file.label"));
		configurationFileLabel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));

		Text configurationFileText = new Text(controlComposite, SWT.BORDER);
		configurationFileText.setEditable(false);
		configurationFileText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		Button configurationFileButton = toolkit.createButton(controlComposite, Messages.getString("LigeiroView.control.configuration.file.button.label"), SWT.PUSH);
		configurationFileButton.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
		configurationFileButton.addSelectionListener(
				new SelectionListener()
				{
					@Override
					public void widgetDefaultSelected(SelectionEvent event)
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void widgetSelected(SelectionEvent event)
					{
						FileDialog fd = new FileDialog(form.getShell(), SWT.OPEN);
						fd.setText(Messages.getString("LigeiroView.control.configuration.file.dialog.title"));
						String[] filterExt = {"*.xml"};
						fd.setFilterExtensions(filterExt);
						String selected = fd.open();
						System.out.println(selected);
					}
				}
		);

		Button fpaButton = toolkit.createButton(controlComposite, Messages.getString("LigeiroView.control.fpa.button.label"), SWT.PUSH);
		fpaButton.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
		fpaButton.addSelectionListener(
				new SelectionListener()
				{
					@Override
					public void widgetDefaultSelected(SelectionEvent event)
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void widgetSelected(SelectionEvent event)
					{
						// TODO Auto-generated method stub
					}
				}
		);
	}

	private void createResultSection(Composite parent)
	{
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;

		Section resultSection = toolkit.createSection(parent, Section.TITLE_BAR);
		resultSection.setText(Messages.getString("LigeiroView.results.section.title"));
		resultSection.setLayoutData(gd);

		Composite resultComposite = toolkit.createComposite(resultSection, SWT.WRAP);
		resultSection.setClient(resultComposite);

		GridLayout layout = new GridLayout(2, true);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		resultComposite.setLayout(layout);

		// Data Function

		dfViewer = new TableViewer(resultComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		createColumns(resultComposite, dfViewer, true);

		Table table = dfViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 300;
		table.setLayoutData(gd);

		dfViewer.setContentProvider(new ArrayContentProvider());
		dfViewer.setInput(ModelProvider.INSTANCE.getResults());

		// Transaction Function

		tfViewer = new TableViewer(resultComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		createColumns(resultComposite, tfViewer, false);

		table = tfViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 300;
		table.setLayoutData(gd);

		tfViewer.setContentProvider(new ArrayContentProvider());
		tfViewer.setInput(ModelProvider.INSTANCE.getResults());

		//getSite().setSelectionProvider(dfViewer);
	}

	// This will create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer, final boolean isDataFunction)
	{
		String message;
		int[] bounds = {100, 100, 100, 100, 100};

		// First column is for the element

		if (isDataFunction)
			message = Messages.getString("LigeiroView.results.table.data.function");
		else
			message = Messages.getString("LigeiroView.results.table.transaction.function");

		TableViewerColumn col = createTableViewerColumn(viewer, message, bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return p.getElement();
			}
		});

		// Second column is for the RET/FRT

		if (isDataFunction)
			message = Messages.getString("LigeiroView.results.table.ret");
		else
			message = Messages.getString("LigeiroView.results.table.ftr");

		col = createTableViewerColumn(viewer, message, bounds[1], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return Integer.toString(p.getRet_ftr());
			}
		});

		// Third column is for the DET

		col = createTableViewerColumn(viewer, Messages.getString("LigeiroView.results.table.det"), bounds[2], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return Integer.toString(p.getDet());
			}
		});

		// Fourth column is for the complexity

		col = createTableViewerColumn(viewer, Messages.getString("LigeiroView.results.table.complexity"), bounds[3], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return p.getComplexity();
			}
		});

		// Fifth column is for the complexity value

		col = createTableViewerColumn(viewer, Messages.getString("LigeiroView.results.table.complexity.value"), bounds[4], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return Integer.toString(p.getComplexityValue());
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(final TableViewer viewer, String title, int bound, final int colNumber)
	{
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	private enum ModelProvider {
		INSTANCE;

		private List<Result> results;

		private ModelProvider() {
			results = new ArrayList<Result>();

			Result result = new Result();
			result.setElement("ReadStudent");
			result.setRet_ftr(1);
			result.setDet(2);
			result.setComplexity("Low");
			result.setComplexityValue(4);
			results.add(result);
		}

		public List<Result> getResults() {
			return results;
		}

	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				LigeiroView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(dfViewer.getControl());
		dfViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, dfViewer);
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions()
	{
		action1 = new Action()
		{
			public void run()
			{
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action()
		{
			public void run()
			{
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action()
		{
			public void run()
			{
				ISelection selection = dfViewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction()
	{
		dfViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message)
	{
		MessageDialog.openInformation(dfViewer.getControl().getShell(),
				"Ligeiro View", message);
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		dfViewer.getControl().setFocus();
	}
}