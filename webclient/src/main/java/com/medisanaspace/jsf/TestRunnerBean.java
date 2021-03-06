package com.medisanaspace.jsf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.medisanaspace.web.library.WebConstants;
import com.medisanaspace.web.main.CloudClient;

@Controller
@Scope("request")
public class TestRunnerBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/***********************************************
	 * Injected Beans
	 **********************************************/
	
	@Autowired private CloudClient cloudClient;
	/*
	 * Store session wide data as
	 * - oauth
	 * - selectedTests
	 * - modules to insert random data
	 */
	@Autowired private SessionDataBean sessionDataBean;
	

	/***********************************************
	 * View attributes and parameters
	 **********************************************/
	
	//output
	private Map<String, String> tests;
	private Map<String, String> serverList;
	private Map<String, String> loggerList;
	private Map<String, String> roleList;
	//  pre configured logging level regarding to the roles
	private Map<String, Set<String>> preConfiguredLoggerOptions;
	
	// input

	private String applicationToken;
	private String applicationSecret;
	
	private String messageLog;
	private boolean ownTokens = false;	

	
	public TestRunnerBean() {
	}
	
	
	@PostConstruct
	public void init(){
		
			tests = new HashMap<String, String>();
			tests.put(WebConstants.TRACKER_ACTIVITY_MODULE,
					"Tracker Activity and Tracker Sleep Test");
			tests.put(WebConstants.ACTIVITYDOCK_MODULE,
					"Activitydock Test");
			tests.put(WebConstants.CARDIODOCK_MODULE,
					"Cardiodock Test");
			tests.put(WebConstants.GLUCODOCK_GLUCOSE_MODULE,
					"Gluckodock Test");
			tests.put(WebConstants.TARGETSCALE_MODULE,
					"Targetscale Test");
			tests.put(WebConstants.THERMODOCK_MODULE,
					"Thermodock Test");
			tests.put(String
					.valueOf(WebConstants.TRACKER_PHASE_MODULE),
					"TrackerPhase Test");
			tests.put(WebConstants.USER_SETTINGS_MODULE,"User Settings Test");
			tests.put(WebConstants.TRACKER_STATS_MODULE, "Tracker Stats Test");
			
			serverList = new HashMap<String, String>();
			serverList.put(WebConstants.TEST_SERVER, "Test Server");
			serverList.put(WebConstants.PRODUCTION_SERVER, "Production Server");
			
			loggerList = new HashMap<String, String>();
			loggerList.put(WebConstants.LOG_ERROR, "Log Errors" );
			loggerList.put(WebConstants.LOG_JSON_DATA,"Log JSON data" );
			loggerList.put(WebConstants.LOG_ACTIVITY,"Log Activities" );
			loggerList.put(WebConstants.LOG_PROTOCOL_MESSAGE,"Log Protocol Messages" );
			loggerList.put(WebConstants.LOG_MESSAGE,"Log Messages");	

			roleList = new HashMap<String, String>();
			roleList.put(WebConstants.MANAGER, "Manager");
			roleList.put(WebConstants.TESTUSER, "Test user");
			roleList.put(WebConstants.DEVELOPER, "Developer");

			// pre configure logger options for the different roles

			preConfiguredLoggerOptions =  new HashMap<String, Set<String>>();
			Set<String> managerOptions = new HashSet<String>();
			Set<String> developerOptions = new HashSet<String>();
			Set<String> testUserOptions = new HashSet<String>();

			managerOptions.add(WebConstants.LOG_ERROR);
			managerOptions.add(WebConstants.LOG_ACTIVITY);
			developerOptions.add(WebConstants.LOG_ACTIVITY);
			developerOptions.add(WebConstants.LOG_ERROR);
			developerOptions.add(WebConstants.LOG_JSON_DATA);
			developerOptions.add(WebConstants.LOG_PROTOCOL_MESSAGE);
			developerOptions.add(WebConstants.LOG_MESSAGE);
			testUserOptions.add(WebConstants.LOG_ERROR);
			testUserOptions.add(WebConstants.LOG_ACTIVITY);
			testUserOptions.add(WebConstants.LOG_MESSAGE);

			preConfiguredLoggerOptions.put(WebConstants.MANAGER, managerOptions);
			preConfiguredLoggerOptions.put(WebConstants.DEVELOPER, developerOptions);
			preConfiguredLoggerOptions.put(WebConstants.TESTUSER, testUserOptions);
			sessionDataBean.setLoggerLevel(preConfiguredLoggerOptions.get(sessionDataBean.getRole()));

	}
	
	public void updateLoggerLevel(AjaxBehaviorEvent e){
		sessionDataBean.setLoggerLevel(preConfiguredLoggerOptions.get(sessionDataBean.getRole()));
	}


	/**
	 * Authorize on a host
	 * 
	 * @return
	 */
	public String authorize() {
		sessionDataBean.setAddRandomData(false);
		try {
			
			String token=""; 
			String secret="";
			if(this.ownTokens){
				token=this.applicationToken;
				secret=this.applicationSecret;
			}
			String url = cloudClient.authorize(sessionDataBean.getServer(), false ,"", "", sessionDataBean.getLoggerLevel(),token, secret);
			
			// redirect the user to the login page to authorize
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect(url);
			// set index on the first test
			if (!sessionDataBean.getSelectedTests().isEmpty()) {
				sessionDataBean.setTestRunIndex(0);
			}else{
				sessionDataBean.setTestRunIndex(-1);
			}
			return null;
		} catch (Exception e) {
			// refer to error page here, user does not need to know specifics
			System.out.println("error");
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			return "error.jsf";
		}
	}


	// getter only
	public Map<String, String> getTests() {
		return tests;
	}

	public String getMessageLog() {
		return messageLog;
	}

	public Map<String, String> getServerList() {
		return serverList;
	}



	public Map<String, String> getRoleList() {
		return roleList;
	}


	public Map<String, String> getLoggerList() {
		return loggerList;
	}
	
	public SessionDataBean getSessionDataBean() {
		return sessionDataBean;
	}
	
	public void setSessionDataBean(SessionDataBean sessionDataBean) {
		this.sessionDataBean = sessionDataBean;
	}


	public String getApplicationToken() {
		return applicationToken;
	}


	public void setApplicationToken(String applicationToken) {
		this.applicationToken = applicationToken;
	}


	public String getApplicationSecret() {
		return applicationSecret;
	}


	public void setApplicationSecret(String applicationSecret) {
		this.applicationSecret = applicationSecret;
	}


	public boolean isOwnTokens() {
		return ownTokens;
	}


	public void setOwnTokens(boolean ownTokens) {
		this.ownTokens = ownTokens;
	}

}
