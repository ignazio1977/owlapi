/**
 * 
 */
package org.semanticweb.owlapi.formats;

import org.kohsuke.MetaInfServices;
import org.openrdf.rio.RDFFormat;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
@MetaInfServices(OWLOntologyFormat.class)
public class BinaryRdfOntologyFormat extends RioRDFOntologyFormat
{
    private static final long serialVersionUID = -2440786029449269231L;
    
    /**
     * RDF format for {@link RDFFormat#BINARY} documents.
     */
    public BinaryRdfOntologyFormat()
    {
        super(RDFFormat.BINARY);
    }
    
}
