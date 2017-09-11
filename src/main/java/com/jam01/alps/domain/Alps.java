package com.jam01.alps.domain;

import com.jam01.alps.domain.exception.AlpsValidationException;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jam01 on 4/4/17.
 * <p>
 * Created a domain model for application/alps per
 * Vernon, V., & Evans, E. (2015). Implementing domain-driven design, 137-138
 */

public class Alps {
	private String version;
	private Doc doc;
	private List<Descriptor> roots = new ArrayList<>();
	private Map<URI, Descriptor> descriptorMap = new HashMap<>();
//	private Link link;

	private Alps() {
	}

	public Alps(String version, Doc doc, List<DescriptorMatrix> graphs) {
		this.version = version;
		this.doc = doc;

		if (graphs != null) {
			// All the relations
			Set<Map.Entry<URI, List<URI>>> childRelations = new HashSet<>();
			Set<Map.Entry<URI, URI>> superDescRelations = new HashSet<>();
			Set<Map.Entry<URI, URI>> returnRelations = new HashSet<>();
			Map<URI, Descriptor> externalDescriptors = new HashMap<>();

			for (DescriptorMatrix graph : graphs) {
				descriptorMap.putAll(graph.getDescriptorMap());
				childRelations.addAll(graph.getChildRelations());
				superDescRelations.addAll(graph.getSuperRelations());
				returnRelations.addAll(graph.getRtRelations());
			}

			for (Map.Entry<URI, URI> superRel : superDescRelations) {
				Descriptor superDescriptor;

				if (!superRel.getValue().isAbsolute()) {
					superDescriptor = descriptorMap.get(superRel.getValue());
					if (superDescriptor == null)
						throw new AlpsValidationException(superRel.getValue().toString() + " does refer to a local 'descriptor' within the document per section 2.2.7.2.");
				} else {
					superDescriptor = externalDescriptors.get(superRel.getValue());
					if (superDescriptor == null) {
						superDescriptor = new Descriptor(null, null, null, null, superRel.getValue());
						externalDescriptors.put(superDescriptor.get_Id(), superDescriptor);
					}
				}
				descriptorMap.get(superRel.getKey()).setSuperDescriptor(superDescriptor);
			}

			for (Map.Entry<URI, URI> returnRel : returnRelations) {
				Descriptor descriptorToReturn;

				if (!returnRel.getValue().isAbsolute()) {
					descriptorToReturn = descriptorMap.get(returnRel.getValue());
					if (descriptorToReturn == null)
						throw new AlpsValidationException(returnRel.getValue().toString() + " does refer to a local 'descriptor' within the document per section 2.2.7.2.");
				} else {
					descriptorToReturn = externalDescriptors.get(returnRel.getValue());
					if (descriptorToReturn == null) {
						descriptorToReturn = new Descriptor(null, null, null, null, returnRel.getValue());
						externalDescriptors.put(descriptorToReturn.get_Id(), descriptorToReturn);
					}
				}

				descriptorMap.get(returnRel.getKey()).setRt(descriptorToReturn);
			}

			for (Map.Entry<URI, List<URI>> childRel : childRelations) {
				for (URI targetDesc : childRel.getValue()) {
					descriptorMap.get(childRel.getKey()).addDescriptor(descriptorMap.get(targetDesc));
				}
			}

			// Add all roots
			roots.addAll(graphs.stream()
					.map(DescriptorMatrix::getLastRoot)
					.map(rootURI -> descriptorMap.get(rootURI))
					.collect(Collectors.toList()));
		}

	}

//	public Link getLink() {
//		return link;
//	}

	public String getVersion() {
		return version;
	}

	public Doc getDoc() {
		return doc;
	}

	public List<Descriptor> getRoots() {
		return roots;
	}

	public void setRoots(List<Descriptor> roots) {
		this.roots = roots;
	}

}
