/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, The University of Manchester
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2011, University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.semanticweb.owlapi.util;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health Informatics
 *         Group, Date: 27-Jul-2007
 */
public class InferredObjectPropertyCharacteristicAxiomGenerator
        extends
        InferredObjectPropertyAxiomGenerator<OWLObjectPropertyCharacteristicAxiom> {

    @Override
    protected void addAxioms(OWLObjectProperty entity, OWLReasoner reasoner,
            OWLDataFactory dataFactory,
            Set<OWLObjectPropertyCharacteristicAxiom> result) {
        addIfEntailed(dataFactory.getOWLFunctionalObjectPropertyAxiom(entity),
                reasoner, result);
        addIfEntailed(
                dataFactory.getOWLInverseFunctionalObjectPropertyAxiom(entity),
                reasoner, result);
        addIfEntailed(dataFactory.getOWLSymmetricObjectPropertyAxiom(entity),
                reasoner, result);
        addIfEntailed(dataFactory.getOWLAsymmetricObjectPropertyAxiom(entity),
                reasoner, result);
        addTransitiveAxiomIfEntailed(entity, reasoner, dataFactory, result);
        addIfEntailed(dataFactory.getOWLReflexiveObjectPropertyAxiom(entity),
                reasoner, result);
        addIfEntailed(dataFactory.getOWLIrreflexiveObjectPropertyAxiom(entity),
                reasoner, result);
    }

    protected void addTransitiveAxiomIfEntailed(OWLObjectProperty property, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLObjectPropertyCharacteristicAxiom> result) {

        OWLObjectPropertyCharacteristicAxiom axiom = dataFactory.getOWLTransitiveObjectPropertyAxiom(property);
        if (reasoner.isEntailmentCheckingSupported(axiom.getAxiomType())
                && reasoner.isEntailed(axiom)) {
            if(!triviallyTransitiveCheck(property, reasoner, dataFactory)) {
                result.add(axiom);
            }

        }
    }

    /**
     * Test to see if a property is only vacuuously transitive; i.e. if there cannot be any three individuals
     * a,b,c such that foo(a,b) and foo(b,c) - e.g. if the domain and range of foo are disjoint.
     * .
     * @param property
     * @param reasoner
     * @param dataFactory
     * @return  Return true if property is trivially transitive.
     * If entailment checking for OWLObjectPropertyAssertionAxioms is not supported, default to true.
     */
    private boolean triviallyTransitiveCheck(OWLObjectProperty property, OWLReasoner reasoner, OWLDataFactory dataFactory) {
        Set<OWLAxiom> trivialityCheckAxioms = new HashSet<OWLAxiom>();
        OWLAnonymousIndividual a = dataFactory.getOWLAnonymousIndividual();
        OWLAnonymousIndividual b = dataFactory.getOWLAnonymousIndividual();
        OWLAnonymousIndividual c = dataFactory.getOWLAnonymousIndividual();

        OWLObjectPropertyAssertionAxiom p1 = dataFactory.getOWLObjectPropertyAssertionAxiom(property, a, b);
        OWLObjectPropertyAssertionAxiom p2 = dataFactory.getOWLObjectPropertyAssertionAxiom(property, b, c);
        trivialityCheckAxioms.add(p1);
        trivialityCheckAxioms.add(p2);
        if(reasoner.isEntailmentCheckingSupported(p1.getAxiomType()))  {
            return !reasoner.isEntailed(trivialityCheckAxioms);
        } else {
            return true;
        }
    }

    protected void addIfEntailed(OWLObjectPropertyCharacteristicAxiom axiom,
            OWLReasoner reasoner,
            Set<OWLObjectPropertyCharacteristicAxiom> result) {
        if (reasoner.isEntailmentCheckingSupported(axiom.getAxiomType())
                && reasoner.isEntailed(axiom)) {
            result.add(axiom);
        }
    }

    @Override
    public String getLabel() {
        return "Object property characteristics";
    }
}
