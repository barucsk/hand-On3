package agents.p2p;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import jade.domain.FIPANames;

import java.util.Date;
import java.util.Vector;
import java.util.Enumeration;


public class CustomerAgent extends Agent {
	private int nResponders;
	
	protected void setup() { 
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//The first argument its the thing that we want to buy and the other arguments its where we will to search//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
  	Object[] args = getArguments();
  	if (args != null && args.length > 0) {
  		nResponders = args.length-1;
  		System.out.println("Cx "+getLocalName()+": Trying to find "+ args[0]+" to one out of "+nResponders+" Stores.");
  		
  		// Fill the CFP message
  		ACLMessage msg = new ACLMessage(ACLMessage.CFP);
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//This for its to add recivers we start i of 1 because the firs spot its not a responder////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
  		for (int i = 1; i < args.length; ++i) {
  			msg.addReceiver(new AID((String) args[i], AID.ISLOCALNAME));
  		}
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
			// We want to receive a reply in 10 secs
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msg.setContent(args[0].toString());
			
			addBehaviour(new ContractNetInitiator(this, msg) {
				
				protected void handlePropose(ACLMessage propose, Vector v) {
					System.out.println("Cx "+getLocalName()+": Seller "+propose.getSender().getName()+" proposed "+propose.getContent());
				}
				
				protected void handleRefuse(ACLMessage refuse) {
					System.out.println("Cx "+getLocalName()+": Seller "+refuse.getSender().getName()+" refused");
				}
				
				protected void handleFailure(ACLMessage failure) {
					if (failure.getSender().equals(myAgent.getAMS())) {
						// FAILURE notification from the JADE runtime: the receiver
						// does not exist
						System.out.println("Cx "+getLocalName()+": Responder does not exist");
					}
					else {
						System.out.println("Cx "+getLocalName()+": Agent "+failure.getSender().getName()+" failed");
					}
					// Immediate failure --> we will not receive a response from this agent
					nResponders--;
				}
				
				protected void handleAllResponses(Vector responses, Vector acceptances) {
					if (responses.size() < nResponders) {
						// Some responder didn't reply within the specified timeout
						System.out.println("Cx "+getLocalName()+": Timeout expired: missing "+(nResponders - responses.size())+" responses");
					}
					// Evaluate proposals.
					int bestProposal = 32000;
					AID bestProposer = null;
					ACLMessage accept = null;
					Enumeration e = responses.elements();
					while (e.hasMoreElements()) {
						ACLMessage msg = (ACLMessage) e.nextElement();
						if (msg.getPerformative() == ACLMessage.PROPOSE) {
							ACLMessage reply = msg.createReply();
							reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
							acceptances.addElement(reply);
							///////////////////////////////////////////////////////
							String[] res = msg.getContent().split("\\+");
							int price = Integer.parseInt(res[0]);
							int msi = Integer.parseInt(res[1]);
							String bank = res[2];
							System.out.println("Cx "+getLocalName()+": Recibi de "+msg.getSender()+" el precio por $"+price);
							if(msi>1)
								System.out.println("Cx "+getLocalName()+": Ademas esta a "+msi+" meses sin intereses con tarjetas "+bank);
							int proposal = price/msi;
							///////////////////////////////////////////////////////
							if (proposal < bestProposal) {
								bestProposal = proposal;
								bestProposer = msg.getSender();
								accept = reply;
							}
						}
					}
					// Accept the proposal of the best proposer
					if (accept != null) {
						System.out.println("Cx "+getLocalName()+": Accepting proposal "+bestProposal+" from responder "+bestProposer.getName());
						accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					}						
				}
				
				protected void handleInform(ACLMessage inform) {
					System.out.println("Cx "+getLocalName()+": Seller "+inform.getSender().getName()+" Te vendio exitosamente "+args[0]);
					if(inform.getContent()!="na")
						System.out.println("Ademas me ofrecieron la sigueinte promocion por haber comprado "+inform.getContent());
				}
			} );
  	}
  	else {
  		System.out.println("Cx "+getLocalName()+": No se pusieron nada para buscar ni donde buscar");
  	}
  } 
}

