package com.medisanaspace.web.main;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.bean.SessionScoped;

import org.springframework.context.annotation.Scope;

import com.medisanaspace.printer.PrinterInterface;
import com.medisanaspace.printer.PrinterInterface.LoggerStatus;
import com.medisanaspace.printer.WebPrinter;
import com.medisanaspace.web.testconfig.AuthorizationModule;
import com.medisanaspace.web.testconfig.OAuthData;
import com.medisanaspace.web.testconfig.ServerType;
import com.medisanaspace.web.testconfig.TestRunner;
import com.medisanaspace.web.testconfig.TestRunnerConfig;
import com.medisanaspace.web.testtask.AbstractTestTask;
import com.medisanaspace.web.testtask.ActivitydockTestTask;
import com.medisanaspace.web.testtask.CardiodockTestTask;
import com.medisanaspace.web.testtask.GluckodockTestTask;
import com.medisanaspace.web.testtask.TargetscaleTestTask;
import com.medisanaspace.web.testtask.ThermodockTestTask;
import com.medisanaspace.web.testtask.TrackerActivityAndTrackerSleepTestTask;
import com.medisanaspace.web.testtask.TrackerPhaseTestTask;
import com.medisanaspace.web.testtask.TrackerSleepTestTask;
import com.medisanaspace.web.testtask.User;
import com.medisanaspace.web.testtask.UserSettingsTestTask;

/**
 * Main class for the test program, configure the TestRunner that runs the tests
 * here and add your TestTasks.
 * 
 * @author Jan Krause (c) Medisana Space Technologies GmbH, 2013
 * 
 * 
 * @version $Revision: 1.0 $
 */
public class CloudClient {

	// Webprinter
	public static final PrinterInterface printer = new WebPrinter(
			LoggerStatus.LOG_ALL);
	
	/*
	 * Set the serverType, user, mobile, maximal number of threads and printer
	 * parameter here. If user set to null, a new random user will be created on
	 * the server
	 */
	private static TestRunnerConfig newConfiguration = new TestRunnerConfig(
			ServerType.TEST_SERVER, new User("test.test", "test.test",
					"en_GB"), false, 1, CloudClient.printer);
	
	private ArrayList<AbstractTestTask> testsToRun = new ArrayList<AbstractTestTask>();
	
	@Resource(name="authorizationModuleBean")
	private AuthorizationModule authorizationModule;
	
	private String messageLog="";
	
	
	public CloudClient(){
	}
	
	/**
	 * First part of the OAuth handshake.
	 * @return url with unauthorizedaccess token to the login page
	 * @throws Exception
	 */
	public String authorize() throws Exception{
		authorizationModule = new AuthorizationModule(newConfiguration);
		return authorizationModule.authorize();
	}
	/**
	 * Second part of the OAuth handshake.
	 * @param verifierToken
	 * @return OAuthData to communicate with the server.
	 * @throws Exception
	 */
	public OAuthData authorizeWithVerifierToken (String verifierToken) throws Exception{
		return	authorizationModule.authorizeWithVerifierToken(verifierToken);
	}
	
	public void runTests(List<String> testList, OAuthData oauthdata){
		
		// maybe configuration of server etc. here
		//
		int numberOfEntries = 1;
		
		// available tests
		final ArrayList<AbstractTestTask> tests = new ArrayList<AbstractTestTask>();
		tests.add(new TrackerActivityAndTrackerSleepTestTask(numberOfEntries));
		tests.add(new ActivitydockTestTask(numberOfEntries));
		tests.add(new CardiodockTestTask(numberOfEntries));
		tests.add(new GluckodockTestTask(numberOfEntries));
		tests.add(new TargetscaleTestTask(numberOfEntries));
		tests.add(new ThermodockTestTask(numberOfEntries));
		tests.add(new TrackerPhaseTestTask(numberOfEntries));
		tests.add(new TrackerSleepTestTask(numberOfEntries));
		tests.add(new UserSettingsTestTask(numberOfEntries));
		

		// get selected tests to run
		for(String index: testList){
			testsToRun.add(tests.get(Integer.parseInt(index)));
		}
		
		TestRunner testRunner = new TestRunner(newConfiguration, oauthdata);
		testRunner.setTestTasks(testsToRun);
		testRunner.runTests();
		
		messageLog=((WebPrinter) printer).getMessages();
		// retrieve latencies
//		for(AbstractTestTask task: testsToRun){
//			task.getLatency()
//		}
		
	}

	public String getMessageLog() {
		return messageLog;
	}





}
