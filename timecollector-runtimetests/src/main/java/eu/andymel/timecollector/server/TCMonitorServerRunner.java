package eu.andymel.timecollector.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCMonitorServerRunner {
	
	private static final Logger LOG = LoggerFactory.getLogger(TCMonitorServerRunner.class);
	
	public static void main(String[] args) {
//
//		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
//		File file = new File("E:/_d_a_t_e_n/programmieren/on_github/TimeCollector/timecollector-runtimetests/src/main/resources/log4j2.xml");
////		
//		if(file.exists()){
//			LOG.info("exists!");
//		}else{
//			LOG.info("Doe not exist! "+file.getAbsolutePath());
//		}
//		LOG.info(""+TCMonitorServerRunner.class);
//		
//		
//		try {
//			String s = readFile(file.getAbsolutePath(), Charset.defaultCharset());
//			LOG.info(s);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		// this will force a reconfiguration
////		LOG.info("-------------------------");
//		context.setConfigLocation(file.toURI());
////		LOG.info("-------------------------");
////		
		LOG.error("error");
		LOG.warn("warn");
		LOG.info("info");
		LOG.debug("debug");
		LOG.trace("trace");
		
//		InputStream stream = TCMonitorServerRunner.class.getClassLoader().getResourceAsStream("log4j2.xml");
//		InputStream stream = TCMonitorServerRunner.class.getClassLoader().getResourceAsStream("log4j2.yaml");
//        System.out.println(stream != null);
//		
//        System.out.println("ClassLoader.getSystemResource(\"log4j2.yaml\") => "+ClassLoader.getSystemResource("log4j2.yaml")); //Check if file is available in CP
//        ClassLoader cl = Thread.currentThread().getContextClassLoader(); //Code as in log4j2 API. Version: 2.8.1
//         String [] classes = 
//        	 {
//        		"com.fasterxml.jackson.databind.ObjectMapper",
//		        "com.fasterxml.jackson.databind.JsonNode",
//		        "com.fasterxml.jackson.core.JsonParser",
//		        "com.fasterxml.jackson.dataformat.yaml.YAMLFactory"
//        	 };
//
//         for(String className : classes) {
//             try {
//				cl.loadClass(className);
//			} catch (ClassNotFoundException e) {
//				LOG.error("Can't load class '"+className+"'", e);
//			}
//         }
        
		TCMonitorServerConfig cfg = new TCMonitorServerConfig() {

			@Override
			public int getPort() {
				return 1234;
			}

			@Override
			public String getContextPath() {
				return "/";
			}
			
		};
		TCMonitorServer s = new TCMonitorServer(cfg);
		
		LOG.info("Starting monitoring server...");
		try {
			s.start(false);
		} catch (Exception e) {
			LOG.error("Can't start Jetty Server", e);
		}
	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

}
