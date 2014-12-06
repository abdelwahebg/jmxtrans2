package org.jmxtrans;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.File;

import org.jmxtrans.cli.JmxTransConfiguration;
import org.jmxtrans.exceptions.LifecycleException;
import org.jmxtrans.monitoring.ManagedJmxTransformerProcessMXBean;
import org.jmxtrans.monitoring.ManagedObject;

/**
 * The Class ManagedJmxTransformerProcess.
 * TODO: Only start/stop working, the setters don't fire update on JmxProcess
 *
 * @author marcos.lois
 */
public class ManagedJmxTransformerProcess implements ManagedJmxTransformerProcessMXBean, ManagedObject {

	/**
	 * The object name.
	 */
	private ObjectName objectName;

	/**
	 * The proc.
	 */
	private JmxTransformer proc;

	private final JmxTransConfiguration configuration;

	/**
	 * The Constructor.
	 *
	 * @param proc          the proc
	 * @param configuration
	 */
	public ManagedJmxTransformerProcess(JmxTransformer proc, JmxTransConfiguration configuration) {
		this.proc = proc;
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see org.jmxtrans.monitoring.ManagedJmxTransformerProcessMXBean#start()
	 */
	@Override
	public void start() throws LifecycleException {
		this.proc.start();
	}

	/* (non-Javadoc)
	 * @see org.jmxtrans.monitoring.ManagedJmxTransformerProcessMXBean#stop()
	 */
	@Override
	public void stop() throws LifecycleException {
		this.proc.stop();
	}

	/* (non-Javadoc)
	 * @see org.jmxtrans.monitoring.ManagedJmxTransformerProcessMXBean#getQuartPropertiesFile()
	 */
	@Override
	public String getQuartPropertiesFile() {
		return configuration.getQuartPropertiesFile();
	}

	/* (non-Javadoc)
	 * @see org.jmxtrans.monitoring.ManagedJmxTransformerProcessMXBean#setQuartPropertiesFile(java.lang.String)
	 */
	@Override
	public void setQuartPropertiesFile(String quartPropertiesFile) {
		configuration.setQuartPropertiesFile(quartPropertiesFile);
	}

	/* (non-Javadoc)
	 * @see org.jmxtrans.monitoring.ManagedJmxTransformerProcessMXBean#getRunPeriod()
	 */
	@Override
	public int getRunPeriod() {
		return configuration.getRunPeriod();
	}

	/* (non-Javadoc)
	 * @see org.jmxtrans.monitoring.ManagedJmxTransformerProcessMXBean#setRunPeriod(int)
	 */
	@Override
	public void setRunPeriod(int runPeriod) {
		configuration.setRunPeriod(runPeriod);
	}

	/* (non-Javadoc)
	 * @see org.jmxtrans.monitoring.ManagedJmxTransformerProcessMXBean#setJsonDirOrFile(java.io.File)
	 */
	@Override
	public void setJsonDirOrFile(String jsonDirOrFile) {
		configuration.setJsonDirOrFile(new File(jsonDirOrFile));
	}

	/* (non-Javadoc)
	 * @see org.jmxtrans.monitoring.ManagedJmxTransformerProcessMXBean#getJsonDirOrFile()
	 */
	@Override
	public String getJsonDirOrFile() {
		return configuration.getJsonDirOrFile().getAbsolutePath();
	}

	/* (non-Javadoc)
	 * @see org.jmxtrans.monitoring.ManagedObject#getObjectName()
	 */
	@Override
	public ObjectName getObjectName() throws MalformedObjectNameException {
		if (objectName == null) {
			objectName = new ObjectName("org.jmxtrans:Type=JmxTransformerProcess,Name=JmxTransformerProcess");
		}
		return objectName;
	}

	/* (non-Javadoc)
	 * @see org.jmxtrans.monitoring.ManagedObject#setObjectName(javax.management.ObjectName)
	 */
	@Override
	public void setObjectName(ObjectName objectName) throws MalformedObjectNameException {
		this.objectName = objectName;
	}

	/* (non-Javadoc)
	 * @see org.jmxtrans.monitoring.ManagedObject#setObjectName(java.lang.String)
	 */
	@Override
	public void setObjectName(String objectName) throws MalformedObjectNameException {
		this.objectName = ObjectName.getInstance(objectName);
	}

}
