/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2011, Clark & Parsia, LLC
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package com.clarkparsia.owlapi.modularity.locality;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Semantic locality evaluator. */
public class SemanticLocalityEvaluator implements LocalityEvaluator {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(SemanticLocalityEvaluator.class);
    @Nonnull
    protected final OWLDataFactory df;
    private final AxiomLocalityVisitor axiomVisitor = new AxiomLocalityVisitor();
    private final BottomReplacer bottomReplacer = new BottomReplacer();
    protected final OWLReasoner reasoner;

    /**
     * Instantiates a new semantic locality evaluator.
     * 
     * @param man
     *        ontology manager
     * @param reasonerFactory
     *        reasoner factory
     */
    public SemanticLocalityEvaluator(@Nonnull OWLOntologyManager man,
            @Nonnull OWLReasonerFactory reasonerFactory) {
        df = checkNotNull(man, "man cannot be null").getOWLDataFactory();
        try {
            reasoner = checkNotNull(reasonerFactory,
                    "reasonerFactory cannot be null")
                    .createNonBufferingReasoner(man.createOntology());
        } catch (Exception e) {
            throw new OWLRuntimeException(e);
        }
    }

    /** The Class AxiomLocalityVisitor. */
    private class AxiomLocalityVisitor extends OWLAxiomVisitorAdapter implements
            OWLAxiomVisitor {

        private boolean isLocal;

        public AxiomLocalityVisitor() {}

        /** @return true, if is local */
        public boolean isLocal() {
            return isLocal;
        }

        /**
         * @param axiom
         *        the axiom
         * @return true, if is local
         */
        public boolean isLocal(@Nonnull OWLAxiom axiom) {
            reset();
            axiom.accept(this);
            return isLocal();
        }

        /** Reset. */
        public void reset() {
            isLocal = false;
        }

        @Override
        public void visit(@Nonnull OWLDisjointClassesAxiom axiom) {
            // XXX this seems wrong
            isLocal = true;
        }

        @Override
        public void visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
            Set<OWLClassExpression> eqClasses = axiom.getClassExpressions();
            if (eqClasses.size() != 2) {
                return;
            }
            LOGGER.info("Calling the Reasoner");
            isLocal = reasoner.isEntailed(axiom);
            LOGGER.info("DONE Calling the Reasoner. isLocal = {}", isLocal);
        }

        @Override
        public void visit(@Nonnull OWLSubClassOfAxiom axiom) {
            LOGGER.info("Calling the Reasoner");
            isLocal = reasoner.isEntailed(axiom);
            LOGGER.info("DONE Calling the Reasoner. isLocal = {}", isLocal);
        }
    }

    /** The Class BottomReplacer. */
    private class BottomReplacer extends OWLAxiomVisitorAdapter implements
            OWLAxiomVisitor, OWLClassExpressionVisitor {

        @Nullable
        private OWLAxiom newAxiom;
        @Nullable
        private OWLClassExpression newClassExpression;
        private Set<? extends OWLEntity> signature;

        public BottomReplacer() {}

        /** @return the result */
        @Nullable
        public OWLAxiom getResult() {
            return newAxiom;
        }

        /**
         * Replace bottom.
         * 
         * @param axiom
         *        the axiom
         * @param sig
         *        the sig
         * @return the modified OWL axiom
         */
        @Nonnull
        public OWLAxiom replaceBottom(@Nonnull OWLAxiom axiom,
                @Nonnull Set<? extends OWLEntity> sig) {
            reset(checkNotNull(sig, "sig cannot be null"));
            checkNotNull(axiom, "axiom cannot be null").accept(this);
            return getResult();
        }

        /**
         * Takes an OWLClassExpression and a signature replaces by bottom the
         * entities not in the signature
         * 
         * @param desc
         *        the desc
         * @return the modified OWL class expression
         */
        @Nonnull
        public OWLClassExpression
                replaceBottom(@Nonnull OWLClassExpression desc) {
            newClassExpression = null;
            checkNotNull(desc, "desc cannot be null").accept(this);
            if (newClassExpression == null) {
                throw new OWLRuntimeException("Unsupported class expression "
                        + desc);
            }
            return newClassExpression;
        }

        /**
         * @param classExpressions
         *        the class expressions
         * @return the set of modified OWL class expressions
         */
        @Nonnull
        public Set<OWLClassExpression> replaceBottom(
                @Nonnull Set<OWLClassExpression> classExpressions) {
            Set<OWLClassExpression> result = new HashSet<OWLClassExpression>();
            for (OWLClassExpression desc : checkNotNull(classExpressions,
                    "classExpressions cannot be null")) {
                result.add(replaceBottom(desc));
            }
            return result;
        }

        /**
         * Reset.
         * 
         * @param s
         *        the signature
         */
        public void reset(@Nonnull Set<? extends OWLEntity> s) {
            signature = checkNotNull(s, "s cannot be null");
            newAxiom = null;
        }

        @Override
        public void visit(@Nonnull OWLClass desc) {
            if (signature.contains(desc)) {
                newClassExpression = desc;
            } else {
                newClassExpression = df.getOWLNothing();
            }
        }

        @Override
        public void visit(@Nonnull OWLDataAllValuesFrom desc) {
            if (signature.contains(desc.getProperty().asOWLDataProperty())) {
                newClassExpression = desc;
            } else {
                newClassExpression = df.getOWLThing();
            }
        }

        @Override
        public void visit(@Nonnull OWLDataExactCardinality desc) {
            if (signature.contains(desc.getProperty().asOWLDataProperty())) {
                newClassExpression = desc;
            } else {
                newClassExpression = df.getOWLNothing();
            }
        }

        @Override
        public void visit(@Nonnull OWLDataMaxCardinality desc) {
            if (signature.contains(desc.getProperty().asOWLDataProperty())) {
                newClassExpression = desc;
            } else {
                newClassExpression = df.getOWLThing();
            }
        }

        @Override
        public void visit(@Nonnull OWLDataMinCardinality desc) {
            if (signature.contains(desc.getProperty().asOWLDataProperty())) {
                newClassExpression = desc;
            } else {
                newClassExpression = df.getOWLNothing();
            }
        }

        @Override
        public void visit(@Nonnull OWLDataSomeValuesFrom desc) {
            if (signature.contains(desc.getProperty().asOWLDataProperty())) {
                newClassExpression = desc;
            } else {
                newClassExpression = df.getOWLNothing();
            }
        }

        @Override
        public void visit(@Nonnull OWLDataHasValue desc) {
            newClassExpression = df.getOWLNothing();
        }

        @Override
        public void visit(@Nonnull OWLDisjointClassesAxiom ax) {
            Set<OWLClassExpression> disjointclasses = replaceBottom(ax
                    .getClassExpressions());
            newAxiom = df.getOWLDisjointClassesAxiom(disjointclasses);
        }

        @Override
        public void visit(@Nonnull OWLEquivalentClassesAxiom ax) {
            Set<OWLClassExpression> eqclasses = replaceBottom(ax
                    .getClassExpressions());
            newAxiom = df.getOWLEquivalentClassesAxiom(eqclasses);
        }

        @Override
        public void visit(@Nonnull OWLObjectAllValuesFrom desc) {
            if (signature.contains(desc.getProperty().getNamedProperty())) {
                newClassExpression = df.getOWLObjectAllValuesFrom(
                        desc.getProperty(), replaceBottom(desc.getFiller()));
            } else {
                newClassExpression = df.getOWLThing();
            }
        }

        @Override
        public void visit(@Nonnull OWLObjectComplementOf desc) {
            newClassExpression = df.getOWLObjectComplementOf(replaceBottom(desc
                    .getOperand()));
        }

        @Override
        public void visit(@Nonnull OWLObjectExactCardinality desc) {
            if (signature.contains(desc.getProperty().getNamedProperty())) {
                newClassExpression = desc;
            } else {
                newClassExpression = df.getOWLNothing();
            }
        }

        @Override
        public void visit(@Nonnull OWLObjectIntersectionOf desc) {
            Set<OWLClassExpression> operands = desc.getOperands();
            newClassExpression = df
                    .getOWLObjectIntersectionOf(replaceBottom(operands));
        }

        @Override
        public void visit(@Nonnull OWLObjectMaxCardinality desc) {
            if (signature.contains(desc.getProperty().getNamedProperty())) {
                newClassExpression = desc;
            } else {
                newClassExpression = df.getOWLThing();
            }
        }

        @Override
        public void visit(@Nonnull OWLObjectMinCardinality desc) {
            if (signature.contains(desc.getProperty().getNamedProperty())) {
                newClassExpression = desc;
            } else {
                newClassExpression = df.getOWLNothing();
            }
        }

        @Override
        public void visit(@Nonnull OWLObjectOneOf desc) {
            newClassExpression = df.getOWLNothing();
        }

        @Override
        public void visit(@Nonnull OWLObjectHasSelf desc) {
            newClassExpression = df.getOWLNothing();
        }

        @Override
        public void visit(@Nonnull OWLObjectSomeValuesFrom desc) {
            if (signature.contains(desc.getProperty().getNamedProperty())) {
                newClassExpression = df.getOWLObjectSomeValuesFrom(
                        desc.getProperty(), replaceBottom(desc.getFiller()));
            } else {
                newClassExpression = df.getOWLNothing();
            }
        }

        @Override
        public void visit(@Nonnull OWLObjectUnionOf desc) {
            Set<OWLClassExpression> operands = desc.getOperands();
            newClassExpression = df
                    .getOWLObjectUnionOf(replaceBottom(operands));
        }

        @Override
        public void visit(@Nonnull OWLObjectHasValue desc) {
            newClassExpression = df.getOWLNothing();
        }

        @Override
        public void visit(@Nonnull OWLSubClassOfAxiom ax) {
            OWLClassExpression sup = replaceBottom(ax.getSuperClass());
            OWLClassExpression sub = replaceBottom(ax.getSubClass());
            newAxiom = df.getOWLSubClassOfAxiom(sub, sup);
        }
    }

    @Override
    public boolean isLocal(@Nonnull OWLAxiom axiom,
            @Nonnull Set<? extends OWLEntity> signature) {
        LOGGER.info("Replacing axiom by Bottom");
        OWLAxiom newAxiom = bottomReplacer.replaceBottom(
                checkNotNull(axiom, "axiom cannot be null"),
                checkNotNull(signature, "signature cannot be null"));
        LOGGER.info("DONE Replacing axiom by Bottom. Success: {}",
                newAxiom != null);
        return newAxiom != null && axiomVisitor.isLocal(newAxiom);
    }
}
