package DellEdgeServices;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
// --- <<IS-END-IMPORTS>> ---

public final class JavaServices

{
	// ---( internal utility methods )---

	final static JavaServices _instance = new JavaServices();

	static JavaServices _newInstance() { return new JavaServices(); }

	static JavaServices _cast(Object o) { return (JavaServices)o; }

	// ---( server methods )---




	public static final void SendToRMQViaQPID (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(SendToRMQViaQPID)>> ---
		// @sigtype java 3.5
		// [i] field:0:required inputMessage
		// [o] field:0:required deliveryStatus
		try {
			
			String currentDirectory = System.getProperty("user.dir");
			String jndiConfigFile = currentDirectory + "/packages/DellPOC/config/qpid-jndi.properties";
			
			Reader jndiReadear = new FileReader(jndiConfigFile);
			
		
			// pipeline
			IDataCursor pipelineCursor = pipeline.getCursor();
				String	inputMessage = IDataUtil.getString( pipelineCursor, "inputMessage" );
			pipelineCursor.destroy();
		
		
			Properties properties = new Properties();
			properties.load(jndiReadear);
			Context context = new InitialContext(properties);
			ConnectionFactory connectionFactory
		      = (ConnectionFactory) context.lookup("dellConnectionFactory");
			
			Queue queue = (Queue) context.lookup("myQueueLookup");
			
		    // Create connection
			Connection connection = connectionFactory.createConnection();
		    connection.start();
		
		    // Create session
		    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		    // Create destination (queue)
		    Destination destination = session.createQueue(queue.getQueueName());
		
		    // Create producer
		    MessageProducer producer = session.createProducer(destination);
		
		    // Create message
		    TextMessage message = session.createTextMessage(inputMessage);
		
		    // Send message
		    producer.send(message);
			
			IDataCursor pipelineCursor_o = pipeline.getCursor();
			IDataUtil.put( pipelineCursor_o, "deliveryStatus", "Message sent to : " + queue.getQueueName());
			pipelineCursor.destroy();
			
			// Clean up
		    session.close();
		    connection.close();
		    
		} catch (Exception exp) {
			IDataCursor pipelineCursor = pipeline.getCursor();
			 IDataUtil.put( pipelineCursor, "deliveryStatus", exp.toString());
			 pipelineCursor.destroy();
		}
		// --- <<IS-END>> ---

                
	}
}

