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

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

import java.io.Serializable;
import java.util.Locale;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** no cache used @author ignazio */
public class InternalsNoCache implements OWLDataFactoryInternals, Serializable {

    private static final long serialVersionUID = 40000L;
    private static final OWLDatatype PLAIN = new OWL2DatatypeImpl(
            RDF_PLAIN_LITERAL);
    private static final OWLDatatype XSDBOOLEAN = new OWL2DatatypeImpl(
            XSD_BOOLEAN);
    private static final OWLDatatype XSDDOUBLE = new OWL2DatatypeImpl(
            XSD_DOUBLE);
    private static final OWLDatatype XSDFLOAT = new OWL2DatatypeImpl(XSD_FLOAT);
    private static final OWLDatatype XSDINTEGER = new OWL2DatatypeImpl(
            XSD_INTEGER);
    private static final OWLDatatype RDFSLITERAL = new OWL2DatatypeImpl(
            RDFS_LITERAL);
    private static final OWLLiteral trueLiteral = new OWLLiteralImplBoolean(
            true, XSDBOOLEAN);
    private static final OWLLiteral falseLiteral = new OWLLiteralImplBoolean(
            false, XSDBOOLEAN);
    @Nullable
    private OWLLiteral negativeFloatZero;
    private final boolean useCompression;

    /**
     * @param useCompression
     *        true if compression of literals should be used
     */
    public InternalsNoCache(boolean useCompression) {
        this.useCompression = useCompression;
    }

    @Override
    public void purge() {}

    @Nonnull
    @Override
    public OWLClass getOWLClass(@Nonnull IRI iri) {
        return new OWLClassImpl(iri);
    }

    @Nonnull
    @Override
    public OWLObjectProperty getOWLObjectProperty(@Nonnull IRI iri) {
        return new OWLObjectPropertyImpl(iri);
    }

    @Nonnull
    @Override
    public OWLDataProperty getOWLDataProperty(@Nonnull IRI iri) {
        return new OWLDataPropertyImpl(iri);
    }

    @Nonnull
    @Override
    public OWLNamedIndividual getOWLNamedIndividual(@Nonnull IRI iri) {
        return new OWLNamedIndividualImpl(iri);
    }

    @Nonnull
    @Override
    public OWLDatatype getOWLDatatype(@Nonnull IRI iri) {
        return new OWLDatatypeImpl(iri);
    }

    @Nonnull
    @Override
    public OWLAnnotationProperty getOWLAnnotationProperty(@Nonnull IRI iri) {
        return new OWLAnnotationPropertyImpl(iri);
    }

    @Nonnull
    @Override
    public OWLLiteral getOWLLiteral(float value) {
        return new OWLLiteralImplFloat(value, getFloatOWLDatatype());
    }

    @Nonnull
    @Override
    public OWLLiteral getOWLLiteral(@Nonnull String value) {
        if (useCompression) {
            return new OWLLiteralImpl(value, "",
                    getOWLDatatype(XSDVocabulary.STRING.getIRI()));
        }
        return new OWLLiteralImplNoCompression(value, "",
                getOWLDatatype(XSDVocabulary.STRING.getIRI()));
    }

    @Nonnull
    @Override
    public OWLLiteral getOWLLiteral(@Nonnull String literal, @Nullable String lang) {
        String normalisedLang;
        if (lang == null) {
            normalisedLang = "";
        } else {
            normalisedLang = lang.trim().toLowerCase(Locale.ENGLISH);
        }
        if (useCompression) {
            return new OWLLiteralImpl(literal, normalisedLang, null);
        }
        return new OWLLiteralImplNoCompression(literal, normalisedLang, null);
    }

    @Nonnull
    @Override
    public OWLLiteral getOWLLiteral(int value) {
        return new OWLLiteralImplInteger(value, getIntegerOWLDatatype());
    }

    @Nonnull
    @Override
    public OWLLiteral getOWLLiteral(boolean value) {
        return value ? trueLiteral : falseLiteral;
    }

    @Nonnull
    @Override
    public OWLLiteral getOWLLiteral(double value) {
        return new OWLLiteralImplDouble(value, getDoubleOWLDatatype());
    }

    @Nonnull
    @Override
    public OWLLiteral getOWLLiteral(@Nonnull String lexicalValue, @SuppressWarnings("NullableProblems") @Nonnull OWLDatatype datatype) {
        OWLLiteral literal;
        if (datatype.isRDFPlainLiteral()) {
            int sep = lexicalValue.lastIndexOf('@');
            if (sep != -1) {
                String lex = lexicalValue.substring(0, sep);
                String lang = lexicalValue.substring(sep + 1);
                literal = getBasicLiteral(lex, lang, getRDFPlainLiteral());
            } else {
                literal = getBasicLiteral(lexicalValue, datatype);
            }
        } else {
            // check the four special cases
            try {
                if (datatype.isBoolean()) {
                    literal = getOWLLiteral(isBooleanTrueValue(lexicalValue
                            .trim()));
                } else if (datatype.isFloat()) {
                    if (lexicalValue.trim().equals("-0.0")) {
                        // according to some W3C test, this needs to be
                        // different from 0.0; Java floats disagree
                        if (negativeFloatZero == null) {
                            negativeFloatZero = getBasicLiteral("-0.0",
                                    XSDFLOAT);
                        }
                        literal = negativeFloatZero;
                    } else {
                        try {
                            float f = Float.parseFloat(lexicalValue);
                            literal = getOWLLiteral(f);
                        } catch (NumberFormatException e) {
                            literal = getBasicLiteral(lexicalValue, datatype);
                        }
                    }
                } else if (datatype.isDouble()) {
                    literal = getOWLLiteral(Double.parseDouble(lexicalValue));
                } else if (datatype.isInteger()) {
                    // again, some W3C tests require padding zeroes to make
                    // literals different
                    if (lexicalValue.trim().charAt(0) == '0') {
                        literal = getBasicLiteral(lexicalValue,
                                getIntegerOWLDatatype());
                    } else {
                        try {
                            literal = getOWLLiteral(Integer
                                    .parseInt(lexicalValue));
                        } catch (NumberFormatException ex) {
                            // try as a big decimal
                            literal = getBasicLiteral(lexicalValue, datatype);
                        }
                    }
                } else {
                    literal = getBasicLiteral(lexicalValue, datatype);
                }
            } catch (NumberFormatException e) {
                // some literal is malformed, i.e., wrong format
                literal = getBasicLiteral(lexicalValue, datatype);
            }
        }
        return literal;
    }

    @Nullable
    protected OWLLiteral getBasicLiteral(@Nonnull String lexicalValue,
            OWLDatatype datatype) {
        return getBasicLiteral(lexicalValue, "", datatype);
    }

    @Nullable
    protected OWLLiteral getBasicLiteral(@Nonnull String lexicalValue, String lang,
            OWLDatatype datatype) {
        OWLLiteral literal = null;
        if (useCompression) {
            literal = new OWLLiteralImpl(lexicalValue, lang, datatype);
        } else {
            literal = new OWLLiteralImplNoCompression(lexicalValue, lang,
                    datatype);
        }
        return literal;
    }

    private boolean isBooleanTrueValue(@Nonnull String lexicalValue) {
        return lexicalValue.equals("1") || lexicalValue.equals("true");
    }

    @Nonnull
    @Override
    public OWLDatatype getTopDatatype() {
        return RDFSLITERAL;
    }

    @Nonnull
    @Override
    public OWLDatatype getIntegerOWLDatatype() {
        return XSDINTEGER;
    }

    @Nonnull
    @Override
    public OWLDatatype getFloatOWLDatatype() {
        return XSDFLOAT;
    }

    @Nonnull
    @Override
    public OWLDatatype getDoubleOWLDatatype() {
        return XSDDOUBLE;
    }

    @Nonnull
    @Override
    public OWLDatatype getBooleanOWLDatatype() {
        return XSDBOOLEAN;
    }

    @Nonnull
    @Override
    public OWLDatatype getRDFPlainLiteral() {
        return PLAIN;
    }
}
