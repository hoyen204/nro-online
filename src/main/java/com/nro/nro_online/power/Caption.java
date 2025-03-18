package com.nro.nro_online.power;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Caption {
    private final int id;
    private final String earth;
    private final String saiya;
    private final String namek;
    private final long power;

    public String getCaption(int planet) {
        return switch (planet) {
            case 1 -> namek;
            case 2 -> saiya;
            default -> earth;
        };
    }
}