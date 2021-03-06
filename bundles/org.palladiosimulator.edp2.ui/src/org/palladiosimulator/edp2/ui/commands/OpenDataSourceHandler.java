package org.palladiosimulator.edp2.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.palladiosimulator.edp2.ui.wizards.datasource.OpenDataSourceWizard;

/**
 * Command for opening the OpenDataSourceWizard.
 * 
 * @author groenda, Sebastian Lehrig
 */
public class OpenDataSourceHandler extends AbstractHandler implements IHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), new OpenDataSourceWizard());
        dialog.open();
        return null;
    }
}
