package fr.afpa.choral_riff.entity;

public enum TypeEnsemble {
    CHOEUR("Chorale"),
    ORCHESTRE("Orchestre"),
    QUATUOR("Quatuor"),
    BAND("Groupe de Rock"),
    AUTRE("Autre");

    private final String label;

    TypeEnsemble(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}


