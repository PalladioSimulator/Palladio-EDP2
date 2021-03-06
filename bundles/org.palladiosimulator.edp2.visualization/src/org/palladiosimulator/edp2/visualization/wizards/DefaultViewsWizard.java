/**
 * 
 */
package org.palladiosimulator.edp2.visualization.wizards;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.palladiosimulator.edp2.datastream.IDataSource;
import org.palladiosimulator.edp2.datastream.chaindescription.ChainDescription;

@Deprecated
/**
 * A wizard which is opened upon selection/doubleclick on an element containing
 * Experiment Data. Provides possible "Default-Combinations" of
 * Filter/Adapter/Editor which allows to visualize the the selected Data in a
 * appropriate way.
 * 
 * @author Dominik Ernst
 * 
 */
public class DefaultViewsWizard extends Wizard implements INewWizard {

	/**
	 * The page, which displays the list of available filters.
	 */
	SelectDefaultCombinationsPage selectCombinationsPage;

	/**
	 * The source, which is associated with the selected RawMeasurements.
	 */
	IDataSource source;

	/**
	 * Variable to indicate, if the user is allowed to finish the Wizard.
	 */
	boolean finishable;

	/**
	 * The default-combination, which was selected by the user.
	 */
	ChainDescription selectedDefault;

	/**
	 * Sets the <finishable> attribute to the specified value.
	 * 
	 * @param finishable
	 *            a boolean value
	 */
	public void setFinishable(final boolean finishable) {
		this.finishable = finishable;
	}

	/**
	 * Constructor assigning the source and assuring the wizard cannot be
	 * finished instantly.
	 */
	public DefaultViewsWizard(final IDataSource source) {
		setWindowTitle("Select a Visualization");
		this.source = source;
		this.finishable = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean performFinish() {
		if (canFinish()) {
			// Set title of visualization input to the name of the chain; The direct name of the property is used to prevent dependencies to JFreeChart representation.
			@SuppressWarnings("rawtypes")
			Map old = new HashMap();
			old.putAll(getSelectedDefault().getVisualizationInput().getProperties());
			old.put("title", selectedDefault.getChainName());
			getSelectedDefault().getVisualizationInput().setProperties(old);
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		selectCombinationsPage = new SelectDefaultCombinationsPage(
				"Select a Visualization", source);
		addPage(selectCombinationsPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#needsPreviousAndNextButtons()
	 */
	@Override
	public boolean needsPreviousAndNextButtons() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPageControls(final Composite pageContainer) {
		super.createPageControls(pageContainer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(final IWorkbench workbench,
			final IStructuredSelection selection) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		return finishable;
	}

	/**
	 * @return the selected Default-combination
	 */
	public ChainDescription getSelectedDefault() {
		return selectedDefault;
	}

	/**
	 * @param selectedDefault
	 *            the selectedDefault to set
	 */
	public void setSelectedDefault(final ChainDescription selectedDefault) {
		this.selectedDefault = selectedDefault;
	}

}
