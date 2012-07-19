package br.ufrj.cos.pinel.ligeiro.plugin.common;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import br.ufrj.cos.pinel.ligeiro.plugin.LigeiroPlugin;
import br.ufrj.cos.pinel.ligeiro.plugin.messages.Messages;

public class ConsoleUtil
{
	public static MessageConsole getConsole()
	{
		return LigeiroPlugin.findConsole(Messages.LigeiroView_console_title);
	}

	public static void clearConsole()
	{
		getConsole().clearConsole();
	}

	public static void writeSection(Control control, String message)
	{
		MessageConsoleStream out = getConsole().newMessageStream();
		out.setColor(new Color(control.getShell().getDisplay(),
				Constants.COLOR_LIME_GREEN_R, Constants.COLOR_LIME_GREEN_G, Constants.COLOR_LIME_GREEN_B));
		out.println(message);
	}

	public static void writeFile(Control control, String fileName)
	{
		MessageConsoleStream out = getConsole().newMessageStream();
		out.setColor(new Color(control.getShell().getDisplay(),
				Constants.COLOR_STEEL_BLUE1_R, Constants.COLOR_STEEL_BLUE1_G, Constants.COLOR_STEEL_BLUE1_B));
		out.print(Messages.LigeiroView_console_file);
		out.print(": "); //$NON-NLS-1$
		out.println(fileName);
	}

	public static void writeElements(Control control, int elementsRead, String type)
	{
		MessageConsoleStream out = getConsole().newMessageStream();
		out.setColor(new Color(control.getShell().getDisplay(),
				Constants.COLOR_DARK_ORANGE2_R, Constants.COLOR_DARK_ORANGE2_G, Constants.COLOR_DARK_ORANGE2_B));
		out.print("\t" + elementsRead); //$NON-NLS-1$
		out.print(" "); //$NON-NLS-1$
		out.print(Util.getLoadReportType(type, elementsRead > 1));
		out.print(" "); //$NON-NLS-1$
		out.print(Messages.LigeiroView_console_read);
		out.println("."); //$NON-NLS-1$
	}

	public static void writeTableResume(Control control, String tableName, int elementsRead)
	{
		MessageConsoleStream out = getConsole().newMessageStream();
		out.setColor(new Color(control.getShell().getDisplay(),
				Constants.COLOR_STEEL_BLUE1_R, Constants.COLOR_STEEL_BLUE1_G, Constants.COLOR_STEEL_BLUE1_B));
		out.print(tableName);
		out.print(": " + elementsRead); //$NON-NLS-1$
		out.print(" "); //$NON-NLS-1$
		if (elementsRead > 1)
			out.print(Messages.LigeiroView_console_element_plural);
		else
			out.print(Messages.LigeiroView_console_element);
		out.println("."); //$NON-NLS-1$
	}
}
