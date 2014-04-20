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
package uk.ac.manchester.cs.owl.owlapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
public class OWLDifferentIndividualsAxiomImpl extends
        OWLNaryIndividualAxiomImpl implements OWLDifferentIndividualsAxiom {

    private static final long serialVersionUID = 40000L;

    /**
     * @param individuals
     *        individuals
     * @param annotations
     *        annotations on the axiom
     */
    public OWLDifferentIndividualsAxiomImpl(
            @Nonnull Set<? extends OWLIndividual> individuals,
            @Nonnull Set<? extends OWLAnnotation> annotations) {
        super(individuals, annotations);
    }

    @Nonnull
    @Override
    public OWLDifferentIndividualsAxiom getAxiomWithoutAnnotations() {
        if (!isAnnotated()) {
            return this;
        }
        return new OWLDifferentIndividualsAxiomImpl(getIndividuals(),
                NO_ANNOTATIONS);
    }

    @Nonnull
    @Override
    public OWLDifferentIndividualsAxiom getAnnotatedAxiom(
            @Nonnull Set<OWLAnnotation> annotations) {
        return new OWLDifferentIndividualsAxiomImpl(getIndividuals(),
                mergeAnnos(annotations));
    }

    @Nonnull
    @Override
    public Set<OWLDifferentIndividualsAxiom> asPairwiseAxioms() {
        List<OWLIndividual> individuals = getIndividualsAsList();
        Set<OWLDifferentIndividualsAxiom> result = new HashSet<OWLDifferentIndividualsAxiom>();
        for (int i = 0; i < individuals.size() - 1; i++) {
            for (int j = i + 1; j < individuals.size(); j++) {
                OWLIndividual indI = individuals.get(i);
                OWLIndividual indJ = individuals.get(j);
                result.add(new OWLDifferentIndividualsAxiomImpl(
                        new HashSet<OWLIndividual>(Arrays.asList(indI, indJ)),
                        NO_ANNOTATIONS));
            }
        }
        return result;
    }

    @Override
    public boolean containsAnonymousIndividuals() {
        for (OWLIndividual ind : getIndividuals()) {
            if (ind.isAnonymous()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof OWLDifferentIndividualsAxiom;
    }

    @Nonnull
    @Override
    public Set<OWLSubClassOfAxiom> asOWLSubClassOfAxioms() {
        List<OWLClassExpression> nominalsList = new ArrayList<OWLClassExpression>();
        for (OWLIndividual individual : getIndividuals()) {
            nominalsList.add(new OWLObjectOneOfImpl(Collections
                    .singleton(individual)));
        }
        Set<OWLSubClassOfAxiom> result = new HashSet<OWLSubClassOfAxiom>();
        for (int i = 0; i < nominalsList.size() - 1; i++) {
            for (int j = i + 1; j < nominalsList.size(); j++) {
                OWLClassExpression ceI = nominalsList.get(i);
                OWLClassExpression ceJ = nominalsList.get(j)
                        .getObjectComplementOf();
                result.add(new OWLSubClassOfAxiomImpl(ceI, ceJ, NO_ANNOTATIONS));
            }
        }
        return result;
    }

    @Override
    public void accept(@Nonnull OWLAxiomVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(@Nonnull OWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <O> O accept(@Nonnull OWLAxiomVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <O> O accept(@Nonnull OWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Nonnull
    @Override
    public AxiomType<?> getAxiomType() {
        return AxiomType.DIFFERENT_INDIVIDUALS;
    }
}
