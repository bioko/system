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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.biokoframework.system.KILL_ME.commons.logger.Loggers;
import org.biokoframework.system.command.ValidationException;
import org.biokoframework.system.entity.binary.BinaryEntity;
import org.biokoframework.system.repository.core.Repository;
import org.biokoframework.system.repository.core.RepositoryException;
import org.biokoframework.system.repository.core.query.Query;
import org.joda.time.DateTime;

public class BinaryEntityRepository extends Repository<BinaryEntity> {

	private static final String DATE_PATH_PATTERN = new StringBuilder()
			.append("yyyy")
			.append(File.separator)
			.append("MM")
			.append(File.separator)
			.append("dd")
			.append(File.separator).toString();

	private File _baseDirectory;
	private Repository<BinaryEntity> _supportRepository;

	public BinaryEntityRepository(File baseDirectory, Repository<BinaryEntity> supportRepository) {
		_baseDirectory = baseDirectory;
		_supportRepository = supportRepository;
	}

	@Override
	public BinaryEntity save(BinaryEntity aBlob) throws ValidationException, RepositoryException {
		try {
			if (aBlob.getId() != null) {
				BinaryEntity existingBlob = _supportRepository.retrieve(aBlob.getId());
				aBlob.setId(existingBlob.getId());
				aBlob.set(BinaryEntity.PATH, existingBlob.get(BinaryEntity.PATH));
				updateFile(aBlob);
			} else {
				createFile(aBlob);
			}
			_supportRepository.save(aBlob);
		} catch (IOException exception) {
			_supportRepository.delete(aBlob.getId());
			Loggers.engagedServer.error("Save blob file", exception);
			return null;
		}
		BinaryEntity filterBlob = new BinaryEntity(aBlob.fields());
		filterBlob.fields().remove(BinaryEntity.PATH);
		return filterBlob;
	}

	@Override
	public BinaryEntity delete(String aBlobId) {
		BinaryEntity aBlob = _supportRepository.delete(aBlobId);
		if (aBlob != null) {
			try {
				File file = new File(aBlob.get(BinaryEntity.PATH));
				aBlob.setStream(new FileInputStream(file));
				file.delete();
				aBlob.fields().remove(BinaryEntity.PATH);
			} catch (IOException exception) {
				Loggers.engagedServer.error("Delete blob file", exception);
				return null;
			}
		}
		return aBlob;
	}

	@Override
	public BinaryEntity retrieve(String aBlobId) {
		BinaryEntity aBlob = _supportRepository.retrieve(aBlobId);
		BinaryEntity returnBlob = null;
		if (aBlob != null) {
			returnBlob = new BinaryEntity(aBlob.fields().copy());
			try {
				returnBlob.setStream(new FileInputStream(aBlob.get(BinaryEntity.PATH)));
			} catch (IOException exception) {
				Loggers.engagedServer.error("Save blob file", exception);
				return null;
			}
		}
		return returnBlob;
	}

	public BinaryEntity retrieveWithoutFile(String aBlobId) {
		BinaryEntity aBlob = _supportRepository.retrieve(aBlobId);
		BinaryEntity returnBlob = null;
		if (aBlob != null) {
			returnBlob = new BinaryEntity(aBlob.fields().copy());
			returnBlob.fields().remove(BinaryEntity.PATH);
		}
		return returnBlob;
	}

	@Override
	public BinaryEntity retrieve(BinaryEntity aBlob) {
		return retrieve(aBlob.getId());
	}

	@Override
	public ArrayList<BinaryEntity> getAll() {
		ArrayList<BinaryEntity> blobs = new ArrayList<BinaryEntity>();
		for (BinaryEntity aBlob : _supportRepository.getAll()) {
			BinaryEntity cleanBlob = new BinaryEntity(aBlob.fields());
			cleanBlob.fields().remove(BinaryEntity.PATH);
			blobs.add(cleanBlob);
		}
		return blobs;
	}

	@Override
	public BinaryEntity retrieveByForeignKey(String foreignKeyName, String foreignKeyValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ArrayList<BinaryEntity> getEntitiesByForeignKey(String foreignKeyName, String foreignKeyValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String report() {
		StringBuffer result = new StringBuffer("");
		for (BinaryEntity each : getAll()) {
			result.append(each.report());
		}
		return result.toString();
	}

	private void createFile(BinaryEntity blob) throws IOException {
		File directory = new File(_baseDirectory, timestampPath());
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
		File file = new File(blob.get(BinaryEntity.PATH));
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
