import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.Behaviors;
import java.util.HashMap;
import java.util.Map;

public class Banquier extends AbstractBehavior<Banquier.Command> {

    private final Map<String, Integer> comptes = new HashMap<>();

    public interface Command {}

    public static final class DeposerArgent implements Command {
        final int montant;
        final String compteId;

        public DeposerArgent(String compteId, int montant) {
            this.compteId = compteId;
            this.montant = montant;
        }
    }

    public static final class RetirerArgent implements Command {
        final int montant;
        final String compteId;

        public RetirerArgent(String compteId, int montant) {
            this.compteId = compteId;
            this.montant = montant;
        }
    }

    public static final class ConsulterSolde implements Command {
        final String compteId;

        public ConsulterSolde(String compteId) {
            this.compteId = compteId;
        }
    }

    private Banquier(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(Banquier::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
            .onMessage(DeposerArgent.class, this::onDeposerArgent)
            .onMessage(RetirerArgent.class, this::onRetirerArgent)
            .onMessage(ConsulterSolde.class, this::onConsulterSolde)
            .build();
    }

    private Behavior<Command> onDeposerArgent(DeposerArgent message) {
        comptes.put(message.compteId, comptes.getOrDefault(message.compteId, 0) + message.montant);
        getContext().getLog().info("Dépôt de {} pour le compte {}", message.montant, message.compteId);
        // Ajouter ici la logique de mise à jour de la base de données
        return this;
    }

    private Behavior<Command> onRetirerArgent(RetirerArgent message) {
        int soldeActuel = comptes.getOrDefault(message.compteId, 0);
        if (soldeActuel >= message.montant) {
            comptes.put(message.compteId, soldeActuel - message.montant);
            getContext().getLog().info("Retrait de {} pour le compte {}", message.montant, message.compteId);
            // Ajouter ici la logique de mise à jour de la base de données
        } else {
            getContext().getLog().info("Solde insuffisant pour le retrait de {} pour le compte {}", message.montant, message.compteId);
            // Gérer le cas où le solde est insuffisant
        }
        return this;
    }

    private Behavior<Command> onConsulterSolde(ConsulterSolde message) {
        int solde = comptes.getOrDefault(message.compteId, 0);
        getContext().getLog().info("Le solde du compte {} est {}", message.compteId, solde);
        // Ici, retournez le solde au client si nécessaire
        return this;
    }
}
