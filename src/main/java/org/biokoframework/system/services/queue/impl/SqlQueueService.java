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

package org.biokoframework.system.services.queue.impl;

import com.google.inject.Inject;
import org.apache.log4j.Logger;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.system.services.queue.IQueueService;
import org.biokoframework.system.services.queue.QueuedItem;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;
import org.biokoframework.utils.repository.query.Query;

import java.util.List;


// TODO all the queue can be replaced by two queries:
// POP = SELECT * FROM _baseRepository WHERE IDX = lowest()
// PUSH = SELECT * FROM _baseRepository WHERE IDX = greatest()

public class SqlQueueService implements IQueueService {

	private static final Long START = 0L;

	private static final Logger LOGGER = Logger.getLogger(SqlQueueService.class);

    private final IEntityBuilderService fEntityBuilderService;
    private final Repository<QueuedItem> fBaseRepository;

	protected final Object popLock = new Object();

	@Inject
	public SqlQueueService(IRepositoryService repoService, IEntityBuilderService entityBuilderService) {
        fEntityBuilderService = entityBuilderService;
		fBaseRepository = repoService.getRepository(QueuedItem.class);
	}
	
	@Override
	public void push(String queueName, String content) throws ValidationException, RepositoryException {
		QueuedItem item = fEntityBuilderService.getInstance(QueuedItem.class);
		item.set(QueuedItem.CONTENT, content);
        item.set(QueuedItem.QUEUE_NAME, queueName);
        fBaseRepository.save(item);
	}

	@Override
	public String pop(String queueName) {
		synchronized (popLock) {
            Query<QueuedItem> q = fBaseRepository.createQuery().
                    select().
                    from(fBaseRepository, QueuedItem.class).
                    where(QueuedItem.QUEUE_NAME).isEqual(queueName).
                    orderBy(QueuedItem.ID);

            List<QueuedItem> items = q.getAll();

            if (!items.isEmpty()) {
                QueuedItem target = items.get(0);
                fBaseRepository.delete(target.getId());
                return target.get(QueuedItem.CONTENT);
            }
		}
		return null;
	}
	
	
	@Override
	public Fields popFields(String queueName) {
		String popped = pop(queueName);
		if (popped == null) {
			return null;
		}
		
		return Fields.fromJson(popped);
	}
	
	public void pushFields(String queueName, Fields fields) {
		try {
			push(queueName, fields.toJSONString());
		} catch (Exception exception) {
			LOGGER.error("Error pushing Fields", exception);
		}
	}
	
}
