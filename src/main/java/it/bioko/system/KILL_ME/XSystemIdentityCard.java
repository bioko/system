package it.bioko.system.KILL_ME;

import it.bioko.system.ConfigurationEnum;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class XSystemIdentityCard {

	private String _xSystemName;
	private String _xSystemVersion;
	private ConfigurationEnum _xSystemConfiguration;

	public XSystemIdentityCard(String xSystemName, String xSystemVersion,
			ConfigurationEnum xSystemConfiguration) {
		_xSystemName = xSystemName;
		_xSystemVersion = xSystemVersion;
		_xSystemConfiguration = xSystemConfiguration;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(obj, this, false);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, false);
	}

	public String report() {
		StringBuilder result = new StringBuilder(this.getClass().getSimpleName());
		result.append("\n");
		result.append("System: " + _xSystemName);
		result.append("\n");
		result.append("Version: " + _xSystemVersion);
		result.append("\n");
		result.append("Configuration: " + _xSystemConfiguration.name());
		result.append("\n");
		return result.toString();
	}
	
	public String getSystemName() {
		return _xSystemName;
	}
	
	public String getSystemVersion() {
		return _xSystemVersion;
	}
	
	public ConfigurationEnum getSystemConfiguration() {
		return _xSystemConfiguration;
	}

}