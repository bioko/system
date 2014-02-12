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

import java.util.List;

import org.biokoframework.system.KILL_ME.commons.logger.Loggers;
import org.biokoframework.system.services.queue.IQueueService;
import org.biokoframework.system.services.queue.QueuedItem;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;

import com.google.inject.Inject;


// TODO all the queue can be replaced by two queries:
// POP = SELECT * FROM _baseRepository WHERE IDX = lowest()
// PUSH = SELECT * FROM _baseRepository WHERE IDX = greatest()

public class QueueService implements IQueueService {

	private static final Long START = 0L;

	private Repository<QueuedItem> fBaseRepository;
	
	protected Long head = Long.MAX_VALUE;
	protected Long tail = START;
	
	protected final Object popLock = new Object();
	protected final Object pushLock = new Object();

	@Inject
	public QueueService(Repository<QueuedItem> baseRepository) {
		fBaseRepository = baseRepository;
		List<QueuedItem> items = fBaseRepository.getAll();
		
		for (QueuedItem anItem : items) {
			if (Long.parseLong(anItem.get(QueuedItem.IDX).toString()) == tail) {
				tail = Long.parseLong(anItem.get(QueuedItem.IDX).toString()) + 1;
			}
			
			if (head > Long.parseLong(anItem.get(QueuedItem.IDX).toString())) {
				head = Long.parseLong(anItem.get(QueuedItem.IDX).toString());
			}
		}
		
		if (tail == START) {
			head = tail;
		}
	}
	
	@Override
	public void push(String content) throws ValidationException, RepositoryException {
		QueuedItem item = new QueuedItem(new Fields());
		item.set(QueuedItem.CONTENT, content);
		synchronized (pushLock) {
			synchronized (tail) {
				item.set(QueuedItem.IDX, Long.toString(tail));
				fBaseRepository.save(item);
				tail++;
			}
		}
	}

	@Override
	public String pop() {
		synchronized (popLock) {
			synchronized (head) {
				 QueuedItem item = fBaseRepository.retrieveByForeignKey(QueuedItem.IDX, Long.toString(head));
				 if (item != null) {
					 String content = item.get(QueuedItem.CONTENT);
					 fBaseRepository.delete(item.getId());
					 head++;
					 return content;
				 }
			}
		}
		return null;
	}
	
	
	@Override
	public Fields popFields() {
		String popped = pop();
		if (popped == null) {
			return null;
		}
		
		return Fields.fromJson(popped);
	}
	
	public void pushFields(Fields fields) {
		try {
			push(fields.toJSONString());
		} catch (Exception exception) {
			Loggers.engagedServer.error("Error pushing Fields", exception);
		}
	}
	
}
