package br.ufrj.coppe.pinel.ligeiro.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import br.ufrj.coppe.pinel.ligeiro.Activator;
import br.ufrj.coppe.pinel.ligeiro.common.Util;
import br.ufrj.coppe.pinel.ligeiro.data.Result;

/**
 * @author Roque Pinel
 *
 */
public class LigeiroView extends ViewPart
{
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.ufrj.coppe.pinel.ligeiro.views.LigeiroView";

	private Table statisticTable;
	private Table dependencyTable;

	private TableViewer dfViewer;
	private TableViewer tfViewer;

	private Action startFPAAction;

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

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		form.getBody().setLayout(layout);

		createFilesSection(form.getBody());

		createControlSection(form.getBody());

		createResultSection(form.getBody());

		appendActions();

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
		createFilesSectionStatistic(filesComposite);

		// Dependency
		createFilesSectionDependency(filesComposite);
	}

	private void createFilesSectionStatistic(Composite parent)
	{
		Composite statisticComposite = toolkit.createComposite(parent, SWT.WRAP);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		statisticComposite.setLayout(layout);

		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = true;

		Label tableLabel = new Label(statisticComposite, SWT.NONE);
		tableLabel.setText(Messages.getString("LigeiroView.files.statistic.table.label"));
		tableLabel.setLayoutData(gd);

		statisticTable = toolkit.createTable(statisticComposite, SWT.MULTI);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.widthHint = 300;
		statisticTable.setLayoutData(gd);

		toolkit.paintBordersFor(statisticComposite);

		DropTarget dropTarget = new DropTarget(statisticTable, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		dropTarget.setTransfer(new Transfer[] {fileTransfer});
		dropTarget.addDropListener(new DropTargetListener()
		{
			@Override
			public void drop(DropTargetEvent event)
			{
				if (fileTransfer.isSupportedType(event.currentDataType))
				{
					String[] files = (String[])event.data;
					for (int i = 0; i < files.length; i++)
					{
						Util.addInputFile(statisticTable, files[i]);
					}
				}
			}

			@Override
			public void dragEnter(DropTargetEvent event)
			{
				// empty
			}

			@Override
			public void dragLeave(DropTargetEvent event)
			{
				// empty
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event)
			{
				// empty
			}

			@Override
			public void dragOver(DropTargetEvent event)
			{
				// empty
			}

			@Override
			public void dropAccept(DropTargetEvent event)
			{
				// empty
			}
		});

		Composite buttonComposite = toolkit.createComposite(statisticComposite, SWT.WRAP);
		layout = new GridLayout(1, true);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		buttonComposite.setLayout(layout);

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);

		// add
		Button button = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.statistic.add.button.label"), SWT.PUSH);
		button.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		button.setToolTipText(Messages.getString("LigeiroView.files.statistic.add.button.tip"));
		button.setLayoutData(gd);
		button.addSelectionListener(
				new SelectionListener()
				{
					@Override
					public void widgetSelected(SelectionEvent event)
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent event)
					{
						// empty
					}
				}
		);

		// remove
		button = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.statistic.remove.button.label"), SWT.PUSH);
		button.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ELCL_REMOVE));
		button.setToolTipText(Messages.getString("LigeiroView.files.statistic.remove.button.tip"));
		button.setLayoutData(gd);
		button.addSelectionListener(
				new SelectionListener()
				{
					@Override
					public void widgetSelected(SelectionEvent event)
					{
						Util.removeInputFiles(statisticTable, statisticTable.getSelection());
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent event)
					{
						// empty
					}
				}
		);
	}

	private void createFilesSectionDependency(Composite parent)
	{
		Composite dependencyComposite = toolkit.createComposite(parent, SWT.WRAP);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		dependencyComposite.setLayout(layout);

		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = true;

		Label tableLabel = new Label(dependencyComposite, SWT.NONE);
		tableLabel.setText(Messages.getString("LigeiroView.files.dependency.table.label"));
		tableLabel.setLayoutData(gd);

		dependencyTable = toolkit.createTable(dependencyComposite, SWT.MULTI);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.widthHint = 300;
		dependencyTable.setLayoutData(gd);

		toolkit.paintBordersFor(dependencyComposite);

		DropTarget dropTarget = new DropTarget(dependencyTable, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		dropTarget.setTransfer(new Transfer[] {fileTransfer});
		dropTarget.addDropListener(new DropTargetListener() {
			@Override
			public void drop(DropTargetEvent event)
			{
				if (fileTransfer.isSupportedType(event.currentDataType))
				{
					String[] files = (String[])event.data;
					for (int i = 0; i < files.length; i++)
					{
						Util.addInputFile(dependencyTable, files[i]);
					}
				}
			}

			@Override
			public void dragEnter(DropTargetEvent event)
			{
				// empty
			}

			@Override
			public void dragLeave(DropTargetEvent event)
			{
				// empty
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event)
			{
				// empty
			}

			@Override
			public void dragOver(DropTargetEvent event)
			{
				// empty
			}

			@Override
			public void dropAccept(DropTargetEvent event)
			{
				// empty
			}
		});

		Composite buttonComposite = toolkit.createComposite(dependencyComposite, SWT.WRAP);
		layout = new GridLayout(1, true);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		buttonComposite.setLayout(layout);

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);

		// add
		Button button = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.dependency.add.button.label"), SWT.PUSH);
		button.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		button.setToolTipText(Messages.getString("LigeiroView.files.dependency.add.button.tip"));
		button.setLayoutData(gd);
		button.addSelectionListener(
				new SelectionListener()
				{
					@Override
					public void widgetSelected(SelectionEvent event)
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent event)
					{
						// empty
					}
				}
		);

		// remove
		button = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.dependency.remove.button.label"), SWT.PUSH);
		button.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ELCL_REMOVE));
		button.setToolTipText(Messages.getString("LigeiroView.files.dependency.remove.button.tip"));
		button.setLayoutData(gd);
		button.addSelectionListener(
				new SelectionListener()
				{
					@Override
					public void widgetSelected(SelectionEvent event)
					{
						Util.removeInputFiles(dependencyTable, dependencyTable.getSelection());
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent event)
					{
						// empty
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
					public void widgetSelected(SelectionEvent event)
					{
						FileDialog fd = new FileDialog(form.getShell(), SWT.OPEN);
						fd.setText(Messages.getString("LigeiroView.control.configuration.file.dialog.title"));
						String[] filterExt = {"*.xml"};
						fd.setFilterExtensions(filterExt);
						String selected = fd.open();
						System.out.println(selected);
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent event)
					{
						// empty
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

		dfViewer = new TableViewer(resultComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
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

		tfViewer = new TableViewer(resultComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
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

	private void appendActions()
	{
		startFPAAction = new Action()
		{
			public void run()
			{
				showMessage("Start FPA executed");
			}
		};

		startFPAAction.setText(Messages.getString("LigeiroView.action.start.fpa.label"));
		startFPAAction.setToolTipText(Messages.getString("LigeiroView.action.start.fpa.tip"));

		startFPAAction.setImageDescriptor(Activator.getImageDescriptor("icons/run.gif"));

		IActionBars bars = getViewSite().getActionBars();
		//bars.getMenuManager().add(startFPAAction);
		bars.getToolBarManager().add(startFPAAction);
	}

	private void showMessage(String message)
	{
		MessageDialog.openInformation(form.getShell(), Messages.getString("LigeiroView.title"), message);
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		dfViewer.getControl().setFocus();
	}
}