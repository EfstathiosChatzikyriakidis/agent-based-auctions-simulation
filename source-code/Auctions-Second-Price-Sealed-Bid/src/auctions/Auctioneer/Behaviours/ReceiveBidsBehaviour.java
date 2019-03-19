package auctions.Auctioneer.Behaviours;

import auctions.Auctioneer.Agents.AuctioneerAgent;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;

public class ReceiveBidsBehaviour extends Behaviour {
    private final AuctioneerAgent agent;

    private int numberOfReplies;

    public ReceiveBidsBehaviour(AuctioneerAgent agent) {
        super(agent);

        this.agent = agent;
    }

    @Override
    public void action() {
        if (!agent.isCfpSent()) {
            return;
        }

        ACLMessage proposalMessage = agent.receive(agent.getMessageTemplate());

        if (proposalMessage == null) {
            block();

            return;
        }

        if (proposalMessage.getPerformative() == ACLMessage.PROPOSE) {
            int bidderPrice = Integer.parseInt(proposalMessage.getContent());

            agent.addBid(new Pair<>(proposalMessage.getSender(), bidderPrice));

            ACLMessage informMessage = proposalMessage.createReply();

            informMessage.setPerformative(ACLMessage.INFORM);

            agent.send(informMessage);
        }

        numberOfReplies++;

        if (numberOfReplies >= agent.getBidders().size()) {
            agent.bidsAreReceived();
        }
    }

    @Override
    public boolean done() {
        return agent.areBidsReceived();
    }
}