package org.semanticweb.owlapi.model;

import javax.annotation.Nonnull;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics
 *         Research Group
 * @since 3.5
 */
public interface HasDataFactory {

    /**
     * @return the data factory
     */
    @Nonnull
    OWLDataFactory getOWLDataFactory();
}
