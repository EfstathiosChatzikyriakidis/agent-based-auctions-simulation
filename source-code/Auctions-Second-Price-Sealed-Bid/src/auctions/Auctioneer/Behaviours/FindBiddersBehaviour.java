package auctions.Auctioneer.Behaviours;

import auctions.Auctioneer.Agents.AuctioneerAgent;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.behaviours.Behaviour;
import java.util.stream.Collectors;
import jade.domain.FIPAException;
import java.util.logging.Level;
import jade.domain.DFService;
import jade.util.Logger;

public class FindBiddersBehaviour extends Behaviour {
    private final Logger logger = Logger.getMyLogger(getClass().getName());

    private final AuctioneerAgent agent;

    public FindBiddersBehaviour(AuctioneerAgent agent) {
        super(agent);

        this.agent = agent;
    }

    @Override
    public void action() {
        if (agent.areBiddersFound()) {
            return;
        }

        String localName = agent.getLocalName();

        DFAgentDescription dfd = new DFAgentDescription();

        ServiceDescription sd = new ServiceDescription();

        sd.setType("auction-second-price-sealed-bid");

        dfd.addServices(sd);

        try {
            DFAgentDescription[] items = DFService.search(agent, dfd);

            if (items.length > 0) {
                for (DFAgentDescription item : items) {
                    agent.addBidder(item.getName());
                }

                String bidders = agent.getBidders().stream().map(o -> o.getLocalName()).collect(Collectors.joining(", "));

                logger.log(Level.INFO, "{0} : Found the following ''{1}'' bidders.", new Object[] { localName, bidders });

                agent.biddersAreFound();
            }
        } catch (FIPAException exception) {
            logger.log(Level.WARNING, "{0} : An exception occurred while trying to find the bidders.", localName);
        }
    }

    @Override
    public boolean done() {
        return agent.areBiddersFound();
    }
}