package com.example.cc;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

public class TrasnferChaincode extends ChaincodeBase {

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
      return query(stub, arguments.stream().skip(0).toArray(String[]::new));
    case "transfer":
      break;
    default:
      break;
    }
    return newErrorResponse(Json.createObjectBuilder().add("Error", "Unknown operation").build().toString());
  }

  protected Response initialize(ChaincodeStub stub, String[] args) {
    String acc1 = args[0];
    String acc1Balance = args[1];
    String acc2 = args[2];
    String acc2Balance = args[3];

    stub.putStringState(acc1, new Integer(acc1Balance).toString());
    stub.putStringState(acc2, new Integer(acc2Balance).toString());

    return newSuccessResponse();
  }
  
  protected Response query(ChaincodeStub stub, String[] args) {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    if (args.length != 1) {
      builder.add("Name", "Argumnets error").add("Amount", "Arguments error");
      return newErrorResponse(builder.build().toString().getBytes(UTF_8));
    }
    String accountKey = args[0];
    int i;
    try {
      i = Integer.parseInt(stub.getStringState(accountKey));
    } catch (Exception e) { 
      builder.add("Name", accountKey).add("Amount", e.getMessage());
      return newErrorResponse(builder.build().toString().getBytes(UTF_8));
    }
    builder.add("Name", accountKey).add("Amount", i );
    return newSuccessResponse( builder.build().toString().getBytes(UTF_8));

  }

  public static void main(String[] args) throws Exception {
    new TrasnferChaincode().start(args);
  }

}
