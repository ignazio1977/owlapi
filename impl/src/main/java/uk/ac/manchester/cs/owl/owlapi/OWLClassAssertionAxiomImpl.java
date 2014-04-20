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

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
public class OWLClassAssertionAxiomImpl extends OWLIndividualAxiomImpl
        implements OWLClassAssertionAxiom {

    private static final long serialVersionUID = 40000L;
    private final OWLIndividual individual;
    private final OWLClassExpression classExpression;

    /**
     * @param individual
     *        individual
     * @param classExpression
     *        class
     * @param annotations
     *        annotations on the axiom
     */
    public OWLClassAssertionAxiomImpl(@Nonnull OWLIndividual individual,
            @Nonnull OWLClassExpression classExpression,
            @Nonnull Collection<? extends OWLAnnotation> annotations) {
        super(annotations);
        this.individual = checkNotNull(individual, "individual cannot be null");
        this.classExpression = checkNotNull(classExpression,
                "classExpression cannot be null");
    }

    @Nonnull
    @Override
    public OWLClassAssertionAxiom getAxiomWithoutAnnotations() {
        if (!isAnnotated()) {
            return this;
        }
        return new OWLClassAssertionAxiomImpl(getIndividual(),
                getClassExpression(), NO_ANNOTATIONS);
    }

    @Nonnull
    @Override
    public OWLClassAssertionAxiom getAnnotatedAxiom(
            @Nonnull Set<OWLAnnotation> annotations) {
        return new OWLClassAssertionAxiomImpl(getIndividual(),
                getClassExpression(), mergeAnnos(annotations));
    }

    @Nonnull
    @Override
    public OWLClassExpression getClassExpression() {
        return classExpression;
    }

    @Nonnull
    @Override
    public OWLIndividual getIndividual() {
        return individual;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (!(obj instanceof OWLClassAssertionAxiom)) {
                return false;
            }
            OWLClassAssertionAxiom other = (OWLClassAssertionAxiom) obj;
            return other.getIndividual().equals(individual)
                    && other.getClassExpression().equals(classExpression);
        }
        return false;
    }

    @Nonnull
    @Override
    public OWLSubClassOfAxiom asOWLSubClassOfAxiom() {
        return new OWLSubClassOfAxiomImpl(new OWLObjectOneOfImpl(
                Collections.singleton(getIndividual())), getClassExpression(),
                NO_ANNOTATIONS);
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
        return AxiomType.CLASS_ASSERTION;
    }

    @Override
    protected int compareObjectOfSameType(OWLObject object) {
        OWLClassAssertionAxiom otherAx = (OWLClassAssertionAxiom) object;
        int diff = getIndividual().compareTo(otherAx.getIndividual());
        if (diff != 0) {
            return diff;
        } else {
            return getClassExpression().compareTo(otherAx.getClassExpression());
        }
    }
}
