package com.recruitee.integration;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.recruitee.integration.model.RecrutieeResponse;

/**
 * RecruiteeIntegrationService is used for recruitee to external integration.
 * @author Kowshik Bharathi M
 */
public interface RecruiteeIntegrationService {
	
	 public void recruiteeToExternalIntegration( RecrutieeResponse recrutieeResponse) throws JsonProcessingException, UnsupportedEncodingException, URISyntaxException;

}
