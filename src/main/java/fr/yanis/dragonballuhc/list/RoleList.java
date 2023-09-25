package fr.yanis.dragonballuhc.list;

import fr.yanis.dragonballuhc.roles.Role;
import fr.yanis.dragonballuhc.roles.duo.BlackGoku;
import fr.yanis.dragonballuhc.roles.duo.Zamasu;
import fr.yanis.dragonballuhc.roles.solo.Baby;

public enum RoleList {

    BlackGoku("BlackGoku", CampList.DUO, new BlackGoku()),
    Zamasu("Zamasu", CampList.DUO, new Zamasu()),
    Baby("Baby", CampList.SOLO, new Baby());

    private String name;
    private CampList camp;
    private Role role;

    RoleList(String name, CampList camp, Role role) {
        this.name = name;
        this.camp = camp;
        this.role = role;
    }

    public static RoleList getByName(String name) {
        for (RoleList role : RoleList.values()) {
            if(role.getName().equalsIgnoreCase(name)){
                return role;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public CampList getCamp() {
        return camp;
    }

    public Role getInstance() {
        return role;
    }
}
