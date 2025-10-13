package grupa;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import java.util.concurrent.ThreadLocalRandom;

public class RandomCountAgent extends Agent {

    private long delayMs; // întârzierea acestui agent (aleasă o singură dată)

    @Override
    protected void setup() {
        // alege aleator în [500, 5000] ms = [0.5, 5] s
        delayMs = ThreadLocalRandom.current().nextLong(500, 5000 + 1);

        System.out.println("Agentul " + getLocalName()
                + " a pornit cu intarziere de ~" + delayMs + " ms/pas.");

        addBehaviour(new SimpleBehaviour(this) {
            private int curent = 0;
            private boolean finished = false;

            @Override
            public void action() {
                curent++;
                System.out.println("Eu, agentul " + myAgent.getLocalName()
                        + ", am numarat pana la " + curent
                        + " (pauza " + delayMs + " ms)");
                try { Thread.sleep(delayMs); } catch (InterruptedException ignored) {}
                if (curent >= 100) finished = true;
            }

            @Override
            public boolean done() { return finished; }

            @Override
            public int onEnd() {
                myAgent.doDelete(); // închidem agentul la final (model AgDel din lab) :contentReference[oaicite:1]{index=1}
                return super.onEnd();
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("Agentul " + getAID().getName() + " se opreste.");
    }
}
