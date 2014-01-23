package it.bioko.system.command;

import it.bioko.utils.fields.Fields;


public class CommandExecution {
	private Fields _executionOutput;
	private boolean _executionResult;

	public CommandExecution setSuccessful(Fields executionOutputFields){
		_executionOutput = executionOutputFields;
		_executionResult = true;
		return this;
	}

	public boolean successful(){
		return _executionResult;
	}
	
	public Fields executionOutput() {
		return _executionOutput;
	}
}