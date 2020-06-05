package com.example.demo;

//POJO ou JavaBean Hello

import java.io.Serializable;

//contient des accesseurs Get et Set
// un ou plusieurs constructeurs
// une implémentation override de la méthode toString()

public class Hello implements Serializable {

    String nom;

    public Hello(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    
}
