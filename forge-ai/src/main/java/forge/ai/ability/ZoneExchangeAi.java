package forge.ai.ability;

import forge.ai.AiAbilityDecision;
import forge.ai.AiPlayDecision;
import forge.ai.ComputerUtilCard;
import forge.ai.SpellAbilityAi;
import forge.game.ability.AbilityUtils;
import forge.game.card.Card;
import forge.game.card.CardCollection;
import forge.game.card.CardLists;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;
import forge.game.zone.ZoneType;
import forge.util.MyRandom;

public class ZoneExchangeAi extends SpellAbilityAi {

/* (non-Javadoc)
 * @see forge.card.abilityfactory.SpellAiLogic#canPlayAI(forge.game.player.Player, java.util.Map, forge.card.spellability.SpellAbility)
 */
    @Override
    protected AiAbilityDecision canPlayAI(Player ai, final SpellAbility sa) {
        Card object1 = null;
        Card object2 = null;
        final Card source = sa.getHostCard();
        final String type = sa.getParam("Type");
        if (sa.hasParam("Object")) {
            object1 = AbilityUtils.getDefinedCards(source, sa.getParam("Object"), sa).get(0);
        } else {
            object1 = source;
        }
        final ZoneType zone1 = sa.hasParam("Zone1") ? ZoneType.smartValueOf(sa.getParam("Zone1")) : ZoneType.Battlefield;
        final ZoneType zone2 = sa.hasParam("Zone2") ? ZoneType.smartValueOf(sa.getParam("Zone2")) : ZoneType.Hand;
        CardCollection list = new CardCollection(ai.getCardsIn(zone2));
        if (type != null) {
            list = CardLists.getValidCards(list, type, ai, source, sa);
        }
        object2 = ComputerUtilCard.getBestAI(list);
        if (object1 == null || object2 == null || !object1.isInZone(zone1) || !object1.getOwner().equals(ai)) {
            return new AiAbilityDecision(0, AiPlayDecision.CantPlayAi);
        }
        if (type.equals("Aura")) {
            Card c = object1.getEnchantingCard();
            if (!c.canBeAttached(object2, sa)) {
                return new AiAbilityDecision(0, AiPlayDecision.CantPlayAi);
            }
        }
        if (object2.getCMC() > object1.getCMC()) {
            if (MyRandom.getRandom().nextFloat() <= Math.pow(.6667, sa.getActivationsThisTurn())) {
                return new AiAbilityDecision(100, AiPlayDecision.WillPlay);
            } else {
                return new AiAbilityDecision(0, AiPlayDecision.StopRunawayActivations);
            }
        }
        return new AiAbilityDecision(0, AiPlayDecision.CantPlayAi);
    }

    /* (non-Javadoc)
     * @see forge.card.abilityfactory.SpellAiLogic#doTriggerAINoCost(forge.game.player.Player, java.util.Map, forge.card.spellability.SpellAbility, boolean)
     */
    @Override
    protected AiAbilityDecision doTriggerAINoCost(Player aiPlayer, SpellAbility sa, boolean mandatory) {
        return new AiAbilityDecision(100, AiPlayDecision.WillPlay);
    }
}
