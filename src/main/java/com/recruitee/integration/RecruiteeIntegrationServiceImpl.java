package com.recruitee.integration;


import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitee.integration.model.Payload;
import com.recruitee.integration.model.RecrutieeResponse;

/**
 * RecruiteeIntegrationServiceImpl implements RecruiteeIntegrationService.
 * @author Kowshik Bharathi M
 */

@Service
public class RecruiteeIntegrationServiceImpl implements RecruiteeIntegrationService {
	
	@Override
    public void  recruiteeToExternalIntegration(RecrutieeResponse recrutieeResponse) {
        
        //Basics
		String eventType = recrutieeResponse.eventType;
        Payload payload = recrutieeResponse.payload;
     if(Objects.nonNull(eventType)) {
 		switch(eventType) {
 		case "candidate_moved":
 			try {
 			   subscribeCandidateInLemlist(payload);
 			}catch(JsonProcessingException | UnsupportedEncodingException | URISyntaxException exception) {
 				System.out.println(exception);
 			}
 		    break;
 	    default:
 		   //donothing
 		}		    
     }
}
	
	/**
	 * subscribeCandidateInLemlist is used create user in lemList.
	 * 
	 * @param payload
	 * @return 
	 * @throws JsonProcessingException 
	 * @throws URISyntaxException 
	 * @throws UnsupportedEncodingException 
	 */
	private CompletableFuture<String> subscribeCandidateInLemlist(Payload payload) throws JsonProcessingException, URISyntaxException, UnsupportedEncodingException {		
	        Map<String, String> campaginMap = new HashMap<String, String>()
	        {{
	             put("Ideas2IT: DOJ in 1 month", "cam_Ko8yJWBrtLgqeFEod");
	             put("Ideas2IT: DOJ in 2 month", "cam_C6TEDMeZMGPYxzBgk");
	             put("Ideas2IT: DOJ in 3 month", "cam_qyJDT27AGJThBe2ME");
	             put("Ideas2IT: DOJ in 15 days", "cam_uvYa6TGqt3JSRbyPs");
	        }};
	       //For Basic Authentication
	  	   String auth =  ":f0777b800f62ebaf6e57b03d8ebb1887";
	  	   String authHeaderValue = "Basic " + new String(Base64.encodeBase64(auth.getBytes("UTF-8")));
	    	  
	        //Lemlist Integration
	        if(Objects.nonNull(payload) && !payload.getOffer().slug.contains("Element5")) {
	    	   String pipeLine = payload.details.toStage.name;
	    	   String email = payload.getCandidate().emails.get(0);
	        
	           if(campaginMap.containsKey(pipeLine)) {
	        	  //API url formation
	        	  String url = "https://api.lemlist.com/api/campaigns/"+campaginMap.get(payload.details.toStage.name)+"/leads/"+ email;
	        	  URI uri = new URI(url);       
	        	  ObjectMapper objectMapper = new ObjectMapper();
	        	  //RequestBody
	        	  Map<String,String>requestMap=new HashMap<>();
	        	  requestMap.put("firstName", payload.getCandidate().name);    
	        	  requestMap.put("companyName", payload.getCompany().name);
	        	  String requestBody = objectMapper
	        			  .writerWithDefaultPrettyPrinter()
	        			  .writeValueAsString(requestMap);

	        	  // Create HTTP request object
	        	  HttpRequest request = HttpRequest.newBuilder()
	        			  .uri(uri)
	        			  .POST((BodyPublishers.ofString(requestBody)))
	        			  .header("Authorization", authHeaderValue)
	        			  .header("Content-Type", "application/json")
	        			  .header("accept", "application/json")
	        			  .build();
	        	  // Send HTTP request
	        	  return  HttpClient.newHttpClient()
	        			  .sendAsync(request, BodyHandlers.ofString())
	        			  .thenApply(HttpResponse::body);		   	   
	           }
	           	   return null;
	        }
	     
		return null;
	}
}
