package org.obolibrary.macro;

import java.util.Set;

import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OntologyAxiomPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** macro expansion gci visitor */
public class MacroExpansionGCIVisitor {

    protected static final Logger LOG = LoggerFactory
            .getLogger(MacroExpansionGCIVisitor.class.getName());
    private final OWLOntology inputOntology;
    private final OWLOntologyManager outputManager;
    private final OWLOntology outputOntology;
    protected final ManchesterSyntaxTool manchesterSyntaxTool;
    private final GCIVisitor visitor;

    /**
     * @param inputOntology
     *        inputOntology
     * @param outputManager
     *        outputManager
     */
    public MacroExpansionGCIVisitor(OWLOntology inputOntology,
            OWLOntologyManager outputManager) {
        this.inputOntology = inputOntology;
        visitor = new GCIVisitor(inputOntology);
        manchesterSyntaxTool = new ManchesterSyntaxTool(inputOntology);
        this.outputManager = outputManager;
        try {
            outputOntology = outputManager.createOntology(inputOntology
                    .getOntologyID());
        } catch (Exception ex) {
            throw new OWLRuntimeException(ex);
        }
    }

    protected void output(OWLAxiom axiom) {
        if (axiom == null) {
            LOG.error("no axiom");
            return;
        }
        // System.out.println("adding:"+axiom);
        AddAxiom addAx = new AddAxiom(outputOntology, axiom);
        try {
            outputManager.applyChange(addAx);
        } catch (Exception e) {
            LOG.error("COULD NOT TRANSLATE AXIOM", e);
        }
    }

    /** @return ontology for gci */
    public OWLOntology createGCIOntology() {
        for (OWLAxiom ax : inputOntology.getAxioms()) {
            if (ax instanceof OWLSubClassOfAxiom) {
                visitor.visit((OWLSubClassOfAxiom) ax);
            } else if (ax instanceof OWLEquivalentClassesAxiom) {
                visitor.visit((OWLEquivalentClassesAxiom) ax);
            } else if (ax instanceof OWLClassAssertionAxiom) {
                visitor.visit((OWLClassAssertionAxiom) ax);
            } else if (ax instanceof OWLAnnotationAssertionAxiom) {
                expand((OWLAnnotationAssertionAxiom) ax);
            }
        }
        return outputOntology;
    }

    @SuppressWarnings("null")
    private void expand(OWLAnnotationAssertionAxiom ax) {
        OWLAnnotationProperty prop = ax.getProperty();
        String expandTo = visitor.expandAssertionToMap.get(prop.getIRI());
        if (expandTo != null) {
            LOG.info("Template to Expand {}", expandTo);
            expandTo = expandTo.replaceAll("\\?X",
                    manchesterSyntaxTool.getId((IRI) ax.getSubject()));
            expandTo = expandTo.replaceAll("\\?Y",
                    manchesterSyntaxTool.getId((IRI) ax.getValue()));
            LOG.info("Expanding {}", expandTo);
            try {
                Set<OntologyAxiomPair> setAxp = manchesterSyntaxTool
                        .parseManchesterExpressionFrames(expandTo);
                for (OntologyAxiomPair axp : setAxp) {
                    output(axp.getAxiom());
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private class GCIVisitor extends AbstractMacroExpansionVisitor {

        GCIVisitor(OWLOntology inputOntology) {
            super(inputOntology);
        }

        @Override
        protected OWLClassExpression expandOWLObjSomeVal(
                OWLClassExpression filler, OWLObjectPropertyExpression p) {
            OWLClassExpression gciRHS = expandObject(filler, p);
            if (gciRHS != null) {
                OWLClassExpression gciLHS = dataFactory
                        .getOWLObjectSomeValuesFrom(p, filler);
                OWLEquivalentClassesAxiom ax = dataFactory
                        .getOWLEquivalentClassesAxiom(gciLHS, gciRHS);
                output(ax);
            }
            return gciRHS;
        }

        @Override
        protected OWLClassExpression expandOWLObjHasVal(OWLObjectHasValue desc,
                OWLIndividual filler, OWLObjectPropertyExpression p) {
            OWLClassExpression gciRHS = expandObject(filler, p);
            if (gciRHS != null) {
                OWLClassExpression gciLHS = dataFactory.getOWLObjectHasValue(p,
                        filler);
                OWLEquivalentClassesAxiom ax = dataFactory
                        .getOWLEquivalentClassesAxiom(gciLHS, gciRHS);
                output(ax);
            }
            return gciRHS;
        }

        @SuppressWarnings("null")
        private OWLClassExpression expandObject(Object filler,
                OWLObjectPropertyExpression p) {
            OWLClassExpression result = null;
            IRI iri = ((OWLObjectProperty) p).getIRI();
            IRI templateVal = null;
            if (expandExpressionMap.containsKey(iri)) {
                if (filler instanceof OWLObjectOneOf) {
                    Set<OWLIndividual> inds = ((OWLObjectOneOf) filler)
                            .getIndividuals();
                    if (inds.size() == 1) {
                        OWLIndividual ind = inds.iterator().next();
                        if (ind instanceof OWLNamedIndividual) {
                            templateVal = ((OWLNamedObject) ind).getIRI();
                        }
                    }
                }
                if (filler instanceof OWLNamedObject) {
                    templateVal = ((OWLNamedObject) filler).getIRI();
                }
                if (templateVal != null) {
                    String tStr = expandExpressionMap.get(iri);
                    String exStr = tStr.replaceAll("\\?Y",
                            manchesterSyntaxTool.getId(templateVal));
                    try {
                        result = manchesterSyntaxTool
                                .parseManchesterExpression(exStr);
                    } catch (OWLParserException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
            return result;
        }
    }

    /** Call this method to clear internal references. */
    public void dispose() {
        manchesterSyntaxTool.dispose();
    }
}
