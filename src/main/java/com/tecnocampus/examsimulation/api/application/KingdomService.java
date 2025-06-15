package com.tecnocampus.examsimulation.api.application;

import com.tecnocampus.examsimulation.api.domain.Kingdom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class KingdomService {
    private final KingdomRepository kingdomRepository;

    public KingdomService(KingdomRepository kingdomRepository) {
        this.kingdomRepository = kingdomRepository;
    }

    @Transactional
    public Kingdom createKingdom(Kingdom kingdom) {
        return kingdomRepository.save(kingdom);
    }

    @Transactional
    public Kingdom startDailyProduction(String id) {
        Kingdom kingdom = kingdomRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Kingdom not found"));

        if (kingdom.getCitizens() == 0) {
            kingdomRepository.delete(id);
            throw new IllegalStateException("Kingdom has no citizens and was deleted");
        }

        int foodNeeded = kingdom.getCitizens();
        if (kingdom.getFood() >= foodNeeded) {
            kingdom.setFood(kingdom.getFood() - foodNeeded);
            kingdom.setGold(kingdom.getGold() + (2 * kingdom.getCitizens()));
        } else {
            kingdom.setCitizens(kingdom.getFood());
            kingdom.setFood(0);
            kingdom.setGold(kingdom.getGold() + (2 * kingdom.getCitizens()));

            if (kingdom.getCitizens() == 0) {
                kingdomRepository.delete(id);
                throw new IllegalStateException("Kingdom has no citizens left and was deleted");
            }
        }

        return kingdomRepository.save(kingdom);
    }

    @Transactional
    public Kingdom invest(String id, String type, int gold) {
        Kingdom kingdom = kingdomRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Kingdom not found"));

        if (kingdom.getGold() < gold) {
            throw new IllegalArgumentException("Not enough gold");
        }

        kingdom.setGold(kingdom.getGold() - gold);

        switch (type.toLowerCase()) {
            case "food":
                kingdom.setFood(kingdom.getFood() + (gold * 2));
                break;
            case "citizens":
                kingdom.setCitizens(kingdom.getCitizens() + gold);
                break;
            default:
                throw new IllegalArgumentException("Invalid investment type");
        }

        return kingdomRepository.save(kingdom);
    }

    public Kingdom getKingdom(String id) {
        return kingdomRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Kingdom not found"));
    }

    public Kingdom getRichestKingdom() {
        return kingdomRepository.findRichest()
                .orElseThrow(() -> new NoSuchElementException("No kingdoms found"));
    }

    @Transactional
    public Kingdom attack(String attackerId, String targetId) {
        Kingdom attacker = kingdomRepository.findById(attackerId)
                .orElseThrow(() -> new NoSuchElementException("Attacker kingdom not found"));
        Kingdom target = kingdomRepository.findById(targetId)
                .orElseThrow(() -> new NoSuchElementException("Target kingdom not found"));

        if (attacker.getCitizens() > target.getCitizens()) {
            // Attacker wins
            int stolenGold = target.getGold();
            int stolenCitizens = target.getCitizens() / 2;

            attacker.setGold(attacker.getGold() + stolenGold);
            attacker.setCitizens(attacker.getCitizens() + stolenCitizens);

            target.setGold(0);
            target.setCitizens(target.getCitizens() - stolenCitizens);
        } else {
            // Defender wins (including tie)
            int stolenGold = attacker.getGold();
            int stolenCitizens = attacker.getCitizens() / 2;

            target.setGold(target.getGold() + stolenGold);
            target.setCitizens(target.getCitizens() + stolenCitizens);

            attacker.setGold(0);
            attacker.setCitizens(attacker.getCitizens() - stolenCitizens);
        }

        kingdomRepository.save(attacker);
        kingdomRepository.save(target);

        return attacker;
    }
}