package br.ufrj.cos.pinel.ligeiro.plugin.common;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import br.ufrj.cos.pinel.ligeiro.plugin.LigeiroPlugin;
import br.ufrj.cos.pinel.ligeiro.plugin.views.Messages;

public class ConsoleUtil
{
	public static MessageConsole getConsole()
	{
		return LigeiroPlugin.findConsole(Messages.getString("LigeiroView.console.title"));
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
		out.print(Messages.getString("LigeiroView.console.file"));
		out.print(": ");
		out.println(fileName);
	}

	public static void writeElements(Control control, int elementsRead, String type)
	{
		MessageConsoleStream out = getConsole().newMessageStream();
		out.setColor(new Color(control.getShell().getDisplay(),
				Constants.COLOR_DARK_ORANGE2_R, Constants.COLOR_DARK_ORANGE2_G, Constants.COLOR_DARK_ORANGE2_B));
		out.print("\t" + elementsRead);
		out.print(" ");
		out.print(Util.getLoadReportType(type, elementsRead > 1));
		out.print(" ");
		out.print(Messages.getString("LigeiroView.console.read"));
		out.println(".");
	}

	public static void writeTableResume(Control control, String tableName, int elementsRead)
	{
		MessageConsoleStream out = getConsole().newMessageStream();
		out.setColor(new Color(control.getShell().getDisplay(),
				Constants.COLOR_STEEL_BLUE1_R, Constants.COLOR_STEEL_BLUE1_G, Constants.COLOR_STEEL_BLUE1_B));
		out.print(tableName);
		out.print(": " + elementsRead);
		out.print(" ");
		if (elementsRead > 1)
			out.print(Messages.getString("LigeiroView.console.element.plural"));
		else
			out.print(Messages.getString("LigeiroView.console.element"));
		out.println(".");
	}
}
