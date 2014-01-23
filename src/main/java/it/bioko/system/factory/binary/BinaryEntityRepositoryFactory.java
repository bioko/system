package it.bioko.system.factory.binary;

import it.bioko.system.entity.binary.BinaryEntity;
import it.bioko.system.repository.core.Repository;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class BinaryEntityRepositoryFactory {

	public static BinaryEntityRepository createForRoot(File rootDirectory, String systemName, Repository<BinaryEntity> supportRepository) {
		File systemDirectory = new File(rootDirectory, systemName);
		return new BinaryEntityRepository(systemDirectory, supportRepository);
	}

	public static BinaryEntityRepository createForTemp(String systemName, Repository<BinaryEntity> supportRepository) {
		return createForRoot(FileUtils.getTempDirectory(), systemName, supportRepository);
	}
	
}
