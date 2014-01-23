package it.bioko.system.service.queue;

import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.command.ValidationException;
import it.bioko.system.repository.core.Repository;
import it.bioko.system.repository.core.RepositoryException;
import it.bioko.utils.fields.Fields;

import java.util.List;


// TODO all the queue can be replaced by two queries:
// POP = SELECT * FROM _baseRepository WHERE IDX = lowest()
// PUSH = SELECT * FROM _baseRepository WHERE IDX = greatest()

public class Queue {

	private static final Long START = 0L;

	private Repository<QueuedItem> _baseRepository;
	
	protected Long head = Long.MAX_VALUE;
	protected Long tail = START;
	
	protected final Object popLock = new Object();
	protected final Object pushLock = new Object();

	public Queue(Repository<QueuedItem> baseRepository) {
		_baseRepository = baseRepository;
		List<QueuedItem> items = _baseRepository.getAll();
		
		for (QueuedItem anItem : items) {
			if (Long.parseLong(anItem.get(QueuedItem.IDX)) == tail) {
				tail = Long.parseLong(anItem.get(QueuedItem.IDX)) + 1;
			}
			
			if (head > Long.parseLong(anItem.get(QueuedItem.IDX))) {
				head = Long.parseLong(anItem.get(QueuedItem.IDX));
			}
		}
		
		if (tail == START) {
			head = tail;
		}
	}
	
	public void push(String content) throws ValidationException, RepositoryException {
		QueuedItem item = new QueuedItem(Fields.empty());
		item.set(QueuedItem.CONTENT, content);
		synchronized (pushLock) {
			synchronized (tail) {
				item.set(QueuedItem.IDX, Long.toString(tail));
				_baseRepository.save(item);
				tail++;
			}
		}
	}

	public String pop() {
		synchronized (popLock) {
			synchronized (head) {
				 QueuedItem item = _baseRepository.retrieveByForeignKey(QueuedItem.IDX, Long.toString(head));
				 if (item != null) {
					 String content = item.get(QueuedItem.CONTENT);
					 _baseRepository.delete(item.getId());
					 head++;
					 return content;
				 }
			}
		}
		return null;
	}
	
	
	public Fields popFields() {
		String popped = pop();
		if (popped == null) {
			return null;
		}
		
		return Fields.empty().fromJson(popped);
	}
	
	public void pushFields(Fields fields) {
		try {
			push(fields.asJson());
		} catch (Exception exception) {
			Loggers.engagedServer.error("Error pushing Fields", exception);
		}
	}
	
}
