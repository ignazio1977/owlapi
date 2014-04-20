/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.semanticweb.owlapi.util;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import java.io.Serializable;
import java.util.Comparator;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitor;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * A {@code Comparator} which compares entities. Entities are compared first by
 * their type (in the following order: Class, Object property, Data property,
 * Individual, Datatype) then by their short form (using the specified short
 * form provider).
 * 
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public class OWLEntityComparator implements Comparator<OWLEntity>,
        OWLEntityVisitor, Serializable {

    private static final long serialVersionUID = 40000L;
    private static final int OWL_CLASS_INDEX = 0;
    private static final int OWL_OBJECT_PROPERTY_INDEX = 1;
    private static final int OWL_DATA_PROPERTY_INDEX = 2;
    private static final int OWL_INDIVIDUAL_INDEX = 3;
    private static final int OWL_DATATYPE_INDEX = 4;
    @Nonnull
    private final ShortFormProvider shortFormProvider;
    int lastValue;

    /**
     * Constructs an entity comparator which uses the specified short form
     * provider.
     * 
     * @param shortFormProvider
     *        the short form provider to use
     */
    public OWLEntityComparator(@Nonnull ShortFormProvider shortFormProvider) {
        this.shortFormProvider = checkNotNull(shortFormProvider,
                "shortFormProvider cannot be null");
    }

    @Override
    public int compare(@Nonnull OWLEntity o1, @Nonnull OWLEntity o2) {
        o1.accept(this);
        int i1 = lastValue;
        o2.accept(this);
        int i2 = lastValue;
        int delta = i1 - i2;
        if (delta != 0) {
            return delta;
        }
        String s1 = getShortForm(o1);
        String s2 = getShortForm(o2);
        return s1.compareTo(s2);
    }

    @Nonnull
    private String getShortForm(@Nonnull OWLEntity entity) {
        return shortFormProvider.getShortForm(entity);
    }

    @Override
    public void visit(@Nonnull OWLClass cls) {
        lastValue = 0;
    }

    @Override
    public void visit(@Nonnull OWLObjectProperty property) {
        lastValue = 1;
    }

    @Override
    public void visit(@Nonnull OWLDataProperty property) {
        lastValue = 2;
    }

    @Override
    public void visit(@Nonnull OWLNamedIndividual individual) {
        lastValue = 3;
    }

    @Override
    public void visit(@Nonnull OWLAnnotationProperty property) {
        lastValue = 4;
    }

    @Override
    public void visit(@Nonnull OWLDatatype datatype) {
        lastValue = 5;
    }
}
