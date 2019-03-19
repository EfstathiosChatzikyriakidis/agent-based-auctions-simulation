package auctions.Bidder.Behaviours;

import auctions.Bidder.Agents.BidderAgent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.ACLMessage;
import java.util.logging.Level;
import jade.util.Logger;

public class ReceiveAcceptProposalBehaviour extends CyclicBehaviour {
    private final Logger logger = Logger.getMyLogger(getClass().getName());

    private final BidderAgent agent;

    public ReceiveAcceptProposalBehaviour(BidderAgent agent) {
        super(agent);

        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);

        ACLMessage acceptProposalMessage = agent.receive(messageTemplate);

        if (acceptProposalMessage == null) {
            block();

            return;
        }

        String messageContent = acceptProposalMessage.getContent();

        String[] parts = messageContent.split(",");

        String itemName = parts[0];

        int itemPrice = Integer.parseInt(parts[1]);

        logger.log(Level.INFO, "{0} : You won ''{1}'' item for ''{2}'' price.", new Object[] { agent.getLocalName(), itemName, itemPrice });

        ACLMessage informMessage = acceptProposalMessage.createReply();

        informMessage.setPerformative(ACLMessage.INFORM);

        agent.send(informMessage);

        agent.decreaseBudget(itemPrice);
        
        if (agent.hasNoBudgetLeft()) {
            logger.log(Level.INFO, "{0} : Has no budget left.", agent.getLocalName());

            agent.doDelete();
        }
    }
}