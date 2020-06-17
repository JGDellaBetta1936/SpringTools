package net.restfulapi.app.rest.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.restfulapi.app.dao.ConfigurationDB;
import net.restfulapi.app.rest.domain.Configuration;
import net.restfulapi.app.rest.domain.Configurations;
import net.restfulapi.app.rest.domain.common.Message;
import net.restfulapi.app.rest.domain.common.Status;
 


/**
 * This REST resource has common path "/configurations" and 
 * represents configurations collection resource as well as individual collection resources.
 * 
 * Default MIME type for this resource is "application/XML"
 * */
@Path("/configurations")
@Produces("application/xml")
public class ConfigurationResource 
{
    /**
     * Use uriInfo to get current context path and to build HATEOAS links 
     * */
    @Context
    UriInfo uriInfo;
     
    /**
     * Get configurations collection resource mapped at path "HTTP GET /configurations"
     * */
    @GET
    public Configurations getConfigurations() {
          
        List<Configuration> list = ConfigurationDB.getAllConfigurations();
          
        Configurations configurations = new Configurations();
        configurations.setConfigurations(list);
        configurations.setSize(list.size());
          
        //Set link for primary collection
        Link link = Link.fromUri(uriInfo.getPath()).rel("uri").build();
        configurations.setLink(link);
          
        //Set links in configuration items
        for(Configuration c: list){
            Link lnk = Link.fromUri(uriInfo.getPath() + "/" + c.getId()).rel("self").build();
            c.setLink(lnk);
        }
        return configurations;
    }
    /**
     * Get configurations collection resource mapped at path "HTTP GET /configurations"
     * */

    @GET
    @Path("/users/{id}")
    @Produces("application/json")
    public String buildJSON(@PathParam("id") Integer id) {
    	JSONObject sampleObject = new JSONObject();
        sampleObject.put("name", "Stackabuser");
        sampleObject.put("age", 35);

        JSONArray messages = new JSONArray();
        messages.put("Hey!");
        messages.put("What's up?!");
        messages.put("Hey! me 1!");
        messages.put("What's up? from 1!");
        messages.put("Hey! me 2");
        messages.put("What's up?! from 2!");
        messages.put("Hey! me3");
        messages.put("What's up? from 3!!");
        sampleObject.put("messages", messages);
        Gson gson = new Gson();
        return gson.toJson(sampleObject); 
        
    }
      
    /**
     * Get individual configuration resource mapped at path "HTTP GET /configurations/{id}"
     * */
	
	  @GET
	  @Produces("application/xml")
	  @Path("/usersXML/{id}") public Response getConfigurationById(@PathParam("id") Integer
	  id){ Configuration config = ConfigurationDB.getConfiguration(id);
	  
	  if(config == null) { return
	  Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build(); }
	  
	  if(config != null){ UriBuilder builder =
	  UriBuilder.fromResource(ConfigurationResource.class)
	  .path(ConfigurationResource.class, "getConfigurationById"); Link link =
	  Link.fromUri(builder.build(id)).rel("self").build(); config.setLink(link); }
	  
	  return
	  Response.status(javax.ws.rs.core.Response.Status.OK).entity(config).build();
	  }
	 
     
    /**
     * Create NEW configuration resource in configurations collection resource
     * */
    @POST
    @Consumes("application/xml")
    public Response createConfiguration(Configuration config){
        if(config.getContent() == null)  {
            return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)
                            .entity(new Message("Config content not found"))
                            .build();
        }
        config.setStatus(Status.ACTIVE);
        Integer id = ConfigurationDB.createConfiguration(config.getContent(), config.getStatus());
        Link lnk = Link.fromUri(uriInfo.getPath() + "/" + id).rel("self").build();
        return Response.status(javax.ws.rs.core.Response.Status.CREATED).location(lnk.getUri()).build();
    }
     
    /**
     * Modify EXISTING configuration resource by it's "id" at path "/configurations/{id}"
     * */
    @PUT
    @Path("/{id}")
    @Consumes("application/xml")
    public Response updateConfiguration(@PathParam("id") Integer id, Configuration config){
         
        Configuration origConfig = ConfigurationDB.getConfiguration(id);
        if(origConfig == null) {
            return Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
        }
         
        if(config.getContent() == null)  {
            return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)
                            .entity(new Message("Config content not found"))
                            .build();
        }
        //config.setStatus(Status.ACTIVE);
 
        ConfigurationDB.updateConfiguration(id, config);
        return Response.status(javax.ws.rs.core.Response.Status.OK).entity(new Message("Config Updated Successfully")).build();
    }
     
    /**
     * Delete configuration resource by it's "id" at path "/configurations/{id}"
     * */
    @DELETE
    @Path("/{id}")
    public Response deleteConfiguration(@PathParam("id") Integer id){
         
        Configuration origConfig = ConfigurationDB.getConfiguration(id);
        if(origConfig == null) {
            return Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
        }
         
        ConfigurationDB.removeConfiguration(id);
        return Response.status(javax.ws.rs.core.Response.Status.OK).build();
    }
      
    /**
     * Initialize the application with these two default configurations
     * the following adds three entries to the stack.
     * */
	
	  static { ConfigurationDB.createConfiguration("Some Content", Status.ACTIVE);
	  ConfigurationDB.createConfiguration("Some More Content", Status.INACTIVE);
	  }
	 
}