/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.product;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import static javax.ws.rs.HttpMethod.POST;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * REST Web Service
 *
 * @author Monal
 */
@Path("products")
public class Product {
    productConnection con=new productConnection();
    Connection connect=null;
    @Context
    private UriInfo context;
    
    /**
     * Creates a new instance of ProductResource
     */
    public Product() {
       connect=con.getConnection();
    }

    /**
     * Retrieves representation of an instance of com.oracle.products.ProductResource
     * @return an instance of java.lang.String
     */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public String getAllProducts() throws SQLException
   {
       if(connect==null)
       {
           return "not connected";
       }
       else {
           String query="Select * from products";
           PreparedStatement prepstmnt = connect.prepareStatement(query);
           ResultSet rs = prepstmnt.executeQuery();
           String result="";
           JSONArray productArr = new JSONArray();
           while (rs.next()) {
                Map productMap = new LinkedHashMap();
                productMap.put("productID", rs.getInt("product_id"));
                productMap.put("name", rs.getString("name"));
                productMap.put("description", rs.getString("description"));
                productMap.put("quantity", rs.getInt("quantity"));
                productArr.add(productMap);
           }
            result = productArr.toString();
          return  result.replace("},", "},\n");
        }
       
   }
   
   @GET
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public String getproduct(@PathParam("id") int id) throws SQLException {
   
      if(connect==null)
       {
           return "not connected";
       }
       else {
           String query="Select * from products where product_id = ?";
           PreparedStatement prepstmnt = connect.prepareStatement(query);
           prepstmnt.setInt(1,id);
           ResultSet rs = prepstmnt.executeQuery();
           String result="";
           JSONArray prodArr = new JSONArray();
           while (rs.next()) {
                 Map prodMap = new LinkedHashMap();
                prodMap.put("productID", rs.getInt("product_id"));
                prodMap.put("name", rs.getString("name"));
                prodMap.put("description", rs.getString("description"));
                prodMap.put("quantity", rs.getInt("quantity"));
                prodArr.add(prodMap);
           }    
                result = prodArr.toString();
                
                 return result;
      }
   
   }
   
   @POST
   @Path("/products")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.TEXT_PLAIN)
   public String postProduct(String str) throws SQLException{
       JsonParser parser= Json.createParser(new StringReader(str));
       Map<String,String> prodMap = new LinkedHashMap<String,String>();
       String key="";
       String val="";
       
       while(parser.hasNext()){
        JsonParser.Event event=parser.next();
            switch (event){
            case KEY_NAME :
              key = parser.getString();
              break;
            case VALUE_STRING:
              val=parser.getString();
              prodMap.put(key, val);
              break;
            case VALUE_NUMBER:     
              val=parser.getString();
              prodMap.put(key, val);
              break;  
            default :
              break;  
            }
       }    
       if(connect == null){
           return "Not connected";
       }
       else {
            String query="INSERT INTO product (product_id,name,description,quantity) VALUES (?,?,?,?)";
            PreparedStatement prepstmnt=connect.prepareStatement(query);
            prepstmnt.setInt(1, Integer.parseInt(prodMap.get("product_id")));
            prepstmnt.setString(2, prodMap.get("name"));
            prepstmnt.setString(3, prodMap.get("description"));
            prepstmnt.setInt(4, Integer.parseInt(prodMap.get("quantity")));
            prepstmnt.executeUpdate();
            return "row has been inserted into the database";
           }
       
       
   }
   
   
   @PUT
   @Path("{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   public String  putProduct(@PathParam("id")  int id,String str) throws SQLException{
    JsonParser parser= Json.createParser(new StringReader(str));
       Map<String,String> prodMap = new LinkedHashMap<String,String>();
       String key="";
       String val="";
       
       while(parser.hasNext()){
        JsonParser.Event event=parser.next();
            switch (event){
            case KEY_NAME :
              key = parser.getString();
              break;
            case VALUE_STRING:
              val=parser.getString();
              prodMap.put(key, val);
              break;
            case VALUE_NUMBER:     
              val=parser.getString();
              prodMap.put(key, val);
              break;  
            default :
              break;  
            }
       }    
       if(connect == null){
           return "Not connected";
       }
       else {
           String query="UPDATE product SET  name = ?, description = ?, quantity = ? WHERE product_id =?" ;          
           PreparedStatement prepstmnt=connect.prepareStatement(query);
            prepstmnt.setString(1, prodMap.get("name"));
            prepstmnt.setString(2, prodMap.get("description"));
            prepstmnt.setInt(3, Integer.parseInt(prodMap.get("quantity")));
            prepstmnt.setInt(4, id);
            prepstmnt.executeUpdate();
            return "row has been updated into the database";
           }
   
   }
 
   @DELETE
   @Path("{id}")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.TEXT_PLAIN)
   public String deleteProduct(@PathParam("id") int id) throws SQLException{
       
        if(connect==null)
        {
           return "not connected";
        }
        else {
           String query="DELETE FROM product WHERE product_id = ?";
           PreparedStatement prepstmnt = connect.prepareStatement(query);
           prepstmnt.setInt(1,id);
           prepstmnt.executeUpdate();
           return "The specified row is deleted";
           
        }
   
    }
}
