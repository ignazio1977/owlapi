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

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObject;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLObjectVisitor;
import org.semanticweb.owlapi.model.SWRLObjectVisitorEx;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.SWRLVariableExtractor;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
public class SWRLRuleImpl extends OWLLogicalAxiomImpl implements SWRLRule {

    private static final long serialVersionUID = 40000L;
    @Nonnull
    private final LinkedHashSet<SWRLAtom> head;
    @Nonnull
    private final LinkedHashSet<SWRLAtom> body;
    private final boolean containsAnonymousClassExpressions;
    @Nullable
    private WeakReference<Set<SWRLVariable>> variables;
    @Nullable
    private WeakReference<Set<OWLClassExpression>> classAtomsPredicates;

    /**
     * @param body
     *        rule body
     * @param head
     *        rule head
     * @param annotations
     *        annotations on the axiom
     */
    public SWRLRuleImpl(@Nonnull Set<? extends SWRLAtom> body,
            @Nonnull Set<? extends SWRLAtom> head,
            @Nonnull Collection<? extends OWLAnnotation> annotations) {
        super(annotations);
        this.head = new LinkedHashSet<SWRLAtom>(checkNotNull(head,
                "head cannot be null"));
        this.body = new LinkedHashSet<SWRLAtom>(checkNotNull(body,
                "body cannot be null"));
        containsAnonymousClassExpressions = hasAnon();
    }

    @Nonnull
    @Override
    public SWRLRule getAxiomWithoutAnnotations() {
        if (!isAnnotated()) {
            return this;
        }
        return new SWRLRuleImpl(getBody(), getHead(), NO_ANNOTATIONS);
    }

    @Nonnull
    @Override
    public OWLAxiom getAnnotatedAxiom(@Nonnull Set<OWLAnnotation> annotations) {
        return new SWRLRuleImpl(getBody(), getHead(), annotations);
    }

    /**
     * @param body
     *        rule body
     * @param head
     *        rule head
     */
    public SWRLRuleImpl(@Nonnull Set<? extends SWRLAtom> body,
            @Nonnull Set<? extends SWRLAtom> head) {
        this(body, head, Collections.<OWLAnnotation> emptyList());
    }

    @Nonnull
    @Override
    public Set<SWRLVariable> getVariables() {
        Set<SWRLVariable> toReturn = null;
        if (variables != null) {
            toReturn = variables.get();
        }
        if (toReturn != null) {
            return toReturn;
        }
        SWRLVariableExtractor extractor = new SWRLVariableExtractor();
        accept(extractor);
        toReturn = extractor.getVariables();
        variables = new WeakReference<Set<SWRLVariable>>(toReturn);
        return toReturn;
    }

    private boolean hasAnon() {
        for (SWRLAtom atom : head) {
            if (atom instanceof SWRLClassAtom
                    && ((SWRLClassAtom) atom).getPredicate().isAnonymous()) {
                return true;
            }
        }
        for (SWRLAtom atom : body) {
            if (atom instanceof SWRLClassAtom
                    && ((SWRLClassAtom) atom).getPredicate().isAnonymous()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAnonymousClassExpressions() {
        return containsAnonymousClassExpressions;
    }

    @Nonnull
    @Override
    public Set<OWLClassExpression> getClassAtomPredicates() {
        Set<OWLClassExpression> toReturn = null;
        if (classAtomsPredicates != null) {
            toReturn = classAtomsPredicates.get();
        }
        if (toReturn != null) {
            return toReturn;
        }
        toReturn = new LinkedHashSet<OWLClassExpression>();
        for (SWRLAtom atom : head) {
            if (atom instanceof SWRLClassAtom) {
                toReturn.add(((SWRLClassAtom) atom).getPredicate());
            }
        }
        for (SWRLAtom atom : body) {
            if (atom instanceof SWRLClassAtom) {
                toReturn.add(((SWRLClassAtom) atom).getPredicate());
            }
        }
        classAtomsPredicates = new WeakReference<Set<OWLClassExpression>>(
                toReturn);
        return toReturn;
    }

    @Override
    public void accept(@Nonnull OWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <O> O accept(@Nonnull OWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void accept(@Nonnull SWRLObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Nullable
    @Override
    public <O> O accept(@Nonnull SWRLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Nonnull
    @Override
    public Set<SWRLAtom> getBody() {
        return CollectionFactory
                .getCopyOnRequestSetFromImmutableCollection(body);
    }

    @Nonnull
    @Override
    public Set<SWRLAtom> getHead() {
        return CollectionFactory
                .getCopyOnRequestSetFromImmutableCollection(head);
    }

    @Override
    public void accept(@Nonnull OWLAxiomVisitor visitor) {
        visitor.visit(this);
    }

    @Nullable
    @Override
    public <O> O accept(@Nonnull OWLAxiomVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Nonnull
    @Override
    public SWRLRule getSimplified() {
        return (SWRLRule) this.accept(ATOM_SIMPLIFIER);
    }

    @Override
    public boolean isLogicalAxiom() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (!(obj instanceof SWRLRule)) {
                return false;
            }
            SWRLRule other = (SWRLRule) obj;
            return other.getBody().equals(body) && other.getHead().equals(head);
        }
        return false;
    }

    @Nonnull
    @Override
    public AxiomType<?> getAxiomType() {
        return AxiomType.SWRL_RULE;
    }

    @Override
    protected int compareObjectOfSameType(OWLObject object) {
        SWRLRule other = (SWRLRule) object;
        int diff = compareSets(getBody(), other.getBody());
        if (diff == 0) {
            diff = compareSets(getHead(), other.getHead());
        }
        return diff;
    }

    protected final AtomSimplifier ATOM_SIMPLIFIER = new AtomSimplifier();

    protected class AtomSimplifier implements SWRLObjectVisitorEx<SWRLObject> {

        @Override
        public SWRLRule visit(@Nonnull SWRLRule node) {
            Set<SWRLAtom> nodebody = new HashSet<SWRLAtom>();
            for (SWRLAtom atom : node.getBody()) {
                nodebody.add((SWRLAtom) atom.accept(this));
            }
            Set<SWRLAtom> nodehead = new HashSet<SWRLAtom>();
            for (SWRLAtom atom : node.getHead()) {
                nodehead.add((SWRLAtom) atom.accept(this));
            }
            return new SWRLRuleImpl(nodebody, nodehead, NO_ANNOTATIONS);
        }

        @Override
        public SWRLClassAtom visit(@Nonnull SWRLClassAtom node) {
            return node;
        }

        @Override
        public SWRLDataRangeAtom visit(@Nonnull SWRLDataRangeAtom node) {
            return node;
        }

        @Override
        public SWRLObjectPropertyAtom visit(@Nonnull SWRLObjectPropertyAtom node) {
            return node.getSimplified();
        }

        @Override
        public SWRLDataPropertyAtom visit(@Nonnull SWRLDataPropertyAtom node) {
            return node;
        }

        @Override
        public SWRLBuiltInAtom visit(@Nonnull SWRLBuiltInAtom node) {
            return node;
        }

        @Override
        public SWRLVariable visit(@Nonnull SWRLVariable node) {
            return node;
        }

        @Override
        public SWRLIndividualArgument visit(@Nonnull SWRLIndividualArgument node) {
            return node;
        }

        @Override
        public SWRLLiteralArgument visit(@Nonnull SWRLLiteralArgument node) {
            return node;
        }

        @Override
        public SWRLSameIndividualAtom visit(@Nonnull SWRLSameIndividualAtom node) {
            return node;
        }

        @Override
        public SWRLDifferentIndividualsAtom visit(
                @Nonnull SWRLDifferentIndividualsAtom node) {
            return node;
        }
    }
}
