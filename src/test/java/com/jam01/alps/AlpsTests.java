package com.jam01.alps;

import com.jam01.alps.application.AlpsMapper;
import com.jam01.alps.application.AlpsMapper;
import com.jam01.alps.domain.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.jam01.alps.domain.Type.SEMANTIC;

/**
 * Created by jam01 on 5/29/17.
 */
public class AlpsTests {
	AlpsMapper mapper = new AlpsMapper();
	Alps alps;

	@Before
	public void setupProfile() {
		URI selfUrl, givenNameUrl, familyNameUrl, emailUrl, telephoneUrl;
		try {
			selfUrl = new URI("https://rawgithub.com/alps-io/profiles/master/contacts.xml");
			givenNameUrl = new URI("http://schema.org/givenName#x");
			familyNameUrl = new URI("http://schema.org/familyName#y");
			emailUrl = new URI("http://schema.org/email");
			telephoneUrl = new URI("http://schema.org/telephone");

			Descriptor contact = new Descriptor(Id.from("contact"), null, null, SEMANTIC, new URI(null, null, "contact"));

			Descriptor givenName = new Descriptor(Id.from("givenName"), null, null, SEMANTIC, new URI(null, null, "givenName"));
			givenName.setSuperDescriptor(new Descriptor(null, null, null, SEMANTIC, givenNameUrl));

			Descriptor familyName = new Descriptor(Id.from("familyName"), null, null, SEMANTIC, new URI(null, null, "familyName"));
			familyName.setSuperDescriptor(new Descriptor(null, null, null, SEMANTIC, familyNameUrl));

			contact.addDescriptor(givenName);
			contact.addDescriptor(familyName);

			Descriptor searchByGivenName = new Descriptor(Id.from("searchByGivenName"), null, null, Type.SAFE, null);
			searchByGivenName.addDescriptor(givenName);
			searchByGivenName.setRt(contact);

			Descriptor addContact = new Descriptor(Id.from("addContact"), new Doc(Format.TEXT, "TESTING"), null, Type.UNSAFE, null);
			addContact.addDescriptor(contact);
			addContact.setRt(contact);

			alps = new Alps("1.0", null, null);
			alps.setRoots(Arrays.asList(contact, addContact, searchByGivenName));


		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	@Test
	public void testJsonSerialization() throws IOException {
		String writeValueAsStringd = mapper.writeValueAsString(alps, AlpsMapper.DataType.JSON);
		System.out.println(writeValueAsStringd);
		assert writeValueAsStringd.equals(new String(Files.readAllBytes(Paths.get("src/test/resources/test-alps.json"))));
	}

	@Test
	public void testXmlSerialization() throws IOException {
		String writeValueAsStringd = mapper.writeValueAsString(alps, AlpsMapper.DataType.XML);
		System.out.println(writeValueAsStringd);
		assert writeValueAsStringd.equals(new String(Files.readAllBytes(Paths.get("src/test/resources/test-alps.xml"))));

	}

	@Test
	public void testXmlDeserialization() throws IOException {
		String toreadValue = new String(Files.readAllBytes(Paths.get("src/test/resources/apr.xml")));
		System.out.println(toreadValue);

		Alps mapped = mapper.readValue(toreadValue, AlpsMapper.DataType.XML);

		assert mapped != null;
	}

	@Test
	public void testJsonDeserialization() throws IOException {
		String toreadValue = new String(Files.readAllBytes(Paths.get("src/test/resources/test-alps.json")));
		System.out.println(toreadValue);

		mapper.readValue(toreadValue, AlpsMapper.DataType.JSON);

		assert true;
	}

	@Test
	public void testXmlToJson() throws IOException {
		String toreadValue = new String(Files.readAllBytes(Paths.get("src/test/resources/test-alps.xml")));
		System.out.println(toreadValue);

		Alps dummy = mapper.readValue(toreadValue, AlpsMapper.DataType.XML);
		String writeValueAsStringd = mapper.writeValueAsString(dummy, AlpsMapper.DataType.JSON);
		System.out.println(writeValueAsStringd);

		assert writeValueAsStringd.equals(new String(Files.readAllBytes(Paths.get("src/test/resources/test-alps.json"))));
	}

	@Test
	public void testJsonToXml() throws IOException {
		String toreadValue = new String(Files.readAllBytes(Paths.get("src/test/resources/test-alps.json")));
		System.out.println(toreadValue);

		Alps dummy = mapper.readValue(toreadValue, AlpsMapper.DataType.JSON);
		String writeValueAsStringd = mapper.writeValueAsString(dummy, AlpsMapper.DataType.XML);
		System.out.println(writeValueAsStringd);

		assert writeValueAsStringd.equals(new String(Files.readAllBytes(Paths.get("src/test/resources/test-alps.xml"))));
	}

//	@Test
//	public void testSerialization() throws IOException {
//		String writeValueAsStringd = mapper.writeValueAsStringRepresentation(AlpsMapper.mapFrom(alps), AlpsMapper.DataType.JSON);
//		System.out.println(writeValueAsStringd);
//
//		writeValueAsStringd = mapper.writeValueAsStringRepresentation(AlpsMapper.mapFrom(alps), AlpsMapper.DataType.XML);
//		System.out.println(writeValueAsStringd);
//	}
//
//	@Test
//	public void testDeserialization() throws IOException {
//		String toreadValue = new String(Files.readAllBytes(Paths.get("src/test/resources/test-alps.json")));
//		System.out.println(toreadValue);
//
//		mapper.readValueRepresentation(toreadValue, AlpsMapper.DataType.JSON);
//
//		toreadValue = new String(Files.readAllBytes(Paths.get("src/test/resources/test-alps.xml")));
//		System.out.println(toreadValue);
//
//		mapper.readValueRepresentation(toreadValue, AlpsMapper.DataType.XML);
//	}

}
