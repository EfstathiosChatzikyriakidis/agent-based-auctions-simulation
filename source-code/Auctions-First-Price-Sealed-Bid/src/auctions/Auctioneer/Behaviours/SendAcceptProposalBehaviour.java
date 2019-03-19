package auctions.Auctioneer.Behaviours;

import auctions.Auctioneer.Agents.AuctioneerAgent;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import java.util.logging.Level;
import java.util.Collections;
import java.util.ArrayList;
import jade.util.Logger;
import javafx.util.Pair;
import java.util.List;
import jade.core.AID;

public class SendAcceptProposalBehaviour extends Behaviour {
    private final Logger logger = Logger.getMyLogger(getClass().getName());

    private final AuctioneerAgent agent;

    private final Pair<String, Integer> item;

    private boolean isDone;

    public SendAcceptProposalBehaviour(AuctioneerAgent agent, Pair<String, Integer> item) {
        super(agent);

        this.agent = agent;
        
        this.item = item;
    }

    @Override
    public void action() {
        if (!agent.areBidsReceived()) {
            return;
        }

        List<Pair<AID, Integer>> bids = new ArrayList<>(agent.getBids());

        if (bids.size() > 0) {
            Collections.sort(bids, Collections.reverseOrder((b1, b2) -> b1.getValue().compareTo(b2.getValue())));

            Pair<AID, Integer> bestBid = bids.stream().findFirst().get();

            ACLMessage orderMessage = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);

            orderMessage.addReceiver(bestBid.getKey());

            orderMessage.setContent(item.getKey() + "," + bestBid.getValue());

            orderMessage.setConversationId("bid-auction-first-price-sealed-bid");
            
            orderMessage.setReplyWith("order-" + System.currentTimeMillis());

            if (agent.removeItem(item)) {
                logger.log(Level.INFO, "{0} : ''{1}'' sold to ''{2}'' bidder.", new Object [] { agent.getLocalName(), item.getKey(), bestBid.getKey().getLocalName()});
            } else {
                orderMessage.setPerformative(ACLMessage.FAILURE);

                orderMessage.setContent("The item is no longer available.");
            }

            agent.send(orderMessage);

            agent.clearCurrentAuction();
        }
        else {
            agent.placeItemAsLast(item);

            logger.log(Level.INFO, "{0} : No winner for the item.", agent.getLocalName());
        }

        isDone = true;
    }

    @Override
    public boolean done() {
        return isDone;
    }
}