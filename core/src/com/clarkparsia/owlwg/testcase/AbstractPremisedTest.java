package com.clarkparsia.owlwg.testcase;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.io.StringInputSource;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;

/**
 * <p>
 * Title: Abstract Premised Test Case
 * </p>
 * <p>
 * Description: Common base implementation shared by consistency, positive
 * entailment, and negative entailment tests.
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a
 * href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public abstract class AbstractPremisedTest extends AbstractBaseTestCase {

	private final EnumMap<SerializationFormat, OWLOntology>	premiseOntology;
	private final EnumSet<SerializationFormat>				premiseFormats;
	private final EnumMap<SerializationFormat, String>		premiseOntologyLiteral;

	public AbstractPremisedTest(OWLOntology ontology, OWLIndividual i) {
		super( ontology, i );

		premiseFormats = EnumSet.noneOf( SerializationFormat.class );
		premiseOntologyLiteral = new EnumMap<SerializationFormat, String>(
				SerializationFormat.class );
		premiseOntology = new EnumMap<SerializationFormat, OWLOntology>( SerializationFormat.class );

		Map<OWLDataPropertyExpression, Set<OWLConstant>> values = i
				.getDataPropertyValues( ontology );

		for( SerializationFormat f : SerializationFormat.values() ) {
			Set<OWLConstant> premises = values.get( f.getPremiseOWLDataProperty() );
			if( premises != null ) {
				if( premises.size() != 1 ) {
				         System.err.println("Error: " + f.toString());
					//throw new IllegalArgumentException( f.toString() );
				} else {
				    premiseOntologyLiteral.put( f, premises.iterator().next().getLiteral() );
				    premiseFormats.add( f );
				}
			}
		}
	}

	public String getPremiseOntology(SerializationFormat format) {
		return premiseOntologyLiteral.get( format );
	}

	public OWLOntology parsePremiseOntology(SerializationFormat format)
			throws OWLOntologyCreationException {
		OWLOntology o = premiseOntology.get( format );
		if( o == null ) {
			String l = premiseOntologyLiteral.get( format );
			if( l == null )
				return null;

			StringInputSource source = new StringInputSource( l );
			o = getOWLOntologyManager().loadOntology( source );
			premiseOntology.put( format, o );
		}
		return o;
	}

	public Set<SerializationFormat> getPremiseFormats() {
		return Collections.unmodifiableSet( premiseFormats );
	}
}
