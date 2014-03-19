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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.OWLMutableOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeVisitorEx;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.model.SetOntologyID;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
public class OWLOntologyImpl extends OWLImmutableOntologyImpl implements
        OWLMutableOntology, Serializable {

    private static final long serialVersionUID = 40000L;

    /**
     * @param manager
     *        ontology manager
     * @param ontologyID
     *        ontology id
     */
    public OWLOntologyImpl(@Nonnull OWLOntologyManager manager,
            @Nonnull OWLOntologyID ontologyID) {
        super(manager, ontologyID);
    }

    @Override
    public <T> OWLOntologyChange<T> applyChange(OWLOntologyChange<T> change) {
        OWLOntologyChangeFilter<OWLOntologyChange<T>> changeFilter = new OWLOntologyChangeFilter<OWLOntologyChange<T>>();
        return change.accept(changeFilter);
    }

    @Override
    public List<OWLOntologyChange<?>> applyChanges(
            List<? extends OWLOntologyChange<?>> changes) {
        List<OWLOntologyChange<?>> appliedChanges = new ArrayList<OWLOntologyChange<?>>();
        OWLOntologyChangeFilter<OWLOntologyChange<?>> changeFilter = new OWLOntologyChangeFilter<OWLOntologyChange<?>>();
        for (OWLOntologyChange<?> change : changes) {
            OWLOntologyChange<?> applied = change.accept(changeFilter);
            if (applied != null) {
                appliedChanges.add(applied);
            }
        }
        return appliedChanges;
    }

    @SuppressWarnings("unchecked")
    protected class OWLOntologyChangeFilter<T extends OWLOntologyChange<?>>
            implements OWLOntologyChangeVisitorEx<T>, Serializable {

        private static final long serialVersionUID = 40000L;

        @Override
        public T visit(RemoveAxiom change) {
            if (ints.removeAxiom(change.getAxiom())) {
                return (T) change;
            }
            return null;
        }

        @Override
        public T visit(SetOntologyID change) {
            OWLOntologyID id = change.getNewOntologyID();
            if (!id.equals(ontologyID)) {
                ontologyID = id;
                return (T) change;
            }
            return null;
        }

        @Override
        public T visit(AddAxiom change) {
            if (ints.addAxiom(change.getAxiom())) {
                return (T) change;
            }
            return null;
        }

        @Override
        public T visit(AddImport change) {
            // TODO change this to be done inside
            if (ints.addImportsDeclaration(change.getImportDeclaration())) {
                return (T) change;
            }
            return null;
        }

        @Override
        public T visit(RemoveImport change) {
            if (ints.removeImportsDeclaration(change.getImportDeclaration())) {
                return (T) change;
            }
            return null;
        }

        @Override
        public T visit(AddOntologyAnnotation change) {
            if (ints.addOntologyAnnotation(change.getAnnotation())) {
                return (T) change;
            }
            return null;
        }

        @Override
        public T visit(RemoveOntologyAnnotation change) {
            if (ints.removeOntologyAnnotation(change.getAnnotation())) {
                return (T) change;
            }
            return null;
        }
    }
}
