/*
 * This file is part of the OWL API.
 * 
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * 
 * Copyright (C) 2011, The University of Queensland
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see http://www.gnu.org/licenses/.
 * 
 * 
 * Alternatively, the contents of this file may be used under the terms of the Apache License,
 * Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable
 * instead of those above.
 * 
 * Copyright 2011, The University of Queensland
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.semanticweb.owlapi.rio;

import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.Iterations;

import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Model;
import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.model.util.Namespaces;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.semanticweb.owlapi.formats.OWLOntologyFormatFactory;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLRuntimeException;

/**
 * An implementation of the OWLOntologyDocumentSource interface that does not implement any of the
 * InputStream, Reader, or IRI source retrieval methods. Instead it holds a reference to an iterator
 * that will generate bare triples to be interpreted by a Sesame Rio based OWLParser implementation.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class RioMemoryTripleSource implements OWLOntologyDocumentSource
{
    private final Map<String, String> namespaces = new LinkedHashMap<String, String>();
    private final Iterator<Statement> statementIterator;
    private IRI documentIRI;
    private static final AtomicInteger counter = new AtomicInteger(0);
    
    /**
     * Creates a RioMemoryTripleSource using an {@link Iterable} of {@link Statement} objects.
     * 
     * @param statements
     *            An {@link Iterator} of {@link Statement} objects that make up this source.
     */
    public RioMemoryTripleSource(final Iterator<Statement> statements)
    {
        this.documentIRI = IRI.create("urn:rio-memory-triples:" + counter.incrementAndGet());
        this.statementIterator = statements;
    }
    
    /**
     * Creates a RioMemoryTripleSource using an {@link Iterable} of {@link Statement} objects.
     * 
     * @param statements
     *            An {@link Iterator} of {@link Statement} objects that make up this source.
     * @param namespaces
     *            A Map of namespaces from prefix to full URI which are to be used by this source.
     */
    public RioMemoryTripleSource(final Iterator<Statement> statements, final Map<String, String> namespaces)
    {
        this(statements);
        this.namespaces.putAll(namespaces);
    }
    
    /**
     * Creates a RioMemoryTripleSource using an {@link Iterable} of {@link Statement} objects. <br>
     * If the Iterable is an instance of {@link Model}, the namespaces from the model are also used.
     * 
     * @param statements
     *            A {@link CloseableIteration} of {@link Statement} objects that make up this
     *            source.
     */
    public RioMemoryTripleSource(final Iterable<Statement> statements)
    {
        this.documentIRI = IRI.create("urn:rio-memory-triples:" + counter.incrementAndGet());
        this.statementIterator = statements.iterator();
        if(statements instanceof Model)
        {
            this.namespaces.putAll(Namespaces.asMap(((Model)statements).getNamespaces()));
        }
    }
    
    /**
     * Creates a RioMemoryTripleSource using a closeable iteration. Internally this wraps the
     * statements as an Iterator, and hence all statements must be read for the iterator to be
     * closed automatically.
     * 
     * @param statements
     *            A {@link CloseableIteration} of {@link Statement} objects that make up this
     *            source.
     */
    public RioMemoryTripleSource(final CloseableIteration<Statement, ? extends OpenRDFException> statements)
    {
        this.documentIRI = IRI.create("urn:rio-memory-triples:" + counter.incrementAndGet());
        this.statementIterator = new Iterator<Statement>()
            {
                
                @Override
                public void remove()
                {
                    throw new UnsupportedOperationException("Cannot remove statements using this iterator");
                }
                
                @Override
                public Statement next()
                {
                    Statement nextStatement = null;
                    try
                    {
                        nextStatement = statements.next();
                        
                        if(nextStatement != null)
                        {
                            return nextStatement;
                        }
                        else
                        {
                            throw new NoSuchElementException("No more statements in this iterator");
                        }
                    }
                    catch(OpenRDFException e)
                    {
                        throw new OWLRuntimeException("Found exception while iterating", e);
                    }
                    finally
                    {
                        if(nextStatement == null)
                        {
                            try
                            {
                                statements.close();
                            }
                            catch(OpenRDFException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                
                @Override
                public boolean hasNext()
                {
                    boolean result = false;
                    
                    try
                    {
                        result = statements.hasNext();
                        
                        return result;
                    }
                    catch(OpenRDFException e)
                    {
                        throw new OWLRuntimeException("Found exception while iterating", e);
                    }
                    finally
                    {
                        if(!result)
                        {
                            try
                            {
                                statements.close();
                            }
                            catch(OpenRDFException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
    }
    
    /**
     * Creates a RioMemoryTripleSource using a closeable iteration. Internally this wraps the
     * statements as an Iterator, and hence all statements must be read for the iterator to be
     * closed automatically.
     * 
     * @param statements
     *            A {@link CloseableIteration} of {@link Statement} objects that make up this
     *            source.
     * @param namespaces
     *            A Map of namespaces from prefix to full URI which are to be used by this source.
     */
    public RioMemoryTripleSource(final CloseableIteration<Statement, ? extends OpenRDFException> statements,
            final Map<String, String> namespaces)
    {
        this(statements);
        
        this.namespaces.putAll(namespaces);
        
    }
    
    @Override
    public boolean isReaderAvailable()
    {
        return false;
    }
    
    @Override
    public Reader getReader()
    {
        return null;
    }
    
    @Override
    public boolean isInputStreamAvailable()
    {
        return false;
    }
    
    @Override
    public InputStream getInputStream()
    {
        return null;
    }
    
    @Override
    public IRI getDocumentIRI()
    {
        return this.documentIRI;
    }
    
    @Override
    public boolean isFormatKnown()
    {
        return false;
    }
    
    @Override
    public OWLOntologyFormatFactory getFormatFactory()
    {
        return null;
    }
    
    public Map<String, String> getNamespaces()
    {
        return this.namespaces;
    }
    
    public Iterator<Statement> getStatementIterator()
    {
        return this.statementIterator;
    }
    
    public void setNamespaces(Set<Namespace> nextNamespaces)
    {
        this.namespaces.clear();
        this.namespaces.putAll(Namespaces.asMap(nextNamespaces));
    }
    
    public void setNamespaces(Map<String, String> nextNamespaces)
    {
        this.namespaces.clear();
        this.namespaces.putAll(nextNamespaces);
    }
    
    public void setNamespaces(RepositoryResult<Namespace> namespaces) throws RepositoryException
    {
        setNamespaces(Namespaces.asMap(Iterations.asSet(namespaces)));
    }
}
