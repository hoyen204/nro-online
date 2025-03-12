/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nro.nro_online.attr;

import java.sql.ResultSet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AttributeTemplate {
    private int id;
    private String name;

    public AttributeTemplate(ResultSet rs) {

    }
}
