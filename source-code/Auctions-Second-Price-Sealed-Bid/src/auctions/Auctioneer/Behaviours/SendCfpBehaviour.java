package auctions.Auctioneer.Behaviours;

import auctions.Auctioneer.Agents.AuctioneerAgent;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.ACLMessage;
import java.util.logging.Level;
import jade.util.Logger;
import javafx.util.Pair;

public class SendCfpBehaviour extends Behaviour {
    private final Logger logger = Logger.getMyLogger(getClass().getName());

    private final AuctioneerAgent agent;

    private final Pair<String, Integer> item;
    
    public SendCfpBehaviour(AuctioneerAgent agent, Pair<String, Integer> item) {
        super(agent);

        this.agent = agent;
        
        this.item = item;
    }

    @Override
    public void action() {
        if (!agent.shouldSendCfp()) {
            return;
        }

        logger.log(Level.INFO, "{0} : Starting new auction for ''{1}'' item.", new Object[] { agent.getLocalName(), item.getKey() });
        
        logger.log(Level.INFO, "{0} : Sending CFP to all bidders.", agent.getLocalName());

        ACLMessage cfpMessage = new ACLMessage(ACLMessage.CFP);

        agent.getBidders().forEach(o -> cfpMessage.addReceiver(o));

        cfpMessage.setContent(item.getKey() + "," + item.getValue());

        String conversationId = "bid-auction-second-price-sealed-bid";

        cfpMessage.setConversationId(conversationId);

        cfpMessage.setReplyWith("cfp-" + System.currentTimeMillis());

        agent.send(cfpMessage);

        agent.setMessageTemplate(MessageTemplate.and(MessageTemplate.MatchConversationId(conversationId), MessageTemplate.MatchInReplyTo(cfpMessage.getReplyWith())));

        agent.cfpIsSent();
    }

    @Override
    public boolean done() {
        return agent.isCfpSent();
    }
}