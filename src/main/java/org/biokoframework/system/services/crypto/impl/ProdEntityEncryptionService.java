/*
 * Copyright (c) 2014																 
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package org.biokoframework.system.services.crypto.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.biokoframework.system.services.crypto.IEncryptionService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.annotation.hint.HintFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProdEntityEncryptionService implements IEncryptionService {

	// BCrypt + SALT is used to encrypt password
	
	// Base64 is used for TWO way encryption
	// When everyone will be using Java7 it will be possible to use AES encryption (commented out code)

	private static final String CHARSET = "utf8";
	public static final String HINT = "encrypt";
	public static final String ONE_WAY_HINT = "oneWay";
	public static final String TWO_WAY_HINT = "twoWay";
	
	public static final String SALT_FIELD_HINT = "saltField";
	
	@Override
	@SuppressWarnings("unchecked")
	public <DE extends DomainEntity> DE encryptEntity(DE plainEntity) {
		DE encryptedEntity = null;
		try {
			encryptedEntity = (DE) plainEntity.getClass().newInstance();

			Map<String, Map<String, String>> hints = HintFactory.createMap(plainEntity.getClass());
			for (String aFieldName : plainEntity.fields().keys()) {
				Map<String, String> fieldHints = hints.get(aFieldName);
				if (fieldHints != null && fieldHints.containsKey(HINT)) {
					encryptedEntity.set(aFieldName, encryptField(plainEntity.get(aFieldName).toString(), fieldHints.get(HINT)));
				} else {
					encryptedEntity.set(aFieldName, plainEntity.get(aFieldName));
				}
			}
			
		} catch (Exception exception) {
			System.err.println("[easy-men] cannot create the entity");
			exception.printStackTrace();
		}
		return encryptedEntity;
	}
	
	@Override
	public <DE extends DomainEntity> boolean matchEncrypted(DE plainEntity, DE encryptedEntity, String encryptionKey) {
		try {
			Map<String, Map<String, String>> hints = HintFactory.createMap(plainEntity.getClass());
			Set<String> allFieldNames = new HashSet<String>(plainEntity.fields().keys());
			allFieldNames.addAll(encryptedEntity.fields().keys());
			
			for (String aFieldName : allFieldNames) {
				Map<String, String> fieldHints = hints.get(aFieldName);
				if (fieldHints != null && fieldHints.containsKey(HINT)) {
					if (!checkField(plainEntity.get(aFieldName).toString(), encryptedEntity.get(aFieldName).toString(), fieldHints.get(HINT))) {
						return false;
					}
				} else {
					if (!StringUtils.equals(plainEntity.get(aFieldName).toString(), encryptedEntity.get(aFieldName).toString())) {
						return false;
					}
				}
			}
			return true;
			
		} catch (Exception exception) {
			System.err.println("[easy-men] cannot create the entity");
			exception.printStackTrace();
			return false;
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <DE extends DomainEntity> DE decryptEntity(DE encryptedEntity) {
		DE decryptedEntity = null;
		try {
			decryptedEntity = (DE) encryptedEntity.getClass().newInstance();
			
			Map<String, Map<String, String>> hints = HintFactory.createMap(encryptedEntity.getClass());
			for (String aFieldName : encryptedEntity.fields().keys()) {
				Map<String, String> fieldHints = hints.get(aFieldName);
				if (fieldHints != null && StringUtils.equals(fieldHints.get(HINT), TWO_WAY_HINT)) {
					decryptedEntity.set(aFieldName, decryptField(encryptedEntity.get(aFieldName).toString()));
				} else {
					decryptedEntity.set(aFieldName, encryptedEntity.get(aFieldName));
				}
			}
			
		} catch (Exception exception) {
			System.err.println("[easy-men] cannot create the entity");
			exception.printStackTrace();
		}
		return decryptedEntity;
	}
	
	private String decryptField(String encryptedValue) {
		try {
			return new String(Base64.decodeBase64(encryptedValue), CHARSET);
		} catch (UnsupportedEncodingException exception) {
			System.out.println("[easy-men] problem with the encoding " + CHARSET);
			return null;
		}
//		//	AES encryption, requires Java7
//		String[] splitted = encryptedValue.split(":");
//		TextEncryptor textEncryptor = Encryptors.queryableText(_password, splitted[1]);
//		return textEncryptor.decrypt(splitted[0]);
	}

	private String encryptField(String plainValue, String encryptionType) {
		if (ONE_WAY_HINT.equals(encryptionType)) {
			String salt = BCrypt.gensalt();
			String encryptedValue = BCrypt.hashpw(plainValue, salt);
			return encryptedValue;
		} else if (TWO_WAY_HINT.equals(encryptionType)) {
			try {
				return Base64.encodeBase64String(plainValue.getBytes(CHARSET));
			} catch (UnsupportedEncodingException exception) {
				System.out.println("[easy-men] problem with the encoding " + CHARSET);
				return null;
			}
//			//	AES encryption, requires Java7
//			String salt = KeyGenerators.string().generateKey();
//			TextEncryptor textEncryptor = Encryptors.queryableText(_password, salt);
//			return new StringBuilder(textEncryptor.encrypt(plainValue)).append(":").append(salt).toString();
		}
		return null;
	}
	
	private boolean checkField(String plainValue, String encryptedValue, String encryptionType) {
		if (ONE_WAY_HINT.equals(encryptionType)) {
			return BCrypt.checkpw(plainValue, encryptedValue);
		} else if (TWO_WAY_HINT.equals(encryptionType)) {
			return encryptedValue.equals(encryptField(plainValue, encryptionType));
//			//	AES encryption, requires Java7
//			String[] splitted = encryptedValue.split(":");
//			TextEncryptor textEncryptor = Encryptors.queryableText(_password, splitted[1]);
//			return textEncryptor.encrypt(plainValue).equals(splitted[0]);
		}
		return false;
	}

}
