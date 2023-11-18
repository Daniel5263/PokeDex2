package com.example.pokedex2;

import java.util.List;
import java.util.Random;

public class Tickers {
    private String name;
    private Integer id;
    private Integer weight;
    private Integer height;
    private Integer base_experience;
    private List<Move> moves;
    private List<Ability> abilities;

    public Tickers(String name, Integer id, Integer weight, Integer height, Integer base_experience, List<Move> moves, List<Ability> abilities) {
        this.name = name;
        this.id = id;
        this.weight = weight;
        this.height = height;
        this.base_experience = base_experience;
        this.moves = moves;
        this.abilities = abilities;
    }

    public String getName(){return name;}
    public Integer getId(){return id;}
    public Integer getWeight(){return weight;}
    public Integer getHeight(){return height;}
    public Integer getBaseXP(){return base_experience;}
    public List<Move> getMoves(){return moves;}
    public List<Ability> getAbilities(){return abilities;}

    public String getMoveName() {
        if (moves != null && !moves.isEmpty()) {
            int randomIndex = new Random().nextInt(moves.size());

            Move randomMove = moves.get(randomIndex);
            if (randomMove != null && randomMove.getMove() != null) {
                return randomMove.getMove().getMoveN();
            }
        }
        return null;
    }

    public String getAbilityName() {
        if (abilities != null && !abilities.isEmpty()) {
            int randomIndex = new Random().nextInt(abilities.size());

            Ability randomAbility = abilities.get(randomIndex);
            if (randomAbility != null && randomAbility.getAbility() != null) {
                return randomAbility.getAbility().getAbilityN();
            }
        }
        return null;
    }

    public class Move {
        private MoveDetails move;

        public MoveDetails getMove() {
            return move;
        }
    }

    public class MoveDetails {
        private String name;
        private String url;

        public String getMoveN() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }

    public class Ability {
        private boolean is_hidden;
        private int slot;
        private AbilityDetails ability;

        public boolean isIs_hidden() {
            return is_hidden;
        }

        public int getSlot() {
            return slot;
        }

        public AbilityDetails getAbility() {
            return ability;
        }
    }

    public class AbilityDetails {
        private String name;
        private String url;

        public String getAbilityN() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
}