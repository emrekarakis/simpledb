package simpledb.server;

import simpledb.remote.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Startup {
   public static void main(String args[]) throws Exception {
      // configure and initialize the database
      String database = args.length == 0 ? "student" : args[0];
      SimpleDB.init( database );
      
      // post the server entry in the rmi registry
      RemoteDriver d = new RemoteDriverImpl();
      // Naming.rebind("simpledb", d);
      Registry registry = LocateRegistry.createRegistry( 1099 );
      registry.rebind( "simpledb", d );

      System.out.println("database server ready");
   }
}
