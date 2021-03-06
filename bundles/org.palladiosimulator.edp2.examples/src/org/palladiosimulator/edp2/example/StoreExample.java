package org.palladiosimulator.edp2.example;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.impl.RepositoryManager;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.repository.local.LocalDirectoryRepositoryHelper;
import org.palladiosimulator.edp2.util.MeasurementsUtility;

/**
 * Contains an example how data can be stored with EDP2. Please note that repeated execution of this
 * class can lead to errors. This behavior is due to brevity and clarity of the example and does
 * demonstrate a general shortcoming.
 *
 * @author groenda, Sebastian Lehrig
 */
public class StoreExample {
    /** (Relative) name of the directory in which the data of the example will be stored. */
    public static final String DEFAULT_DIRECTORY = "LocalRepository";
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(StoreExample.class.getCanonicalName());

    /** Repository which is used to store the data. */
    private final Repository ldRepo;
    /** Helper class used to process data for the example. */
    private final ExampleData exampleData;

    /**
     * Initializes an instance of this class with the default directory as target.
     */
    public StoreExample() {
        this(DEFAULT_DIRECTORY);
    }

    /**
     * Initializes an instance of this class.
     *
     * @param directory
     *            Directory to be used to store measurements.
     */
    public StoreExample(final String directory) {
        ldRepo = initializeRepository(directory);
        exampleData = new ExampleData(ldRepo.getDescriptions());
    }

    /**
     * Initializes the repository in which the data will be stored.
     *
     * @param directory
     *            Path to directory in which the data should be stored.
     * @return the initialized repository.
     */
    private Repository initializeRepository(final String directory) {
        final Repository repo = LocalDirectoryRepositoryHelper.initializeLocalDirectoryRepository(new File(directory));
        /*
         * Add repository to a (optional) central directory of repositories. This can be useful to
         * manage more than one repository or have links between different existing repositories. A
         * repository must be connected to an instance of Repositories in order to be opened.
         */
        RepositoryManager.addRepository(RepositoryManager.getCentralRepository(), repo);
        return repo;
    }

    private void createExample() {
        exampleData.createExampleExperimentMetadata();
        ldRepo.getExperimentGroups().add(exampleData.getExampleExperimentGroup());
        // create experiment data
        exampleData.simulateExperimentRun();
    }

    /**
     * Method body which executes all necessary steps to create and store an example.
     */
    public void run() {
        try {
            MeasurementsUtility.ensureOpenRepository(ldRepo);
            createExample();
            MeasurementsUtility.ensureClosedRepository(ldRepo);
        } catch (final DataNotAccessibleException e) {
            LOGGER.log(Level.SEVERE, "Error while accessing the datastore.", e);
        }
    }

    /**
     * Main method to run the example.
     *
     * @param args
     *            Not used.
     */
    public static void main(final String[] args) {
        final StoreExample example = new StoreExample();
        example.run();
    }
}
