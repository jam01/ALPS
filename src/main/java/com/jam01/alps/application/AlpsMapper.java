package com.jam01.alps.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jam01.alps.application.representation.AlpsRepresentation;
import com.jam01.alps.domain.Alps;

import java.io.IOException;

/**
 * Created by jam01 on 4/7/17.
 */
public class AlpsMapper {
	private final ObjectMapper jsonMapper;
	private final XmlMapper xmlMapper;

	public AlpsMapper() {
		jsonMapper = new ObjectMapper();
		jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		jsonMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
		jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		/*
		  Currently there's no way to selectively wrap json objects in a root element.
		  See: https://github.com/FasterXML/jackson-annotations/issues/33
		  This works around that.
		 */
		jsonMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
		jsonMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

		xmlMapper = new XmlMapper();
		xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		xmlMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
		xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	public ObjectMapper getJsonMapper() {
		return jsonMapper;
	}

	public XmlMapper getXmlMapper() {
		return xmlMapper;
	}

	public String writeValueAsString(Alps alps, DataType type) {
		AlpsRepresentation representation = AlpsRepresentation.mapFrom(alps);
		try {
			return type == DataType.JSON ? jsonMapper.writeValueAsString(representation)
					: xmlMapper.writeValueAsString(representation);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public Alps readValue(String alps, DataType type) {
		try {
			switch (type) {
				case JSON:
					return AlpsRepresentation.mapFrom(jsonMapper.readValue(alps, AlpsRepresentation.class));
				case XML:
					return AlpsRepresentation.mapFrom(xmlMapper.readValue(alps, AlpsRepresentation.class));
				default:
					return null;
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public enum DataType {
		JSON, XML
	}
}
