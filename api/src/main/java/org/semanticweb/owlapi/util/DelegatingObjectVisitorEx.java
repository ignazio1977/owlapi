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

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;

import javax.annotation.Nonnull;

/**
 * A delegating visitor. This utility class can be used to override visiting a
 * particular type of object.
 * 
 * @author Matthew Horridge, The University Of Manchester, Information
 *         Management Group
 * @since 2.2.0
 * @param <O>
 *        the returned type
 */
public class DelegatingObjectVisitorEx<O> implements OWLObjectVisitorEx<O> {

    private final OWLObjectVisitorEx<O> delegate;

    /**
     * Constructs a visitor where the specified delegate will be used to visit
     * all objects unless one of the visit methods in this visitor is overriden.
     * 
     * @param delegate
     *        The delegate.
     */
    public DelegatingObjectVisitorEx(OWLObjectVisitorEx<O> delegate) {
        this.delegate = delegate;
    }

    @Override
    public O visit(@Nonnull OWLAsymmetricObjectPropertyAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLClassAssertionAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLDeclarationAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLDisjointClassesAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLDisjointUnionAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLInverseObjectPropertiesAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLSubPropertyChainOfAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLSameIndividualAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLSubClassOfAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLSymmetricObjectPropertyAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLTransitiveObjectPropertyAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull SWRLRule rule) {
        return delegate.visit(rule);
    }

    @Override
    public O visit(@Nonnull OWLClass desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLDataAllValuesFrom desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLDataExactCardinality desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLDataMaxCardinality desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLDataMinCardinality desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLDataSomeValuesFrom desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLDataHasValue desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLObjectAllValuesFrom desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLObjectComplementOf desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLObjectExactCardinality desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLObjectIntersectionOf desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLObjectMaxCardinality desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLObjectMinCardinality desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLObjectOneOf desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLObjectHasSelf desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLObjectSomeValuesFrom desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLObjectUnionOf desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLObjectHasValue desc) {
        return delegate.visit(desc);
    }

    @Override
    public O visit(@Nonnull OWLDataComplementOf node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull OWLDataOneOf node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull OWLFacetRestriction node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull OWLDatatypeRestriction node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull OWLDatatype node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull OWLLiteral node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull OWLDataProperty property) {
        return delegate.visit(property);
    }

    @Override
    public O visit(@Nonnull OWLObjectProperty property) {
        return delegate.visit(property);
    }

    @Override
    public O visit(@Nonnull OWLObjectInverseOf property) {
        return delegate.visit(property);
    }

    @Override
    public O visit(@Nonnull SWRLLiteralArgument node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull SWRLVariable node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull SWRLIndividualArgument node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull SWRLBuiltInAtom node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull SWRLClassAtom node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull SWRLDataRangeAtom node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull SWRLDataPropertyAtom node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull SWRLDifferentIndividualsAtom node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull SWRLObjectPropertyAtom node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull SWRLSameIndividualAtom node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull OWLOntology ontology) {
        return delegate.visit(ontology);
    }

    @Override
    public O visit(@Nonnull OWLAnnotation annotation) {
        return delegate.visit(annotation);
    }

    @Override
    public O visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLAnnotationProperty property) {
        return delegate.visit(property);
    }

    @Override
    public O visit(@Nonnull OWLHasKeyAxiom axiom) {
        return delegate.visit(axiom);
    }

    @Override
    public O visit(@Nonnull OWLDataIntersectionOf node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull OWLDataUnionOf node) {
        return delegate.visit(node);
    }

    @Override
    public O visit(@Nonnull OWLNamedIndividual individual) {
        return delegate.visit(individual);
    }

    @Override
    public O visit(@Nonnull OWLAnonymousIndividual individual) {
        return delegate.visit(individual);
    }

    @Override
    public O visit(@Nonnull IRI iri) {
        return delegate.visit(iri);
    }

    @Override
    public O visit(@Nonnull OWLDatatypeDefinitionAxiom axiom) {
        return delegate.visit(axiom);
    }
}
