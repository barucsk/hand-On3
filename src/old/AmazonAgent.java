/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package agents.p2p;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;
import net.sf.clipsrules.jni.*;

/**
   This example shows how to implement the responder role in 
   a FIPA-contract-net interaction protocol. In this case in particular 
   we use a <code>ContractNetResponder</code>  
   to participate into a negotiation where an initiator needs to assign
   a task to an agent among a set of candidates.
   @author Giovanni Caire - TILAB
 */
public class AmazonAgent extends Agent {
	Environment clips;
	protected void setup() {
		try{
			clips = new Environment();
			clips.clear();
			clips.load("src/clips/amazon1-facts.clp");
			clips.load("src/clips/amazon1-rules.clp");
			clips.reset();
			clips.run();
		}catch(Exception e) {}

		System.out.println("Agent "+getLocalName()+" waiting for CFP...");
		MessageTemplate template = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
				MessageTemplate.MatchPerformative(ACLMessage.CFP) );

		addBehaviour(new ContractNetResponder(this, template) {
			@Override
			protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent "+getLocalName()+": CFP received from "+cfp.getSender().getName()+". Action is "+cfp.getContent());
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				try
				{
					FactAddressValue fv = (FactAddressValue)((MultifieldValue) clips.eval("(find-fact ((?f product)) (eq ?f:name "+cfp.getContent()+"))")).get(0);
					String messageStr;
					messageStr = " El precio es " + ((NumberValue) fv.getSlotValue("price")).intValue() + ". " ;
					try
					{
						System.out.println("***********************************");
						FactAddressValue fv2 = (FactAddressValue)((MultifieldValue) clips.eval("(find-fact ((?f "+cfp.getContent()+"promo))")).get(0);
						String message;
						System.out.println("TEST***********************************");
						message = " tiene una promocion: " + ((LexemeValue) fv2.getSlotValue(cfp.getContent()+"promo")).getValue() + ". " ;
						System.out.println(cfp.getContent()+message);
						// We provide a proposal
						String proposal = ((LexemeValue) fv2.getSlotValue(cfp.getContent()+"promo")).getValue();
						System.out.println("Agent "+getLocalName()+": Proposing "+proposal);
						ACLMessage propose = cfp.createReply();
						propose.setPerformative(ACLMessage.PROPOSE);
						propose.setContent(String.valueOf(proposal));
						return propose;
					}
					catch(Exception e)
					{
						System.out.println(cfp.getContent()+messageStr);
						// We provide a proposal
						int proposal = ((NumberValue) fv.getSlotValue("price")).intValue();
						System.out.println("Agent "+getLocalName()+": Proposing "+proposal);
						ACLMessage propose = cfp.createReply();
						propose.setPerformative(ACLMessage.PROPOSE);
						propose.setContent(String.valueOf(proposal));
						return propose;
					}
				}
				catch(Exception e)
				{
					// We refuse to provide a proposal
					System.out.println("Agent "+getLocalName()+": Refuse not found what you its looking for. Good look next time");
					throw new RefuseException("evaluation-failed");
				}
			}

			@Override
			protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
				System.out.println("Agent "+getLocalName()+": Proposal accepted");
				if (performAction()) {
					System.out.println("Agent "+getLocalName()+": Compra completada con exito");
					ACLMessage inform = accept.createReply();
					inform.setPerformative(ACLMessage.INFORM);
					return inform;
				}
				else {
					System.out.println("Agent "+getLocalName()+": Lo siento vendimos elultimo articulo de esa clase Suerte para la proxima");
					throw new FailureException("unexpected-error");
				}	
			}

			protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
				System.out.println("Agent "+getLocalName()+": Proposal rejected");
			}
		} );
	}

	private int evaluateAction() {
		// Simulate an evaluation by generating a random number
		return (int) (Math.random() * 10);
	}

	private boolean performAction() {
		// Simulate action execution by generating a random number
		return (Math.random() > 0.2);
	}
}

