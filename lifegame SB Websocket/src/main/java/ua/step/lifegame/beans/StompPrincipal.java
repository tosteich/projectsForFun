package ua.step.lifegame.beans;

import java.security.Principal;

public class StompPrincipal implements Principal {
    private String name;

    StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
