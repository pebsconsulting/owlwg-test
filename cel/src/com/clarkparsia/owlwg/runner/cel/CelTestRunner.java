package com.clarkparsia.owlwg.runner.cel;

import java.net.URI;
import java.util.Collections;
import java.util.Iterator;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLLogicalAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

import com.clarkparsia.owlwg.runner.AbstractTestRunner;
import com.clarkparsia.owlwg.runner.EntailmentChecker;

/**
 * Test runner for CEL.
 */
public class CelTestRunner extends AbstractTestRunner {

	private static final URI uri;

	static {
		uri = URI.create("http://lat.inf.tu-dresden.de/systems/cel/");
	}

	private OWLReasoner reasoner = null;

	public void dispose() throws OWLReasonerException {
		this.reasoner.dispose();
	}

	public URI getURI() {
		return uri;
	}

	protected boolean isConsistent(OWLOntologyManager manager, OWLOntology o)
			throws OWLReasonerException {
		OWLReasoner reasoner = CelReasonerManager.getInstance().getCelReasoner(
				manager);
		this.reasoner = reasoner;

		reasoner.clearOntologies();
		reasoner.loadOntologies(Collections.singleton(o));
		reasoner.classify();
		boolean ret = reasoner.isConsistent(o);
		return ret;
	}

	protected boolean isEntailed(OWLOntologyManager manager,
			OWLOntology premise, OWLOntology conclusion)
			throws OWLReasonerException {

		OWLReasoner reasoner = CelReasonerManager.getInstance().getCelReasoner(
				manager);
		this.reasoner = reasoner;

		reasoner.clearOntologies();
		reasoner.loadOntologies(Collections.singleton(premise));
		reasoner.classify();

		EntailmentChecker checker = new EntailmentChecker(reasoner, manager
				.getOWLDataFactory());

		boolean ret = true;
		for (Iterator<OWLLogicalAxiom> it = conclusion.getLogicalAxioms()
				.iterator(); ret && it.hasNext();) {
			OWLLogicalAxiom axiom = it.next();

			if (!checker.isEntailed(axiom)) {
				ret = false;
			}
		}
		return ret;
	}
}
