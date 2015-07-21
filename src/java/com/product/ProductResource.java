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
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonParser;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.json.simple.JSONArray;

/**
 *
 * @author Monal
 */
@Path("Product")
public class ProductResource {
    productConnection con=new productConnection();
    Connection conn=null;
    @Context
    private UriInfo context;
    
    /**
     * Creates a new instance of ProductResource
     */
    public ProductResource() {
       conn=con.getConnection();
    }

    /**
     * Retrieves representation of an instance of com.oracle.products.ProductResource
     * @return an instance of java.lang.String
     */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public String getAllProducts() throws SQLException
   {
       if(conn==null)
       {
           return "not connected";
       }
       else {
           String query="Select * from product";
           PreparedStatement pstmt = conn.prepareStatement(query);
           ResultSet rs = pstmt.executeQuery();
           String result="";
           JSONArray productArr = new JSONArray();
           while (rs.next()) {
                JsonObjectBuilder prodMap = Json.createObjectBuilder();
                prodMap.add("productID", rs.getInt("product_id"));
                prodMap.add("name", rs.getString("name"));
                prodMap.add("description", rs.getString("description"));
                prodMap.add("quantity", rs.getInt("quantity"));
                productArr.add(prodMap);
           }
            result = productArr.toString();
          return  result.replace("},", "},\n");
        }
       
   }
   
   @GET
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public String getproduct(@PathParam("id") int id) throws SQLException {
   
      if(conn==null)
       {
           return "not connected";
       }
       else {
           String query="Select * from product where product_id = ?";
           PreparedStatement pstmt = conn.prepareStatement(query);
           pstmt.setInt(1,id);
           ResultSet rs = pstmt.executeQuery();
           String result="";
           JsonArrayBuilder productArr = Json.createArrayBuilder();
           while (rs.next()) {
                 JsonObjectBuilder prodMap = Json.createObjectBuilder();
                prodMap.add("productID", rs.getInt("product_id"));
                prodMap.add("name", rs.getString("name"));
                prodMap.add("description", rs.getString("description"));
                prodMap.add("quantity", rs.getInt("quantity"));
                productArr.add(prodMap);
           }    
                result = productArr.build().toString();
                
                 return result;
      }
   
   }
   
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.TEXT_PLAIN)
   public String postProduct(String str) throws SQLException{
       JsonParser parser= Json.createParser(new StringReader(str));
       Map<String,String> productMap = new LinkedHashMap<String,String>();
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
              productMap.put(key, val);
              break;
            case VALUE_NUMBER:     
              val=parser.getString();
              productMap.put(key, val);
              break;  
            default :
              break;  
            }
       }    
       if(conn == null){
           return "Not connected";
       }
       else {
            String query="INSERT INTO product (product_id,name,description,quantity) VALUES (?,?,?,?)";
            PreparedStatement pstmt=conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(productMap.get("product_id")));
            pstmt.setString(2, productMap.get("name"));
            pstmt.setString(3, productMap.get("description"));
            pstmt.setInt(4, Integer.parseInt(productMap.get("quantity")));
            pstmt.executeUpdate();
            return "row has been inserted into the database";
           }
       
       
   }
   
   
   @PUT
   @Path("{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   public String  putProduct(@PathParam("id")  int id,String str) throws SQLException{
    JsonParser parser= Json.createParser(new StringReader(str));
       Map<String,String> productMap = new LinkedHashMap<>();
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
              productMap.put(key, val);
              break;
            case VALUE_NUMBER:     
              val=parser.getString();
              productMap.put(key, val);
              break;  
            default :
              break;  
            }
       }    
       if(conn == null){
           return "Not connected";
       }
       else {
            String query="UPDATE product SET  name = ?, description = ?, quantity = ? WHERE product_id =?" ;          PreparedStatement pstmt=conn.prepareStatement(query);
            pstmt.setString(1, productMap.get("name"));
            pstmt.setString(2, productMap.get("description"));
            pstmt.setInt(3, Integer.parseInt(productMap.get("quantity")));
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            return "row has been updated into the database";
           }
   
   }
 
   @DELETE
   @Path("{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.TEXT_PLAIN)
   public String deleteProduct(@PathParam("id") int id) throws SQLException{
       
        if(conn==null)
        {
           return "not connected";
        }
        else {
           String query="DELETE FROM product WHERE product_id = ?";
           PreparedStatement pstmt = conn.prepareStatement(query);
           pstmt.setInt(1,id);
           pstmt.executeUpdate();
           return "The specified row is deleted";
           
        }
   
    }
}
