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
package org.semanticweb.owlapi.search;

import java.util.Collection;
import java.util.Collections;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.util.OWLAxiomSearchFilter;

/**
 * Collection of filters for use in searching through ontology axioms.
 * 
 * @author ignazio
 * @since 4.0.0
 */
public class Filters {

    /**
     * @author ignazio
     * @param <A>
     *        axiom type
     */
    public static abstract class AxiomFilter<A extends OWLAxiom> implements
            OWLAxiomSearchFilter {

        private static final long serialVersionUID = 40000L;
        private Collection<AxiomType<?>> types;

        /**
         * @param type
         *        axiom type to filter on
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public AxiomFilter(AxiomType<?> type) {
            this.types = (Collection) Collections.singleton(type);
        }

        @Override
        public Collection<AxiomType<?>> getAxiomTypes() {
            return types;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean pass(OWLAxiom axiom, Object key) {
            return axiomValue((A) axiom).equals(key);
        }

        /**
         * Override this method to select what part of the axiom should be
         * compared with the input key
         * 
         * @param axiom
         *        axiom to check
         * @return Object to compare to the input key
         */
        protected abstract Object axiomValue(A axiom);
    }

    /**
     * filter returning subannotation axioms where the super property matches
     * the input key.
     */
    public static OWLAxiomSearchFilter subAnnotationWithSuper = new AxiomFilter<OWLSubAnnotationPropertyOfAxiom>(
            AxiomType.SUB_ANNOTATION_PROPERTY_OF) {

        private static final long serialVersionUID = 40000L;

        @Override
        protected Object axiomValue(OWLSubAnnotationPropertyOfAxiom axiom) {
            return axiom.getSuperProperty();
        }
    };
    /**
     * filter returning subannotation axioms where the sub property matches the
     * input key.
     */
    public static OWLAxiomSearchFilter subAnnotationWithSub = new AxiomFilter<OWLSubAnnotationPropertyOfAxiom>(
            AxiomType.SUB_ANNOTATION_PROPERTY_OF) {

        private static final long serialVersionUID = 40000L;

        @Override
        protected Object axiomValue(OWLSubAnnotationPropertyOfAxiom axiom) {
            return axiom.getSubProperty();
        }
    };
    /**
     * filter returning subclass axioms where the super class matches the input
     * key.
     */
    public static OWLAxiomSearchFilter subClassWithSuper = new AxiomFilter<OWLSubClassOfAxiom>(
            AxiomType.SUBCLASS_OF) {

        private static final long serialVersionUID = 40000L;

        @Override
        protected Object axiomValue(OWLSubClassOfAxiom axiom) {
            return axiom.getSuperClass();
        }
    };
    /**
     * filter returning subclass axioms where the sub class matches the input
     * key.
     */
    public static OWLAxiomSearchFilter subClassWithSub = new AxiomFilter<OWLSubClassOfAxiom>(
            AxiomType.SUBCLASS_OF) {

        private static final long serialVersionUID = 40000L;

        @Override
        protected Object axiomValue(OWLSubClassOfAxiom axiom) {
            return axiom.getSubClass();
        }
    };
    /**
     * filter returning sub object property axioms where the super property
     * matches the input key.
     */
    public static OWLAxiomSearchFilter subObjectPropertyWithSuper = new AxiomFilter<OWLSubObjectPropertyOfAxiom>(
            AxiomType.SUB_OBJECT_PROPERTY) {

        private static final long serialVersionUID = 40000L;

        @Override
        protected Object axiomValue(OWLSubObjectPropertyOfAxiom axiom) {
            return axiom.getSuperProperty();
        }
    };
    /**
     * filter returning sub object property axioms where the sub property
     * matches the input key.
     */
    public static OWLAxiomSearchFilter subObjectPropertyWithSub = new AxiomFilter<OWLSubObjectPropertyOfAxiom>(
            AxiomType.SUB_OBJECT_PROPERTY) {

        private static final long serialVersionUID = 40000L;

        @Override
        protected Object axiomValue(OWLSubObjectPropertyOfAxiom axiom) {
            return axiom.getSubProperty();
        }
    };
    /**
     * filter returning sub data property axioms where the super property
     * matches the input key.
     */
    public static OWLAxiomSearchFilter subDataPropertyWithSuper = new AxiomFilter<OWLSubDataPropertyOfAxiom>(
            AxiomType.SUB_DATA_PROPERTY) {

        private static final long serialVersionUID = 40000L;

        @Override
        protected Object axiomValue(OWLSubDataPropertyOfAxiom axiom) {
            return axiom.getSuperProperty();
        }
    };
    /**
     * filter returning sub data property axioms where the sub property matches
     * the input key.
     */
    public static OWLAxiomSearchFilter subDataPropertyWithSub = new AxiomFilter<OWLSubDataPropertyOfAxiom>(
            AxiomType.SUB_DATA_PROPERTY) {

        private static final long serialVersionUID = 40000L;

        @Override
        protected Object axiomValue(OWLSubDataPropertyOfAxiom axiom) {
            return axiom.getSubProperty();
        }
    };
    /**
     * filter returning datatype definition axioms where the datatype matches
     * the input key.
     */
    public static OWLAxiomSearchFilter datatypeDefFilter = new AxiomFilter<OWLDatatypeDefinitionAxiom>(
            AxiomType.DATATYPE_DEFINITION) {

        private static final long serialVersionUID = 40000L;

        @Override
        protected Object axiomValue(OWLDatatypeDefinitionAxiom axiom) {
            return axiom.getDatatype();
        }
    };
    /**
     * filter returning annotation property range axioms where the property
     * matches the input key.
     */
    public static OWLAxiomSearchFilter apRangeFilter = new AxiomFilter<OWLAnnotationPropertyRangeAxiom>(
            AxiomType.ANNOTATION_PROPERTY_RANGE) {

        private static final long serialVersionUID = 40000L;

        @Override
        protected Object axiomValue(OWLAnnotationPropertyRangeAxiom axiom) {
            return axiom.getProperty();
        }
    };
    /**
     * filter returning annotation property domain axioms where the property
     * matches the input key.
     */
    public static OWLAxiomSearchFilter apDomainFilter = new AxiomFilter<OWLAnnotationPropertyDomainAxiom>(
            AxiomType.ANNOTATION_PROPERTY_DOMAIN) {

        private static final long serialVersionUID = 40000L;

        @Override
        protected Object axiomValue(OWLAnnotationPropertyDomainAxiom axiom) {
            return axiom.getProperty();
        }
    };
    /**
     * filter returning annotation assertions where the subject matches the
     * input key.
     */
    public static OWLAxiomSearchFilter annotations = new AxiomFilter<OWLAnnotationAssertionAxiom>(
            AxiomType.ANNOTATION_ASSERTION) {

        private static final long serialVersionUID = 40000L;

        @Override
        protected Object axiomValue(OWLAnnotationAssertionAxiom axiom) {
            return axiom.getSubject();
        }
    };
}
