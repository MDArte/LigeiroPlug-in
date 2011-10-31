package br.ufrj.cos.pinel.ligeiro.plugin.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

import br.ufrj.cos.pinel.ligeiro.Core;
import br.ufrj.cos.pinel.ligeiro.common.FPAConfig;
import br.ufrj.cos.pinel.ligeiro.data.FPAReport;
import br.ufrj.cos.pinel.ligeiro.data.ReportResult;
import br.ufrj.cos.pinel.ligeiro.plugin.LigeiroPlugin;
import br.ufrj.cos.pinel.ligeiro.plugin.common.LigeiroPreferences;
import br.ufrj.cos.pinel.ligeiro.plugin.common.Util;
import br.ufrj.cos.pinel.ligeiro.plugin.data.InputFile;
import br.ufrj.cos.pinel.ligeiro.plugin.data.Result;
import br.ufrj.cos.pinel.ligeiro.plugin.provider.ResultsTableProvider;
import br.ufrj.cos.pinel.ligeiro.xml.exception.ReadXMLException;

/**
 * @author Roque Pinel
 *
 */
public class LigeiroView extends ViewPart
{
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.ufrj.cos.pinel.ligeiro.plugin.views.LigeiroView";

	private FormToolkit toolkit;
	private ScrolledForm form;

	private Table statisticTable;
	private Button statisticAddButton;
	private Button statisticRemoveButton;

	private Table dependencyTable;
	private Button dependencyAddButton;
	private Button dependencyRemoveButton;

	private TableViewer dfTable;
	private ResultsTableProvider dfTableProvider;

	private TableViewer tfTable;
	private ResultsTableProvider tfTableProvider;

	private Text configurationFileText;

	/**
	 * The constructor.
	 */
	public LigeiroView()
	{
		super();

		dfTableProvider = new ResultsTableProvider();
		tfTableProvider = new ResultsTableProvider();
	}

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

		loadPreviousInformation();

		form.reflow(true);
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		dfTable.getControl().setFocus();
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

		createFilesSectionStatistic(filesComposite);

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
		LigeiroPreferences.loadStatisticFiles(statisticTable);

		toolkit.paintBordersFor(statisticComposite);

		DropTarget dropTarget = new DropTarget(statisticTable, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		dropTarget.setTransfer(new Transfer[] {fileTransfer});
		dropTarget.addDropListener(
			new DropTargetListener()
			{
				public void drop(DropTargetEvent event)
				{
					if (fileTransfer.isSupportedType(event.currentDataType))
					{
						String[] paths = (String[]) event.data;

						for (String path : paths)
						{
							if (!verifyFileType(path))
								return;
						}

						for (String path : paths)
						{
							if (Util.addInputFile(statisticTable, path)
								&& !statisticRemoveButton.isEnabled())
							{
								statisticRemoveButton.setEnabled(true);
							}
						}

						if (paths.length > 0)
						{
							LigeiroPreferences.saveStatisticFiles(statisticTable);
						}
					}
				}
				public void dragEnter(DropTargetEvent event) { }
				public void dragLeave(DropTargetEvent event) { }
				public void dragOperationChanged(DropTargetEvent event) { }
				public void dragOver(DropTargetEvent event) { }
				public void dropAccept(DropTargetEvent event) { }
			}
		);

		Composite buttonComposite = toolkit.createComposite(statisticComposite, SWT.WRAP);
		layout = new GridLayout(1, true);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		buttonComposite.setLayout(layout);

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);

		// add
		statisticAddButton = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.statistic.add.button.label"), SWT.PUSH);
		statisticAddButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		statisticAddButton.setToolTipText(Messages.getString("LigeiroView.files.statistic.add.button.tip"));
		statisticAddButton.setLayoutData(gd);
		statisticAddButton.addSelectionListener(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent event)
				{
					List<String> paths = createFileCheckedTreeSelectionDialog(
							Messages.getString("LigeiroView.files.statistic.add.dialog.title"),
							Messages.getString("LigeiroView.files.add.dialog.message"));

					if (paths != null)
					{
						for (String path : paths)
						{
							if (Util.addInputFile(statisticTable, path)
								&& !statisticRemoveButton.isEnabled())
							{
								statisticRemoveButton.setEnabled(true);
							}
						}

						if (paths.size() > 0)
						{
							LigeiroPreferences.saveStatisticFiles(statisticTable);
						}
					}
				}
				public void widgetDefaultSelected(SelectionEvent event) { }
			}
		);

		// remove
		statisticRemoveButton = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.statistic.remove.button.label"), SWT.PUSH);
		statisticRemoveButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ELCL_REMOVE));
		statisticRemoveButton.setToolTipText(Messages.getString("LigeiroView.files.statistic.remove.button.tip"));
		statisticRemoveButton.setLayoutData(gd);
		statisticRemoveButton.addSelectionListener(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent event)
				{
					if (Util.removeInputFiles(statisticTable, statisticTable.getSelection()))
					{
						LigeiroPreferences.saveStatisticFiles(statisticTable);

						if (statisticTable.getItemCount() == 0)
							statisticRemoveButton.setEnabled(false);
					}
				}
				public void widgetDefaultSelected(SelectionEvent event) { }
			}
		);

		if (statisticTable.getItemCount() == 0)
			statisticRemoveButton.setEnabled(false);
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
		LigeiroPreferences.loadDependencyFiles(dependencyTable);

		toolkit.paintBordersFor(dependencyComposite);

		DropTarget dropTarget = new DropTarget(dependencyTable, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		dropTarget.setTransfer(new Transfer[] {fileTransfer});
		dropTarget.addDropListener(
			new DropTargetListener()
			{
				public void drop(DropTargetEvent event)
				{
					if (fileTransfer.isSupportedType(event.currentDataType))
					{
						String[] paths = (String[]) event.data;

						for (String path : paths)
						{
							if (!verifyFileType(path))
								return;
						}

						for (String path : paths)
						{
							if (Util.addInputFile(dependencyTable, path)
								&& !dependencyRemoveButton.isEnabled())
							{
								dependencyRemoveButton.setEnabled(true);
							}
						}

						if (paths.length > 0)
						{
							LigeiroPreferences.saveDependencyFiles(dependencyTable);
						}
					}
				}
				public void dragEnter(DropTargetEvent event) { }
				public void dragLeave(DropTargetEvent event) { }
				public void dragOperationChanged(DropTargetEvent event) { }
				public void dragOver(DropTargetEvent event) { }
				public void dropAccept(DropTargetEvent event) { }
			}
		);

		Composite buttonComposite = toolkit.createComposite(dependencyComposite, SWT.WRAP);
		layout = new GridLayout(1, true);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		buttonComposite.setLayout(layout);

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);

		// add
		dependencyAddButton = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.dependency.add.button.label"), SWT.PUSH);
		dependencyAddButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		dependencyAddButton.setToolTipText(Messages.getString("LigeiroView.files.dependency.add.button.tip"));
		dependencyAddButton.setLayoutData(gd);
		dependencyAddButton.addSelectionListener(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent event)
				{
					List<String> paths = createFileCheckedTreeSelectionDialog(
							Messages.getString("LigeiroView.files.dependency.add.dialog.title"),
							Messages.getString("LigeiroView.files.add.dialog.message"));

					if (paths != null)
					{
						for (String path : paths)
						{
							if (Util.addInputFile(dependencyTable, path)
								&& !dependencyRemoveButton.isEnabled())
							{
								dependencyRemoveButton.setEnabled(true);
							}
						}

						if (paths.size() > 0)
						{
							LigeiroPreferences.saveDependencyFiles(dependencyTable);
						}
					}
				}
				public void widgetDefaultSelected(SelectionEvent event) { }
			}
		);

		// remove
		dependencyRemoveButton = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.dependency.remove.button.label"), SWT.PUSH);
		dependencyRemoveButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ELCL_REMOVE));
		dependencyRemoveButton.setToolTipText(Messages.getString("LigeiroView.files.dependency.remove.button.tip"));
		dependencyRemoveButton.setLayoutData(gd);
		dependencyRemoveButton.addSelectionListener(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent event)
				{
					if (Util.removeInputFiles(dependencyTable, dependencyTable.getSelection()))
					{
						LigeiroPreferences.saveDependencyFiles(dependencyTable);

						if (dependencyTable.getItemCount() == 0)
							dependencyRemoveButton.setEnabled(false);
					}
				}
				public void widgetDefaultSelected(SelectionEvent event){ }
			}
		);

		if (dependencyTable.getItemCount() == 0)
			dependencyRemoveButton.setEnabled(false);
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

		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.widthHint = 500;

		configurationFileText = new Text(controlComposite, SWT.BORDER);
		configurationFileText.setEditable(false);
		configurationFileText.setLayoutData(gd);
		configurationFileText.setBackground(new Color(form.getShell().getDisplay(), 235, 235, 235));

		DropTarget dropTarget = new DropTarget(configurationFileText, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		dropTarget.setTransfer(new Transfer[] {fileTransfer});
		dropTarget.addDropListener(
			new DropTargetListener()
			{
				public void drop(DropTargetEvent event)
				{
					if (fileTransfer.isSupportedType(event.currentDataType))
					{
						String[] files = (String[]) event.data;
	
						if (files.length > 1)
						{
							showInformation(Messages.getString("LigeiroView.error.control.configuration.many.files"));
							return;
						}
						else if (files.length == 1 && verifyFileType(files[0]))
						{
							configurationFileText.setText(files[0]);
							LigeiroPreferences.setFPAConfigurationFile(files[0]);
						}
					}
				}
				public void dragEnter(DropTargetEvent event) { }
				public void dragLeave(DropTargetEvent event) { }
				public void dragOperationChanged(DropTargetEvent event) { }
				public void dragOver(DropTargetEvent event) { }
				public void dropAccept(DropTargetEvent event) { }
			}
		);

		Button configurationFileButton = toolkit.createButton(controlComposite, Messages.getString("LigeiroView.control.configuration.file.button.label"), SWT.PUSH);
		configurationFileButton.addSelectionListener(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent event)
				{
					List<String> paths = createFileCheckedTreeSelectionDialog(
							Messages.getString("LigeiroView.control.configuration.add.dialog.title"),
							Messages.getString("LigeiroView.control.configuration.add.dialog.message"));

					if (paths != null)
					{
						if (paths.size() > 1)
						{
							showInformation(Messages.getString("LigeiroView.error.control.configuration.many.files"));
						}
						else if (!paths.isEmpty())
						{
							configurationFileText.setText(paths.get(0));
							LigeiroPreferences.setFPAConfigurationFile(paths.get(0));
						}
					}
				}
				public void widgetDefaultSelected(SelectionEvent event) { }
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

		Composite toolbarComposite = toolkit.createComposite(resultSection, SWT.WRAP);
		RowLayout rowlayout = new RowLayout(SWT.HORIZONTAL);
		rowlayout.marginLeft = 0;
		rowlayout.marginRight = 0;
		rowlayout.spacing = 0;
		rowlayout.marginTop = 0;
		rowlayout.marginBottom = 0;
		toolbarComposite.setLayout(rowlayout);
		resultSection.setTextClient(toolbarComposite);
		ImageHyperlink imageHyperLink = new ImageHyperlink(toolbarComposite, SWT.LEFT);
		imageHyperLink.setBackgroundImage(resultSection.getBackgroundImage());
		imageHyperLink.setToolTipText(Messages.getString("LigeiroView.results.toolbar.clear.tip"));
		imageHyperLink.setImage(LigeiroPlugin.getImageDescriptor(LigeiroPlugin.IMG_TRASH).createImage());
		imageHyperLink.addHyperlinkListener(new HyperlinkAdapter()
		{
			public void linkActivated(HyperlinkEvent e)
			{
				dfTableProvider.clear();
				dfTable.refresh();

				tfTableProvider.clear();
				tfTable.refresh();
			}
		});

		Composite resultComposite = toolkit.createComposite(resultSection, SWT.WRAP);
		resultSection.setClient(resultComposite);

		GridLayout layout = new GridLayout(2, true);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		resultComposite.setLayout(layout);

		// Data Function

		dfTable = new TableViewer(resultComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		createResultColumns(resultComposite, dfTable, true);

		Table table = dfTable.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 300;
		table.setLayoutData(gd);

		dfTable.setContentProvider(new ArrayContentProvider());
		dfTable.setInput(dfTableProvider.getResults());
		dfTable.getTable().getHorizontalBar().setEnabled(true);

		// Transaction Function

		tfTable = new TableViewer(resultComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		createResultColumns(resultComposite, tfTable, false);

		table = tfTable.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 300;
		table.setLayoutData(gd);

		tfTable.setContentProvider(new ArrayContentProvider());
		tfTable.setInput(tfTableProvider.getResults());
		tfTable.getTable().getHorizontalBar().setEnabled(true);
	}

	private void createResultColumns(final Composite parent, final TableViewer viewer, final boolean isDataFunction)
	{
		String message;

		// First column is for the element

		if (isDataFunction)
			message = Messages.getString("LigeiroView.results.table.data.function");
		else
			message = Messages.getString("LigeiroView.results.table.transaction.function");

		TableViewerColumn col = Util.createTableViewerColumn(viewer, message);
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

		col = Util.createTableViewerColumn(viewer, message);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return Integer.toString(p.getRet_ftr());
			}
		});

		// Third column is for the DET

		col = Util.createTableViewerColumn(viewer, Messages.getString("LigeiroView.results.table.det"));
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return Integer.toString(p.getDet());
			}
		});

		// Fourth column is for the complexity

		col = Util.createTableViewerColumn(viewer, Messages.getString("LigeiroView.results.table.complexity"));
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return p.getComplexity();
			}
		});

		// Fifth column is for the complexity value

		col = Util.createTableViewerColumn(viewer, Messages.getString("LigeiroView.results.table.complexity.value"));
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return Integer.toString(p.getComplexityValue());
			}
		});
	}

	private void appendActions()
	{
		IActionBars bars = getViewSite().getActionBars();

		// start FPA
		Action action = new Action()
		{
			public void run()
			{
				startFPAA();
			}
		};
		action.setText(Messages.getString("LigeiroView.action.start.fpa.label"));
		action.setToolTipText(Messages.getString("LigeiroView.action.start.fpa.tip"));
		action.setImageDescriptor(LigeiroPlugin.getImageDescriptor(LigeiroPlugin.IMG_RUN));
		bars.getToolBarManager().add(action);

		bars.getToolBarManager().add(new Separator());

		// reset fields
		action = new Action()
		{
			public void run()
			{
				LigeiroPreferences.clear();

				statisticTable.clearAll();
				dependencyTable.clearAll();

				configurationFileText.setText("");
			}
		};
		action.setText(Messages.getString("LigeiroView.action.reset.fields.label"));
		action.setToolTipText(Messages.getString("LigeiroView.action.reset.fields.tip"));
		action.setImageDescriptor(LigeiroPlugin.getImageDescriptor(LigeiroPlugin.IMG_TRASH));
		bars.getToolBarManager().add(action);
	}

	private void loadPreviousInformation()
	{
		configurationFileText.setText(LigeiroPreferences.getFPAConfigurationFile());
	}

	private void showInformation(String message)
	{
		MessageDialog.openInformation(form.getShell(), Messages.getString("LigeiroView.title"), message);
	}

	private List<String> createFileCheckedTreeSelectionDialog(String title, String message)
	{
		CheckedTreeSelectionDialog dialog = new CheckedTreeSelectionDialog(form.getShell(),
				new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		int status = dialog.open();

		List<String> paths = new ArrayList<String>();

		if (status == Window.OK && dialog.getResult() != null)
		{
			Object[] results = dialog.getResult();

			for (int i = 0; i < results.length; i++)
			{
				// get only files
				if (results[i] instanceof org.eclipse.core.resources.IFile)
				{
					org.eclipse.core.resources.IFile file = (org.eclipse.core.resources.IFile) results[i];
					paths.add(file.getLocationURI().getPath());
				}
				else
				{
					showInformation(Messages.getString("LigeiroView.error.files.type.not.file"));
					return null;
				}
			}
		}

		return paths;
	}

	private boolean verifyFileType(String path)
	{
		File file = new File(path);

		if (file.isFile())
			return true;

		showInformation(Messages.getString("LigeiroView.error.files.type.not.file"));

		return false;
	}

	private void startFPAA()
	{
		Core core = new Core();

		try
		{
			TableItem[] items = statisticTable.getItems();
			for (TableItem item : items)
			{
				if (item.getData() instanceof InputFile)
				{
					core.readStatistics(((InputFile) item.getData()).getPath());
				}
			}

			items = dependencyTable.getItems();
			for (TableItem item : items)
			{
				if (item.getData() instanceof InputFile)
				{
					core.readDependencies(((InputFile) item.getData()).getPath());
				}
			}

			FPAConfig fpaConfig = core.readFPAConfiguration(configurationFileText.getText());

			FPAReport fpaReport = core.startFunctionPointAnalysis(fpaConfig);

			dfTableProvider.clear();
			for (ReportResult reportResult : fpaReport.getDFReport())
			{
				Result result = new Result();
				result.setElement(reportResult.getElement());
				result.setType(reportResult.getType());
				result.setRet_ftr(reportResult.getRet_ftr());
				result.setDet(reportResult.getDet());
				result.setComplexity(reportResult.getComplexity());
				result.setComplexityValue(reportResult.getComplexityValue());
				dfTableProvider.addResult(result);
			}
			dfTable.refresh();

			tfTableProvider.clear();
			for (ReportResult reportResult : fpaReport.getTFReport())
			{
				Result result = new Result();
				result.setElement(reportResult.getElement());
				result.setType(reportResult.getType());
				result.setRet_ftr(reportResult.getRet_ftr());
				result.setDet(reportResult.getDet());
				result.setComplexity(reportResult.getComplexity());
				result.setComplexityValue(reportResult.getComplexityValue());
				tfTableProvider.addResult(result);
			}
			tfTable.refresh();
		}
		catch (ReadXMLException e)
		{
			e.printStackTrace();
		}
	}
}