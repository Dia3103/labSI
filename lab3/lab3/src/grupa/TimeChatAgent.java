package grupa;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TimeChatAgent extends Agent {

    private List<String> peers; // numele celorlalți agenți (fără mine)

    @Override
    protected void setup() {
        // 1) Preluăm colegii din argumente (setate la lansare)
        //    Exemplu: pentru Agent_1, argumentele vor fi: Agent_2 Agent_3
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            peers = Arrays.stream(args).map(Object::toString).toList();
        } else {
            // fallback (dacă uiți argumentele): presupunem cele 3 nume standard
            peers = Arrays.asList("Agent_1", "Agent_2", "Agent_3");
        }
        // scoate-mă din listă ca să nu-mi trimit mie
        peers = peers.stream().filter(p -> !p.equals(getLocalName())).toList();

        System.out.println(getLocalName() + " pornit. Peers: " + peers);

        // 2) COMPORTAMENT DE TRIMITERE PERIODICĂ (REQUEST "Ce ora este?")
        //    - Folosim TickerBehaviour (discutat în lab) ca să trimitem la fiecare interval. :contentReference[oaicite:2]{index=2}
        addBehaviour(new TickerBehaviour(this, 2000) { // 2 secunde baza
            @Override
            protected void onTick() {
                if (peers.isEmpty()) return;

                // alegem întâmplător un coleg
                String target = peers.get(ThreadLocalRandom.current().nextInt(peers.size()));

                // mai variem puțin perioada (2s ... 5s) ca să pară „aleator”
                long extraMs = ThreadLocalRandom.current().nextLong(0, 3000);
                reset(getPeriod() + extraMs); // data viitoare vom avea (2000 + 0..3000) ms

                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST); // tipul REQUEST din FIPA/ACL :contentReference[oaicite:3]{index=3}
                msg.addReceiver(new AID(target, AID.ISLOCALNAME));
                msg.setContent("Ce ora este?");
                send(msg); // Agent.send() — conform laboratorului :contentReference[oaicite:4]{index=4}

                System.out.println(getLocalName() + " -> " + target + " [REQUEST]: Ce ora este?");
            }
        });

        // 3) COMPORTAMENT DE RECEPȚIE & RĂSPUNS (INFORM "Ora este HH:MM.")
        addBehaviour(new CyclicBehaviour(this) {
            final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");

            // filtrez doar REQUEST (poți filtra și după content dacă vrei)
            final MessageTemplate onlyRequests =
                    MessageTemplate.MatchPerformative(ACLMessage.REQUEST); // MessageTemplate din lab :contentReference[oaicite:5]{index=5}

            @Override
            public void action() {
                ACLMessage msg = myAgent.receive(onlyRequests); // Agent.receive() — coada privată de mesaje :contentReference[oaicite:6]{index=6}
                if (msg == null) {
                    block(); // economisim CPU până vine un mesaj (recomandat în lab) :contentReference[oaicite:7]{index=7}
                    return;
                }

                String content = msg.getContent();
                String sender = msg.getSender().getLocalName();

                System.out.println(getLocalName() + " <~ " + sender + " [REQUEST]: " + content);

                // răspuns INFORMATIV (INFORM)
                ACLMessage reply = msg.createReply(); // păstrează automat receiver = expeditor :contentReference[oaicite:8]{index=8}
                reply.setPerformative(ACLMessage.INFORM);
                String now = LocalTime.now().format(HHMM);
                reply.setContent("Ora este " + now + ".");
                send(reply);

                System.out.println(getLocalName() + " -> " + sender + " [INFORM]: Ora este " + now + ".");
            }
        });

        // 4) (opțional) COMPORTAMENT DE PRIMIT INFORMAREA (INFORM) — doar ca să afișăm clar
        addBehaviour(new CyclicBehaviour(this) {
            final MessageTemplate onlyInforms =
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM);

            @Override
            public void action() {
                ACLMessage msg = myAgent.receive(onlyInforms);
                if (msg == null) { block(); return; }

                System.out.println(getLocalName() + " <~ " + msg.getSender().getLocalName()
                        + " [INFORM]: " + msg.getContent());
            }
        });
    }
}
