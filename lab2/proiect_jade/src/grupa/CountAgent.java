package grupa;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;

public class CountAgent extends Agent {

    @Override
    protected void setup() {
        // Opțional: vezi numele agentului în consolă la start
        System.out.println("Agentul " + getLocalName() + " a pornit.");

        addBehaviour(new SimpleBehaviour(this) {
            private int curent = 0;
            private boolean finished = false;

            @Override
            public void action() {
                // creștem numărul
                curent++;

                // afișăm mesajul cerut
                System.out.println("Eu, agentul " + myAgent.getLocalName()
                        + ", am numarat pana la " + curent);

                // pauză 0,5 s (500 ms), exact ca în laborator cu Thread.sleep
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // nu facem nimic special; reluăm
                }

                // când am ajuns la 100, marcăm comportamentul ca terminat
                if (curent >= 100) {
                    finished = true;
                }
            }

            @Override
            public boolean done() {
                return finished;
            }

            @Override
            public int onEnd() {
                // după ce termină comportamentul, oprim agentul curent
                myAgent.doDelete();
                return super.onEnd();
            }
        });
    }

    @Override
    protected void takeDown() {
        // mesaj de închidere (practic din exemplul AgDel)
        System.out.println("Agentul " + getAID().getName() + " se opreste.");
    }
}
