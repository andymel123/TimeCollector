package eu.andymel.timecollector.server;

public interface TCMonitorServerConfig {

	/**
	 * @return the port to listen 
	 */
	int getPort();

//	/**
//	 * @return the parent context path 
//	 */
//	String getContextPath();
//
//	/**
//	 * @return the path on the disk where the static files are
//	 */
//	String getStaticWebContentDir();

	/**
	 * @return the sub context path under the parent context path that has to be
	 * mapped to static files lying in the directory that is retrieved with getStaticWebContentDir(). 
	 * If getContextPath() returns /foo and this method returns /bar/*, all paths
	 * that start http://host:port/foo/bar/ will map to static files
	 */
	String getContextPath();

	double getUpdatesPerMinute();

}
