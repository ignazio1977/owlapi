package org.obolibrary.obo2owl;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Set;

import org.junit.Test;
import org.obolibrary.obo2owl.Obo2OWLConstants.Obo2OWLVocabulary;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.google.common.base.Optional;

@SuppressWarnings("javadoc")
public class Owl2OboAltIdTest extends OboFormatTestBasics {

    @Test
    public void testOwl2OboClass() throws Exception {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLOntology simple = m.createOntology(IRI.generateDocumentIRI());

        // add class A
        OWLClass classA = f.getOWLClass(IRI
                .create(Obo2OWLConstants.DEFAULT_IRI_PREFIX + "TEST_0001"));
        m.addAxiom(simple, f.getOWLDeclarationAxiom(classA));
        // add a label and OBO style ID
        addLabelAndId(classA, "test1", "TEST:0001", simple);

        // add deprecated class B as an alternate ID for A
        OWLClass classB = f.getOWLClass(IRI
                .create(Obo2OWLConstants.DEFAULT_IRI_PREFIX + "TEST_0002"));
        m.addAxiom(simple, f.getOWLDeclarationAxiom(classB));
        setAltId(classB, "TEST:0001", simple);
        // add comment to alt_id class, which is not expressible in OBO
        addAnnotation(classB, f.getRDFSComment(), f.getOWLLiteral("Comment"), simple);

        // translate to OBO
        OWLAPIOwl2Obo owl2obo = new OWLAPIOwl2Obo(m);
        OBODoc oboDoc = owl2obo.convert(simple);

        // check result: expect only one term frame for class TEST:0001 with
        // alt_id Test:0002
        Collection<Frame> termFrames = oboDoc.getTermFrames();
        assertEquals(1, termFrames.size());
        Frame frame = termFrames.iterator().next();
        assertEquals("TEST:0001", frame.getId());
        Collection<Clause> altIdClauses = frame
                .getClauses(OboFormatTag.TAG_ALT_ID);
        assertEquals(1, altIdClauses.size());
        String altId = altIdClauses.iterator().next().getValue(String.class);
        assertEquals("TEST:0002", altId);

        // roundtrip back to OWL, check that comment is still there
        OWLAPIObo2Owl obo2owl = new OWLAPIObo2Owl(OWLManager.createOWLOntologyManager());
        OWLOntology roundTripped = obo2owl.convert(oboDoc);
        Set<OWLAnnotationAssertionAxiom> annotations = roundTripped.getAnnotationAssertionAxioms(classB.getIRI());
        assertEquals(4, annotations.size()); // three for the alt-id plus one for the comment
        String comment = null;
        for (OWLAnnotationAssertionAxiom ax : annotations) {
            if (ax.getProperty().isComment()) {
                Optional<OWLLiteral> asLiteral = ax.getValue().asLiteral();
                if (asLiteral.isPresent()) {
                    comment = asLiteral.get().getLiteral();
                }
            }
        }
        assertEquals("Comment", comment);
    }

    @Test
    public void testOwl2OboProperty() throws Exception {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLOntology simple = m.createOntology(IRI.generateDocumentIRI());

        // add prop1
        OWLObjectProperty p1 = f.getOWLObjectProperty(IRI
                .create(Obo2OWLConstants.DEFAULT_IRI_PREFIX + "TEST_0001"));
        m.addAxiom(simple, f.getOWLDeclarationAxiom(p1));
        // add label and OBO style id for
        addLabelAndId(p1, "prop1", "TEST:0001", simple);

        // add deprecated prop 2 as an alternate ID for prop 1
        OWLObjectProperty p2 = f.getOWLObjectProperty(IRI
                .create(Obo2OWLConstants.DEFAULT_IRI_PREFIX + "TEST_0002"));
        m.addAxiom(simple, f.getOWLDeclarationAxiom(p2));
        setAltId(p2, "TEST:0002", simple);
        // add comment to alt_id class, which is not expressible in OBO
        addAnnotation(p2, f.getRDFSComment(), f.getOWLLiteral("Comment"), simple);

        // translate to OBO
        OWLAPIOwl2Obo owl2obo = new OWLAPIOwl2Obo(m);
        OBODoc oboDoc = owl2obo.convert(simple);

        // check result: expect only one typdef frame for prop TEST:0001 with
        // alt_id Test:0002
        Collection<Frame> termFrames = oboDoc.getTypedefFrames();
        assertEquals(1, termFrames.size());
        Frame frame = termFrames.iterator().next();
        assertEquals("TEST:0001", frame.getId());
        Collection<Clause> altIdClauses = frame
                .getClauses(OboFormatTag.TAG_ALT_ID);
        assertEquals(1, altIdClauses.size());
        String altId = altIdClauses.iterator().next().getValue(String.class);
        assertEquals("TEST:0002", altId);

        // roundtrip back to OWL, check that comment is still there
        OWLAPIObo2Owl obo2owl = new OWLAPIObo2Owl(OWLManager.createOWLOntologyManager());
        OWLOntology roundTripped = obo2owl.convert(oboDoc);
        Set<OWLAnnotationAssertionAxiom> annotations = roundTripped.getAnnotationAssertionAxioms(p2.getIRI());
        assertEquals(4, annotations.size()); // three for the alt-id plus one for the comment
        String comment = null;
        for (OWLAnnotationAssertionAxiom ax : annotations) {
            if (ax.getProperty().isComment()) {
                Optional<OWLLiteral> asLiteral = ax.getValue().asLiteral();
                if (asLiteral.isPresent()) {
                    comment = asLiteral.get().getLiteral();
                }
            }
        }
        assertEquals("Comment", comment);
    }

    private void addLabelAndId(OWLNamedObject obj, String label, String id,
            OWLOntology o) {
        OWLDataFactory f = o.getOWLOntologyManager().getOWLDataFactory();
        addAnnotation(obj, f.getRDFSLabel(), f.getOWLLiteral(label), o);
        OWLAnnotationProperty idProp = f.getOWLAnnotationProperty(OWLAPIObo2Owl
                .trTagToIRI(OboFormatTag.TAG_ID.getTag()));
        addAnnotation(obj, idProp, f.getOWLLiteral(id), o);
    }

    private void setAltId(OWLNamedObject obj, String replacedBy, OWLOntology o) {
        OWLDataFactory f = o.getOWLOntologyManager().getOWLDataFactory();
        addAnnotation(
                obj,
                f.getOWLAnnotationProperty(Obo2OWLVocabulary.IRI_IAO_0100001.iri),
                f.getOWLLiteral("TEST:0001"), o);
        addAnnotation(obj,
                f.getOWLAnnotationProperty(Obo2OWLConstants.IRI_IAO_0000231),
                Obo2OWLConstants.IRI_IAO_0000227, o);
        addAnnotation(obj, f.getOWLDeprecated(), f.getOWLLiteral(true), o);
    }

    private void addAnnotation(OWLNamedObject obj, OWLAnnotationProperty p,
            OWLAnnotationValue v, OWLOntology ont) {
        OWLOntologyManager m = ont.getOWLOntologyManager();
        OWLDataFactory f = m.getOWLDataFactory();
        m.addAxiom(
                ont,
                f.getOWLAnnotationAssertionAxiom(obj.getIRI(),
                        f.getOWLAnnotation(p, v)));
    }
}
