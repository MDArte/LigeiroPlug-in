package br.ufrj.cos.pinel.ligeiro.plugin.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.internal.ui.viewsupport.SelectionProviderMediator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredViewer;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
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
import br.ufrj.cos.pinel.ligeiro.exception.LigeiroException;
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
import br.ufrj.cos.pinel.ligeiro.plugin.messages.Messages;
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

	private TableViewer statisticTable;
	private Button statisticAddButton;
	private Button statisticRemoveButton;

	private TableViewer dependencyTable;
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
	private Calendar startTime;
	private Calendar endTime;

	private FPAReport fpaReport;

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

		fpaReport = null;
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText(Messages.LigeiroView_title);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		form.getBody().setLayout(layout);

		createFilesSection(form.getBody());

		createControlSection(form.getBody());

		createResultSection(form.getBody());

		appendActions();

		loadPreviousInformation();

		StructuredViewer[] trackedViewers = new StructuredViewer[]
			{
				statisticTable,
				dependencyTable,
				summaryTable,
				dfTable,
				tfTable
			};
		ISelectionProvider selectionProvider = new SelectionProviderMediator(trackedViewers, statisticTable);
		getSite().setSelectionProvider(selectionProvider); 

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
		filesSection.setText(Messages.LigeiroView_files_section_title);
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
		tableLabel.setText(Messages.LigeiroView_files_statistic_table_label);
		tableLabel.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.widthHint = 300;

		statisticTable = new TableViewer(statisticComposite, SWT.MULTI);
		statisticTable.getTable().setLayoutData(gd);
		LigeiroPreferences.loadStatisticFiles(statisticTable.getTable());

		toolkit.paintBordersFor(statisticComposite);

		DropTarget dropTarget = new DropTarget(statisticTable.getTable(), DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
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
							if (Util.addInputFile(statisticTable.getTable(), path)
								&& !statisticRemoveButton.isEnabled())
							{
								statisticRemoveButton.setEnabled(true);
							}
						}

						if (paths.length > 0)
						{
							LigeiroPreferences.saveStatisticFiles(statisticTable.getTable());
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
		statisticAddButton = toolkit.createButton(buttonComposite, Messages.LigeiroView_files_statistic_add_button_label, SWT.PUSH);//$NON-NLS-1$
		statisticAddButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		statisticAddButton.setToolTipText(Messages.LigeiroView_files_statistic_add_button_tip);
		statisticAddButton.setLayoutData(gd);
		statisticAddButton.addSelectionListener(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent event)
				{
					List<String> paths = createFileCheckedTreeSelectionDialog(
							Messages.LigeiroView_files_statistic_add_dialog_title,
							Messages.LigeiroView_files_add_dialog_message);

					if (paths != null)
					{
						for (String path : paths)
						{
							if (Util.addInputFile(statisticTable.getTable(), path)
								&& !statisticRemoveButton.isEnabled())
							{
								statisticRemoveButton.setEnabled(true);
							}
						}

						if (paths.size() > 0)
						{
							LigeiroPreferences.saveStatisticFiles(statisticTable.getTable());
						}
					}
				}
				public void widgetDefaultSelected(SelectionEvent event) { }
			}
		);

		// remove
		statisticRemoveButton = toolkit.createButton(buttonComposite, Messages.LigeiroView_files_statistic_remove_button_label, SWT.PUSH);
		statisticRemoveButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ELCL_REMOVE));
		statisticRemoveButton.setToolTipText(Messages.LigeiroView_files_statistic_remove_button_tip);
		statisticRemoveButton.setLayoutData(gd);
		statisticRemoveButton.addSelectionListener(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent event)
				{
					if (Util.removeInputFiles(statisticTable.getTable(), statisticTable.getTable().getSelection()))
					{
						LigeiroPreferences.saveStatisticFiles(statisticTable.getTable());

						if (statisticTable.getTable().getItemCount() == 0)
							statisticRemoveButton.setEnabled(false);
					}
				}
				public void widgetDefaultSelected(SelectionEvent event) { }
			}
		);

		if (statisticTable.getTable().getItemCount() == 0)
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
		tableLabel.setText(Messages.LigeiroView_files_dependency_table_label);
		tableLabel.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.widthHint = 300;

		dependencyTable = new TableViewer(dependencyComposite, SWT.MULTI);
		dependencyTable.getTable().setLayoutData(gd);
		LigeiroPreferences.loadDependencyFiles(dependencyTable.getTable());

		toolkit.paintBordersFor(dependencyComposite);

		DropTarget dropTarget = new DropTarget(dependencyTable.getTable(), DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
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
							if (Util.addInputFile(dependencyTable.getTable(), path)
								&& !dependencyRemoveButton.isEnabled())
							{
								dependencyRemoveButton.setEnabled(true);
							}
						}

						if (paths.length > 0)
						{
							LigeiroPreferences.saveDependencyFiles(dependencyTable.getTable());
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
		dependencyAddButton = toolkit.createButton(buttonComposite, Messages.LigeiroView_files_dependency_add_button_label, SWT.PUSH);
		dependencyAddButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		dependencyAddButton.setToolTipText(Messages.LigeiroView_files_dependency_add_button_tip);
		dependencyAddButton.setLayoutData(gd);
		dependencyAddButton.addSelectionListener(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent event)
				{
					List<String> paths = createFileCheckedTreeSelectionDialog(
							Messages.LigeiroView_files_dependency_add_dialog_title,
							Messages.LigeiroView_files_add_dialog_message);

					if (paths != null)
					{
						for (String path : paths)
						{
							if (Util.addInputFile(dependencyTable.getTable(), path)
								&& !dependencyRemoveButton.isEnabled())
							{
								dependencyRemoveButton.setEnabled(true);
							}
						}

						if (paths.size() > 0)
						{
							LigeiroPreferences.saveDependencyFiles(dependencyTable.getTable());
						}
					}
				}
				public void widgetDefaultSelected(SelectionEvent event) { }
			}
		);

		// remove
		dependencyRemoveButton = toolkit.createButton(buttonComposite, Messages.LigeiroView_files_dependency_remove_button_label, SWT.PUSH);
		dependencyRemoveButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ELCL_REMOVE));
		dependencyRemoveButton.setToolTipText(Messages.LigeiroView_files_dependency_remove_button_tip);
		dependencyRemoveButton.setLayoutData(gd);
		dependencyRemoveButton.addSelectionListener(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent event)
				{
					if (Util.removeInputFiles(dependencyTable.getTable(), dependencyTable.getTable().getSelection()))
					{
						LigeiroPreferences.saveDependencyFiles(dependencyTable.getTable());

						if (dependencyTable.getTable().getItemCount() == 0)
							dependencyRemoveButton.setEnabled(false);
					}
				}
				public void widgetDefaultSelected(SelectionEvent event){ }
			}
		);

		if (dependencyTable.getTable().getItemCount() == 0)
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
		tableLabel.setText(Messages.LigeiroView_files_summary_table_label);

		ImageHyperlink imageHyperLink = new ImageHyperlink(toolbarComposite, SWT.LEFT);
		imageHyperLink.setBackgroundImage(toolbarComposite.getBackgroundImage());
		imageHyperLink.setToolTipText(Messages.LigeiroView_files_summary_clear_tip);
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
				Messages.LigeiroView_files_summary_table_type,
				Constants.SUMMARY_COLUMNS_WIDTH[position], position++);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SummaryElement summaryElement = (SummaryElement) element;
				return summaryElement.getType();
			}
		});

		col = Util.createTableViewerColumn(summaryTable, summaryTableComparator,
				Messages.LigeiroView_files_summary_table_total,
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
		controlSection.setText(Messages.LigeiroView_control_section_title);
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
		configurationFileLabel.setText(Messages.LigeiroView_control_configuration_file_label);
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
							showInformation(Messages.LigeiroView_error_many_configuration_files);
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
				Messages.LigeiroView_control_configuration_file_button_label, SWT.PUSH);
		configurationFileButton.addSelectionListener(
			new SelectionListener()
			{
				public void widgetSelected(SelectionEvent event)
				{
					List<String> paths = createFileCheckedTreeSelectionDialog(
							Messages.LigeiroView_control_configuration_add_dialog_title,
							Messages.LigeiroView_control_configuration_add_dialog_message);

					if (paths != null)
					{
						if (paths.size() > 1)
						{
							showInformation(Messages.LigeiroView_error_many_configuration_files);
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
		resultSection.setText(Messages.LigeiroView_results_section_title);
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
		imageHyperLink.setToolTipText(Messages.LigeiroView_results_toolbar_export_csv);
		imageHyperLink.setImage(LigeiroPlugin.getImageDescriptor(LigeiroPlugin.IMG_CSV).createImage());
		imageHyperLink.addHyperlinkListener(new HyperlinkAdapter()
		{
			public void linkActivated(HyperlinkEvent e)
			{
				exportCSV();
			}
		});

		imageHyperLink = new ImageHyperlink(toolbarComposite, SWT.LEFT);
		imageHyperLink.setBackgroundImage(toolbarComposite.getBackgroundImage());
		imageHyperLink.setToolTipText(Messages.LigeiroView_results_toolbar_clear_tip);
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
		label.setText(Messages.LigeiroView_results_data_function_total_label);
		unadjustedDFTotalText = new Text(groupComposite, SWT.BORDER);
		unadjustedDFTotalText.setLayoutData(gd);
		unadjustedDFTotalText.setEditable(false);
		unadjustedDFTotalText.setBackground(fieldColor);
		unadjustedDFTotalText.setSize(Constants.RESULT_FIELD_WIDTH, Constants.RESULT_FIELD_HEIGHT);
		unadjustedDFTotalText.setToolTipText(Messages.LigeiroView_results_data_function_total_tip);

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.LigeiroView_results_plus);
		label.setForeground(mathOperationColor);

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.LigeiroView_results_transaction_function_total_label);
		unadjustedTFTotalText = new Text(groupComposite, SWT.BORDER);
		unadjustedTFTotalText.setLayoutData(gd);
		unadjustedTFTotalText.setEditable(false);
		unadjustedTFTotalText.setBackground(fieldColor);
		unadjustedTFTotalText.setSize(Constants.RESULT_FIELD_WIDTH, Constants.RESULT_FIELD_HEIGHT);
		unadjustedTFTotalText.setToolTipText(Messages.LigeiroView_results_transaction_function_total_tip);

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.LigeiroView_results_equals);
		label.setForeground(mathOperationColor);

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.LigeiroView_results_unadjusted_fpa_total_label);
		unadjustedFPATotalText = new Text(groupComposite, SWT.BORDER);
		unadjustedFPATotalText.setLayoutData(gd);
		unadjustedFPATotalText.setEditable(false);
		unadjustedFPATotalText.setBackground(fieldColor);
		unadjustedFPATotalText.setSize(Constants.RESULT_FIELD_WIDTH, Constants.RESULT_FIELD_HEIGHT);
		unadjustedFPATotalText.setToolTipText(Messages.LigeiroView_results_unadjusted_fpa_total_tip);

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.LigeiroView_results_times);
		label.setForeground(mathOperationColor);

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.LigeiroView_results_vaf_label);
		vafText = new Text(groupComposite, SWT.BORDER);
		vafText.setLayoutData(gd);
		vafText.setEditable(false);
		vafText.setBackground(fieldColor);
		vafText.setSize(20, 50);
		vafText.setToolTipText(Messages.LigeiroView_results_vaf_tip);

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.LigeiroView_results_equals);
		label.setForeground(mathOperationColor);

		groupComposite = toolkit.createComposite(allComposite, SWT.WRAP);
		groupComposite.setLayout(layout);
		label = new Label(groupComposite, SWT.NONE);
		label.setText(Messages.LigeiroView_results_adjusted_fpa_total_label);
		adjustedFPATotalText = new Text(groupComposite, SWT.BORDER);
		adjustedFPATotalText.setLayoutData(gd);
		adjustedFPATotalText.setEditable(false);
		adjustedFPATotalText.setBackground(fieldColor);
		adjustedFPATotalText.setSize(Constants.RESULT_FIELD_WIDTH, Constants.RESULT_FIELD_HEIGHT);
		adjustedFPATotalText.setToolTipText(Messages.LigeiroView_results_adjusted_fpa_total_tip);
	}

	private void createResultColumns(final Composite parent, final TableViewer viewer, final ResultTableComparator comparator, final boolean isDataFunction)
	{
		int position = 0;
		String message;

		if (isDataFunction)
			message = Messages.LigeiroView_results_table_data_function;
		else
			message = Messages.LigeiroView_results_table_transaction_function;

		TableViewerColumn col = Util.createTableViewerColumn(viewer, comparator, message, Constants.RESULT_COLUMNS_WIDTH[position], position++);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return p.getElement();
			}
		});

		col = Util.createTableViewerColumn(viewer, comparator,
				Messages.LigeiroView_results_table_type,
				Constants.RESULT_COLUMNS_WIDTH[position], position++);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return p.getType();
			}
		});

		if (isDataFunction)
			message = Messages.LigeiroView_results_table_ret;
		else
			message = Messages.LigeiroView_results_table_ftr;

		col = Util.createTableViewerColumn(viewer, comparator, message, Constants.RESULT_COLUMNS_WIDTH[position], position++);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return Integer.toString(p.getRet_ftr());
			}
		});

		col = Util.createTableViewerColumn(viewer, comparator,
				Messages.LigeiroView_results_table_det,
				Constants.RESULT_COLUMNS_WIDTH[position], position++);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return Integer.toString(p.getDet());
			}
		});

		col = Util.createTableViewerColumn(viewer, comparator,
				Messages.LigeiroView_results_table_complexity,
				Constants.RESULT_COLUMNS_WIDTH[position], position++);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Result p = (Result) element;
				return p.getComplexity();
			}
		});

		col = Util.createTableViewerColumn(viewer, comparator,
				Messages.LigeiroView_results_table_complexity_value,
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
				ActionJob job = new ActionJob(Messages.LigeiroView_job_start_fpa_name, true);

				job.setUser(true);
				job.schedule();
			}
		};
		action.setText(Messages.LigeiroView_action_start_fpa_label);
		action.setToolTipText(Messages.LigeiroView_action_start_fpa_tip);
		action.setImageDescriptor(LigeiroPlugin.getImageDescriptor(LigeiroPlugin.IMG_RUN));
		bars.getToolBarManager().add(action);

		// load files
		action = new Action()
		{
			public void run()
			{
				ActionJob job = new ActionJob(Messages.LigeiroView_job_load_files_name, false);

				job.setUser(true);
				job.schedule();
			}
		};
		action.setText(Messages.LigeiroView_action_load_files_label);
		action.setToolTipText(Messages.LigeiroView_action_load_files_tip);
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
		action.setText(Messages.LigeiroView_action_reset_fields_label);
		action.setToolTipText(Messages.LigeiroView_action_reset_fields_tip);
		action.setImageDescriptor(LigeiroPlugin.getImageDescriptor(LigeiroPlugin.IMG_TRASH));
		bars.getToolBarManager().add(action);
	}

	private void resetFields()
	{
		LigeiroPreferences.clear();

		statisticTable.getTable().removeAll();
		dependencyTable.getTable().removeAll();

		configurationFileText.setText(""); //$NON-NLS-1$
	}

	private void resetSummary()
	{
		summaryProvider.clear();
		summaryTable.refresh();
	}

	private void resetResults()
	{
		fpaReport = null;

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

	private void exportCSV()
	{
		if (fpaReport == null)
		{
			showInformation(Messages.LigeiroView_error_no_fpa_report);
			return;
		}

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(form.getShell(),
				new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		dialog.setTitle(Messages.LigeiroView_results_export_csv_dialog_title);
		dialog.setMessage(Messages.LigeiroView_results_export_csv_dialog_message);
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		int status = dialog.open();

		if (status == Window.OK && dialog.getResult() != null)
		{
			Object[] results = dialog.getResult();

			if (results.length > 0)
			{
				if (results[0] instanceof org.eclipse.core.resources.IProject
					|| results[0] instanceof org.eclipse.core.resources.IFolder)
				{
					org.eclipse.core.resources.IResource resource = (org.eclipse.core.resources.IResource) results[0];

					String directoryPath = resource.getLocationURI().getPath();

					try
					{
						br.ufrj.cos.pinel.ligeiro.common.Util.writeCSVReport(fpaReport, directoryPath + Messages.LigeiroView_results_export_csv_default_file);
					}
					catch (LigeiroException e)
					{
						showInformation(Messages.LigeiroView_error_export_csv);
					}
				}
				else
				{
					showInformation(Messages.LigeiroView_error_type_not_folder);
					return;
				}
			}


		}
	}

	private void loadPreviousInformation()
	{
		configurationFileText.setText(LigeiroPreferences.getFPAConfigurationFile());
	}

	private void showInformation(String message)
	{
		MessageDialog.openInformation(form.getShell(), Messages.LigeiroView_title, message);
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
					showInformation(Messages.LigeiroView_error_type_not_file);
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

		showInformation(Messages.LigeiroView_error_type_not_file);

		return false;
	}

	/**
	 * Job to execute the action in background.
	 * 
	 * @author Roque Pinel
	 *
	 */
	class ActionJob extends Job
	{
		boolean startFPA;

		boolean canceled;

		public ActionJob(String name, boolean startFPA)
		{
			super(name);

			this.startFPA = startFPA;
			this.canceled = false;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor)
		{
			startTime = Calendar.getInstance();
			try
			{
				if (startFPA)
				{
					startFPAA();
				}
				else
				{
					loadStatisticAndDependencyFiles();
				}
			}
			finally
			{
				endTime = Calendar.getInstance();

				/*
				 * (Sync) Updating results.
				 */
				Display.getDefault().asyncExec(new UpdateResults()
				{
					public void run()
					{
						ConsoleUtil.writeSection(form, Messages.LigeiroView_console_runtime + " "
							+ Long.toString(endTime.getTimeInMillis() - startTime.getTimeInMillis())
							+ " " + Messages.LigeiroView_console_runtime_ms);
					}
				});
			}

			return Status.OK_STATUS;
		}

		@Override
		protected void canceling()
		{
			this.canceled = true;
		}

		protected boolean isCanceled()
		{
			if (this.canceled)
			{
				/*
				 * (sync) Cleaning all data before cancelling.
				 * 
				 */
				Display.getDefault().syncExec(new UpdateResults()
				{
					public void run()
					{
						if (startFPA)
						{
							// cleaning the function tables 
							dfTableProvider.clear();
							tfTableProvider.clear();

							// cleaning the results
							unadjustedDFTotalText.setText("");
							unadjustedTFTotalText.setText("");
							unadjustedFPATotalText.setText("");
							vafText.setText("");
							adjustedFPATotalText.setText("");
						}

						if (core == null)
							core.clear();

						// cleaning the summary table
						summaryProvider.clear();
						summaryTable.refresh();
					}
				});
			}

			return this.canceled;
		}

		private void startFPAA()
		{
			StringBuilder sbMessage = loadStatisticAndDependencyFiles();

			// if is canceled
			if (sbMessage == null && this.isCanceled())
				return;

			/*
			 * (Sync) Verifing if input files are ready.
			 * 
			 */
			UpdateResults updateResults = new UpdateResults(sbMessage)
			{
				public void run()
				{
					StringBuilder sbMessage = (StringBuilder) this.obj1;

					if (statisticTable.getTable().getItemCount() == 0)
					{
						Util.appendMessage(sbMessage, Messages.LigeiroView_error_no_statistic_file);
					}
					if (dependencyTable.getTable().getItemCount() == 0)
					{
						Util.appendMessage(sbMessage, Messages.LigeiroView_error_no_dependency_file);
					}
					if (br.ufrj.cos.pinel.ligeiro.common.Util.isEmptyOrNull(configurationFileText.getText()))
					{
						Util.appendMessage(sbMessage, Messages.LigeiroView_error_no_configuration_file);
					}

					ConsoleUtil.writeSection(form, Messages.LigeiroView_console_reading_configuration_file);

					this.obj2 = configurationFileText.getText();
				}
			};
			Display.getDefault().syncExec(updateResults);

			String configurationFileStr = (String) updateResults.obj2;

			FPAConfig fpaConfig = null;

			try
			{
				if (!br.ufrj.cos.pinel.ligeiro.common.Util.isEmptyOrNull(configurationFileStr))
				{
					fpaConfig = core.readFPAConfiguration(configurationFileStr);

					/*
					 * (Async) Marking the field as good input.
					 * 
					 */
					Display.getDefault().asyncExec(new UpdateResults()
					{
						public void run()
						{
							Color goodItemColor = new Color(form.getShell().getDisplay(),
									Constants.COLOR_BLACK_R, Constants.COLOR_BLACK_G, Constants.COLOR_BLACK_B);

							configurationFileText.setForeground(goodItemColor);
						}
					});
				}
			}
			catch (ReadXMLException e)
			{
				/*
				 * (Async) Marking the field as broken input.
				 * 
				 */
				Display.getDefault().asyncExec(new UpdateResults(sbMessage)
				{
					public void run()
					{
						Util.appendMessage((StringBuilder) this.obj1, Messages.LigeiroView_error_load_configuration_file);

						Color brokenItemColor = new Color(form.getShell().getDisplay(),
								Constants.COLOR_RED_R, Constants.COLOR_RED_G, Constants.COLOR_RED_B);

						configurationFileText.setForeground(brokenItemColor);
					}
				});
			}

			if (sbMessage.length() > 0)
			{
				/*
				 * (Async) Showing the error messages.
				 * 
				 */
				Display.getDefault().asyncExec(new UpdateResults(sbMessage)
				{
					public void run()
					{
						showInformation(((StringBuilder) this.obj1).toString());
					}
				});

				return;
			}

			/*
			 * (Sync) Wrinting the console.
			 * and clearing the DF table.
			 */
			Display.getDefault().syncExec(new UpdateResults()
			{
				public void run()
				{
					ConsoleUtil.writeSection(form, Messages.LigeiroView_console_starting_fpa);

					dfTableProvider.clear();
				}
			});

			fpaReport = core.startFunctionPointAnalysis(fpaConfig);

			for (ReportResult reportResult : fpaReport.getDFReport())
			{
				if (this.isCanceled())
					return;

				Result result = new Result();
				result.setNamespace(reportResult.getNamespace());
				result.setElement(reportResult.getElement());
				result.setType(reportResult.getType());
				result.setRet_ftr(reportResult.getRet_ftr());
				result.setDet(reportResult.getDet());
				result.setComplexity(reportResult.getComplexity());
				result.setComplexityValue(reportResult.getComplexityValue());

				/*
				 * (Async) Updating the table.
				 */
				Display.getDefault().asyncExec(new UpdateResults(result)
				{
					public void run()
					{
						dfTableProvider.addResult((Result) this.obj1);
					}
				});
			}

			/*
			 * (Sync) Updating results.
			 */
			Display.getDefault().syncExec(new UpdateResults(fpaReport)
			{
				public void run()
				{
					FPAReport fpaReport = (FPAReport) this.obj1;

					dfTable.refresh();

					ConsoleUtil.writeTableResume(form, Messages.LigeiroView_results_table_data_function, fpaReport.getDFReport().size());

					unadjustedDFTotalText.setText(Integer.toString(fpaReport.getDFReportTotal()));

					tfTableProvider.clear();
				}
			});

			for (ReportResult reportResult : fpaReport.getTFReport())
			{
				if (this.isCanceled())
					return;

				Result result = new Result();
				result.setNamespace(reportResult.getNamespace());
				result.setElement(reportResult.getElement());
				result.setType(reportResult.getType());
				result.setRet_ftr(reportResult.getRet_ftr());
				result.setDet(reportResult.getDet());
				result.setComplexity(reportResult.getComplexity());
				result.setComplexityValue(reportResult.getComplexityValue());

				/*
				 * (Async) Updating the table.
				 */
				Display.getDefault().asyncExec(new UpdateResults(result)
				{
					public void run()
					{
						tfTableProvider.addResult((Result) this.obj1);
					}
				});
			}

			/*
			 * (Sync) Updating results.
			 */
			Display.getDefault().asyncExec(new UpdateResults(fpaReport, fpaConfig)
			{
				public void run()
				{
					FPAReport fpaReport = (FPAReport) this.obj1;
					FPAConfig fpaConfig = (FPAConfig) this.obj2;

					tfTable.refresh();

					ConsoleUtil.writeTableResume(form, Messages.LigeiroView_results_table_transaction_function,fpaReport.getTFReport().size());

					unadjustedTFTotalText.setText(Integer.toString(fpaReport.getTFReportTotal()));

					unadjustedFPATotalText.setText(Integer.toString(fpaReport.getReportTotal()));

					vafText.setText(Double.toString(fpaConfig.getVaf()));

					adjustedFPATotalText.setText(Double.toString(fpaReport.getAdjustedReportTotal(fpaConfig.getVaf())));

					ConsoleUtil.writeSection(form, Messages.LigeiroView_console_done);
				}
			});
		}

		private StringBuilder loadStatisticAndDependencyFiles()
		{
			if (core == null)
				core = new Core();

			core.clear();

			StringBuilder sbMessage = new StringBuilder();

			/*
			 * (Sync) Wait for the tables and cosole to be ready.
			 * 
			 */
			UpdateResults updateResults = new UpdateResults()
			{
				public void run()
				{
					// cleaning the summary table
					summaryProvider.clear();
					summaryTable.refresh();

					// getting the console ready
					ConsoleUtil.clearConsole();
					ConsoleUtil.writeSection(form, Messages.LigeiroView_console_reading_statistic_files);

					// getting the values from the table
					this.obj1 = statisticTable.getTable().getItems();
				}
			};
			Display.getDefault().syncExec(updateResults);

			TableItem[] items = (TableItem[]) updateResults.obj1;

			boolean hasBrokenItem = false;
			for (int i = 0; i < items.length; i++)
			{
				if (this.isCanceled())
					return null;

				/*
				 * (Sync) Reading the data from the table.
				 */
				updateResults = new UpdateResults(items[i])
				{
					public void run()
					{
						this.obj1 = ((TableItem) obj1).getData();
					}
				};
				Display.getDefault().syncExec(updateResults);

				if (updateResults.obj1 instanceof InputFile)
				{
					InputFile inputFile = (InputFile) updateResults.obj1;

					try
					{
						final LoadReport loadReport = core.readStatistics(inputFile.getPath());

						/*
						 * (Async) Updating the tables.
						 */
						Display.getDefault().asyncExec(new UpdateResults(items[i])
						{
							public void run()
							{
								ConsoleUtil.writeFile(form, loadReport.getFileName());
								ConsoleUtil.writeElements(form, loadReport.getElementsRead(), loadReport.getType());

								Color goodItemColor = new Color(form.getShell().getDisplay(),
										Constants.COLOR_BLACK_R, Constants.COLOR_BLACK_G, Constants.COLOR_BLACK_B);

								((TableItem)this.obj1).setForeground(goodItemColor);

								updateFilesSumamary(loadReport);
							}
						});
					}
					catch (ReadXMLException e)
					{
						if (!hasBrokenItem)
						{
							hasBrokenItem = true;
							Util.appendMessage(sbMessage, Messages.LigeiroView_error_load_statistic_file);
						}

						/*
						 * (Async) Coloring the broken item.
						 */
						Display.getDefault().asyncExec(new UpdateResults(items[i])
						{
							public void run()
							{
								Color brokenItemColor = new Color(form.getShell().getDisplay(),
										Constants.COLOR_RED_R, Constants.COLOR_RED_G, Constants.COLOR_RED_B);

								((TableItem)this.obj1).setForeground(brokenItemColor);
							}
						});
					}
				}
			}

			/*
			 * (Sync) Getting the values from table.
			 */
			updateResults = new UpdateResults()
			{
				public void run()
				{
					// getting the values from the table
					this.obj1 = dependencyTable.getTable().getItems();
				}
			};
			Display.getDefault().syncExec(updateResults);

			items = (TableItem[]) updateResults.obj1;

			hasBrokenItem = false;
			for (int i = 0; i < items.length; i++)
			{
				if (this.isCanceled())
					return null;

				/*
				 * (Sync) Reading the data from the table.
				 */
				updateResults = new UpdateResults(items[i])
				{
					public void run()
					{
						this.obj1 = ((TableItem) obj1).getData();
					}
				};
				Display.getDefault().syncExec(updateResults);

				if (updateResults.obj1 instanceof InputFile)
				{
					InputFile inputFile = (InputFile) updateResults.obj1;

					try
					{
						final LoadReport loadReport = core.readDependencies(inputFile.getPath());

						/*
						 * (Async) Updating the tables.
						 */
						Display.getDefault().asyncExec(new UpdateResults(items[i])
						{
							public void run()
							{
								ConsoleUtil.writeFile(form, loadReport.getFileName());
								ConsoleUtil.writeElements(form, loadReport.getElementsRead(), loadReport.getType());

								Color goodItemColor = new Color(form.getShell().getDisplay(),
										Constants.COLOR_BLACK_R, Constants.COLOR_BLACK_G, Constants.COLOR_BLACK_B);

								((TableItem)this.obj1).setForeground(goodItemColor);

								updateFilesSumamary(loadReport);
							}
						});
					}
					catch (ReadXMLException e)
					{
						if (!hasBrokenItem)
						{
							hasBrokenItem = true;
							Util.appendMessage(sbMessage, Messages.LigeiroView_error_load_dependency_file);
						}

						/*
						 * (Async) Coloring the broken item.
						 */
						Display.getDefault().asyncExec(new UpdateResults(items[i])
						{
							public void run()
							{
								Color brokeItemColor = new Color(form.getShell().getDisplay(),
										Constants.COLOR_RED_R, Constants.COLOR_RED_G, Constants.COLOR_RED_B);

								((TableItem)this.obj1).setForeground(brokeItemColor);
							}
						});
					}
				}
			}

			// if it will not start FPA
			if (!this.startFPA)
			{
				/*
				 * (Async) Showing the final result, good or bad.
				 */
				Display.getDefault().asyncExec(new UpdateResults(sbMessage)
				{
					public void run()
					{
						StringBuilder sbMessage = (StringBuilder) this.obj1;

						// then show messages, if any
						if (sbMessage.length() > 0)
							showInformation(sbMessage.toString());
						else
							ConsoleUtil.writeSection(form, Messages.LigeiroView_console_done);
					}
				});
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

		/**
		 * Helps to update the results using
		 * the UI Thread.
		 * 
		 * @author Roque Pinel
		 */
		abstract class UpdateResults implements Runnable
		{
			Object obj1;
			Object obj2;

			public UpdateResults()
			{
				// empty
			}

			public UpdateResults(Object obj1)
			{
				this.obj1 = obj1;
			}

			public UpdateResults(Object obj1, Object obj2)
			{
				this.obj1 = obj1;
				this.obj2 = obj2;
			}
		}
	}
}