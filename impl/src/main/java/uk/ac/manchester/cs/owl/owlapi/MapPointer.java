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

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.Internals;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.owl.owlapi.InitVisitorFactory.InitCollectionVisitor;
import uk.ac.manchester.cs.owl.owlapi.InitVisitorFactory.InitVisitor;

/**
 * Map implementation for a pointer.
 * 
 * @author ignazio
 * @param <K>
 *        key
 * @param <V>
 *        value
 */
public class MapPointer<K, V extends OWLAxiom> implements
        Internals.Pointer<K, V>, Serializable {

    private static final long serialVersionUID = 40000L;
    private final MultiMap<K, V> map;
    private final AxiomType<?> type;
    private final OWLAxiomVisitorEx<?> visitor;
    private boolean initialized;
    protected final Internals i;

    /**
     * @param t
     *        type of axioms contained
     * @param v
     *        visitor
     * @param initialized
     *        true if initialized
     * @param i
     *        internals containing this pointer
     */
    public MapPointer(@Nullable AxiomType<?> t,
            @Nullable OWLAxiomVisitorEx<?> v, boolean initialized,
            @Nonnull Internals i) {
        type = t;
        visitor = v;
        map = new MultiMap<K, V>();
        this.initialized = initialized;
        this.i = checkNotNull(i, "i cannot be null");
    }

    /** @return true if initialized */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * init the map pointer
     * 
     * @return the map pointer
     */
    @SuppressWarnings("unchecked")
    public MapPointer<K, V> init() {
        if (initialized) {
            return this;
        }
        initialized = true;
        if (visitor == null) {
            return this;
        }
        if (visitor instanceof InitVisitor) {
            for (V ax : (Set<V>) i.getValues(i.getAxiomsByType(), type)) {
                K key = ax.accept((InitVisitor<K>) visitor);
                if (key != null) {
                    map.put(key, ax);
                }
            }
        } else {
            for (V ax : (Set<V>) i.getValues(i.getAxiomsByType(), type)) {
                Collection<K> keys = ax
                        .accept((InitCollectionVisitor<K>) visitor);
                for (K key : keys) {
                    map.put(key, ax);
                }
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return initialized + map.toString();
    }

    /** @return keyset */
    public Set<K> keySet() {
        return CollectionFactory.getCopyOnRequestSetFromMutableCollection(map
                .keySet());
    }

    /**
     * @param key
     *        key to look up
     * @return value
     */
    public Set<V> getValues(K key) {
        return CollectionFactory.getCopyOnRequestSetFromMutableCollection(map
                .get(key));
    }

    /**
     * @param key
     *        key to look up
     * @return true if there are values for key
     */
    public boolean hasValues(K key) {
        return map.containsKey(key);
    }

    /**
     * @param key
     *        key to add
     * @param value
     *        value to add
     * @return true if addition happens
     */
    public boolean put(K key, V value) {
        return map.put(key, value);
    }

    /**
     * @param key
     *        key to look up
     * @param value
     *        value to remove
     * @return true if removal happens
     */
    public boolean remove(K key, V value) {
        return map.remove(key, value);
    }

    /**
     * @param key
     *        key to look up
     * @return true if there are values for key
     */
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    /**
     * @param key
     *        key to look up
     * @param value
     *        value to look up
     * @return true if key and value are contained
     */
    public boolean contains(K key, V value) {
        return map.contains(key, value);
    }

    /** @return all values contained */
    public Set<V> getAllValues() {
        return map.getAllValues();
    }

    /** @return number of mapping contained */
    public int size() {
        return map.size();
    }

    /** @return true if empty */
    public boolean isEmpty() {
        return map.size() == 0;
    }
}
