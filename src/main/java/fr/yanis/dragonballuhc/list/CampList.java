package fr.yanis.dragonballuhc.list;

public enum CampList {

    UNIVERS7("Univers 7"),
    ANTAGONISTE("Antagoniste"),
    UNIVERS_RIVAUX("Univers Rivaux"),
    DUO("Duo"),
    SOLO("Solo");

    private String displayName;

    CampList(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
