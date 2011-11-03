package br.ufrj.cos.pinel.ligeiro.plugin.perspectives;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import br.ufrj.cos.pinel.ligeiro.plugin.views.LigeiroView;

/**
 * @author Roque Pinel
 *
 */
public class LigeiroPerspectiveFactory implements IPerspectiveFactory
{
	private static final String TOP_RIGHT = "topRight";
	private static final String BOTTOM_RIGHT = "bottomRight";
	private static final String LEFT = "left";

	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout pageLayout)
	{
		pageLayout.setEditorAreaVisible(false);

		pageLayout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
		pageLayout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");

		pageLayout.addShowViewShortcut(LigeiroView.ID);
		pageLayout.addShowViewShortcut(JavaUI.ID_PACKAGES);
		pageLayout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		pageLayout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		pageLayout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		pageLayout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);

		pageLayout.addPerspectiveShortcut(JavaUI.ID_PERSPECTIVE);
		pageLayout.addPerspectiveShortcut(IDebugUIConstants.ID_DEBUG_PERSPECTIVE);

		IFolderLayout left = pageLayout.createFolder(LEFT, IPageLayout.LEFT, 0.195f, pageLayout.getEditorArea());
		left.addView(JavaUI.ID_PACKAGES);
	

		IFolderLayout topRight = pageLayout.createFolder(TOP_RIGHT, IPageLayout.TOP, 0.84f, pageLayout.getEditorArea());
		topRight.addView(LigeiroView.ID);
		topRight.addView(IConsoleConstants.ID_CONSOLE_VIEW);

		IFolderLayout bottomRight = pageLayout.createFolder(BOTTOM_RIGHT, IPageLayout.BOTTOM, 0.4f, pageLayout.getEditorArea());
		bottomRight.addView(IPageLayout.ID_PROP_SHEET);
	}
}
