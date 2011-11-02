package br.ufrj.cos.pinel.ligeiro.plugin.perspectives;

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

	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout pageLayout)
	{
		pageLayout.addView(JavaUI.ID_PACKAGES, IPageLayout.LEFT, 0.18f, pageLayout.getEditorArea());
		pageLayout.setEditorAreaVisible(false);

		IFolderLayout bot = pageLayout.createFolder(TOP_RIGHT, IPageLayout.TOP, 0.76f, pageLayout.getEditorArea());

		bot.addView(LigeiroView.ID);
		bot.addView(IConsoleConstants.ID_CONSOLE_VIEW);
	}
}
