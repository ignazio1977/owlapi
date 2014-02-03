/**
 * 
 */
package org.semanticweb.owlapi.formats;

import org.openrdf.rio.RDFFormat;
import org.semanticweb.owlapi.io.RDFOntologyFormat;

/**
 * This format is designed to encapsulate any Sesame Rio RDFFormat within RDFOntologyFormat, and
 * more generally OWLOntologyFormat.
 * 
 * <br>
 * 
 * NOTE: This class extends the deprecated org.semanticweb.owlapi.io.RDFOntologyFormat so that it is
 * recognised as an instanceof that class by users who have not yet migrated to the
 * org.semanticweb.owlapi.formats namespace.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
@SuppressWarnings("deprecation")
public class RioRDFOntologyFormat extends org.semanticweb.owlapi.formats.RDFOntologyFormat
{
    private static final long serialVersionUID = 4211005024818991313L;
    
    private RDFFormat format;
    
    /**
     * Constructor for super-classes to specify which {@link RDFFormat} they support.
     * @param format The {@link RDFFormat} that this instance supports.
     */
    public RioRDFOntologyFormat(RDFFormat format)
    {
        super();
        this.format = format;
    }
    
    @Override
    public String getKey()
    {
        return getRioFormat().toString();
    }
    
    public RDFFormat getRioFormat()
    {
        return this.format;
    }
}
