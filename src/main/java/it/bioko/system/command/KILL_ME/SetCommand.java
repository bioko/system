package it.bioko.system.command.KILL_ME;

import it.bioko.system.command.Command;
import it.bioko.utils.fields.Fields;

import java.util.LinkedHashMap;


public abstract class SetCommand extends Command {

	private LinkedHashMap<String, Fields> _inputKeysMap;
	private LinkedHashMap<String, Fields> _outputKeysMap;

	public SetCommand(LinkedHashMap<String, Fields> inputKeysMap,
			LinkedHashMap<String, Fields> outputKeysMap) {
//		if (!_inputKeysMap.keySet().equals(_outputKeysMap.keySet())) {
//			throw new UnsupportedOperationException("The two sets must have the same keys");
//		}
		_inputKeysMap = inputKeysMap;
		_outputKeysMap = outputKeysMap;
	}
	
	public Fields componingInputKeys(String aCommandName) {
		if (_inputKeysMap.containsKey(aCommandName)) {
			return _inputKeysMap.get(aCommandName); 
		}
		return Fields.empty();
	}

	public Fields componingOutputKeys(String aCommandName) {
		if (_outputKeysMap.containsKey(aCommandName)) {
			return _outputKeysMap.get(aCommandName);
		}
		return Fields.empty();
	}
	
}
