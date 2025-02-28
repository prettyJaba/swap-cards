package com.swaps.swap_cards.service;

import com.swaps.swap_cards.entity.SwapCard;
import com.swaps.swap_cards.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

@Service
public class SwapCardService {
    @PersistenceContext
    private EntityManager entityManager;

    public SwapCard createSwapCard(String name, String linkToImage, String description, User creator) {
        SwapCard swapCard = new SwapCard();
        swapCard.setName(name);
        swapCard.setDescription(description);
        swapCard.setLinkToImage(linkToImage);
        swapCard.setCreator(creator);
        swapCard.setOwner(creator);
        entityManager.persist(swapCard);
        return swapCard;
    }

    public void transferCard(Integer cardId, User currentOwner, User newOwner) {
        SwapCard card = entityManager.find(SwapCard.class, cardId);

        if (card != null) {
            if (card.getOwner().equals(currentOwner)) {
                card.setOwner(newOwner);
                entityManager.merge(card);
            } else {
                throw new IllegalArgumentException("You are not the owner of this card");
            }

        } else {
            throw new IllegalArgumentException("Card not found");
        }
    }
}
