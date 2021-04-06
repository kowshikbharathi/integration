package com.recruitee.integration;


import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.recruitee.integration.model.RecrutieeResponse;

/**
 * RecruiteeIntegrationController handles recruitee Integrations
 * @author Kowshik Bharathi M
 */
@RestController
@RequestMapping("/integration/api")
public class RecruiteeIntegrationController {
	
	@Autowired
	public RecruiteeIntegrationController(RecruiteeIntegrationService recruiteeIntegrationService) {
	     super();	
		 this.recruiteeIntegrationService = recruiteeIntegrationService;
	}
	private final RecruiteeIntegrationService recruiteeIntegrationService;
	
	/**
	 * recruiteePipelineChange is used to integrate LemList & IAssistant while pipeline change.
	 * 
	 * @param recrutieeResponse
	 * @return
	 * @throws JsonProcessingException
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 */
    @PostMapping("/recruitee")
    public void recruiteeToExternalIntegration(@RequestBody RecrutieeResponse recrutieeResponse) throws JsonProcessingException, UnsupportedEncodingException, URISyntaxException {  
    	 recruiteeIntegrationService.recruiteeToExternalIntegration(recrutieeResponse);
    }
      
}