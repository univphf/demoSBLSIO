package com.example.demo;

//POJO ou JavaBean Etudiants
//contient des accesseurs Get et Set
// un ou plusieurs constructeurs
// une implémentation override de la méthode toString()

import java.io.Serializable;


public class Etudiants implements Serializable{
int ID;
String nom;
String prenom;
String constante;
String date_insc;
//ces variables seront sérialisé et desérialisé via le format Json grâce 
// aux méthodes GET et SET des accesseurs.


    @Override
    public String toString() {
        return "Etudiants{" + "ID=" + ID + ", nom=" + nom + ", prenom=" + prenom + ", constante=" + constante + ", date_insc=" + date_insc + '}';
    }

    public Etudiants(int ID, String nom, String prenom, String constante, String date_insc) {
        this.ID = ID;
        this.nom = nom;
        this.prenom = prenom;
        this.constante = constante;
        this.date_insc = date_insc;
    }

    public String getConstante() {
        return constante;
    }

    public void setConstante(String constante) {
        this.constante = constante;
    }

    public String getDate_insc() {
        return date_insc;
    }

    public void setDate_insc(String date_insc) {
        this.date_insc = date_insc;
    }

    public Etudiants() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    
}
