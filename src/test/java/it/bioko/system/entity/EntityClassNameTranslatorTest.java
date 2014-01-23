package it.bioko.system.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EntityClassNameTranslatorTest {
	
	@Test
	public void toHyphenedTest() {
		assertEquals("domain-entity1", EntityClassNameTranslator.toHyphened("DomainEntity1"));
	}
	
	@Test
	public void toHyphenedTest2() {
		assertEquals("domain-entity1", EntityClassNameTranslator.toHyphened("domainEntity1"));
	}
	
	@Test
	public void fromHyphenedTest() {
		assertEquals("DomainEntity1", EntityClassNameTranslator.fromHyphened("domain-entity1"));
	}
	
	@Test
	public void toFieldNameTest() {
		assertEquals("domainEntity1", EntityClassNameTranslator.toFieldName("DomainEntity1"));
	}
	
	@Test
	public void fromFieldNameTest() {
		assertEquals("DomainEntity1", EntityClassNameTranslator.fromFieldName("domainEntity1"));
	}
}
