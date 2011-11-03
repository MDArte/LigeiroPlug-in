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
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

import br.ufrj.cos.pinel.ligeiro.Core;
import br.ufrj.cos.pinel.ligeiro.common.FPAConfig;
import br.ufrj.cos.pinel.ligeiro.plugin.LigeiroPlugin;
import br.ufrj.cos.pinel.ligeiro.plugin.common.ConsoleUtil;
import br.ufrj.cos.pinel.ligeiro.plugin.common.Constants;
import br.ufrj.cos.pinel.ligeiro.plugin.common.LigeiroPreferences;
import br.ufrj.cos.pinel.ligeiro.plugin.common.Util;
import br.ufrj.cos.pinel.ligeiro.plugin.comparator.ResultTableComparator;
import br.ufrj.cos.pinel.ligeiro.plugin.comparator.SummaryTableComparator;
import br.ufrj.cos.pinel.ligeiro.plugin.data.InputFile;
import br.ufrj.cos.pinel.ligeiro.plugin.data.Result;
import br.ufrj.cos.pinel.ligeiro.plugin.data.SummaryElement;
import br.ufrj.cos.pinel.ligeiro.plugin.provider.ResultsTableProvider;
import br.ufrj.cos.pinel.ligeiro.plugin.provider.SummaryTableProvider;
import br.ufrj.cos.pinel.ligeiro.report.FPAReport;
import br.ufrj.cos.pinel.ligeiro.report.LoadReport;
import br.ufrj.cos.pinel.ligeiro.report.ReportResult;
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
	public static final String ID = "br.ufrj.cos.pinel.ligeiro.plugin.views.LigeiroView"; //$NON-NLS-1$

	private FormToolkit toolkit;
	private ScrolledForm form;

	private Table statisticTable;
	private Button statisticAddButton;
	private Button statisticRemoveButton;

	private Table dependencyTable;
	private Button dependencyAddButton;
	private Button dependencyRemoveButton;

	private TableViewer summaryTable;
	private SummaryTableProvider summaryProvider;
	private SummaryTableComparator summaryTableComparator;

	private TableViewer dfTable;
	private ResultsTableProvider dfTableProvider;
	private ResultTableComparator dfTableComparator;

	private TableViewer tfTable;
	private ResultsTableProvider tfTableProvider;
	private ResultTableComparator tfTableComparator;

	private Text configurationFileText;

	private Text unadjustedDFTotalText;
	private Text unadjustedTFTotalText;
	private Text unadjustedFPATotalText;
	private Text vafText;
	private Text adjustedFPATotalText;

	private Core core; 

	/**
	 * The constructor.
	 */
	public LigeiroView()
	{
		super();

		summaryProvider = new SummaryTableProvider();
		summaryTableComparator = new SummaryTableComparator();

		dfTableProvider = new ResultsTableProvider();
		dfTableComparator = new ResultTableComparator();

		tfTableProvider = new ResultsTableProvider();
		tfTableComparator = new ResultTableComparator();
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText(Messages.getString("LigeiroView.title")); //$NON-NLS-1$

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

		Section filesSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
		filesSection.setText(Messages.getString("LigeiroView.files.section.title")); //$NON-NLS-1$
		filesSection.setLayoutData(gd);
		filesSection.setExpanded(LigeiroPreferences.isSectionFilesExpanded());
		filesSection.addExpansionListener(
			new IExpansionListener()
			{
				public void expansionStateChanged(ExpansionEvent event)
				{
					LigeiroPreferences.setSectionFilesExpanded(event.getState());
				}
				public void expansionStateChanging(ExpansionEvent event) { }
			}
		);

		Composite filesComposite = toolkit.createComposite(filesSection, SWT.WRAP);
		filesSection.setClient(filesComposite);
		GridLayout layout = new GridLayout(3, true);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		filesComposite.setLayout(layout);

		createFilesSectionStatistic(filesComposite);

		createFilesSectionDependency(filesComposite);

		createFilesSectionSummary(filesComposite);
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
		tableLabel.setText(Messages.getString("LigeiroView.files.statistic.table.label")); //$NON-NLS-1$
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
		statisticAddButton = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.statistic.add.button.label"), SWT.PUSH);//$NON-NLS-1$
		statisticAddButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		statisticAddButton.setToolTipText(Messages.getString("LigeiroView.files.statistic.add.button.tip")); //$NON-NLS-1$
		statisticAddButton.setLayoutData(gd);
		statisticAddButton.addSelectionListener(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent event)
				{
					List<String> paths = createFileCheckedTreeSelectionDialog(
							Messages.getString("LigeiroView.files.statistic.add.dialog.title"), //$NON-NLS-1$
							Messages.getString("LigeiroView.files.add.dialog.message")); //$NON-NLS-1$

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
		statisticRemoveButton = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.statistic.remove.button.label"), SWT.PUSH); //$NON-NLS-1$
		statisticRemoveButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ELCL_REMOVE));
		statisticRemoveButton.setToolTipText(Messages.getString("LigeiroView.files.statistic.remove.button.tip")); //$NON-NLS-1$
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
		tableLabel.setText(Messages.getString("LigeiroView.files.dependency.table.label")); //$NON-NLS-1$
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
		dependencyAddButton = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.dependency.add.button.label"), SWT.PUSH); //$NON-NLS-1$
		dependencyAddButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		dependencyAddButton.setToolTipText(Messages.getString("LigeiroView.files.dependency.add.button.tip")); //$NON-NLS-1$
		dependencyAddButton.setLayoutData(gd);
		dependencyAddButton.addSelectionListener(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent event)
				{
					List<String> paths = createFileCheckedTreeSelectionDialog(
							Messages.getString("LigeiroView.files.dependency.add.dialog.title"), //$NON-NLS-1$
							Messages.getString("LigeiroView.files.add.dialog.message")); //$NON-NLS-1$

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
		dependencyRemoveButton = toolkit.createButton(buttonComposite, Messages.getString("LigeiroView.files.dependency.remove.button.label"), SWT.PUSH); //$NON-NLS-1$
		dependencyRemoveButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ELCL_REMOVE));
		dependencyRemoveButton.setToolTipText(Messages.getString("LigeiroView.files.dependency.remove.button.tip")); //$NON-NLS-1$
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

	private void createFilesSectionSummary(Composite parent)
	{
		Composite summaryComposite = toolkit.createComposite(parent, SWT.WRAP);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		summaryComposite.setLayout(layout);

		Composite toolbarComposite = toolkit.createComposite(summaryComposite, SWT.WRAP);
		RowLayout rowlayout = new RowLayout(SWT.HORIZONTAL);
		rowlayout.marginLeft = 0;
		rowlayout.marginRight = 10;
		rowlayout.spacing = 20;
		rowlayout.marginTop = 2;
		rowlayout.marginBottom = 2;
		toolbarComposite.setLayout(rowlayout);

		Label tableLabel = new Label(toolbarComposite, SWT.NONE);
		tableLabel.setText(Messages.getString("LigeiroView.files.summary.table.label")); //$NON-NLS-1$

		ImageHyperlink imageHyperLink = new ImageHyperlink(toolbarComposite, SWT.LEFT);
		imageHyperLink.setBackgroundImage(toolbarComposite.getBackgroundImage());
		imageHyperLink.setToolTipText(Messages.getString("LigeiroView.files.summary.clear.tip")); //$NON-NLS-1$
		imageHyperLink.setImage(LigeiroPlugin.getImageDescriptor(LigeiroPlugin.IMG_TRASH).createImage());
		imageHyperLink.addHyperlinkListener(new HyperlinkAdapter()
		{
			public void linkActivated(HyperlinkEvent e)
			{
				resetSummary();
			}
		});

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.widthHint = 300;

		summaryTable = new TableViewer(summaryComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		createSummaryColumns(summaryComposite);

		Table table = summaryTable.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(gd);

		summaryTable.setContentProvider(new ArrayContentProvider());
		summaryTable.setInput(summaryProvider.getElements());
		summaryTable.setComparator(summaryTableComparator);
		summaryTable.getTable().getHorizontalBar().setEnabled(true);

		summaryTable.getTable().setBackground(new Color(form.getShell().getDisplay(),
				Constants.COLOR_LIGHT_GRAY_R, Constants.COLOR_LIGHT_GRAY_G, Constants.COLOR_LIGHT_GRAY_B));

		toolkit.paintBordersFor(summaryComposite);
	}

	private void createSummaryColumns(final Composite parent)
	{
		int position = 0;

		TableViewerColumn col = Util.createTableViewerColumn(summaryTable, summaryTableComparator,
				Messages.getString("LigeiroView.files.summary.table.type"), //$NON-NLS-1$
				Constants.SUMMARY_COLUMNS_WIDTH[position], position++);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SummaryElement summaryElement = (SummaryElement) element;
				return summaryElement.getType();
			}
		});

		col = Util.createTableViewerColumn(summaryTable, summaryTableComparator,
				Messages.getString("LigeiroView.files.summary.table.total"), //$NON-NLS-1$
				Constants.SUMMARY_COLUMNS_WIDTH[position], position++);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SummaryElement summaryElement = (SummaryElement) element;
				return Integer.toString(summaryElement.getTotal());
			}
		});
	}

	private void createControlSection(Composite parent)
	{
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;

		Section controlSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
		controlSection.setText(Messages.getString("LigeiroView.control.section.title")); //$NON-NLS-1$
		controlSection.setLayoutData(gd);
		controlSection.setExpanded(LigeiroPreferences.isSectionControlExpanded());
		controlSection.addExpansionListener(
			new IExpansionListener()
			{
				public void expansionStateChanged(ExpansionEvent event)
				{
					LigeiroPreferences.setSectionControlExpanded(event.getState());
				}
				public void expansionStateChanging(ExpansionEvent event) { }
			}
		);

		Composite controlComposite = toolkit.createComposite(controlSection, SWT.WRAP);
		controlSection.setClient(controlComposite);

		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		controlComposite.setLayout(layout);

		Label configurationFileLabel = new Label(controlComposite, SWT.NONE);
		configurationFileLabel.setText(Messages.getString("LigeiroView.control.configuration.file.label")); //$NON-NLS-1$
		configurationFileLabel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));

		gd = new GridData();
		gd.widthHint = Constants.CONTROL_CONFIGURATION_WIDTH;

		configurationFileText = new Text(controlComposite, SWT.BORDER);
		configurationFileText.setEditable(false);
		configurationFileText.setLayoutData(gd);
		configurationFileText.setBackground(new Color(form.getShell().getDisplay(),
			Constants.COLOR_LIGHT_GRAY_R, Constants.COLOR_LIGHT_GRAY_G, Constants.COLOR_LIGHT_GRAY_B));

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
							showInformation(Messages.getString("LigeiroView.error.many.configuration.files")); //$NON-NLS-1$
							return;
						}
						else if (files.length == 1 && verifyFileType(files[0]))
						{
							configurationFileText.setText(files[0]);
							LigeiroPreferences.setFPAConfigurationFile(files[0]);

							configurationFileText.setForeground(new Color(form.getShell().getDisplay(),
								Constants.COLOR_BLACK_R, Constants.COLOR_BLACK_G, Constants.COLOR_BLACK_B));
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

		Button configurationFileButton = toolkit.createButton(controlComposite,
				Messages.getString("LigeiroView.control.configuration.file.button.label"), SWT.PUSH); //$NON-NLS-1$
		configurationFileButton.addSelectionListener(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent event)
				{
					List<String> paths = createFileCheckedTreeSelectionDialog(
							Messages.getString("LigeiroView.control.configuration.add.dialog.title"), //$NON-NLS-1$
							Messages.getString("LigeiroView.control.configuration.add.dialog.message")); //$NON-NLS-1$

					if (paths != null)
					{
						if (paths.size() > 1)
						{
							showInformation(Messages.getString("LigeiroView.error.many.configuration.files")); //$NON-NLS-1$
						}
						else if (!paths.isEmpty())
						{
							configurationFileText.setText(paths.get(0));
							LigeiroPreferences.setFPAConfigurationFile(paths.get(0));

							configurationFileText.setForeground(new Color(form.getShell().getDisplay(),
									Constants.COLOR_BLACK_R, Constants.COLOR_BLACK_G, Constants.COLOR_BLACK_B));
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

		Section resultSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
		resultSection.setText(Messages.getString("LigeiroView.results.section.title")); //$NON-NLS-1$
		resultSection.setLayoutData(gd);
		resultSection.setExpanded(LigeiroPreferences.isSectionResultsExpanded());
		resultSection.addExpansionListener(
			new IExpansionListener()
			{
				public void expansionStateChanged(ExpansionEvent event)
				{
					LigeiroPreferences.setSectionResultsExpanded(event.getState());
				}
				public void expansionStateChanging(ExpansionEvent event) { }
			}
		);

		Composite toolbarComposite = toolkit.createComposite(resultSection, SWT.WRAP);
		RowLayout rowlayout = new RowLayout(SWT.HORIZONTAL);
		rowlayout.marginLeft = 0;
		rowlayout.marginRight = 10;
		rowlayout.spacing = 0;
		rowlayout.marginTop = 2;
		rowlayout.marginBottom = 2;
		toolbarComposite.setLayout(rowlayout);
		resultSection.setTextClient(toolbarComposite);
		ImageHyperlink imageHyperLink = new ImageHyperlink(toolbarComposite, SWT.LEFT);
		imageHyperLink.setBackgroundImage(toolbarComposite.getBackgroundImage());
		imageHyperLink.setToolTipText(Messages.getString("LigeiroView.results.toolbar.clear.tip")); //$NON-NLS-1$
		imageHyperLink.setImage(LigeiroPlugin.getImageDescriptor(LigeiroPlugin.IMG_TRASH).createImage());
		imageHyperLink.addHyperlinkListener(new HyperlinkAdapter()
		{
			public void linkActivated(HyperlinkEvent e)
			{
				resetResults();
			}
		});

		Composite resultComposite = toolkit.createComposite(resultSection, SWT.WRAP);
		resultSection.setClient(resultComposite);

		GridLayout layout = new GridLayout(2, true);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		resultComposite.setLayout(layout);

		// for tables
		gd = new GridData(GridData.GRAB_HORIZONTAL);
		gd.heightHint = 300;

		// Data Function table

		dfTable = new TableViewer(resultComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		createResultColumns(resultComposite, dfTable, dfTableComparator, true);

		Table table = dfTable.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(gd);

		dfTable.setContentProvider(new ArrayContentProvider());
		dfTable.setInput(dfTableProvider.getResults());
		dfTable.setComparator(dfTableComparator);
		dfTable.getTable().getHorizontalBar().setEnabled(true);

		// Transaction Function table

		tfTable = new TableViewer(resultComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		createResultColumns(resultComposite, tfTable, tfTableComparator, false);

		table = tfTable.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(gd);

		tfTable.setContentProvider(new ArrayContentProvider());
		tfTable.setInput(tfTableProvider.getResults());
		tfTable.setComparator(tfTableComparator);
		tfTable.getTable().getHorizontalBar().setEnabled(true);

		// additional information

		Color mathOperationColor = new Color(form.getShell().getDisplay(),
				Constants.COLOR_RED_R, Constants.COLOR_RED_G, Constants.COLOR_RED_B);

		Color fieldColor = new Color(form.getShell().getDisplay(),
				Constants.COLOR_LIGHT_GRAY_R, Constants.COLOR_LIGHT_GRAY_G, Constants.COLOR_LIGHT_GRAY_B);

		Composite allComposite = toolkit.createComposite(resultComposite, SWT.WRAP);
		layout = new GridLayout(9, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		allComposite.setLayout(layout);

		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gd.horizontalSpan = 2;
		allComposite.setLayoutData(gd);

		// for fields
		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);

		Composite groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		layout = new GridLayout(1, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		groupComposite.setLayout(layout);
		Label label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.getString("LigeiroView.results.data.function.total.label")); //$NON-NLS-1$
		unadjustedDFTotalText = new Text(groupComposite, SWT.BORDER);
		unadjustedDFTotalText.setLayoutData(gd);
		unadjustedDFTotalText.setEditable(false);
		unadjustedDFTotalText.setBackground(fieldColor);
		unadjustedDFTotalText.setSize(Constants.RESULT_FIELD_WIDTH, Constants.RESULT_FIELD_HEIGHT);
		unadjustedDFTotalText.setToolTipText(Messages.getString("LigeiroView.results.data.function.total.tip")); //$NON-NLS-1$

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.getString("LigeiroView.results.plus")); //$NON-NLS-1$
		label.setForeground(mathOperationColor);

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.getString("LigeiroView.results.transaction.function.total.label")); //$NON-NLS-1$
		unadjustedTFTotalText = new Text(groupComposite, SWT.BORDER);
		unadjustedTFTotalText.setLayoutData(gd);
		unadjustedTFTotalText.setEditable(false);
		unadjustedTFTotalText.setBackground(fieldColor);
		unadjustedTFTotalText.setSize(Constants.RESULT_FIELD_WIDTH, Constants.RESULT_FIELD_HEIGHT);
		unadjustedTFTotalText.setToolTipText(Messages.getString("LigeiroView.results.transaction.function.total.tip")); //$NON-NLS-1$

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.getString("LigeiroView.results.equals")); //$NON-NLS-1$
		label.setForeground(mathOperationColor);

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.getString("LigeiroView.results.unadjusted.fpa.total.label")); //$NON-NLS-1$
		unadjustedFPATotalText = new Text(groupComposite, SWT.BORDER);
		unadjustedFPATotalText.setLayoutData(gd);
		unadjustedFPATotalText.setEditable(false);
		unadjustedFPATotalText.setBackground(fieldColor);
		unadjustedFPATotalText.setSize(Constants.RESULT_FIELD_WIDTH, Constants.RESULT_FIELD_HEIGHT);
		unadjustedFPATotalText.setToolTipText(Messages.getString("LigeiroView.results.unadjusted.fpa.total.tip")); //$NON-NLS-1$

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.getString("LigeiroView.results.times")); //$NON-NLS-1$
		label.setForeground(mathOperationColor);

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.getString("LigeiroView.results.vaf.label")); //$NON-NLS-1$
		vafText = new Text(groupComposite, SWT.BORDER);
		vafText.setLayoutData(gd);
		vafText.setEditable(false);
		vafText.setBackground(fieldColor);
		vafText.setSize(20, 50);
		vafText.setToolTipText(Messages.getString("LigeiroView.results.vaf.tip")); //$NON-NLS-1$

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.getString("LigeiroView.results.equals")); //$NON-NLS-1$
		label.setForeground(mathOperationColor);

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.getString("LigeiroView.results.adjusted.fpa.total.label")); //$NON-NLS-1$
		adjustedFPATotalText = new Text(groupComposite, SWT.BORDER);
		adjustedFPATotalText.setLayoutData(gd);
		adjustedFPATotalText.setEditable(false);
		adjustedFPATotalText.setBackground(fieldColor);
		adjustedFPATotalText.setSize(Constants.RESULT_FIELD_WIDTH, Constants.RESULT_FIELD_HEIGHT);
		adjustedFPATotalText.setToolTipText(Messages.getString("LigeiroView.results.adjusted.fpa.total.tip")); //$NON-NLS-1$
	}

	private void createResultColumns(final Composite parent, final TableViewer viewer, final ResultTableComparator comparator, final boolean isDataFunction)
	{
		int position = 0;
		String message;

		if (isDataFunction)
			message = Messages.getString("LigeiroView.results.table.data.function"); //$NON-NLS-1$
		else
			message = Messages.getString("LigeiroView.results.table.transaction.function"); //$NON-NLS-1$

		TableViewerColumn col = Util.createTableViewerColumn(viewer, comparator, message, Constants.RESULT_COLUMNS_WIDTH[position], position++);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return p.getElement();
			}
		});

		col = Util.createTableViewerColumn(viewer, comparator,
				Messages.getString("LigeiroView.results.table.type"), //$NON-NLS-1$
				Constants.RESULT_COLUMNS_WIDTH[position], position++);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return p.getType();
			}
		});

		if (isDataFunction)
			message = Messages.getString("LigeiroView.results.table.ret"); //$NON-NLS-1$
		else
			message = Messages.getString("LigeiroView.results.table.ftr"); //$NON-NLS-1$

		col = Util.createTableViewerColumn(viewer, comparator, message, Constants.RESULT_COLUMNS_WIDTH[position], position++);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return Integer.toString(p.getRet_ftr());
			}
		});

		col = Util.createTableViewerColumn(viewer, comparator,
				Messages.getString("LigeiroView.results.table.det"), //$NON-NLS-1$
				Constants.RESULT_COLUMNS_WIDTH[position], position++);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return Integer.toString(p.getDet());
			}
		});

		col = Util.createTableViewerColumn(viewer, comparator,
				Messages.getString("LigeiroView.results.table.complexity"), //$NON-NLS-1$
				Constants.RESULT_COLUMNS_WIDTH[position], position++);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return p.getComplexity();
			}
		});

		col = Util.createTableViewerColumn(viewer, comparator,
				Messages.getString("LigeiroView.results.table.complexity.value"), //$NON-NLS-1$
				Constants.RESULT_COLUMNS_WIDTH[position], position++);
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
		action.setText(Messages.getString("LigeiroView.action.start.fpa.label")); //$NON-NLS-1$
		action.setToolTipText(Messages.getString("LigeiroView.action.start.fpa.tip")); //$NON-NLS-1$
		action.setImageDescriptor(LigeiroPlugin.getImageDescriptor(LigeiroPlugin.IMG_RUN));
		bars.getToolBarManager().add(action);

		// load files
		action = new Action()
		{
			public void run()
			{
				loadStatisticAndDependencyFiles(false);
			}
		};
		action.setText(Messages.getString("LigeiroView.action.load.files.label")); //$NON-NLS-1$
		action.setToolTipText(Messages.getString("LigeiroView.action.load.files.tip")); //$NON-NLS-1$
		action.setImageDescriptor(LigeiroPlugin.getImageDescriptor(LigeiroPlugin.IMG_PROPERTIES));
		bars.getToolBarManager().add(action);

		// separator
		bars.getToolBarManager().add(new Separator());

		// reset fields
		action = new Action()
		{
			public void run()
			{
				resetFields();
				resetSummary();
				resetResults();

				if (core != null)
					core.clear();
			}
		};
		action.setText(Messages.getString("LigeiroView.action.reset.fields.label")); //$NON-NLS-1$
		action.setToolTipText(Messages.getString("LigeiroView.action.reset.fields.tip")); //$NON-NLS-1$
		action.setImageDescriptor(LigeiroPlugin.getImageDescriptor(LigeiroPlugin.IMG_TRASH));
		bars.getToolBarManager().add(action);
	}

	private void resetFields()
	{
		LigeiroPreferences.clear();

		statisticTable.removeAll();
		dependencyTable.removeAll();

		configurationFileText.setText(""); //$NON-NLS-1$
	}

	private void resetSummary()
	{
		summaryProvider.clear();
		summaryTable.refresh();
	}

	private void resetResults()
	{
		dfTableProvider.clear();
		dfTable.refresh();
		unadjustedDFTotalText.setText(""); //$NON-NLS-1$

		tfTableProvider.clear();
		tfTable.refresh();
		unadjustedTFTotalText.setText(""); //$NON-NLS-1$

		unadjustedFPATotalText.setText(""); //$NON-NLS-1$
		vafText.setText(""); //$NON-NLS-1$
		adjustedFPATotalText.setText(""); //$NON-NLS-1$
	}

	private void loadPreviousInformation()
	{
		configurationFileText.setText(LigeiroPreferences.getFPAConfigurationFile());
	}

	private void showInformation(String message)
	{
		MessageDialog.openInformation(form.getShell(), Messages.getString("LigeiroView.title"), message); //$NON-NLS-1$
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
					showInformation(Messages.getString("LigeiroView.error.type.not.file")); //$NON-NLS-1$
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

		showInformation(Messages.getString("LigeiroView.error.type.not.file")); //$NON-NLS-1$

		return false;
	}

	private void startFPAA()
	{
		Color brokeItemColor = new Color(form.getShell().getDisplay(),
				Constants.COLOR_RED_R, Constants.COLOR_RED_G, Constants.COLOR_RED_B);

		Color goodItemColor = new Color(form.getShell().getDisplay(),
				Constants.COLOR_BLACK_R, Constants.COLOR_BLACK_G, Constants.COLOR_BLACK_B);

		StringBuilder sbMessage = loadStatisticAndDependencyFiles(true);

		ConsoleUtil.writeSection(form, Messages.getString("LigeiroView.console.reading.configuration.file")); //$NON-NLS-1$

		FPAConfig fpaConfig = null;

		try
		{
			if (!br.ufrj.cos.pinel.ligeiro.common.Util.isEmptyOrNull(configurationFileText.getText()))
			{
				fpaConfig = core.readFPAConfiguration(configurationFileText.getText());
				configurationFileText.setForeground(goodItemColor);
			}
		}
		catch (ReadXMLException e)
		{
			Util.appendMessage(sbMessage, Messages.getString("LigeiroView.error.load.configuration.file")); //$NON-NLS-1$
			configurationFileText.setForeground(brokeItemColor);
		}

		if (sbMessage.length() > 0)
		{
			showInformation(sbMessage.toString());
			return;
		}

		ConsoleUtil.writeSection(form, Messages.getString("LigeiroView.console.starting.fpa")); //$NON-NLS-1$

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

		ConsoleUtil.writeTableResume(form, Messages.getString("LigeiroView.results.table.data.function"),fpaReport.getDFReport().size()); //$NON-NLS-1$

		unadjustedDFTotalText.setText(Integer.toString(fpaReport.getDFReportTotal()));

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

		ConsoleUtil.writeTableResume(form, Messages.getString("LigeiroView.results.table.transaction.function"),fpaReport.getTFReport().size()); //$NON-NLS-1$

		unadjustedTFTotalText.setText(Integer.toString(fpaReport.getTFReportTotal()));

		unadjustedFPATotalText.setText(Integer.toString(fpaReport.getReportTotal()));

		vafText.setText(Double.toString(fpaConfig.getVaf()));

		adjustedFPATotalText.setText(Double.toString(fpaReport.getAdjustedReportTotal(fpaConfig.getVaf())));

		ConsoleUtil.writeSection(form, Messages.getString("LigeiroView.console.done")); //$NON-NLS-1$
	}

	private StringBuilder loadStatisticAndDependencyFiles(boolean startFPA)
	{
		if (core == null)
			core = new Core();

		core.clear();

		// clearing the summary table
		summaryProvider.clear();
		summaryTable.refresh();

		Color brokeItemColor = new Color(form.getShell().getDisplay(),
				Constants.COLOR_RED_R, Constants.COLOR_RED_G, Constants.COLOR_RED_B);

		Color goodItemColor = new Color(form.getShell().getDisplay(),
				Constants.COLOR_BLACK_R, Constants.COLOR_BLACK_G, Constants.COLOR_BLACK_B);

		StringBuilder sbMessage = new StringBuilder();

		if (statisticTable.getItemCount() == 0)
		{
			Util.appendMessage(sbMessage, Messages.getString("LigeiroView.error.no.statistic.file")); //$NON-NLS-1$
		}
		if (dependencyTable.getItemCount() == 0)
		{
			Util.appendMessage(sbMessage, Messages.getString("LigeiroView.error.no.dependency.file")); //$NON-NLS-1$
		}
		if (br.ufrj.cos.pinel.ligeiro.common.Util.isEmptyOrNull(configurationFileText.getText()))
		{
			Util.appendMessage(sbMessage, Messages.getString("LigeiroView.error.no.configuration.file")); //$NON-NLS-1$
		}

		ConsoleUtil.clearConsole();
		ConsoleUtil.writeSection(form, Messages.getString("LigeiroView.console.reading.statistic.files")); //$NON-NLS-1$

		boolean hasBrokenItem = false;
		TableItem[] items = statisticTable.getItems();
		for (int i = 0; i < items.length; i++)
		{
			if (items[i].getData() instanceof InputFile)
			{
				try
				{
					LoadReport loadReport = core.readStatistics(((InputFile) items[i].getData()).getPath());

					ConsoleUtil.writeFile(form, loadReport.getFileName());
					ConsoleUtil.writeElements(form, loadReport.getElementsRead(), loadReport.getType());

					items[i].setForeground(goodItemColor);

					updateFilesSumamary(loadReport);
				}
				catch (ReadXMLException e)
				{
					if (!hasBrokenItem)
					{
						hasBrokenItem = true;
						Util.appendMessage(sbMessage, Messages.getString("LigeiroView.error.load.statistic.file")); //$NON-NLS-1$
					}

					items[i].setForeground(brokeItemColor);
				}
			}
		}

		ConsoleUtil.writeSection(form, Messages.getString("LigeiroView.console.reading.dependency.files")); //$NON-NLS-1$

		hasBrokenItem = false;
		items = dependencyTable.getItems();
		for (int i = 0; i < items.length; i++)
		{
			if (items[i].getData() instanceof InputFile)
			{
				try
				{
					LoadReport loadReport = core.readDependencies(((InputFile) items[i].getData()).getPath());

					ConsoleUtil.writeFile(form, loadReport.getFileName());
					ConsoleUtil.writeElements(form, loadReport.getElementsRead(), loadReport.getType());

					items[i].setForeground(goodItemColor);

					updateFilesSumamary(loadReport);
				}
				catch (ReadXMLException e)
				{
					if (!hasBrokenItem)
					{
						hasBrokenItem = true;
						Util.appendMessage(sbMessage, Messages.getString("LigeiroView.error.load.dependency.file")); //$NON-NLS-1$
					}

					items[i].setForeground(brokeItemColor);
				}
			}
		}

		// if it will not start FPA, then show messages, if any
		if (!startFPA && sbMessage.length() > 0)
		{
			showInformation(sbMessage.toString());
		}

		return sbMessage;
	}

	private void updateFilesSumamary(LoadReport loadReport)
	{
		SummaryElement element = null;

		if (loadReport.getType().equals(br.ufrj.cos.pinel.ligeiro.common.Constants.XML_CLASS))
		{
			element = summaryProvider.getSummaryClass();
		}
		else if (loadReport.getType().equals(br.ufrj.cos.pinel.ligeiro.common.Constants.XML_DEPENDENCY))
		{
			element = summaryProvider.getSummaryDependency();
		}
		else if (loadReport.getType().equals(br.ufrj.cos.pinel.ligeiro.common.Constants.XML_ENTITY))
		{
			element = summaryProvider.getSummaryEntity();
		}
		else if (loadReport.getType().equals(br.ufrj.cos.pinel.ligeiro.common.Constants.XML_SERVICE))
		{
			element = summaryProvider.getSummaryService();
		}
		else if (loadReport.getType().equals(br.ufrj.cos.pinel.ligeiro.common.Constants.XML_USE_CASE))
		{
			element = summaryProvider.getSummaryUseCase();
		}

		if (element != null)
		{
			element.setType(Util.getLoadReportType(loadReport.getType(), loadReport.getElementsRead() > 1));
			element.addTotal(loadReport.getElementsRead());

			summaryTable.refresh();
		}
	}
}