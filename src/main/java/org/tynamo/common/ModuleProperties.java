package org.tynamo.common;

import java.io.IOException;
import java.util.Properties;

public class ModuleProperties {
	public static final String PROPERTYFILE = "module.properties";
	public static final String VERSION = "module.version";

	public static String getVersion(Class<?> aClass) {
		String expectedPropertyPath = aClass.getPackage().getName() + "/" + PROPERTYFILE;
		Properties moduleProperties = new Properties();
		String version = null;
		try {
			moduleProperties.load(aClass.getResourceAsStream("module.properties"));
			version = moduleProperties.getProperty("module.version");
			if (version == null) throw new IllegalArgumentException(VERSION + " was not found from " + expectedPropertyPath);
			if (version.startsWith("${")) throw new IllegalArgumentException(VERSION + " is not filtered in resource " + expectedPropertyPath);
		} catch (IOException e) {
			throw new IllegalArgumentException("No property file resource found from " + expectedPropertyPath);
		}
		if (version.endsWith("SNAPSHOT")) version += '-' + System.currentTimeMillis();
		return version;
	}

}
