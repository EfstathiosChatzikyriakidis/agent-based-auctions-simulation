package auctions.Bidder.Agents;

import auctions.Bidder.Behaviours.ReceiveAcceptProposalBehaviour;
import auctions.Bidder.Behaviours.ReceiveInformBehaviour;
import auctions.Bidder.Behaviours.ReceiveCfpBehaviour;
import auctions.Agent.Exceptions.AgentException;
import auctions.Helpers.RandomValueGenerator;
import auctions.Helpers.Catalogue;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.util.logging.Level;
import jade.domain.DFService;
import jade.util.Logger;
import jade.core.Agent;

public class BidderAgent extends Agent {
    private final Logger logger = Logger.getMyLogger(getClass().getName());

    private final RandomValueGenerator rng = new RandomValueGenerator();
    
    private final Catalogue catalogue = new Catalogue();

    private String itemsFilePath;

    private int budget, budget_lower_bound, budget_upper_bound;
   
    @Override
    protected void setup() {
        handleArguments();

        initializeBudget();

        initializeCatalogue();

        registerToDF();

        logger.log(Level.INFO, "{0}: Ready with initial ''{1}'' budget.", new Object[] { getLocalName(), budget });

        addBehaviour(new ReceiveCfpBehaviour(this));
        addBehaviour(new ReceiveAcceptProposalBehaviour(this));
        addBehaviour(new ReceiveInformBehaviour(this));
    }

    @Override
    protected void takeDown() {
        deregisterFromDF();

        logger.log(Level.INFO, "{0}: Takes down!", getLocalName());
    }

    private void registerToDF () {
        String localName = getLocalName();
        
        DFAgentDescription dfd = new DFAgentDescription();

        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();

        sd.setType("auction-first-price-sealed-bid");

        sd.setName(localName);

        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException exception) {
            logger.log(Level.WARNING, "{0} : An exception occurred while registering to the DF agent.", localName);
        }
    }

    private void deregisterFromDF () {
        String localName = getLocalName();

        try {
            DFService.deregister(this);
        } catch (FIPAException exception) {
            logger.log(Level.WARNING, "{0} : An exception occurred while deregistering from the DF agent.", localName);
        }
    }

    private void handleArguments () {
        Object[] arguments = getArguments();

        if (arguments == null || arguments.length != 3) {
            logger.log(Level.SEVERE, "{0} : Incorrect number of provided arguments.", getLocalName());

            throw new AgentException("Incorrect number of provided arguments.");
        }

        String lowerBound = (String) arguments[0];
        String upperBound = (String) arguments[1];
        
        handleBudgetBounds (lowerBound, upperBound);

        itemsFilePath = (String) arguments[2];
    }

    private void handleBudgetBounds (String lowerBound, String upperBound) {
        try {
            budget_lower_bound = Integer.parseInt(lowerBound);
        } catch (NumberFormatException exception) {
            logger.log(Level.SEVERE, "{0} : Incorrect lower budget bound value.", getLocalName());

            throw new AgentException("Incorrect lower budget bound value.", exception);
        }

        try {
            budget_upper_bound = Integer.parseInt(upperBound);
        } catch (NumberFormatException exception) {
            logger.log(Level.SEVERE, "{0} : Incorrect upper budget bound value.", getLocalName());

            throw new AgentException("Incorrect upper budget bound value.", exception);
        }
    }
    
    private void initializeBudget () {
        budget = rng.getNumber(budget_lower_bound, budget_upper_bound);
    }

    private void initializeCatalogue () {
        catalogue.initializeFromFile(itemsFilePath);
    }

    public int getItemEvaluation (String itemName) {
        return catalogue.getItemPriceByName (itemName);
    }

    public void decreaseBudget(int money) {
        budget -= money;
    }

    public boolean hasNoBudgetLeft() {
        return budget <= 0;
    }

    public int getBudget() {
        return budget;
    }
}