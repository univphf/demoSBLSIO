package com.example.demo;

//POJO ou JavaBean Token

import java.io.Serializable;

//contient des accesseurs Get et Set
// un ou plusieurs constructeurs
// une implémentation override de la méthode toString()

class Token implements Serializable{

String jeton;

    public Token() {
    }

    public Token(String jeton) {
        this.jeton = jeton;
    }

    public String getJeton() {
        return jeton;
    }

    public void setJeton(String jeton) {
        this.jeton = jeton;
    }
    
}
