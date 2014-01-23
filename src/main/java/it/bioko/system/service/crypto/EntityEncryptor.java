package it.bioko.system.service.crypto;

import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.hint.HintFactory;
import it.bioko.utils.fields.Fields;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class EntityEncryptor {

	// BCrypt + SALT is used to encrypt password
	
	// Base64 is used for TWO way encryption
	// When everyone will be using Java7 it will be possible to use AES encryption (commented out code)

	private static final String CHARSET = "utf8";
	public static final String HINT = "encrypt";
	public static final String ONE_WAY_HINT = "oneWay";
	public static final String TWO_WAY_HINT = "twoWay";
	
	public static final String SALT_FIELD_HINT = "saltField";
	
	@SuppressWarnings("unchecked")
	public <DE extends DomainEntity> DE encryptEntity(DE plainEntity) {
		DE encryptedEntity = null;
		try {
			encryptedEntity = (DE) plainEntity.getClass().getConstructor(Fields.class).newInstance(Fields.empty());
			
			Map<String, Map<String, String>> hints = HintFactory.createMap(plainEntity.getClass());
			for (String aFieldName : plainEntity.fields().keys()) {
				Map<String, String> fieldHints = hints.get(aFieldName);
				if (fieldHints != null && fieldHints.containsKey(HINT)) {
					encryptedEntity.set(aFieldName, encryptField(plainEntity.get(aFieldName), fieldHints.get(HINT)));
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
	
	public <DE extends DomainEntity> boolean matchEncrypted(DE plainEntity, DE encryptedEntity, String password) {
		try {
			Map<String, Map<String, String>> hints = HintFactory.createMap(plainEntity.getClass());
			Set<String> allFieldNames = new HashSet<String>(plainEntity.fields().keys());
			allFieldNames.addAll(encryptedEntity.fields().keys());
			
			for (String aFieldName : allFieldNames) {
				Map<String, String> fieldHints = hints.get(aFieldName);
				if (fieldHints != null && fieldHints.containsKey(HINT)) {
					if (!checkField(plainEntity.get(aFieldName), encryptedEntity.get(aFieldName), fieldHints.get(HINT))) {
						return false;
					}
				} else {
					if (!StringUtils.equals(plainEntity.get(aFieldName), encryptedEntity.get(aFieldName))) {
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
		
	@SuppressWarnings("unchecked")
	public <DE extends DomainEntity> DE decryptEntity(DE encryptedEntity) {
		DE decryptedEntity = null;
		try {
			decryptedEntity = (DE) encryptedEntity.getClass().getConstructor(Fields.class).newInstance(Fields.empty());
			
			Map<String, Map<String, String>> hints = HintFactory.createMap(encryptedEntity.getClass());
			for (String aFieldName : encryptedEntity.fields().keys()) {
				Map<String, String> fieldHints = hints.get(aFieldName);
				if (fieldHints != null && StringUtils.equals(fieldHints.get(HINT), TWO_WAY_HINT)) {
					decryptedEntity.set(aFieldName, decryptField(encryptedEntity.get(aFieldName)));
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
