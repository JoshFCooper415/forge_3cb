package forge.ai.ability;

import forge.ai.AiAbilityDecision;
import forge.ai.AiAttackController;
import forge.ai.AiPlayDecision;
import forge.ai.SpellAbilityAi;
import forge.game.ability.AbilityUtils;
import forge.game.card.Card;
import forge.game.card.CardCollectionView;
import forge.game.card.CardLists;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;
import forge.game.zone.ZoneType;
import forge.util.collect.FCollection;

import java.util.List;

public class TwoPilesAi extends SpellAbilityAi {
    @Override
    protected AiAbilityDecision canPlayAI(Player ai, SpellAbility sa) {
        final Card card = sa.getHostCard();
        ZoneType zone = null;

        if (sa.hasParam("Zone")) {
            zone = ZoneType.smartValueOf(sa.getParam("Zone"));
        }

        final String valid = sa.getParamOrDefault("ValidCards", "");

        final Player opp = AiAttackController.choosePreferredDefenderPlayer(ai);

        if (sa.usesTargeting()) {
            sa.resetTargets();
            if (sa.canTarget(opp)) {
                sa.getTargets().add(opp);
            } else {
                return new AiAbilityDecision(0, AiPlayDecision.CantPlayAi);
            }
        }

        final List<Player> tgtPlayers = sa.usesTargeting() && !sa.hasParam("Defined")
                ? new FCollection<>(sa.getTargets().getTargetPlayers())
                : AbilityUtils.getDefinedPlayers(card, sa.getParam("Defined"), sa);

        final Player p = tgtPlayers.get(0);
        CardCollectionView pool;
        if (sa.hasParam("DefinedCards")) {
            pool = AbilityUtils.getDefinedCards(card, sa.getParam("DefinedCards"), sa);
        } else {
            pool = p.getCardsIn(zone);
        }
        pool = CardLists.getValidCards(pool, valid, card.getController(), card, sa);
        int size = pool.size();
        if (size > 2) {
            return new AiAbilityDecision(100, AiPlayDecision.WillPlay);
        } else {
            return new AiAbilityDecision(0, AiPlayDecision.CantPlayAi);
        }
    }
}
