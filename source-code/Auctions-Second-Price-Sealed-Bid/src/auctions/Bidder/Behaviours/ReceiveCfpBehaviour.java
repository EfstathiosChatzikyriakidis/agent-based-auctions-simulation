package auctions.Bidder.Behaviours;

import auctions.Bidder.Agents.BidderAgent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.ACLMessage;
import java.util.logging.Level;
import jade.util.Logger;

public class ReceiveCfpBehaviour extends CyclicBehaviour {
    private final Logger logger = Logger.getMyLogger(getClass().getName());

    private final BidderAgent agent;

    public ReceiveCfpBehaviour(BidderAgent agent) {
        super(agent);

        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);

        ACLMessage cfpMessage = agent.receive(messageTemplate);

        if (cfpMessage == null) {
            block();

            return;
        }

        String messageContent = cfpMessage.getContent();

        String[] parts = messageContent.split(",");

        String itemName = parts[0];

        int itemPrice = Integer.parseInt(parts[1]);

        logger.log(Level.INFO, "{0} : New auction for ''{1}'' item with ''{2}'' price.", new Object[] { agent.getLocalName(), itemName, itemPrice });

        ACLMessage replyMessage = cfpMessage.createReply();

        int bidderEvaluation = agent.getItemEvaluation(itemName);

        if (bidderEvaluation <= itemPrice) {
            replyMessage.setPerformative(ACLMessage.REFUSE);

            agent.send(replyMessage);

            logger.log(Level.INFO, "{0} : is not joining this auction (no gain).", agent.getLocalName());
            
            return;
        }

        if (agent.getBudget() < bidderEvaluation) {
            replyMessage.setPerformative(ACLMessage.REFUSE);

            agent.send(replyMessage);

            logger.log(Level.INFO, "{0} : is not joining this auction (not enough budget).", agent.getLocalName());

            return;
        }

        replyMessage.setPerformative(ACLMessage.PROPOSE);

        replyMessage.setContent(String.valueOf(bidderEvaluation));

        agent.send(replyMessage);

        logger.log(Level.INFO, "{0} : Sent proposal with ''{1}'' price.", new Object[] { agent.getLocalName(), bidderEvaluation });
    }
}