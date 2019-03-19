package auctions.Auctioneer.Behaviours;

import auctions.Auctioneer.Agents.AuctioneerAgent;

import jade.core.behaviours.TickerBehaviour;

public class HandleAuctionsBehaviour extends TickerBehaviour {
    private final AuctioneerAgent agent;

    public HandleAuctionsBehaviour(AuctioneerAgent agent) {
        super(agent, 20000);

        this.agent = agent;
    }

    @Override
    protected void onTick() {
        agent.handleNextAuction();
    }
}