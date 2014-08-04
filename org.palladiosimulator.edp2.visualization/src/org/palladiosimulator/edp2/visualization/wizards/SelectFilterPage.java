/**
 * 
 */
package org.palladiosimulator.edp2.visualization.wizards;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.palladiosimulator.edp2.datastream.AbstractDataSource;
import org.palladiosimulator.edp2.datastream.filter.AbstractFilter;

/**
 * @author Dominik Ernst
 */
public class SelectFilterPage extends WizardPage implements ISelectionChangedListener {

    private static final Logger LOGGER = Logger.getLogger(SelectFilterPage.class.getCanonicalName());

    private static final String FILTER_EXTENSION_POINT_ID = "org.palladiosimulator.edp2.visualization.filter";

    private static final String FILTER_CLASS_ATTRIBUTE = "class";
    private static final String FILTER_WIZARD_ATTRIBUTE = "wizard";

    AbstractDataSource selectedSource;
    ArrayList<IFilterWizard> availableFilters;

    List filterList;
    IStatus selectionStatus;
    Status statusOK;
    IFilterWizard selectedFilterWizard;
    TableViewer filterViewer;
    AbstractFilter createdFilter;

    protected SelectFilterPage(final String pageName, final AbstractDataSource selectedSource) {
        super(pageName);
        this.selectedSource = selectedSource;
        setDescription("Select the Filter you wish to add.");
        statusOK = new Status(IStatus.OK, "not_used", 0, "", null);
        selectionStatus = statusOK;
    }

    @Override
    public boolean canFlipToNextPage() {
        return updatePageStatus().isOK();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets .Composite)
     */
    @Override
    public void createControl(final Composite parent) {
        // create the composite to hold the widgets
        GridData gd;
        final Composite composite = new Composite(parent, SWT.NONE);

        // create the desired layout for this wizard page
        final GridLayout gl = new GridLayout();
        composite.setLayout(gl);

        final Label label = new Label(composite, SWT.NONE);
        label.setText("Available Filters:");

        final SashForm sashForm = new SashForm(composite, SWT.HORIZONTAL);
        gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = 200;
        sashForm.setLayoutData(gd);

        filterViewer = new TableViewer(sashForm, SWT.BORDER);
        filterViewer.setContentProvider(new IStructuredContentProvider() {

            @Override
            public void dispose() {
                // TODO Auto-generated method stub

            }

            @Override
            public Object[] getElements(final Object inputElement) {
                return getApplicableFilters(selectedSource).toArray();
            }

            @Override
            public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
                // TODO Auto-generated method stub

            }
        });
        filterViewer.setLabelProvider(new ILabelProvider() {

            @Override
            public void addListener(final ILabelProviderListener listener) {
                // TODO Auto-generated method stub

            }

            @Override
            public void dispose() {
                // TODO Auto-generated method stub

            }

            @Override
            public Image getImage(final Object element) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getText(final Object element) {
                if (element != null) {
                    return ((IWizard) element).getWindowTitle();
                }
                return null;
            }

            @Override
            public boolean isLabelProperty(final Object element, final String property) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void removeListener(final ILabelProviderListener listener) {
                // TODO Auto-generated method stub

            }
        });

        filterViewer.setInput(getApplicableFilters(selectedSource));
        filterViewer.addSelectionChangedListener(this);

        // set the composite as the control for this page
        setControl(composite);
        updatePageStatus();
    }

    protected ArrayList<IFilterWizard> getApplicableFilters(final AbstractDataSource forSource) {
        availableFilters = new ArrayList<IFilterWizard>();
        // checks the extension registry for all registered filters and adds
        // them to the list
        final IConfigurationElement[] filterExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(
                FILTER_EXTENSION_POINT_ID);
        for (final IConfigurationElement e : filterExtensions) {
            Object w, o = null;
            try {
                w = e.createExecutableExtension(FILTER_WIZARD_ATTRIBUTE);
                o = e.createExecutableExtension(FILTER_CLASS_ATTRIBUTE);
                if (((AbstractFilter) o).getDataSource().isCompatibleWith(forSource.getMetricDesciption())) {
                    availableFilters.add((IFilterWizard) w);
                }
            } catch (final CoreException e1) {
                LOGGER.log(Level.SEVERE, "Error in creating an Object referenced in an extension.");
                throw new RuntimeException();
            }
            LOGGER.log(Level.INFO, o.toString());
        }
        return availableFilters;
    }

    /**
     * Method which is called when the "Next" Button in the Wizard is clicked. Must call
     * {@link IFilterWizard#setSourceFromCaller(AbstractDataSource, SelectFilterPage)}, where the
     * {@link AbstractDataSource} is the source handed from the RawMeasurements object, which was
     * selected in the first place and SelectFilterPage is a reference to {@link this} page.
     */
    @Override
    public IWizardPage getNextPage() {
        selectedFilterWizard.setSourceFromCaller(selectedSource, this);
        selectedFilterWizard.addPages();
        return selectedFilterWizard.getStartingPage();
    }

    @Override
    public void selectionChanged(final SelectionChangedEvent event) {
        selectionStatus = statusOK;
        final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        selectedFilterWizard = null;
        if (selection == null) {
            selectionStatus = new Status(IStatus.ERROR, "", 0, "Must select a Filter to proceed.", null);
        } else {
            selectedFilterWizard = (IFilterWizard) selection.getFirstElement();
            LOGGER.log(Level.INFO, selectedFilterWizard.getWindowTitle());
        }

        updatePageStatus();
        getContainer().updateButtons();

    }

    public IStatus updatePageStatus() {
        IStatus pageStatus = statusOK;
        switch (selectionStatus.getSeverity()) {
        case IStatus.OK:
            setErrorMessage(null);
            setMessage(selectionStatus.getMessage());
            pageStatus = statusOK;
            break;
        case IStatus.WARNING:
            setErrorMessage(null);
            setMessage(selectionStatus.getMessage(), WizardPage.WARNING);
            pageStatus = selectionStatus;
            break;
        case IStatus.INFO:
            setErrorMessage(null);
            setMessage(selectionStatus.getMessage(), WizardPage.INFORMATION);
            pageStatus = selectionStatus;
            break;
        default:
            setErrorMessage(selectionStatus.getMessage());
            setMessage(null);
            pageStatus = selectionStatus;
            break;
        }
        return pageStatus;
    }

    public void setFilter(final AbstractFilter filter) {
        LOGGER.log(Level.INFO, "Filter of FilterWizard set");
        this.createdFilter = filter;
        final FilterWizard wizard = (FilterWizard) getWizard();
        wizard.setFilter(filter);
    }

}
