/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.skill;

import java.util.ArrayList;
import java.util.List;

import nro.models.skill.Skill;

/**
 *
 * @author Kitak
 */
public class SkillTemplate {

    public byte id;

    public int classId;

    public String name;

    public int maxPoint;

    public int manaUseType;

    public int type;

    public int iconId;

    public String description;

    public Skill[] skills;

    public List<Skill> skillss = new ArrayList<>();

    public String damInfo;
}
