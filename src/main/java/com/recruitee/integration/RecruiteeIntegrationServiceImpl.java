package com.recruitee.integration;


import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    public void  recruiteeToExternalIntegration(RecrutieeResponse recrutieeResponse) throws JsonProcessingException, UnsupportedEncodingException, URISyntaxException  {
        
        //Basics
		String eventType = recrutieeResponse.eventType;
        Payload payload = recrutieeResponse.payload;
        //send notification
     if(Objects.nonNull(eventType)) {
    	String candidateName = payload.getCandidate().name;
 		String dot = ".";
 		String message = "";
 		switch(eventType) {
 		case "new_candidate":
 			String additionalProperties =  payload.getAdditionalProperties().get("offers").toString();
 			List<String> propertyList = Arrays.asList(additionalProperties.split(",")).stream().collect(Collectors.toList());
 			String jobTitle = propertyList.get(propertyList.size()-2).replace(" title=", "");
 			message = "Candidate "+ candidateName +" applied for the job " + jobTitle+ dot;
 			System.out.println(message);
 			//call google notification method
 		    break; 
 		case "candidate_assigned":
 			message = "New job/talent pool " +  payload.getOffer().title + " is added to the candidate "+ candidateName + dot ;
 			System.out.println(message);
 			//call google notification method
 		    break;
 		case "candidate_moved":
 			String fromStage = payload.details.fromStage.name;
 			String toStage = payload.details.toStage.name;
 			message = "Candidate of position " + payload.getOffer().title + " moved from " + fromStage + " to " + toStage + dot ;
 			System.out.println(message);
 			//call google notification method
 			initiateExternalIntegration(payload);
 		    break;
 	    default:
 		   //donothing
 		}		    
     }
}
	
	/**
	 * sendNotification is used to send notification to chat room.
	 * 
	 * @param eventType
	 * @param payload
	 * @return 
	 * @throws JsonProcessingException 
	 * @throws URISyntaxException 
	 * @throws UnsupportedEncodingException 
	 */
	private CompletableFuture<String> initiateExternalIntegration(Payload payload) throws JsonProcessingException, URISyntaxException, UnsupportedEncodingException {		
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
	           //IAssistant Integration
	           if(pipeLine.equals("Hired")) {
	        	   String url = "https://timesheet.ideas2it.com/api/on-boarding/recruitee";
	        	   URI uri = new URI(url);       
	        	   ObjectMapper objectMapper = new ObjectMapper();
	        	   //RequestBody
	        	   Map<String,String>requestMap=new HashMap<>();
	        	   requestMap.put("name", payload.getCandidate().name);
	        	   requestMap.put("email", email);
	        	   String requestBody = objectMapper
	        			   .writerWithDefaultPrettyPrinter()
	        			   .writeValueAsString(requestMap);

	        	   // Create HTTP request object
	        	   HttpRequest request = HttpRequest.newBuilder()
	        			   .uri(uri)
	                	.POST((BodyPublishers.ofString(requestBody)))
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
