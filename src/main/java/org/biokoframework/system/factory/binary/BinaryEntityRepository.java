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

package org.biokoframework.system.factory.binary;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.biokoframework.system.entity.binary.BinaryEntity;
import org.biokoframework.system.repository.core.AbstractRepository;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;
import org.biokoframework.utils.repository.query.Query;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class BinaryEntityRepository extends AbstractRepository<BinaryEntity> {

	private static final Logger LOGGER = Logger.getLogger(BinaryEntityRepository.class);

	private static final String DATE_PATH_PATTERN = new StringBuilder()
			.append("yyyy")
			.append(File.separator)
			.append("MM")
			.append(File.separator)
			.append("dd")
			.append(File.separator).toString();


	private File fBaseDirectory;
	private Repository<BinaryEntity> fSupportRepository;

	@Inject
	public BinaryEntityRepository(@Named("fileBaseDirectory") File baseDirectory, Repository<BinaryEntity> supportRepository, IEntityBuilderService entityBuilderService) {
		super(entityBuilderService);
		fBaseDirectory = baseDirectory;
		fSupportRepository = supportRepository;
	}

	@Override
	public BinaryEntity save(DomainEntity anEntity) throws ValidationException, RepositoryException {
		BinaryEntity aBlob = (BinaryEntity) anEntity;
		try {
			if (aBlob.getId() != null) {
				BinaryEntity existingBlob = fSupportRepository.retrieve(aBlob.getId());
				aBlob.setId(existingBlob.getId());
				aBlob.set(BinaryEntity.PATH, existingBlob.get(BinaryEntity.PATH));
				updateFile(aBlob);
			} else {
				createFile(aBlob);
			}
			fSupportRepository.save(aBlob);
		} catch (IOException exception) {
			fSupportRepository.delete(aBlob.getId());
			LOGGER.error("Save blob file", exception);
			return null;
		}
		BinaryEntity filterBlob = new BinaryEntity();
		filterBlob.setAll(aBlob.fields());
		filterBlob.fields().remove(BinaryEntity.PATH);
		return filterBlob;
	}

	@Override
	public BinaryEntity delete(String aBlobId) {
		BinaryEntity aBlob = fSupportRepository.delete(aBlobId);
		if (aBlob != null) {
			try {
				File file = new File(aBlob.get(BinaryEntity.PATH).toString());
				aBlob.setStream(new FileInputStream(file));
				file.delete();
				aBlob.fields().remove(BinaryEntity.PATH);
			} catch (IOException exception) {
				LOGGER.error("Delete blob file", exception);
				return null;
			}
		}
		return aBlob;
	}

	@Override
	public BinaryEntity retrieve(String aBlobId) {
		BinaryEntity aBlob = fSupportRepository.retrieve(aBlobId);
		BinaryEntity returnBlob = null;
		if (aBlob != null) {
			returnBlob = new BinaryEntity();
			returnBlob.setAll(aBlob.fields().copy());
			try {
				returnBlob.setStream(new FileInputStream(aBlob.get(BinaryEntity.PATH).toString()));
			} catch (IOException exception) {
				LOGGER.error("Save blob file", exception);
				return null;
			}
		}
		return returnBlob;
	}

	public BinaryEntity retrieveWithoutFile(String aBlobId) {
		BinaryEntity aBlob = fSupportRepository.retrieve(aBlobId);
		BinaryEntity returnBlob = null;
		if (aBlob != null) {
			returnBlob = new BinaryEntity();
			returnBlob.setAll(aBlob.fields().copy());
			returnBlob.fields().remove(BinaryEntity.PATH);
		}
		return returnBlob;
	}

	@Override
	public BinaryEntity retrieve(DomainEntity anEntity) {
		BinaryEntity aBlob = (BinaryEntity) anEntity;
		return retrieve(aBlob.getId());
	}

	@Override
	public ArrayList<BinaryEntity> getAll() {
		ArrayList<BinaryEntity> blobs = new ArrayList<BinaryEntity>();
		for (BinaryEntity aBlob : fSupportRepository.getAll()) {
			BinaryEntity cleanBlob = new BinaryEntity();
			cleanBlob.setAll(aBlob.fields());
			cleanBlob.fields().remove(BinaryEntity.PATH);
			blobs.add(cleanBlob);
		}
		return blobs;
	}

	@Override
	public BinaryEntity retrieveByForeignKey(String foreignKeyName, Object foreignKeyValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ArrayList<BinaryEntity> getEntitiesByForeignKey(String foreignKeyName, Object foreignKeyValue) {
		throw new UnsupportedOperationException();
	}

	private void createFile(BinaryEntity blob) throws IOException {
		File directory = new File(fBaseDirectory, timestampPath());
		FileUtils.forceMkdir(directory);
		File file = new File(directory, UUID.randomUUID().toString());
		blob.set(BinaryEntity.PATH, file.getAbsolutePath());

		FileOutputStream fileOutputStream = new FileOutputStream(file);
		IOUtils.copy(blob.getStream(), fileOutputStream);
		blob.getStream().close();
		fileOutputStream.close();
		blob.set(BinaryEntity.SIZE_BYTES, Long.toString(file.length()));
	}

	private void updateFile(BinaryEntity blob) throws IOException {
		File file = new File(blob.get(BinaryEntity.PATH).toString());
		FileOutputStream fileOutputStream = new FileOutputStream(file);

		IOUtils.copy(blob.getStream(), fileOutputStream);
		blob.getStream().close();
		fileOutputStream.close();
		blob.set(BinaryEntity.SIZE_BYTES, Long.toString(file.length()));
	}

	private String timestampPath() {
		return new DateTime().toString(DATE_PATH_PATTERN);
	}

	
	@Override
	public Query<BinaryEntity> createQuery() {
		throw new UnsupportedOperationException("BinaryEntityRepositories don't like queries");
	}
	
}
