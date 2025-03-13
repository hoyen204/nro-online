package com.nro.nro_online.card;

import java.util.HashMap;
import java.util.Map;

import com.nro.nro_online.models.player.Player;
import lombok.Getter;
import lombok.Setter;

public class CollectionBook {

    @Getter
    @Setter
    private Map<Integer, Card> cards = new HashMap<>();
    private final Player player;

    public CollectionBook(Player player) {
        this.player = player;
    }

    public void init() {
        if (cards == null) cards = new HashMap<>();
        Map<Integer, CardTemplate> templates = CardManager.gI().getCardTemplates();
        for (CardTemplate template : templates.values()) {
            cards.computeIfAbsent(template.getId(), k -> {
                Card card = new Card();
                card.setId(template.getId());
                return card;
            });
        }
        cards.values().forEach(Card::setTemplate);
    }

    public void add(Card card) {
        cards.put(card.getId(), card);
    }

    public void remove(Card card) {
        cards.remove(card.getId());
    }

    public Card find(int id) {
        return cards.get(id);
    }

    public Card findWithItemID(int itemId) {
        return cards.values().stream()
                .filter(card -> card.getCardTemplate() != null && card.getCardTemplate().getItemID() == itemId)
                .findFirst()
                .orElse(null);
    }

    public Card findWithMobID(int mobId) {
        return cards.values().stream()
                .filter(card -> card.getCardTemplate() != null && card.getCardTemplate().getMobID() == mobId)
                .findFirst()
                .orElse(null);
    }
}