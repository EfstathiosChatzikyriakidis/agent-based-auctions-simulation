package auctions.Auctioneer.Agents;

import auctions.Auctioneer.Behaviours.SendAcceptProposalBehaviour;
import auctions.Auctioneer.Behaviours.HandleAuctionsBehaviour;
import auctions.Auctioneer.Behaviours.FindBiddersBehaviour;
import auctions.Auctioneer.Behaviours.ReceiveBidsBehaviour;
import auctions.Auctioneer.Behaviours.SendCfpBehaviour;
import auctions.Agent.Exceptions.AgentException;
import auctions.Helpers.Catalogue;

import jade.lang.acl.MessageTemplate;
import java.util.logging.Level;
import java.util.Collections;
import java.util.ArrayList;
import javafx.util.Pair;
import jade.util.Logger;
import jade.core.Agent;
import java.util.List;
import jade.core.AID;

public class AuctioneerAgent extends Agent {
    private final Logger logger = Logger.getMyLogger(getClass().getName());

    private final List<AID> bidders = new ArrayList<>();
    
    private final List<Pair<AID, Integer>> bids = new ArrayList<>();

    private final Catalogue catalogue = new Catalogue();

    private String itemsFilePath;

    private MessageTemplate messageTemplate;

    private boolean biddersFound, CfpSent, bidsReceived;

    private FindBiddersBehaviour p;

    private SendCfpBehaviour q;

    private ReceiveBidsBehaviour r;

    private SendAcceptProposalBehaviour s;

    @Override
    protected void setup() {
        handleArguments();

        initializeCatalogue();

        logger.log(Level.INFO, "{0}: is ready.", getLocalName());

        addBehaviour(new HandleAuctionsBehaviour(this));
    }

    @Override
    protected void takeDown() {
        logger.log(Level.INFO, "{0}: Takes down!", getLocalName());
    }

    private void handleArguments () {
        Object[] arguments = getArguments();

        if (arguments == null || arguments.length != 1) {
            logger.log(Level.SEVERE, "{0} : Incorrect number of provided arguments.", getLocalName());

            throw new AgentException("Incorrect number of provided arguments.");
        }

        itemsFilePath = (String) arguments[0];
    }

    private void initializeCatalogue () {
        catalogue.initializeFromFile(itemsFilePath);
    }

    public List<AID> getBidders () {
        return Collections.unmodifiableList(bidders);
    }

    public List<Pair<AID, Integer>> getBids () {
        return Collections.unmodifiableList(bids);
    }

    public void addBidder(AID bidder) {
        bidders.add(bidder);
    }

    public void addBid(Pair<AID, Integer> bid) {
        bids.add(bid);
    }

    public boolean removeItem (Pair<String, Integer> item) {
        return catalogue.removeItem(item);
    }
    
    public void placeItemAsLast (Pair<String, Integer> item) {
        catalogue.placeItemAsLast(item);
    }

    public void setMessageTemplate (MessageTemplate messageTemplate) {
        this.messageTemplate = messageTemplate;
    }
    
    public MessageTemplate getMessageTemplate () {
        return messageTemplate;
    }

    public boolean shouldSendCfp() {
        return biddersFound && !CfpSent;
    }

    public void cfpIsSent() {
        CfpSent = true;
    }
    
    public boolean isCfpSent() {
        return CfpSent;
    }

    public void biddersAreFound() {
        biddersFound = true;
    }

    public boolean areBiddersFound() {
        return biddersFound;
    }

    public void bidsAreReceived() {
        bidsReceived = true;
    }

    public boolean areBidsReceived() {
        return bidsReceived;
    }

    public void clearCurrentAuction() {
        biddersFound = CfpSent = bidsReceived = false;

        bidders.clear();

        bids.clear();
    }

    public void handleNextAuction() {
        clearCurrentAuction();

        if (catalogue.hasItems()) {
            Pair<String, Integer> item = catalogue.getFirstItem();

            if (p != null) removeBehaviour(p);
            if (q != null) removeBehaviour(q);
            if (r != null) removeBehaviour(r);
            if (s != null) removeBehaviour(s);

            p = new FindBiddersBehaviour(this);
            q = new SendCfpBehaviour(this, item);
            r = new ReceiveBidsBehaviour(this);
            s = new SendAcceptProposalBehaviour(this, item);

            addBehaviour(p);
            addBehaviour(q);
            addBehaviour(r);
            addBehaviour(s);
        }
        else {
            logger.log(Level.INFO, "{0} : No more items.", getLocalName());
        }
    }
}