package auctions.Bidder.Behaviours;

import auctions.Bidder.Agents.BidderAgent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.ACLMessage;
import java.util.logging.Level;
import jade.util.Logger;

public class ReceiveInformBehaviour extends CyclicBehaviour {
    private final Logger logger = Logger.getMyLogger(getClass().getName());

    private final BidderAgent agent;

    public ReceiveInformBehaviour(BidderAgent agent) {
        super(agent);

        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

        ACLMessage informMessage = agent.receive(messageTemplate);

        if (informMessage == null) {
            block();

            return;
        }

        logger.log(Level.INFO, "{0} : The proposal is received.", agent.getLocalName());
    }
}