package com.example.cc;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

public class TransferChaincode extends ChaincodeBase {

  @Override
  public Response init(ChaincodeStub stub) {
    List<String> arguments = stub.getStringArgs();
    if (arguments.size() != 5) {
      return newErrorResponse("Wrong number of arguments! Expected 5");
    }
    return initialize(stub, arguments.stream().skip(0).toArray(String[]::new));
  }

  @Override
  public Response invoke(ChaincodeStub stub) {
    List<String> arguments = stub.getStringArgs();
    String operation = arguments.get(0);
    switch (operation) {
    case "query":
      return query(stub, arguments.stream().toArray(String[]::new));
    case "transfer":
      byte[] bytes = new byte[1];
      stub.setEvent("event",  arguments.get(1).getBytes());
      return transfer(stub, arguments.stream().toArray(String[]::new));
      
    default:
      break; 
    }
    return newErrorResponse(Json.createObjectBuilder().add("Error", "Unknown operation").build().toString());
  }

  protected Response transfer(ChaincodeStub stub, String[] args) {
    
    int fromBalance= Integer.parseInt(stub.getStringState(args[1]));
    int toBalance = Integer.parseInt(stub.getStringState(args[2]));
    int amount = Integer.parseInt(args[3]);
     
    stub.putStringState(args[1], String.valueOf( fromBalance - amount));
    stub.putStringState(args[2], String.valueOf( toBalance  + amount));
    return newSuccessResponse(); 
  }

  protected Response initialize(ChaincodeStub stub, String[] args) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < args.length; i++) {
      builder.append("<").append(args[i]).append(">");
    }
    stub.putStringState("INIT", builder.toString());
    String acc1 = args[1];
    String acc1Balance = args[2];
    String acc2 = args[3];
    String acc2Balance = args[4];

    stub.putStringState(acc1, new Integer(acc1Balance).toString());
    stub.putStringState(acc2, new Integer(acc2Balance).toString());

    return newSuccessResponse();
  }
  
  protected Response query(ChaincodeStub stub, String[] args) {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    String accountKey = args[1];
    int i;
    try {
      i = Integer.parseInt(stub.getStringState(accountKey));
    } catch (Exception e) { 
      builder.add("Name", accountKey).add("Amount", e.getMessage());
      return newSuccessResponse(builder.build().toString().getBytes(UTF_8));
    }
    builder.add("Name", accountKey).add("Amount", i );
    return newSuccessResponse( builder.build().toString().getBytes(UTF_8));

  }

  public static void main(String[] args) throws Exception {
    new TransferChaincode().start(args);
  }

}
