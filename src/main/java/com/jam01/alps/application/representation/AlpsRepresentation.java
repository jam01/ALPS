package com.jam01.alps.application.representation;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.jam01.alps.domain.Alps;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by jam01 on 4/5/17.
 */

@JsonRootName(value = "alps")
public class AlpsRepresentation {
    @JacksonXmlProperty(isAttribute = true)
    public String version;
    public DocRepresentation doc;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<DescriptorRepresentation> descriptor;
//    public LinkRepresentation link;

    public static Alps mapFrom(AlpsRepresentation alpsRepresentation) {

        return new Alps(alpsRepresentation.version,
                DocRepresentation.mapFrom(alpsRepresentation.doc),
                alpsRepresentation.descriptor
                        .stream()
                        .map(DescriptorMapper::generateMatrix)
                        .collect(Collectors.toList()));
    }

    public static AlpsRepresentation mapFrom(Alps alps) {
        AlpsRepresentation toReturn = new AlpsRepresentation();
        toReturn.version = alps.getVersion();
        toReturn.doc = DocRepresentation.mapFrom(alps.getDoc());
        toReturn.descriptor = DescriptorMapper.mappedFrom(alps.getRoots());
//		toReturn.link = LinkRepresentation.mapFrom(alps.getLink());

        return toReturn;
    }
}