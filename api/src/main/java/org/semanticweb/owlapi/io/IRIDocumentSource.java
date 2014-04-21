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
package org.semanticweb.owlapi.io;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import java.io.InputStream;
import java.io.Reader;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLRuntimeException;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
public class IRIDocumentSource extends OWLOntologyDocumentSourceBase {

    @Nonnull
    private final IRI documentIRI;

    /**
     * @param documentIRI
     *        the source document IRI
     * @param format
     *        ontology format
     * @param mime
     *        mime type
     */
    public IRIDocumentSource(@Nonnull IRI documentIRI,
            OWLOntologyFormat format, String mime) {
        super(format, mime);
        this.documentIRI = checkNotNull(documentIRI,
                "document iri cannot be null");
    }

    @Nonnull
    @Override
    public IRI getDocumentIRI() {
        return documentIRI;
    }

    @Override
    public boolean isInputStreamAvailable() {
        return false;
    }

    @Nonnull
    @Override
    public InputStream getInputStream() {
        throw new OWLRuntimeException(
                "InputStream not available.  Check with IRIDocumentSource.isInputStreamAvailable() first!");
    }

    @Override
    public boolean isReaderAvailable() {
        return false;
    }

    @Nonnull
    @Override
    public Reader getReader() {
        throw new OWLRuntimeException(
                "Reader not available.  Check with IRIDocumentSource.isReaderAvailable() first!");
    }

    @Nonnull
    @Override
    public String toString() {
        return documentIRI.toString();
    }
}
