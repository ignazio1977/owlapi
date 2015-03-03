package org.semanticweb.owlapi.api.test.annotations;/**
 * Created by ses on 3/2/15.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import static org.junit.Assert.assertEquals;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

public class PunRunner extends org.junit.runner.Runner {
    private final Class testClass;

    public PunRunner(Class testClass) {
        this.testClass = testClass;
        System.err.println("PunRunner started");
    }

    class TestSetting {
        OWLEntity[] entities;
        Class<? extends PrefixOWLOntologyFormat> formatClass;

        public TestSetting(Class<? extends PrefixOWLOntologyFormat> formatClass, OWLEntity... entities) {
            this.formatClass = formatClass;
            this.entities = entities;
        }
    }

    private Description suiteDescription;
    private Map<Description, TestSetting> testSettings = new HashMap<Description, TestSetting>();

    @Override
    public Description getDescription() {
        suiteDescription = Description.createSuiteDescription(testClass);
        addAllTests();
        return suiteDescription;
    }

    private void addAllTests() {
        DefaultPrefixManager pm = new DefaultPrefixManager("http://localhost#");
        OWLDataFactory df = OWLManager.getOWLDataFactory();
        List<? extends OWLEntity> entities = Arrays.asList(
                df.getOWLClass("a", pm), df.getOWLDatatype("a", pm),
                df.getOWLAnnotationProperty("a", pm),
                df.getOWLDataProperty("a", pm),
                df.getOWLObjectProperty("a", pm),
                df.getOWLNamedIndividual("a", pm));
        List<Class<? extends PrefixOWLOntologyFormat>> formats = Arrays.asList(
                RDFXMLOntologyFormat.class,
                TurtleOntologyFormat.class,
                OWLFunctionalSyntaxOntologyFormat.class,
                ManchesterOWLSyntaxOntologyFormat.class
        );
        for (Class<? extends PrefixOWLOntologyFormat> formatClass : formats) {
            for (int i = 0; i < entities.size(); i++) {
                OWLEntity e1 = entities.get(i);
                for (int j = i + 1; j < entities.size(); j++) {
                    OWLEntity e2 = entities.get(j);
                    String formatClassName = formatClass.getName();
                    int i1 = formatClassName.lastIndexOf('.');
                    if (i1 > -1) {
                        formatClassName = formatClassName.substring(i1 + 1);
                    }
                    String name = String.format("%sVs%sFor%s", e1.getEntityType(), e2.getEntityType(), formatClassName);
                    Description testDescription = Description.createTestDescription(testClass, name);
                    testSettings.put(testDescription, new TestSetting(formatClass, e1, e2));
                    suiteDescription.addChild(testDescription);
                }
            }
            String name = "multiPun for " + formatClass.getName();
            Description testDescription = Description.createTestDescription(testClass, name);
            suiteDescription.addChild(testDescription);
            TestSetting setting =
                    new TestSetting(formatClass,
                            df.getOWLClass("a", pm),
                            df.getOWLDatatype("a", pm),
                            df.getOWLAnnotationProperty("a", pm),
                            df.getOWLDataProperty("a", pm),
                            df.getOWLObjectProperty("a", pm),
                            df.getOWLNamedIndividual("a", pm));
            testSettings.put(testDescription, setting);
        }
    }

    /**
     * Run the tests for this runner.
     *
     * @param notifier will be notified of events while tests are being run--tests being
     *                 started, finishing, and failing
     */
    @Override
    public void run(RunNotifier notifier) {
        for (Map.Entry<Description, TestSetting> entry : testSettings.entrySet()) {
            Description description = entry.getKey();
            notifier.fireTestStarted(description);
            try {
                TestSetting setting = entry.getValue();
                runTestForAnnotationsOnPunnedEntitiesForFormat(setting.formatClass, setting.entities);
            } catch (Throwable t) {
                notifier.fireTestFailure(new Failure(description, t));
            } finally {
                notifier.fireTestFinished(description);
            }
        }
    }

    public void runTestForAnnotationsOnPunnedEntitiesForFormat(
            Class<? extends PrefixOWLOntologyFormat> formatClass, OWLEntity... entities)
            throws OWLOntologyCreationException, OWLOntologyStorageException,
            IllegalAccessException, InstantiationException {

        OWLOntologyManager ontologyManager;
        OWLDataFactory df;
        synchronized (OWLManager.class) {
            ontologyManager = OWLManager.createOWLOntologyManager();
            df = ontologyManager.getOWLDataFactory();
        }

        OWLAnnotationProperty annotationProperty = df.getOWLAnnotationProperty(
                ":ap", new DefaultPrefixManager(
                        "http://localhost#"));

        OWLOntology o = makeOwlOntologyWithDeclarationsAndAnnotationAssertions(
                annotationProperty, ontologyManager, entities);
        for (int i = 0; i < 10; i++) {
            PrefixOWLOntologyFormat format = formatClass.newInstance();
            format.setPrefixManager(new DefaultPrefixManager(
                    "http://localhost#"));
            ByteArrayInputStream in = saveForRereading(o, format, ontologyManager);
            ontologyManager.removeOntology(o);
            o = ontologyManager.loadOntologyFromOntologyDocument(in);
        }
        assertEquals("annotationCount", entities.length,
                o.getAxioms(AxiomType.ANNOTATION_ASSERTION).size());
    }

    public static OWLOntology makeOwlOntologyWithDeclarationsAndAnnotationAssertions(
            OWLAnnotationProperty annotationProperty, OWLOntologyManager manager, OWLEntity... entities)
            throws OWLOntologyCreationException {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        axioms.add(dataFactory.getOWLDeclarationAxiom(annotationProperty));
        for (OWLEntity entity : entities) {
            axioms.add(dataFactory.getOWLAnnotationAssertionAxiom(annotationProperty,
                    entity.getIRI(), dataFactory.getOWLAnonymousIndividual()));
            axioms.add(dataFactory.getOWLDeclarationAxiom(entity));
        }
        return manager.createOntology(axioms);
    }

    public static ByteArrayInputStream saveForRereading(OWLOntology o,
                                                        PrefixOWLOntologyFormat format, OWLOntologyManager manager) throws OWLOntologyStorageException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        manager.saveOntology(o, format, out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
